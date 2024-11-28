package com.tekclover.wms.api.transaction.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.*;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DirectStockReceiptService extends BaseService {

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    WarehouseRepository warehouseRepository;

    //=====================================================================================//

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    StagingLineService stagingLineService;

    @Autowired
    MastersService mastersService;

    @Autowired
    InboundOrderProcessingService inboundOrderProcessingService;

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
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
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

            com.tekclover.wms.api.transaction.model.warehouse.Warehouse warehouse = null;
            try {
                Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> optWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                inboundOrder.getCompanyCode(),
                                inboundOrder.getBranchCode(),
                                LANG_ID,
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
                    imBasicData1.setLanguageId(LANG_ID);                                           // LANG_ID
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

    @Transactional
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 8000))
    public InboundHeaderV2 processInboundReceivedV3(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {

        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
        String plantId = inboundIntegrationHeader.getBranchCode();
        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        log.info("CompanyCodeId, plantId, languageId, warehouseId : " + companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId);

        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundIntegrationHeader.getInboundOrderTypeId());
            if (inboundIntegrationHeader.getLoginUserId() != null) {
                MW_AMS = inboundIntegrationHeader.getLoginUserId();
            }

            //Checking whether received refDocNumber processed already.
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }

            List<InboundIntegrationLine> inboundIntegrationLines = new ArrayList<>();

            String idMasterAuthToken = getIDMasterAuthToken();
            String masterAuthToken = getMasterAuthToken();
            Long statusId = 24L;

            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);

            statusDescription = getStatusDescription(statusId, languageId);

            IKeyValuePair description = getDescription(companyCodeId, plantId, languageId, warehouseId);

            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
            List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                log.info("inboundIntegrationLine : " + inboundIntegrationLine);

                ImBasicData1V2 imBasicData1 = null;
                if (inboundIntegrationLine.getManufacturerName() != null) {
                    imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                            languageId, companyCodeId, plantId, warehouseId,
                            inboundIntegrationLine.getItemCode().trim(), inboundIntegrationLine.getManufacturerName(), 0L);
                } else {
                    imBasicData1 = imBasicData1V2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                            languageId, companyCodeId, plantId, warehouseId,
                            inboundIntegrationLine.getItemCode().trim(), 0L);
                }
                log.info("imBasicData1 exists: " + imBasicData1);
                if (inboundIntegrationLine.getItemText() == null && imBasicData1 != null) {
                    inboundIntegrationLine.setItemText(imBasicData1.getDescription());
                }

                // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
                if (imBasicData1 == null) {
                    imBasicData1 = new ImBasicData1V2();
                    BeanUtils.copyProperties(inboundIntegrationLine, imBasicData1, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
                    imBasicData1.setLanguageId(languageId);                                         // LANG_ID
                    imBasicData1.setWarehouseId(warehouseId);                                       // WH_ID
                    imBasicData1.setCompanyCodeId(companyCodeId);                                   // C_ID
                    imBasicData1.setPlantId(plantId);                                               // PLANT_ID
                    imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                 // ITM_CODE
                    imBasicData1.setUomId(inboundIntegrationLine.getUom());                         // UOM_ID
                    imBasicData1.setDescription(inboundIntegrationLine.getItemText());              // ITEM_TEXT
                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setCapacityCheck(false);
                    imBasicData1.setDeletionIndicator(0L);
                    imBasicData1.setCompanyDescription(description.getCompanyDesc());
                    imBasicData1.setPlantDescription(description.getPlantDesc());
                    imBasicData1.setWarehouseDescription(description.getWarehouseDesc());

                    imBasicData1.setStatusId(1L);                                                // STATUS_ID
                    ImBasicData1 createdImBasicData1 = mastersService.createImBasicData1V2(imBasicData1, MW_AMS, masterAuthToken);
                    log.info("ImBasicData1 created: " + createdImBasicData1);
                }

                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
                /*
                 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
                 */
                BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId,
                                                                  companyCodeId, plantId, languageId, masterAuthToken);
                log.info("bomHeader [BOM] : " + bomHeader);
                if (bomHeader != null) {
                    BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(),
                                                                  companyCodeId, plantId, languageId, warehouseId, masterAuthToken);
                    for (BomLine dbBomLine : bomLine) {
                        PreInboundLineEntityV2 preInboundLineEntity = inboundOrderProcessingService.createPreInboundLineBOMBasedV2(companyCodeId, plantId, languageId, warehouseId,
                                                                                                     preInboundNo, inboundIntegrationHeader, inboundIntegrationLine,
                                                                                                     dbBomLine, MW_AMS, description, statusId, statusDescription);
                        toBeCreatedPreInboundLineList.add(preInboundLineEntity);
                        log.info("preInboundLineEntity [BOM] : " + toBeCreatedPreInboundLineList.size());
                    }

                    // Batch Insert - PreInboundLines
                    if (!toBeCreatedPreInboundLineList.isEmpty()) {
                        List<PreInboundLineEntityV2> createdPreInboundLine = preInboundLineV2Repository.saveAll(toBeCreatedPreInboundLineList);
                        log.info("createdPreInboundLine [BOM] : " + createdPreInboundLine);
                        overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
                    }
                }
                inboundIntegrationLines.add(inboundIntegrationLine);
            }

            /*
             * Append PREINBOUNDLINE table through below logic
             */
            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
                try {
                    finalToBeCreatedPreInboundLineList.add(inboundOrderProcessingService.createPreInboundLineV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                                                                                                                inboundIntegrationHeader, inboundIntegrationLine, MW_AMS,
                                                                                                                description, statusId, statusDescription));
                } catch (Exception e) {
                    throw new BadRequestException("Exception While PreInboundLine Create" + e.toString());
                }
            });
            log.info("toBeCreatedPreInboundLineList [API] : " + finalToBeCreatedPreInboundLineList.size());

            // Batch Insert - PreInboundLines
            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
                List<PreInboundLineEntityV2> createdPreInboundLine = preInboundLineV2Repository.saveAll(finalToBeCreatedPreInboundLineList);
                log.info("createdPreInboundLine [API] : " + createdPreInboundLine);
                overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
            }

            List<InboundLineV2> inboundLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
                return inboundLine;
            }).collect(Collectors.toList());
            inboundLineV2Repository.saveAll(inboundLines);

            List<StagingLineEntityV2> stagingLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
                stagingLine.setStagingNo(stagingNo);
                stagingLine.setCaseCode(caseCode);
                stagingLine.setPalletCode(caseCode);

                if(preInboundLine.getBarcodeId() == null) {
                    //Barcode
                    List<String> barcode = getBarCodeId(preInboundLine.getCompanyCode(), preInboundLine.getPlantId(), preInboundLine.getLanguageId(), preInboundLine.getWarehouseId(),
                                                        preInboundLine.getItemCode(), preInboundLine.getManufacturerName());
                    log.info("Barcode : " + barcode);
                    if (barcode != null && !barcode.isEmpty()) {
                        stagingLine.setBarcodeId(barcode.get(0));
                    }
                }

                if(companyCodeId.equalsIgnoreCase(WK_COMPANY_CODE)) {
                    //-----------------PROP_ST_BIN---------------------------------------------
                    StorageBin storageBin = null;
                    try {
                        String referenceField1 = preInboundLine.getArticleNo().substring(0, 2);
                        String referenceField2 = preInboundLine.getGender();
                        log.info("referenceField1, referenceField2, companyCode, plantId, warehouseId, languageId: " + referenceField1 + "," + referenceField2 + "," + companyCodeId + "," + plantId + "," + warehouseId + "," + languageId);
                        storageBin = mastersService.getStorageBinV3(referenceField1, referenceField2, companyCodeId, plantId, warehouseId, languageId, masterAuthToken);
                        log.info("InterimStorageBin: " + storageBin);
                        if (storageBin != null) {
                            stagingLine.setReferenceField5(storageBin.getStorageBin());
                        }
                    } catch (Exception e) {
                        throw new BadRequestException("Invalid StorageBin");
                    }
                }

                return stagingLine;
            }).collect(Collectors.toList());
            stagingLineV2Repository.saveAll(stagingLines);
            log.info("StagingLines Created : " + stagingLines);

            stagingLineV2Repository.updateStagingLineInvQtyUpdateProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo);

            /*------------------Insert into PreInboundHeader table-----------------------------*/
            PreInboundHeaderEntityV2 createPreInboundHeader = inboundOrderProcessingService.createPreInboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                                                                                                                     inboundIntegrationHeader, MW_AMS, description, statusId, statusDescription);
            /*------------------Insert into Inbound Header----------------------------*/
            InboundHeaderV2 createInboundHeader = inboundOrderProcessingService.createInboundHeaderV2(createPreInboundHeader, overallCreatedPreInboundLineList);
            inboundHeaderV2Repository.save(createInboundHeader);
            log.info("createdInboundHeader : " + createInboundHeader);

            StagingHeaderV2 stagingHeader = inboundOrderProcessingService.createStagingHeaderV2(createPreInboundHeader, stagingNo);
            stagingHeaderV2Repository.save(stagingHeader);
            log.info("StagingHeader Created : " + stagingHeader);

            //Gr Header Creation
            GrHeaderV2 createGrHeader = createGrHeaderV2(stagingHeader, caseCode, grNumber);
            grHeaderV2Repository.save(createGrHeader);
            log.info("Create GrHeader : " + createGrHeader);

            if(companyCodeId.equalsIgnoreCase(WK_COMPANY_CODE)) {
                inboundOrderProcessingService.createGrLineV3(companyCodeId, plantId, languageId, warehouseId, createGrHeader.getParentProductionOrderNo(), createGrHeader, idMasterAuthToken, MW_AMS);
            }

            PreInboundHeaderEntityV2 createdPreInboundHeader = preInboundHeaderV2Repository.save(createPreInboundHeader);
            log.info("Created PreInboundHeader : " + createdPreInboundHeader);

            // Inserting into InboundLog Table.
//            createInboundIntegrationLogV2(createPreInboundHeader);

            return createInboundHeader;

        } catch (Exception e) {
//            createInboundIntegrationLogV2(inboundIntegrationHeader, MW_AMS);
            log.error("Inbound Order Processing Exception ----> " + e.toString());
//            e.printStackTrace();

            // Updating the Processed Status
//            log.info("Inbound Rollback Initiated...!" + refDocNumber);

//            preInboundHeaderService.initiateInboundRollBack(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
//                                                            inboundIntegrationHeader.getInboundOrderTypeId());
//            orderService.updateProcessedIbOrderV2(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId());
//            log.info("Inbound Rollback Finished...!" + refDocNumber);
            throw e;
        }
    }

    /**
     *
     * @param stagingHeader
     * @param caseCode
     * @param grNumber
     * @return
     * @throws Exception
     */
    public GrHeaderV2 createGrHeaderV2(StagingHeaderV2 stagingHeader, String caseCode, String grNumber) throws Exception {
        try {
            GrHeaderV2 grHeader = new GrHeaderV2();
            BeanUtils.copyProperties(stagingHeader, grHeader, CommonUtils.getNullPropertyNames(stagingHeader));
            grHeader.setCaseCode(caseCode);
            grHeader.setPalletCode(caseCode);
            grHeader.setGoodsReceiptNo(grNumber);
            return grHeader;
        } catch (Exception e) {
            log.error("Exception while GrHeader Create : " + e.toString());
            throw e;
        }
    }
}