package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.OperationConsumptionImpl;
import com.tekclover.wms.api.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.mnc.InhouseTransferHeader;
import com.tekclover.wms.api.transaction.model.mnc.InhouseTransferLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.repository.InhouseTransferHeaderRepository;
import com.tekclover.wms.api.transaction.repository.InhouseTransferLineRepository;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class IndusMegaFoodService extends BaseService {

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    InhouseTransferHeaderRepository inhouseTransferHeaderRepository;

    @Autowired
    InhouseTransferLineRepository inhouseTransferLineRepository;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    StorageBinService storageBinService;

    @Autowired
    InhouseTransferHeaderService inhouseTransferHeaderService;

    @Autowired
    MastersService mastersService;

    @Autowired
    OrderManagementLineService orderManagementLineService;

    @Transactional
    public void createInboundInventoryTransfer(PutAwayLineV2 putAwayLine, String loginUserID) throws InvocationTargetException, IllegalAccessException {

        if (putAwayLine != null) {
            if (putAwayLine.getOrderQty() == null || putAwayLine.getPutawayConfirmedQty() == null) {
                throw new BadRequestException("TransferQty is Missing!");
            }
        }

        InhouseTransferHeader dbInhouseTransferHeader = new InhouseTransferHeader();

        BeanUtils.copyProperties(putAwayLine, dbInhouseTransferHeader, CommonUtils.getNullPropertyNames(putAwayLine));
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();

        dbInhouseTransferHeader.setLanguageId(putAwayLine.getLanguageId());
        dbInhouseTransferHeader.setCompanyCodeId(putAwayLine.getCompanyCode());
        dbInhouseTransferHeader.setPlantId(putAwayLine.getPlantId());
        dbInhouseTransferHeader.setWarehouseId(putAwayLine.getWarehouseId());
        dbInhouseTransferHeader.setTransferTypeId(putAwayLine.getInboundOrderTypeId());
        dbInhouseTransferHeader.setTransferMethod(putAwayLine.getReferenceDocumentType());
        // TR_NO
        String TRANSFER_NO = getTransferNoV2(putAwayLine.getCompanyCode(),
                                             putAwayLine.getPlantId(),
                                             putAwayLine.getLanguageId(),
                                             putAwayLine.getWarehouseId(),
                                             authTokenForIDMasterService.getAccess_token());
        dbInhouseTransferHeader.setTransferNumber(TRANSFER_NO);

        IKeyValuePair description = stagingLineV2Repository.getDescription(putAwayLine.getCompanyCode(),
                                                                           putAwayLine.getLanguageId(),
                                                                           putAwayLine.getPlantId(),
                                                                           putAwayLine.getWarehouseId());

        // STATUS_ID - Hard Coded Value="30" at the time of Confirmation
        dbInhouseTransferHeader.setStatusId(30L);
        String statusDescription = stagingLineV2Repository.getStatusDescription(30L, putAwayLine.getLanguageId());
        dbInhouseTransferHeader.setStatusDescription(statusDescription);
        dbInhouseTransferHeader.setCompanyDescription(description.getCompanyDesc());
        dbInhouseTransferHeader.setPlantDescription(description.getPlantDesc());
        dbInhouseTransferHeader.setWarehouseDescription(description.getWarehouseDesc());
        dbInhouseTransferHeader.setDeletionIndicator(0L);
        dbInhouseTransferHeader.setCreatedBy(loginUserID);
        dbInhouseTransferHeader.setUpdatedBy(loginUserID);
        dbInhouseTransferHeader.setCreatedOn(new Date());
        dbInhouseTransferHeader.setUpdatedOn(new Date());

        // - TR_TYP_ID -
//        Long transferTypeId = dbInhouseTransferHeader.getTransferTypeId();
        Long transferTypeId = 3L;

        /*
         * LINES Table
         */
//        for (PutAwayLineV2 putAwayLine : putAwayLines) {

        InhouseTransferLine newInhouseTransferLine = createCopyInboundInhouseTransferLineEntity(putAwayLine);

        if (newInhouseTransferLine.getSourceStorageBin().equalsIgnoreCase(newInhouseTransferLine.getTargetStorageBin())) {
            throw new BadRequestException("Source Bin and Target Bin cannot be same");
        }
        if (newInhouseTransferLine.getTransferOrderQty() <= 0L || newInhouseTransferLine.getTransferConfirmedQty() <= 0L) {
            throw new BadRequestException("Transfer Qty must be greater than zero");
        }

        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
        storageBinPutAway.setCompanyCodeId(newInhouseTransferLine.getCompanyCodeId());
        storageBinPutAway.setPlantId(newInhouseTransferLine.getPlantId());
        storageBinPutAway.setLanguageId(newInhouseTransferLine.getLanguageId());
        storageBinPutAway.setWarehouseId(newInhouseTransferLine.getWarehouseId());
        storageBinPutAway.setBin(newInhouseTransferLine.getTargetStorageBin());
        StorageBinV2 dbStorageBin = null;
        try {
            dbStorageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
        } catch (Exception e) {
            throw new BadRequestException("Invalid StorageBin");
        }

        //restrict bin to bin transfer from bin class Id 3
        //11-03-2024 - Ticket No. ALM/2024/004
        storageBinPutAway.setBin(newInhouseTransferLine.getSourceStorageBin());
        StorageBinV2 dbSourceStorageBin = null;
        try {
            dbSourceStorageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
        } catch (Exception e) {
            throw new BadRequestException("Invalid StorageBin");
        }
        if (dbSourceStorageBin != null && dbSourceStorageBin.getBinClassId() == 3L) {
            throw new BadRequestException("Source Bin must be a Live Location - Either BinClassId 1 or 7");
        }

        if (dbStorageBin != null && dbStorageBin.getBinClassId() == 3L) {
            throw new BadRequestException("Target Bin must be a Live Location - Either BinClassId 1 or 7");
        }

        InhouseTransferLine dbInhouseTransferLine = new InhouseTransferLine();
        BeanUtils.copyProperties(newInhouseTransferLine, dbInhouseTransferLine, CommonUtils.getNullPropertyNames(newInhouseTransferLine));
        dbInhouseTransferLine.setLanguageId(newInhouseTransferLine.getLanguageId());
        dbInhouseTransferLine.setCompanyCodeId(newInhouseTransferLine.getCompanyCodeId());
        dbInhouseTransferLine.setPlantId(newInhouseTransferLine.getPlantId());

        // WH_ID
        dbInhouseTransferLine.setWarehouseId(dbInhouseTransferHeader.getWarehouseId());

        // TR_NO
        dbInhouseTransferLine.setTransferNumber(TRANSFER_NO);
        dbInhouseTransferLine.setManufacturerName(newInhouseTransferLine.getManufacturerName());
        //===========================================Handle MfrName==============================================================//
        if (newInhouseTransferLine.getCompanyCodeId() != null && newInhouseTransferLine.getManufacturerName() == null) {
            dbInhouseTransferLine.setManufacturerName(getMfrName(newInhouseTransferLine.getCompanyCodeId()));
        }
        //=========================================================================================================//
        dbInhouseTransferLine.setTransferUom(newInhouseTransferLine.getTransferUom());

        // STATUS_ID - Hard Coded Value="30" at the time of Confirmation
        dbInhouseTransferLine.setStatusId(30L);
        dbInhouseTransferLine.setStatusDescription(statusDescription);
        dbInhouseTransferLine.setDeletionIndicator(0L);
        dbInhouseTransferLine.setCreatedBy(loginUserID);
        dbInhouseTransferLine.setCreatedOn(new Date());
        dbInhouseTransferLine.setUpdatedBy(loginUserID);
        dbInhouseTransferLine.setUpdatedOn(new Date());
        dbInhouseTransferLine.setConfirmedBy(loginUserID);
        dbInhouseTransferLine.setConfirmedOn(new Date());

        dbInhouseTransferLine.setCompanyDescription(description.getCompanyDesc());
        dbInhouseTransferLine.setPlantDescription(description.getPlantDesc());
        dbInhouseTransferLine.setWarehouseDescription(description.getWarehouseDesc());

        List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(dbInhouseTransferLine.getSourceItemCode(),
                                                                             dbInhouseTransferLine.getCompanyCodeId(),
                                                                             dbInhouseTransferLine.getPlantId(),
                                                                             dbInhouseTransferLine.getWarehouseId(),
                                                                             dbInhouseTransferLine.getManufacturerName(),
                                                                             dbInhouseTransferLine.getLanguageId());
        log.info("source item Barcode : " + barcode);
        if (barcode != null && !barcode.isEmpty()) {
            dbInhouseTransferLine.setSourceBarcodeId(barcode.get(0));
            dbInhouseTransferLine.setTargetBarcodeId(barcode.get(0));
        }

//        List<String> targetBarcode = stagingLineV2Repository.getPartnerItemBarcode(dbInhouseTransferLine.getTargetItemCode(),
//                dbInhouseTransferLine.getCompanyCodeId(),
//                dbInhouseTransferLine.getPlantId(),
//                dbInhouseTransferLine.getWarehouseId(),
//                dbInhouseTransferLine.getManufacturerName(),
//                dbInhouseTransferLine.getLanguageId());
//        log.info("target item Barcode : " + targetBarcode);
//        if (targetBarcode != null && !targetBarcode.isEmpty()) {
//            dbInhouseTransferLine.setTargetBarcodeId(targetBarcode.get(0));
//        }

        // Save InhouseTransferLine
        InhouseTransferLine createdInhouseTransferLine = inhouseTransferLineRepository.save(dbInhouseTransferLine);
        log.info("InhouseTransferLine created : " + createdInhouseTransferLine);

        if (createdInhouseTransferLine != null) {
            // Save InhouseTransferHeader
            log.info("InhouseTransferHeader before create-->: " + dbInhouseTransferHeader);
            InhouseTransferHeader createdInhouseTransferHeader = inhouseTransferHeaderRepository.save(dbInhouseTransferHeader);
            log.info("InhouseTransferHeader created: " + createdInhouseTransferHeader);

            /*--------------------INVENTORY TABLE UPDATES-----------------------------------------------*/
            updateIBInventoryV2(createdInhouseTransferHeader, createdInhouseTransferLine, loginUserID);

            /*--------------------INVENTORYMOVEMENT TABLE UPDATES-----------------------------------------------*/

            /*
             * If TR_TYP_ID = 03, insert 2 records in INVENTORYMOVEMENT table.
             * One record with ST_BIN = SRCE_ST_BIN and other record with ST_BIN = TGT_ST_BIN
             */
            if (transferTypeId == 3L) {
                // Row insertion for Source
                Long stockTypeId = createdInhouseTransferLine.getSourceStockTypeId();
                String itemCode = createdInhouseTransferLine.getSourceItemCode();
                String movementQtyValue = "N";
                String storageBin = createdInhouseTransferLine.getSourceStorageBin();
                inhouseTransferHeaderService.createInventoryMovementV2(createdInhouseTransferLine, transferTypeId, stockTypeId, itemCode, createdInhouseTransferLine.getManufacturerName(),
                                                                       storageBin, movementQtyValue, loginUserID);

                // Row insertion for Target
                movementQtyValue = "P";
                storageBin = createdInhouseTransferLine.getTargetStorageBin();
                inhouseTransferHeaderService.createInventoryMovementV2(createdInhouseTransferLine, transferTypeId, stockTypeId, itemCode, createdInhouseTransferLine.getManufacturerName(),
                                                                       storageBin, movementQtyValue, loginUserID);
            }

        }
//        }
    }

    /**
     * @param pickupLine
     * @param loginUserID
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void createOutboundInventoryTransfer(PickupLineV2 pickupLine, String loginUserID) throws InvocationTargetException, IllegalAccessException {

        if (pickupLine != null) {
            if (pickupLine.getAllocatedQty() == null || pickupLine.getPickConfirmQty() == null) {
                throw new BadRequestException("TransferQty is Missing!");
            }
        }

        InhouseTransferHeader dbInhouseTransferHeader = new InhouseTransferHeader();

        BeanUtils.copyProperties(pickupLine, dbInhouseTransferHeader, CommonUtils.getNullPropertyNames(pickupLine));
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();

        dbInhouseTransferHeader.setLanguageId(pickupLine.getLanguageId());
        dbInhouseTransferHeader.setCompanyCodeId(pickupLine.getCompanyCodeId());
        dbInhouseTransferHeader.setPlantId(pickupLine.getPlantId());
        dbInhouseTransferHeader.setWarehouseId(pickupLine.getWarehouseId());
        dbInhouseTransferHeader.setTransferTypeId(pickupLine.getOutboundOrderTypeId());
        dbInhouseTransferHeader.setTransferMethod(pickupLine.getReferenceDocumentType());
        // TR_NO
        String TRANSFER_NO = getTransferNoV2(pickupLine.getCompanyCodeId(),
                                             pickupLine.getPlantId(),
                                             pickupLine.getLanguageId(),
                                             pickupLine.getWarehouseId(),
                                             authTokenForIDMasterService.getAccess_token());
        dbInhouseTransferHeader.setTransferNumber(TRANSFER_NO);

        IKeyValuePair description = stagingLineV2Repository.getDescription(pickupLine.getCompanyCodeId(),
                                                                           pickupLine.getLanguageId(),
                                                                           pickupLine.getPlantId(),
                                                                           pickupLine.getWarehouseId());

        // STATUS_ID - Hard Coded Value="30" at the time of Confirmation
        dbInhouseTransferHeader.setStatusId(30L);
        String statusDescription = stagingLineV2Repository.getStatusDescription(30L, pickupLine.getLanguageId());
        dbInhouseTransferHeader.setStatusDescription(statusDescription);
        dbInhouseTransferHeader.setCompanyDescription(description.getCompanyDesc());
        dbInhouseTransferHeader.setPlantDescription(description.getPlantDesc());
        dbInhouseTransferHeader.setWarehouseDescription(description.getWarehouseDesc());
        dbInhouseTransferHeader.setDeletionIndicator(0L);
        dbInhouseTransferHeader.setCreatedBy(loginUserID);
        dbInhouseTransferHeader.setUpdatedBy(loginUserID);
        dbInhouseTransferHeader.setCreatedOn(new Date());
        dbInhouseTransferHeader.setUpdatedOn(new Date());

        // - TR_TYP_ID -
//        Long transferTypeId = dbInhouseTransferHeader.getTransferTypeId();
        Long transferTypeId = 3L;

        /*
         * LINES Table
         */

        InhouseTransferLine newInhouseTransferLine = createCopyOutboundInhouseTransferLineEntity(pickupLine);

        if (newInhouseTransferLine.getSourceStorageBin().equalsIgnoreCase(newInhouseTransferLine.getTargetStorageBin())) {
            throw new BadRequestException("Source Bin and Target Bin cannot be same");
        }
        if (newInhouseTransferLine.getTransferOrderQty() <= 0L || newInhouseTransferLine.getTransferConfirmedQty() <= 0L) {
            throw new BadRequestException("Transfer Qty must be greater than zero");
        }

        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
        storageBinPutAway.setCompanyCodeId(newInhouseTransferLine.getCompanyCodeId());
        storageBinPutAway.setPlantId(newInhouseTransferLine.getPlantId());
        storageBinPutAway.setLanguageId(newInhouseTransferLine.getLanguageId());
        storageBinPutAway.setWarehouseId(newInhouseTransferLine.getWarehouseId());
        storageBinPutAway.setBin(newInhouseTransferLine.getTargetStorageBin());
        StorageBinV2 dbStorageBin = null;
        try {
            dbStorageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
        } catch (Exception e) {
            throw new BadRequestException("Invalid StorageBin");
        }

        //restrict bin to bin transfer from bin class Id 3
        //11-03-2024 - Ticket No. ALM/2024/004
        storageBinPutAway.setBin(newInhouseTransferLine.getSourceStorageBin());
        StorageBinV2 dbSourceStorageBin = null;
        try {
            dbSourceStorageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
        } catch (Exception e) {
            throw new BadRequestException("Invalid StorageBin");
        }
        if (dbSourceStorageBin != null && dbSourceStorageBin.getBinClassId() == 3L) {
            throw new BadRequestException("Source Bin must be a Live Location - Either BinClassId 1 or 7");
        }

        if (dbStorageBin != null && dbStorageBin.getBinClassId() == 3L) {
            throw new BadRequestException("Target Bin must be a Live Location - Either BinClassId 1 or 7");
        }

        InhouseTransferLine dbInhouseTransferLine = new InhouseTransferLine();
        BeanUtils.copyProperties(newInhouseTransferLine, dbInhouseTransferLine, CommonUtils.getNullPropertyNames(newInhouseTransferLine));
        dbInhouseTransferLine.setLanguageId(newInhouseTransferLine.getLanguageId());
        dbInhouseTransferLine.setCompanyCodeId(newInhouseTransferLine.getCompanyCodeId());
        dbInhouseTransferLine.setPlantId(newInhouseTransferLine.getPlantId());

        // WH_ID
        dbInhouseTransferLine.setWarehouseId(dbInhouseTransferHeader.getWarehouseId());

        // TR_NO
        dbInhouseTransferLine.setTransferNumber(TRANSFER_NO);
        dbInhouseTransferLine.setManufacturerName(newInhouseTransferLine.getManufacturerName());
        //===========================================Handle MfrName==============================================================//
        if (newInhouseTransferLine.getCompanyCodeId() != null && newInhouseTransferLine.getManufacturerName() == null) {
            dbInhouseTransferLine.setManufacturerName(getMfrName(newInhouseTransferLine.getCompanyCodeId()));
        }
        //=========================================================================================================//
        dbInhouseTransferLine.setTransferUom(newInhouseTransferLine.getTransferUom());

        // STATUS_ID - Hard Coded Value="30" at the time of Confirmation
        dbInhouseTransferLine.setStatusId(30L);
        dbInhouseTransferLine.setStatusDescription(statusDescription);
        dbInhouseTransferLine.setDeletionIndicator(0L);
        dbInhouseTransferLine.setCreatedBy(loginUserID);
        dbInhouseTransferLine.setCreatedOn(new Date());
        dbInhouseTransferLine.setUpdatedBy(loginUserID);
        dbInhouseTransferLine.setUpdatedOn(new Date());
        dbInhouseTransferLine.setConfirmedBy(loginUserID);
        dbInhouseTransferLine.setConfirmedOn(new Date());

        dbInhouseTransferLine.setCompanyDescription(description.getCompanyDesc());
        dbInhouseTransferLine.setPlantDescription(description.getPlantDesc());
        dbInhouseTransferLine.setWarehouseDescription(description.getWarehouseDesc());

        List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(dbInhouseTransferLine.getSourceItemCode(),
                                                                             dbInhouseTransferLine.getCompanyCodeId(),
                                                                             dbInhouseTransferLine.getPlantId(),
                                                                             dbInhouseTransferLine.getWarehouseId(),
                                                                             dbInhouseTransferLine.getManufacturerName(),
                                                                             dbInhouseTransferLine.getLanguageId());
        log.info("source item Barcode : " + barcode);
        if (barcode != null && !barcode.isEmpty()) {
            dbInhouseTransferLine.setSourceBarcodeId(barcode.get(0));
        }

        List<String> targetBarcode = stagingLineV2Repository.getPartnerItemBarcode(dbInhouseTransferLine.getTargetItemCode(),
                                                                                   dbInhouseTransferLine.getCompanyCodeId(),
                                                                                   dbInhouseTransferLine.getPlantId(),
                                                                                   dbInhouseTransferLine.getWarehouseId(),
                                                                                   dbInhouseTransferLine.getManufacturerName(),
                                                                                   dbInhouseTransferLine.getLanguageId());
        log.info("target item Barcode : " + targetBarcode);
        if (targetBarcode != null && !targetBarcode.isEmpty()) {
            dbInhouseTransferLine.setTargetBarcodeId(targetBarcode.get(0));
        }

        // Save InhouseTransferLine
        InhouseTransferLine createdInhouseTransferLine = inhouseTransferLineRepository.save(dbInhouseTransferLine);
        log.info("InhouseTransferLine created : " + createdInhouseTransferLine);

        if (createdInhouseTransferLine != null) {
            // Save InhouseTransferHeader
            log.info("InhouseTransferHeader before create-->: " + dbInhouseTransferHeader);
            InhouseTransferHeader createdInhouseTransferHeader = inhouseTransferHeaderRepository.save(dbInhouseTransferHeader);
            log.info("InhouseTransferHeader created: " + createdInhouseTransferHeader);

            /*--------------------INVENTORY TABLE UPDATES-----------------------------------------------*/
            updateOBInventoryV2(createdInhouseTransferHeader, createdInhouseTransferLine, loginUserID);

            /*--------------------INVENTORYMOVEMENT TABLE UPDATES-----------------------------------------------*/

            /*
             * If TR_TYP_ID = 03, insert 2 records in INVENTORYMOVEMENT table.
             * One record with ST_BIN = SRCE_ST_BIN and other record with ST_BIN = TGT_ST_BIN
             */
            if (transferTypeId == 3L) {
                // Row insertion for Source
                Long stockTypeId = createdInhouseTransferLine.getSourceStockTypeId();
                String itemCode = createdInhouseTransferLine.getSourceItemCode();
                String movementQtyValue = "N";
                String storageBin = createdInhouseTransferLine.getSourceStorageBin();
                inhouseTransferHeaderService.createInventoryMovementV2(createdInhouseTransferLine, transferTypeId, stockTypeId, itemCode, createdInhouseTransferLine.getManufacturerName(),
                                                                       storageBin, movementQtyValue, loginUserID);

                // Row insertion for Target
                movementQtyValue = "P";
                storageBin = createdInhouseTransferLine.getTargetStorageBin();
                inhouseTransferHeaderService.createInventoryMovementV2(createdInhouseTransferLine, transferTypeId, stockTypeId, itemCode, createdInhouseTransferLine.getManufacturerName(),
                                                                       storageBin, movementQtyValue, loginUserID);
            }

        }
    }

    /**
     * @param createdInhouseTransferHeader
     * @param createdInhouseTransferLine
     * @param loginUserID
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void updateIBInventoryV2(InhouseTransferHeader createdInhouseTransferHeader,
                                     InhouseTransferLine createdInhouseTransferLine, String loginUserID) throws InvocationTargetException, IllegalAccessException {

        String warehouseId = createdInhouseTransferHeader.getWarehouseId();
        String itemCode = createdInhouseTransferLine.getSourceItemCode(); // sourceItemCode

        String companyCode = createdInhouseTransferHeader.getCompanyCodeId();
        String plantId = createdInhouseTransferHeader.getPlantId();
        String languageId = createdInhouseTransferHeader.getLanguageId();

        /*
         * 3 .TR_TYP_ID = 03, TR_MTD=IMF_SFG
         * Pass WH_ID/SRCE_ITM_CODE/PACK_BARCOE/SRCE_ST_BIN in INVENTORY TABLE and
         * update INV_QTY value (INV_QTY - TR_CNF_QTY) and delete the record if INV_QTY becomes Zero
         */

        InventoryV2 inventorySourceItemCode =
                inventoryService.imfInventoryTransferV2(companyCode, plantId, languageId, warehouseId,
                                                        createdInhouseTransferLine.getPackBarcodes(),
                                                        createdInhouseTransferLine.getSourceItemCode(),
                                                        createdInhouseTransferLine.getSourceStorageSectionId(),
                                                        createdInhouseTransferLine.getSourceStorageBin(),
                                                        createdInhouseTransferLine.getSourceBarcodeId());
        log.info("---------inventory----------> : " + inventorySourceItemCode);
        if (inventorySourceItemCode != null) {
            Double inventoryQty = inventorySourceItemCode.getInventoryQuantity();
            Double ALLOC_QTY = 0D;
            if (inventorySourceItemCode.getAllocatedQuantity() != null) {
                ALLOC_QTY = inventorySourceItemCode.getAllocatedQuantity();
            }
            Double transferConfirmedQty = createdInhouseTransferLine.getTransferConfirmedQty();
            double INV_QTY = inventoryQty - transferConfirmedQty;
            if (INV_QTY < 0) {
                throw new BadRequestException("Inventory became negative." + INV_QTY);
            }
            log.info("-----Source----INV_QTY-----------> : " + INV_QTY);
            log.info("-----Source----ALLOC_QTY-----------> : " + ALLOC_QTY);
            inventorySourceItemCode.setInventoryQuantity(round(INV_QTY));
            inventorySourceItemCode.setAllocatedQuantity(round(ALLOC_QTY));
            InventoryV2 newInventoryV2 = new InventoryV2();
            BeanUtils.copyProperties(inventorySourceItemCode, newInventoryV2, CommonUtils.getNullPropertyNames(inventorySourceItemCode));
            newInventoryV2.setUpdatedOn(new Date());
            newInventoryV2.setInventoryId(System.currentTimeMillis());
            Double totalQty = inventorySourceItemCode.getInventoryQuantity() + inventorySourceItemCode.getAllocatedQuantity();
            newInventoryV2.setReferenceField4(round(totalQty));
            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
            log.info("InventoryV2 created : " + createdInventoryV2);

            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            if (INV_QTY == 0 && (inventorySourceItemCode.getAllocatedQuantity() == null || inventorySourceItemCode.getAllocatedQuantity() == 0D)) {
                // Deleting record

                InventoryV2 deleteInventoryV2 = new InventoryV2();
                BeanUtils.copyProperties(inventorySourceItemCode, deleteInventoryV2, CommonUtils.getNullPropertyNames(inventorySourceItemCode));
                deleteInventoryV2.setUpdatedOn(new Date());
                deleteInventoryV2.setInventoryQuantity(0D);
                deleteInventoryV2.setAllocatedQuantity(0D);
                deleteInventoryV2.setReferenceField4(0D);
                deleteInventoryV2.setInventoryId(System.currentTimeMillis());
                inventoryV2Repository.save(deleteInventoryV2);
                log.info("---------inventory-----deleted-----");
                try {
                    StorageBinV2 dbStorageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId,
                                                                                  createdInhouseTransferLine.getWarehouseId(), createdInhouseTransferLine.getSourceStorageBin());

                    if (dbStorageBin != null) {
                        dbStorageBin.setStatusId(0L);
                        if (dbStorageBin.isCapacityCheck()) {
                            dbStorageBin.setRemainingVolume(dbStorageBin.getTotalVolume());
                            dbStorageBin.setOccupiedVolume("0");
                        }
                        dbStorageBin.setUpdatedBy(loginUserID);
                        dbStorageBin.setUpdatedOn(new Date());
                        storageBinService.updateStorageBinV2(dbStorageBin.getStorageBin(), dbStorageBin, companyCode,
                                                             plantId, languageId, warehouseId, loginUserID);
                        log.info("---------storage bin updated-------" + dbStorageBin);
                    }
                } catch (Exception e) {
                    log.error("---------storagebin-update-----" + e);
                }

            }
            // Pass WH_ID/ TGT_ITM_CODE/PACK_BARCODE/TGT_ST_BIN in INVENTORY TABLE validate for a record.
            InventoryV2 inventoryTargetItemCode =
                    inventoryService.imfInventoryTransferV2(companyCode, plantId, languageId, warehouseId,
                                                            createdInhouseTransferLine.getPackBarcodes(),
                                                            createdInhouseTransferLine.getTargetItemCode(),
                                                            createdInhouseTransferLine.getTargetStorageSectionId(),
                                                            createdInhouseTransferLine.getTargetStorageBin(),
                                                            createdInhouseTransferLine.getTargetBarcodeId());
            if (inventoryTargetItemCode != null) {
                // update INV_QTY value (INV_QTY + TR_CNF_QTY)
                inventoryQty = inventoryTargetItemCode.getInventoryQuantity();
                ALLOC_QTY = 0D;
                if (inventoryTargetItemCode.getAllocatedQuantity() != null) {
                    ALLOC_QTY = inventoryTargetItemCode.getAllocatedQuantity();
                }
                transferConfirmedQty = createdInhouseTransferLine.getTransferConfirmedQty();
                INV_QTY = inventoryQty + transferConfirmedQty;
                log.info("-----Target----INV_QTY-----------> : " + INV_QTY);
                log.info("-----Target----ALLOC_QTY-----------> : " + ALLOC_QTY);

                inventoryTargetItemCode.setInventoryQuantity(round(INV_QTY));
                inventoryTargetItemCode.setAllocatedQuantity(round(ALLOC_QTY));
                inventoryTargetItemCode.setBarcodeId(inventorySourceItemCode.getBarcodeId());
                inventoryTargetItemCode.setStorageSectionId(createdInhouseTransferLine.getTargetStorageSectionId());
                InventoryV2 newInventoryV2_1 = new InventoryV2();
                BeanUtils.copyProperties(inventoryTargetItemCode, newInventoryV2_1, CommonUtils.getNullPropertyNames(inventoryTargetItemCode));
                newInventoryV2_1.setReferenceField4(round(inventoryTargetItemCode.getInventoryQuantity() + inventoryTargetItemCode.getAllocatedQuantity()));
                newInventoryV2_1.setUpdatedOn(new Date());
                newInventoryV2_1.setInventoryId(System.currentTimeMillis());
                createdInventoryV2 = inventoryV2Repository.save(newInventoryV2_1);
                log.info("InventoryV2 created : " + createdInventoryV2);
            } else {
                /*
                 * Fetch from INHOUSETRANSFERLINE table and insert in INVENTORY table as
                 * WH_ID/ TGT_ITM_CODE/PAL_CODE/PACK_BARCODE/TGT_ST_BIN/CASE_CODE/STCK_TYP_ID/SP_ST_IND_ID/INV_QTY=TR_CNF_QTY/
                 * INV_UOM as QTY_UOM/BIN_CL_ID of ST_BIN from STORAGEBIN table
                 */

                // "LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PACK_BARCODE", "ITM_CODE", "ST_BIN", "SP_ST_IND_ID"
                InventoryV2 newInventory = new InventoryV2();
                BeanUtils.copyProperties(createdInhouseTransferLine, newInventory, CommonUtils.getNullPropertyNames(createdInhouseTransferLine));

                newInventory.setItemCode(createdInhouseTransferLine.getTargetItemCode());
                newInventory.setPalletCode(createdInhouseTransferLine.getPalletCode());
                newInventory.setPackBarcodes(createdInhouseTransferLine.getPackBarcodes());
                newInventory.setStorageBin(createdInhouseTransferLine.getTargetStorageBin());
                newInventory.setCaseCode(createdInhouseTransferLine.getCaseCode());
                newInventory.setStockTypeId(createdInhouseTransferLine.getTargetStockTypeId());

                String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, createdInhouseTransferLine.getSourceStockTypeId());
                newInventory.setStockTypeDescription(stockTypeDesc);

                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);

                newInventory.setCompanyDescription(description.getCompanyDesc());
                newInventory.setPlantDescription(description.getPlantDesc());
                newInventory.setWarehouseDescription(description.getWarehouseDesc());

                if (createdInhouseTransferLine.getSpecialStockIndicatorId() == null) {
                    newInventory.setSpecialStockIndicatorId(1L);
                } else {
                    newInventory.setSpecialStockIndicatorId(createdInhouseTransferLine.getSpecialStockIndicatorId());
                }

                if (inventorySourceItemCode.getBarcodeId() != null) {
                    newInventory.setBarcodeId(inventorySourceItemCode.getBarcodeId());
                }
                List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(itemCode, companyCode, plantId, warehouseId,
                                                                                     createdInhouseTransferLine.getManufacturerName(), languageId);
                log.info("Barcode : " + barcode);
                if (inventorySourceItemCode.getBarcodeId() == null) {
                    if (barcode != null && !barcode.isEmpty()) {
                        newInventory.setBarcodeId(barcode.get(0));
                    }
                }

                newInventory.setInventoryQuantity(round(createdInhouseTransferLine.getTransferConfirmedQty()));
                newInventory.setAllocatedQuantity(0D);
                newInventory.setReferenceField4(round(newInventory.getInventoryQuantity() + newInventory.getAllocatedQuantity()));
                log.info("INV_QTY--->ALLOC_QTY--->TOT_QTY----> : " + newInventory.getInventoryQuantity() + ", " + newInventory.getAllocatedQuantity() + ", " + newInventory.getReferenceField4());
                newInventory.setInventoryUom(createdInhouseTransferLine.getTransferUom());

                StorageBinV2 storageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, createdInhouseTransferLine.getWarehouseId(), createdInhouseTransferLine.getTargetStorageBin());

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(companyCode);
                imBasicData.setPlantId(plantId);
                imBasicData.setLanguageId(languageId);
                imBasicData.setWarehouseId(warehouseId);
                imBasicData.setItemCode(itemCode);
//                imBasicData.setManufacturerName(createdInhouseTransferLine.getManufacturerName());
                ImBasicData1 imbasicdata1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImBasicData1 : " + imbasicdata1);

                if (imbasicdata1 != null) {
                    newInventory.setReferenceField8(imbasicdata1.getDescription());
                    newInventory.setReferenceField9(imbasicdata1.getManufacturerPartNo());
                    newInventory.setManufacturerCode(imbasicdata1.getManufacturerPartNo());
                    newInventory.setManufacturerName(imbasicdata1.getManufacturerPartNo());
                    newInventory.setDescription(imbasicdata1.getDescription());
                }
                if (storageBin != null) {
                    newInventory.setBinClassId(storageBin.getBinClassId());
                    newInventory.setReferenceField10(storageBin.getStorageSectionId());
                    newInventory.setReferenceField5(storageBin.getAisleNumber());
                    newInventory.setReferenceField6(storageBin.getShelfId());
                    newInventory.setReferenceField7(storageBin.getRowId());
                    newInventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                }
                if (newInventory.getManufacturerCode() == null || newInventory.getManufacturerName() == null) {
                    if (createdInhouseTransferLine.getManufacturerName() != null && !createdInhouseTransferLine.getManufacturerName().equalsIgnoreCase(COMPANY_CODE)) {
                        newInventory.setManufacturerName(createdInhouseTransferLine.getManufacturerName());
                        newInventory.setManufacturerCode(createdInhouseTransferLine.getManufacturerName());
                    } else {
                        newInventory.setManufacturerCode(getMfrName(COMPANY_CODE));
                        newInventory.setManufacturerName(getMfrName(COMPANY_CODE));
                    }
                }
                newInventory.setStorageSectionId(createdInhouseTransferLine.getTargetStorageSectionId());
                newInventory.setCreatedOn(new Date());
                newInventory.setUpdatedOn(new Date());
//                newInventory.setInventoryId(System.currentTimeMillis());
                InventoryV2 createdInventory = inventoryService.createInventoryV2(newInventory, loginUserID);
                log.info("createdInventory------> : " + createdInventory);
            }
        }

    }

    /**
     * @param createdInhouseTransferHeader
     * @param createdInhouseTransferLine
     * @param loginUserID
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void updateOBInventoryV2(InhouseTransferHeader createdInhouseTransferHeader,
                                     InhouseTransferLine createdInhouseTransferLine, String loginUserID) throws InvocationTargetException, IllegalAccessException {

        String warehouseId = createdInhouseTransferHeader.getWarehouseId();
        String itemCode = createdInhouseTransferLine.getSourceItemCode(); // sourceItemCode

        String companyCode = createdInhouseTransferHeader.getCompanyCodeId();
        String plantId = createdInhouseTransferHeader.getPlantId();
        String languageId = createdInhouseTransferHeader.getLanguageId();

        /*
         * 3 .TR_TYP_ID = 03, TR_MTD=IMF_SFG
         * Pass WH_ID/SRCE_ITM_CODE/PACK_BARCOE/SRCE_ST_BIN in INVENTORY TABLE and
         * update INV_QTY value (INV_QTY - TR_CNF_QTY) and delete the record if INV_QTY becomes Zero
         */

        InventoryV2 inventorySourceItemCode =
                inventoryService.imfInventoryTransferV2(companyCode, plantId, languageId, warehouseId,
                                                        createdInhouseTransferLine.getPackBarcodes(),
                                                        createdInhouseTransferLine.getSourceItemCode(),
                                                        createdInhouseTransferLine.getSourceStorageSectionId(),
                                                        createdInhouseTransferLine.getSourceStorageBin(),
                                                        createdInhouseTransferLine.getSourceBarcodeId());
        log.info("---------inventory----------> : " + inventorySourceItemCode);
        if (inventorySourceItemCode != null) {
//            Double inventoryQty = inventorySourceItemCode.getInventoryQuantity();
//            Double ALLOC_QTY = 0D;
//            if (inventorySourceItemCode.getAllocatedQuantity() != null) {
//                ALLOC_QTY = inventorySourceItemCode.getAllocatedQuantity();
//            }
//            Double transferConfirmedQty = createdInhouseTransferLine.getTransferConfirmedQty();
//            double INV_QTY = inventoryQty - transferConfirmedQty;
//            if (INV_QTY < 0) {
//                throw new BadRequestException("Inventory became negative." + INV_QTY);
//            }
//            log.info("-----Source----INV_QTY-----------> : " + INV_QTY);
//            log.info("-----Source----ALLOC_QTY-----------> : " + ALLOC_QTY);
//            inventorySourceItemCode.setInventoryQuantity(round(INV_QTY));
//            inventorySourceItemCode.setAllocatedQuantity(round(ALLOC_QTY));
//            InventoryV2 newInventoryV2 = new InventoryV2();
//            BeanUtils.copyProperties(inventorySourceItemCode, newInventoryV2, CommonUtils.getNullPropertyNames(inventorySourceItemCode));
//            newInventoryV2.setUpdatedOn(new Date());
//            newInventoryV2.setInventoryId(System.currentTimeMillis());
//            Double totalQty = inventorySourceItemCode.getInventoryQuantity() + inventorySourceItemCode.getAllocatedQuantity();
//            newInventoryV2.setReferenceField4(totalQty);
//            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
//            log.info("InventoryV2 created : " + createdInventoryV2);
//
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//            if (INV_QTY == 0 && (inventorySourceItemCode.getAllocatedQuantity() == null || inventorySourceItemCode.getAllocatedQuantity() == 0D)) {
//                // Deleting record
//
//                InventoryV2 deleteInventoryV2 = new InventoryV2();
//                BeanUtils.copyProperties(inventorySourceItemCode, deleteInventoryV2, CommonUtils.getNullPropertyNames(inventorySourceItemCode));
//                deleteInventoryV2.setUpdatedOn(new Date());
//                deleteInventoryV2.setInventoryQuantity(0D);
//                deleteInventoryV2.setAllocatedQuantity(0D);
//                deleteInventoryV2.setReferenceField4(0D);
//                deleteInventoryV2.setInventoryId(System.currentTimeMillis());
//                inventoryV2Repository.save(deleteInventoryV2);
//                log.info("---------inventory-----deleted-----");
//                try {
//                    StorageBinV2 dbStorageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId,
//                            createdInhouseTransferLine.getWarehouseId(), createdInhouseTransferLine.getSourceStorageBin());
//
//                    if (dbStorageBin != null) {
//                        dbStorageBin.setStatusId(0L);
//                        if (dbStorageBin.isCapacityCheck()) {
//                            dbStorageBin.setRemainingVolume(dbStorageBin.getTotalVolume());
//                            dbStorageBin.setOccupiedVolume("0");
//                        }
//                        dbStorageBin.setUpdatedBy(loginUserID);
//                        dbStorageBin.setUpdatedOn(new Date());
//                        storageBinService.updateStorageBinV2(dbStorageBin.getStorageBin(), dbStorageBin, companyCode,
//                                plantId, languageId, warehouseId, loginUserID);
//                        log.info("---------storage bin updated-------" + dbStorageBin);
//                    }
//                } catch (Exception e) {
//                    log.error("---------storagebin-update-----" + e);
//                }
//
//            }

            if (createdInhouseTransferLine.getTransferOrderQty() > 0D) {
                try {
                    Double INV_QTY = (inventorySourceItemCode.getInventoryQuantity() + createdInhouseTransferLine.getTransferOrderQty()) - createdInhouseTransferLine.getTransferConfirmedQty();
                    Double ALLOC_QTY = inventorySourceItemCode.getAllocatedQuantity() - createdInhouseTransferLine.getTransferOrderQty();

                    /*
                     * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                     */
                    // Start
                    if (INV_QTY < 0D) {
                        INV_QTY = 0D;
                    }

                    if (ALLOC_QTY < 0D) {
                        ALLOC_QTY = 0D;
                    }
                    // End
                    Double TOT_QTY = INV_QTY + ALLOC_QTY;
                    inventorySourceItemCode.setInventoryQuantity(round(INV_QTY));
                    inventorySourceItemCode.setAllocatedQuantity(round(ALLOC_QTY));
                    inventorySourceItemCode.setReferenceField4(round(TOT_QTY));

                    if (inventorySourceItemCode.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                        if (itemType != null) {
                            inventorySourceItemCode.setItemType(itemType.getItemType());
                            inventorySourceItemCode.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }
                    // INV_QTY > 0 then, update Inventory Table
//                        inventory = inventoryV2Repository.save(inventory);
//                        log.info("inventory updated : " + inventory);
                    InventoryV2 inventoryV2 = new InventoryV2();
                    BeanUtils.copyProperties(inventorySourceItemCode, inventoryV2, CommonUtils.getNullPropertyNames(inventorySourceItemCode));
                    inventoryV2.setUpdatedOn(new Date());
                    inventoryV2.setInventoryId(System.currentTimeMillis());
                    inventoryV2 = inventoryV2Repository.save(inventoryV2);
                    log.info("-----Inventory2 updated-------: " + inventoryV2);

                    if (INV_QTY == 0) {
                        // Setting up statusId = 0
                        try {
                            // Check whether Inventory has record or not
                            InventoryV2 inventoryByStBin = inventoryService.getInventoryByStorageBinV2(companyCode, plantId, languageId, warehouseId, inventorySourceItemCode.getStorageBin());
                            if (inventoryByStBin == null || (inventoryByStBin != null && inventoryByStBin.getReferenceField4() == 0)) {
                                StorageBinV2 dbStorageBin = storageBinService.getStorageBinV2(
                                        companyCode, plantId, languageId, warehouseId, inventorySourceItemCode.getStorageBin());

                                if (dbStorageBin != null) {

                                    dbStorageBin.setStatusId(0L);
                                    log.info("Bin Emptied");

                                    storageBinService.updateStorageBinV2(inventorySourceItemCode.getStorageBin(), dbStorageBin, companyCode,
                                                                         plantId, languageId, warehouseId, loginUserID);
                                    log.info("Bin Update Success");
                                }
                            }
                        } catch (Exception e) {
                            log.error("updateStorageBin Error :" + e.toString());
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    log.error("Inventory Update :" + e.toString());
                    e.printStackTrace();
                }
            }

            if (createdInhouseTransferLine.getTransferOrderQty() == null || createdInhouseTransferLine.getTransferOrderQty() == 0D) {
                Double INV_QTY;
                try {
                    INV_QTY = inventorySourceItemCode.getInventoryQuantity() - createdInhouseTransferLine.getTransferConfirmedQty();
                    /*
                     * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                     */
                    // Start
                    if (INV_QTY < 0D) {
                        INV_QTY = 0D;
                    }
                    // End
                    inventorySourceItemCode.setInventoryQuantity(round(INV_QTY));
                    inventorySourceItemCode.setReferenceField4(round(INV_QTY));

                    if (inventorySourceItemCode.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                        if (itemType != null) {
                            inventorySourceItemCode.setItemType(itemType.getItemType());
                            inventorySourceItemCode.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }
//                        inventory = inventoryV2Repository.save(inventory);
//                        log.info("inventory updated : " + inventory);
                    InventoryV2 newInventoryV2 = new InventoryV2();
                    BeanUtils.copyProperties(inventorySourceItemCode, newInventoryV2, CommonUtils.getNullPropertyNames(inventorySourceItemCode));
                    newInventoryV2.setUpdatedOn(new Date());
                    newInventoryV2.setInventoryId(System.currentTimeMillis());
                    InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                    log.info("InventoryV2 created : " + createdInventoryV2);

                    //-------------------------------------------------------------------
                    // PASS PickedConfirmedStBin, WH_ID to inventory
                    // 	If inv_qty && alloc_qty is zero or null then do the below logic.
                    //-------------------------------------------------------------------
                    InventoryV2 inventoryBySTBIN = inventoryService.getInventoryByStorageBinV2(companyCode, plantId, languageId, warehouseId, createdInhouseTransferLine.getSourceStorageBin());
                    if (inventoryBySTBIN != null && (inventoryBySTBIN.getAllocatedQuantity() == null || inventoryBySTBIN.getAllocatedQuantity() == 0D)
                            && (inventoryBySTBIN.getInventoryQuantity() == null || inventoryBySTBIN.getInventoryQuantity() == 0D)) {
                        try {
                            // Setting up statusId = 0
                            StorageBinV2 dbStorageBin = storageBinService.getStorageBinV2(
                                    companyCode,
                                    plantId,
                                    languageId,
                                    warehouseId,
                                    inventorySourceItemCode.getStorageBin());
                            dbStorageBin.setStatusId(0L);

                            storageBinService.updateStorageBinV2(inventorySourceItemCode.getStorageBin(), dbStorageBin, companyCode, plantId, languageId, warehouseId, loginUserID);
                        } catch (Exception e) {
                            log.error("updateStorageBin Error :" + e.toString());
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e1) {
                    log.error("Inventory cum StorageBin update: Error :" + e1.toString());
                    e1.printStackTrace();
                }
            }

            // Pass WH_ID/ TGT_ITM_CODE/PACK_BARCODE/TGT_ST_BIN in INVENTORY TABLE validate for a record.
            InventoryV2 inventoryTargetItemCode =
                    inventoryService.imfInventoryTransferV2(companyCode, plantId, languageId, warehouseId,
                                                            createdInhouseTransferLine.getPackBarcodes(),
                                                            createdInhouseTransferLine.getTargetItemCode(),
                                                            createdInhouseTransferLine.getTargetStorageSectionId(),
                                                            createdInhouseTransferLine.getTargetStorageBin(),
                                                            createdInhouseTransferLine.getTargetBarcodeId());
            if (inventoryTargetItemCode != null) {
                // update INV_QTY value (INV_QTY + TR_CNF_QTY)
                Double inventoryQty = inventoryTargetItemCode.getInventoryQuantity();
                Double ALLOC_QTY = 0D;
                if (inventoryTargetItemCode.getAllocatedQuantity() != null) {
                    ALLOC_QTY = inventoryTargetItemCode.getAllocatedQuantity();
                }
                Double transferConfirmedQty = createdInhouseTransferLine.getTransferConfirmedQty();
                Double INV_QTY = inventoryQty + transferConfirmedQty;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                log.info("-----Target----INV_QTY-----------> : " + INV_QTY);
                log.info("-----Target----ALLOC_QTY-----------> : " + ALLOC_QTY);

                inventoryTargetItemCode.setInventoryQuantity(round(INV_QTY));
                inventoryTargetItemCode.setAllocatedQuantity(round(ALLOC_QTY));
                inventoryTargetItemCode.setReferenceField4(round(TOT_QTY));
                inventoryTargetItemCode.setBarcodeId(inventorySourceItemCode.getBarcodeId());
                inventoryTargetItemCode.setStorageSectionId(createdInhouseTransferLine.getTargetStorageSectionId());
                if (inventoryTargetItemCode.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                    if (itemType != null) {
                        inventoryTargetItemCode.setItemType(itemType.getItemType());
                        inventoryTargetItemCode.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }
                InventoryV2 newInventoryV2_1 = new InventoryV2();
                BeanUtils.copyProperties(inventoryTargetItemCode, newInventoryV2_1, CommonUtils.getNullPropertyNames(inventoryTargetItemCode));
//                newInventoryV2_1.setReferenceField4(round(inventoryTargetItemCode.getInventoryQuantity() + inventoryTargetItemCode.getAllocatedQuantity()));
                newInventoryV2_1.setUpdatedOn(new Date());
                newInventoryV2_1.setInventoryId(System.currentTimeMillis());
                InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2_1);
                log.info("InventoryV2 created : " + createdInventoryV2);
            } else {
                /*
                 * Fetch from INHOUSETRANSFERLINE table and insert in INVENTORY table as
                 * WH_ID/ TGT_ITM_CODE/PAL_CODE/PACK_BARCODE/TGT_ST_BIN/CASE_CODE/STCK_TYP_ID/SP_ST_IND_ID/INV_QTY=TR_CNF_QTY/
                 * INV_UOM as QTY_UOM/BIN_CL_ID of ST_BIN from STORAGEBIN table
                 */

                // "LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PACK_BARCODE", "ITM_CODE", "ST_BIN", "SP_ST_IND_ID"
                InventoryV2 newInventory = new InventoryV2();
                BeanUtils.copyProperties(createdInhouseTransferLine, newInventory, CommonUtils.getNullPropertyNames(createdInhouseTransferLine));

                newInventory.setItemCode(createdInhouseTransferLine.getTargetItemCode());
                newInventory.setPalletCode(createdInhouseTransferLine.getPalletCode());
                newInventory.setPackBarcodes(createdInhouseTransferLine.getPackBarcodes());
                newInventory.setStorageBin(createdInhouseTransferLine.getTargetStorageBin());
                newInventory.setCaseCode(createdInhouseTransferLine.getCaseCode());
                newInventory.setStockTypeId(createdInhouseTransferLine.getTargetStockTypeId());

                String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, createdInhouseTransferLine.getSourceStockTypeId());
                newInventory.setStockTypeDescription(stockTypeDesc);

                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
                                                                                   languageId,
                                                                                   plantId,
                                                                                   warehouseId);

                newInventory.setCompanyDescription(description.getCompanyDesc());
                newInventory.setPlantDescription(description.getPlantDesc());
                newInventory.setWarehouseDescription(description.getWarehouseDesc());

                if (createdInhouseTransferLine.getSpecialStockIndicatorId() == null) {
                    newInventory.setSpecialStockIndicatorId(1L);
                } else {
                    newInventory.setSpecialStockIndicatorId(createdInhouseTransferLine.getSpecialStockIndicatorId());
                }

                if (inventorySourceItemCode.getBarcodeId() != null) {
                    newInventory.setBarcodeId(inventorySourceItemCode.getBarcodeId());
                }
                List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(itemCode, companyCode, plantId, warehouseId,
                                                                                     createdInhouseTransferLine.getManufacturerName(), languageId);
                log.info("Barcode : " + barcode);
                if (inventorySourceItemCode.getBarcodeId() == null) {
                    if (barcode != null && !barcode.isEmpty()) {
                        newInventory.setBarcodeId(barcode.get(0));
                    }
                }

                newInventory.setInventoryQuantity(round(createdInhouseTransferLine.getTransferConfirmedQty()));
                newInventory.setAllocatedQuantity(0D);
                newInventory.setReferenceField4(round(newInventory.getInventoryQuantity() + newInventory.getAllocatedQuantity()));
                log.info("INV_QTY--->ALLOC_QTY--->TOT_QTY----> : " + newInventory.getInventoryQuantity() + ", " + newInventory.getAllocatedQuantity() + ", " + newInventory.getReferenceField4());
                newInventory.setInventoryUom(createdInhouseTransferLine.getTransferUom());

                StorageBinV2 storageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, createdInhouseTransferLine.getWarehouseId(), createdInhouseTransferLine.getTargetStorageBin());

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(companyCode);
                imBasicData.setPlantId(plantId);
                imBasicData.setLanguageId(languageId);
                imBasicData.setWarehouseId(warehouseId);
                imBasicData.setItemCode(itemCode);
//                imBasicData.setManufacturerName(createdInhouseTransferLine.getManufacturerName());
                ImBasicData1 imbasicdata1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImBasicData1 : " + imbasicdata1);

                if (imbasicdata1 != null) {
                    newInventory.setReferenceField8(imbasicdata1.getDescription());
                    newInventory.setReferenceField9(imbasicdata1.getManufacturerPartNo());
                    newInventory.setManufacturerCode(imbasicdata1.getManufacturerPartNo());
                    newInventory.setManufacturerName(imbasicdata1.getManufacturerPartNo());
                    newInventory.setDescription(imbasicdata1.getDescription());
                    newInventory.setItemType(imbasicdata1.getItemType());
                    newInventory.setItemTypeDescription(getItemTypeDesc(companyCode, plantId, languageId, warehouseId, imbasicdata1.getItemType()));
                }
                if (storageBin != null) {
                    newInventory.setBinClassId(storageBin.getBinClassId());
                    newInventory.setReferenceField10(storageBin.getStorageSectionId());
                    newInventory.setReferenceField5(storageBin.getAisleNumber());
                    newInventory.setReferenceField6(storageBin.getShelfId());
                    newInventory.setReferenceField7(storageBin.getRowId());
                    newInventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                }
                if (newInventory.getManufacturerCode() == null || newInventory.getManufacturerName() == null) {
                    if (createdInhouseTransferLine.getManufacturerName() != null && !createdInhouseTransferLine.getManufacturerName().equalsIgnoreCase(COMPANY_CODE)) {
                        newInventory.setManufacturerName(createdInhouseTransferLine.getManufacturerName());
                        newInventory.setManufacturerCode(createdInhouseTransferLine.getManufacturerName());
                    } else {
                        newInventory.setManufacturerCode(getMfrName(COMPANY_CODE));
                        newInventory.setManufacturerName(getMfrName(COMPANY_CODE));
                    }
                }
                newInventory.setStorageSectionId(createdInhouseTransferLine.getTargetStorageSectionId());
                newInventory.setCreatedOn(new Date());
                newInventory.setUpdatedOn(new Date());
//                newInventory.setInventoryId(System.currentTimeMillis());
                InventoryV2 createdInventory = inventoryService.createInventoryV2(newInventory, loginUserID);
                log.info("createdInventory------> : " + createdInventory);
            }
        }

    }

    /**
     * @param putAwayLineV2
     * @return
     */
    private InhouseTransferLine createCopyInboundInhouseTransferLineEntity(PutAwayLineV2 putAwayLineV2) {

        InhouseTransferLine inhouseTransferLine = new InhouseTransferLine();
        BeanUtils.copyProperties(putAwayLineV2, inhouseTransferLine, CommonUtils.getNullPropertyNames(putAwayLineV2));
        inhouseTransferLine.setSourceItemCode(putAwayLineV2.getItemCode());
        inhouseTransferLine.setSourceStockTypeId(putAwayLineV2.getStockTypeId());
        inhouseTransferLine.setTargetStorageBin(putAwayLineV2.getConfirmedStorageBin());
        inhouseTransferLine.setTargetItemCode(putAwayLineV2.getItemCode());
        inhouseTransferLine.setTargetBarcodeId(putAwayLineV2.getBarcodeId());
        inhouseTransferLine.setCompanyCodeId(putAwayLineV2.getCompanyCode());
        inhouseTransferLine.setSourceBarcodeId(putAwayLineV2.getBarcodeId());
        inhouseTransferLine.setSourceStorageSectionId(putAwayLineV2.getStorageSectionId());
        inhouseTransferLine.setPackBarcodes(putAwayLineV2.getPackBarcodes());
        if (putAwayLineV2.getStorageSectionId().equalsIgnoreCase(ST_SEC_ID_PFG)) {
            inhouseTransferLine.setTargetStorageSectionId(ST_SEC_ID_FG);
        }
        if (putAwayLineV2.getStorageSectionId().equalsIgnoreCase(ST_SEC_ID_PSFG)) {
            inhouseTransferLine.setTargetStorageSectionId(ST_SEC_ID_SFG);
        }
        inhouseTransferLine.setTransferConfirmedQty(putAwayLineV2.getPutawayConfirmedQty());
        inhouseTransferLine.setTransferOrderQty(putAwayLineV2.getOrderQty());
        inhouseTransferLine.setTransferUom(putAwayLineV2.getPutAwayUom());

        String sourceStorageBin = orderManagementLineService.getSourceStorageBin(putAwayLineV2.getCompanyCode(), putAwayLineV2.getPlantId(), putAwayLineV2.getLanguageId(),
                                                                                 putAwayLineV2.getWarehouseId(), putAwayLineV2.getRefDocNumber(), putAwayLineV2.getLineNo(), putAwayLineV2.getItemCode());

        if (sourceStorageBin != null) {
            inhouseTransferLine.setSourceStorageBin(sourceStorageBin);
        }
        if (sourceStorageBin == null) {
            throw new BadRequestException("Pick List Not yet generated for this Inbound Order");
        }
        return inhouseTransferLine;
    }

    /**
     * @param pickupLine
     * @return
     */
    private InhouseTransferLine createCopyOutboundInhouseTransferLineEntity(PickupLineV2 pickupLine) {

        InhouseTransferLine inhouseTransferLine = new InhouseTransferLine();
        BeanUtils.copyProperties(pickupLine, inhouseTransferLine, CommonUtils.getNullPropertyNames(pickupLine));
        inhouseTransferLine.setSourceItemCode(pickupLine.getItemCode());
        inhouseTransferLine.setSourceStockTypeId(pickupLine.getStockTypeId());
        inhouseTransferLine.setTargetStockTypeId(pickupLine.getStockTypeId());
        inhouseTransferLine.setSourceStorageBin(pickupLine.getPickedStorageBin());
        inhouseTransferLine.setTargetItemCode(pickupLine.getItemCode());
        inhouseTransferLine.setTargetBarcodeId(pickupLine.getBarcodeId());
        inhouseTransferLine.setCompanyCodeId(pickupLine.getCompanyCodeId());
        inhouseTransferLine.setSourceBarcodeId(pickupLine.getBarcodeId());
        inhouseTransferLine.setSourceStorageSectionId(pickupLine.getStorageSectionId());
        inhouseTransferLine.setPackBarcodes(pickupLine.getPickedPackCode());
        inhouseTransferLine.setManufacturerName(pickupLine.getManufacturerName());

        boolean pass = pickupLine.getStorageSectionId() != null &&
                (pickupLine.getStorageSectionId().equalsIgnoreCase(ST_SEC_ID_DS) ||
                        pickupLine.getStorageSectionId().equalsIgnoreCase(ST_SEC_ID_CS) ||
                        pickupLine.getStorageSectionId().equalsIgnoreCase(ST_SEC_ID_SFG));

        if (pass) {
            if (pickupLine.getOutboundOrderTypeId().equals(OB_IPL_ORD_TYP_ID_FG)) {
                inhouseTransferLine.setTargetStorageSectionId(ST_SEC_ID_PFG);
            }
            if (pickupLine.getOutboundOrderTypeId().equals(OB_IPL_ORD_TYP_ID_SFG)) {
                inhouseTransferLine.setTargetStorageSectionId(ST_SEC_ID_PSFG);
            }
        }
        inhouseTransferLine.setTransferConfirmedQty(pickupLine.getPickConfirmQty());
        inhouseTransferLine.setTransferOrderQty(pickupLine.getAllocatedQty());
        inhouseTransferLine.setTransferUom(pickupLine.getPickUom());

        InventoryV2 targetStorageBin = inventoryService.getInventoryForImfInhouseTransferV2(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                                                                                            pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), pickupLine.getItemCode(), pickupLine.getManufacturerName(),
                                                                                            1L, inhouseTransferLine.getTargetStorageSectionId(), inhouseTransferLine.getSourceBarcodeId());

        if (targetStorageBin != null) {
            inhouseTransferLine.setTargetStorageBin(targetStorageBin.getStorageBin());
        }
        if (targetStorageBin == null) {
            StorageBinV2 storageBin = storageBinService.getStorageBinV2(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(), pickupLine.getWarehouseId(), inhouseTransferLine.getTargetStorageSectionId(), 1L);
            if (storageBin == null) {
                throw new BadRequestException("Target Storage Bin - Bin section id : " + pickupLine.getStorageSectionId() + " was not found");
            }
            inhouseTransferLine.setTargetStorageBin(storageBin.getStorageBin());
        }
        return inhouseTransferLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param productionOrderNo
     * @param batchNumber
     * @param parentProductionOrderNo
     * @param orderType
     */
    public void updateOperationConsumptionInventory(String companyCodeId, String plantId, String languageId,
                                                    String warehouseId, String productionOrderNo, String batchNumber,
                                                    String parentProductionOrderNo, String orderType) {
        try {
            List<OperationConsumptionImpl> operationConsumptions = inventoryV2Repository.operationConsumptionInventoryUpdate(
                    companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, parentProductionOrderNo, orderType);
            String storageSectionId = orderType.equalsIgnoreCase("FG") ? ST_SEC_ID_PFG : ST_SEC_ID_PSFG;
            if (operationConsumptions != null && !operationConsumptions.isEmpty()) {
                for (OperationConsumptionImpl operationConsumption : operationConsumptions) {
                    OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineForIMFInventoryDeleteV2(companyCodeId, plantId, languageId, warehouseId, batchNumber, operationConsumption.getItemCode());
                    if (dbOrderManagementLine != null) {
                        InventoryV2 inventoryDelete =
                                inventoryService.imfInventoryTransferV2(companyCodeId, plantId, languageId, warehouseId,
                                                                        operationConsumption.getItemCode(), storageSectionId, dbOrderManagementLine.getBarcodeId());
                        if (inventoryDelete != null) {
                            Double inventoryQty = inventoryDelete.getInventoryQuantity();
                            Double ALLOC_QTY = 0D;
                            if (inventoryDelete.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventoryDelete.getAllocatedQuantity();
                            }
                            Double transferConfirmedQty = operationConsumption.getReceipeQuantity();
                            double INV_QTY = inventoryQty - transferConfirmedQty;
                            if (INV_QTY < 0) {
                                throw new BadRequestException("Inventory became negative." + INV_QTY);
                            }
                            log.info("-----Source----INV_QTY-----------> : " + INV_QTY);
                            log.info("-----Source----ALLOC_QTY-----------> : " + ALLOC_QTY);
                            inventoryDelete.setInventoryQuantity(round(INV_QTY));
                            inventoryDelete.setAllocatedQuantity(round(ALLOC_QTY));
                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventoryDelete, newInventoryV2, CommonUtils.getNullPropertyNames(inventoryDelete));
                            Double totalQty = inventoryDelete.getInventoryQuantity() + inventoryDelete.getAllocatedQuantity();
                            newInventoryV2.setReferenceField4(round(totalQty));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(System.currentTimeMillis());
                            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + createdInventoryV2);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}