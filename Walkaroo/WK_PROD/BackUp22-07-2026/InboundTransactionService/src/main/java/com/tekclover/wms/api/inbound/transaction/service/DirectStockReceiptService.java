package com.tekclover.wms.api.inbound.transaction.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.dto.BomHeader;
import com.tekclover.wms.api.inbound.transaction.model.dto.BomLine;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.repository.ImBasicData1V2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.InboundOrderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.PreInboundHeaderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.PreInboundLineV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.WarehouseRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DirectStockReceiptService extends BaseService {

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    WarehouseRepository warehouseRepository;

    //=====================================================================================//

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    StagingLineService stagingLineService;

    @Autowired
    MastersService mastersService;

    /**
     * Direct Stock Receipt
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    @Transactional
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 8000))
    public InboundHeaderV2 processInboundReceivedV2(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {

        try {
            log.info("Direct Stock Receipt - Inbound Process Initiated ------> " + refDocNumber + ", " + inboundIntegrationHeader.getInboundOrderTypeId());
            /*
             * Checking whether received refDocNumber processed already.
             */
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber,  inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }

            String warehouseId = inboundIntegrationHeader.getWarehouseID();
            log.info("warehouseId : " + warehouseId);

            // Fetch ITM_CODE inserted in INBOUNDINTEGRATION table and pass the ITM_CODE in IMBASICDATA1 table and
            // validate the ITM_CODE result is Not Null
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            InboundOrderV2 inboundOrder = inboundOrderV2Repository.findByRefDocumentNoAndInboundOrderTypeId(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId());
            log.info("inboundOrder : " + inboundOrder);

            com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse warehouse = null;
            try {
                Optional<com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse> optWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                inboundOrder.getCompanyCode(),
                                inboundOrder.getBranchCode(),
                                "EN",
                                0L
                        );
                log.info("dbWarehouse : " + optWarehouse);

                if (optWarehouse != null && optWarehouse.isEmpty()) {
                    log.info("warehouse not found.");
                    throw new BadRequestException("Warehouse cannot be null.");
                }

                warehouse = optWarehouse.get();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            // Getting PreInboundNo from NumberRangeTable
            String preInboundNo = preInboundHeaderService.getPreInboundNo(warehouseId, inboundOrder.getCompanyCode(), inboundOrder.getBranchCode(), warehouse.getLanguageId());

            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                log.info("inboundIntegrationLine : " + inboundIntegrationLine);
                ImBasicData1V2 imBasicData1 =
                        imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                                warehouse.getLanguageId(),
                                warehouse.getCompanyCodeId(),
                                warehouse.getPlantId(),
                                warehouse.getWarehouseId(),
                                inboundIntegrationLine.getItemCode(),
                                inboundIntegrationLine.getManufacturerName(),
                                0L);
                log.info("imBasicData1 exists: " + imBasicData1);

                // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
                if (imBasicData1 == null) {
                    imBasicData1 = new ImBasicData1V2();
                    imBasicData1.setLanguageId("EN");                                           // LANG_ID
                    imBasicData1.setWarehouseId(warehouseId);                                   // WH_ID
                    imBasicData1.setCompanyCodeId(warehouse.getCompanyCodeId());                // C_ID
                    imBasicData1.setPlantId(warehouse.getPlantId());                            // PLANT_ID
                    imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());             // ITM_CODE
                    imBasicData1.setUomId(inboundIntegrationLine.getUom());                     // UOM_ID
                    imBasicData1.setDescription(inboundIntegrationLine.getItemText());          // ITEM_TEXT
                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setCapacityCheck(false);
                    imBasicData1.setDeletionIndicator(0L);

                    imBasicData1.setStatusId(1L);                                               // STATUS_ID
                    ImBasicData1 createdImBasicData1 =
                            mastersService.createImBasicData1V2(imBasicData1, "MW_AMS", authTokenForMastersService.getAccess_token());
                    log.info("ImBasicData1 created: " + createdImBasicData1);
                }

                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
                /*
                 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
                 */
                BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId,
                        warehouse.getCompanyCodeId(),
                        warehouse.getPlantId(),
                        warehouse.getLanguageId(),
                        authTokenForMastersService.getAccess_token());
                log.info("bomHeader [BOM] : " + bomHeader);
                if (bomHeader != null) {
                    BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), bomHeader.getWarehouseId(),
                            authTokenForMastersService.getAccess_token());
                    List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
                    for (BomLine dbBomLine : bomLine) {
                        PreInboundLineEntityV2 preInboundLineEntity = preInboundHeaderService.createPreInboundLineBOMBasedV2(warehouse, preInboundNo, inboundIntegrationHeader, dbBomLine, inboundIntegrationLine);
                        log.info("preInboundLineEntity [BOM] : " + preInboundLineEntity);
                        toBeCreatedPreInboundLineList.add(preInboundLineEntity);
                    }

                    // Batch Insert - PreInboundLines
                    if (!toBeCreatedPreInboundLineList.isEmpty()) {
                        List<PreInboundLineEntityV2> createdPreInboundLine = preInboundLineV2Repository.saveAll(toBeCreatedPreInboundLineList);
                        log.info("createdPreInboundLine [BOM] : " + createdPreInboundLine);
                        overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
                    }
                }
            }

            /*
             * Append PREINBOUNDLINE table through below logic
             */
            List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                toBeCreatedPreInboundLineList.add(preInboundHeaderService.createPreInboundLineV2(warehouse, preInboundNo, inboundIntegrationHeader, inboundIntegrationLine));
            }

            log.info("toBeCreatedPreInboundLineList [API] : " + toBeCreatedPreInboundLineList);

            // Batch Insert - PreInboundLines
            List<PreInboundLineEntityV2> createdPreInboundLine = new ArrayList<>();
            if (!toBeCreatedPreInboundLineList.isEmpty()) {
                createdPreInboundLine = preInboundLineV2Repository.saveAll(toBeCreatedPreInboundLineList);
                log.info("createdPreInboundLine [API] : " + createdPreInboundLine);
                overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
            }

            /*------------------Insert into PreInboundHeader table-----------------------------*/
            PreInboundHeaderEntityV2 createdPreInboundHeader = preInboundHeaderService.createPreInboundHeaderV2(warehouse, preInboundNo, inboundIntegrationHeader);
            log.info("preInboundHeader Created : " + createdPreInboundHeader);

            /*------------------Insert into Inbound Header And Line----------------------------*/
            InboundHeaderV2 createdInboundHeader = preInboundHeaderService.createInboundHeaderAndLineV2(createdPreInboundHeader, overallCreatedPreInboundLineList);

            // Inserting into InboundLog Table.
            preInboundHeaderService.createInboundIntegrationLogV2(createdPreInboundHeader);

            // process ASN
            StagingHeaderV2 stagingHeader = preInboundHeaderService.processNewASNV2(createdPreInboundHeader, createdInboundHeader.getCreatedBy());
            log.info("StagingHeader Created : " + stagingHeader);

            List<StagingLineEntityV2> stagingLines =
                    stagingLineService.createStagingLineV2(createdPreInboundLine, stagingHeader.getStagingNo(), stagingHeader.getWarehouseId(),
                            stagingHeader.getCompanyCode(), stagingHeader.getPlantId(), stagingHeader.getLanguageId(),
                            createdInboundHeader.getCreatedBy());
            log.info("StagingLines Created : " + stagingLines);

            return createdInboundHeader;
        } catch (Exception e) {
            log.error("Inbound Order Processing - Exception ");
            e.printStackTrace();
            throw e;
        }
    }
}