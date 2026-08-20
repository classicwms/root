package com.tekclover.wms.api.inbound.transaction.service;


import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.v2.ContainerReceiptV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class InventoryAsyncProcessService extends BaseService{


    @Autowired
    GrLineService grLineService;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    InventoryTransRepository inventoryTransRepository;

    @Autowired
    StorageBinV2Repository storageBinV2Repository;

    @Autowired
    private ContainerReceiptRepository containerReceiptRepository;

    @Autowired
    StorageBinService storageBinService;

    @Autowired
    ImBasicData1Repository imbasicdata1Repository;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    boolean alreadyExecuted = true;


    /**
     *
     * @param putAwayLineV2List putAwayLine List
     * @param loginUserID userID
     */
//    @Async("asyncExecutor")
    @Async("asyncExecutor")
    public void createInventoryAsyncProcessV4(List<PutAwayLineV2> putAwayLineV2List, String loginUserID) {
        List<InventoryV2> inventoryV2List = new ArrayList<>();
        log.info("Inventory BinClassId 1 process started -----------------V4-----------> Value Size is {}", putAwayLineV2List.size());
        for(PutAwayLineV2 putAwayLineV2 : putAwayLineV2List) {
            inventoryV2List.add(createInventoryNonCBMV4(putAwayLineV2, loginUserID));
        }

        if(!inventoryV2List.isEmpty()) {
            log.info("Inventory BinClassId 1 process completed -----------------V4-----------> Value Size is {}", inventoryV2List.size());
            inventoryV2Repository.saveAll(inventoryV2List);
        }
    }

    /**
     * @param putAwayLine putAwayLine Input's
     * @return return
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    /**
     * @param putAwayLine putAwayLine Input's
     * @return return
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public synchronized InventoryV2 createInventoryNonCBMV4(PutAwayLineV2 putAwayLine, String loginUserId) {
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("NAMRATHA");

//        alreadyExecuted = false;
//        log.info("Create Inventory Initiated ---> alreadyExecuted ---> " + new Date() + ", " + alreadyExecuted);
        String palletCode = null;
        String caseCode = null;
        String companyCode = putAwayLine.getCompanyCode();
        String plantId = putAwayLine.getPlantId();
        String warehouseId = putAwayLine.getWarehouseId();
        String itemCode = putAwayLine.getItemCode();
        String manufacturerName = putAwayLine.getManufacturerName();
        String refDocNumber = putAwayLine.getRefDocNumber();
        String languageId = putAwayLine.getLanguageId();

        try {
            log.info("Inventory Started barcodeId ---------------------->>> {} ", putAwayLine.getBarcodeId());
            InventoryV2 existinginventory = grLineService.getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName,
                    putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom());
            if (existinginventory != null) {
                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
                double physicalQty = putAwayLine.getPutawayConfirmedQty();
                double INV_QTY = existinginventory.getInventoryQuantity() - physicalQty;
                if (INV_QTY < 0) {
                    INV_QTY = 0;
                }
                log.info("INV_QTY : " + INV_QTY);
                Double TOT_NO_OF_BAGS = (existinginventory.getNoBags() != null ? existinginventory.getNoBags() : 0D) - (putAwayLine.getNoBags() != null ? putAwayLine.getNoBags() : 0D);

                if (INV_QTY >= 0) {
                    InventoryV2 inventory2 = new InventoryV2();
                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
                    String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, existinginventory.getStockTypeId());
                    inventory2.setStockTypeDescription(stockTypeDesc);
                    inventory2.setInventoryQuantity(round(INV_QTY));
                    inventory2.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
                    inventory2.setNoBags(TOT_NO_OF_BAGS);
                    inventory2.setBagSize(putAwayLine.getBagSize());
                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY + ", " + TOT_NO_OF_BAGS);

                    palletCode = existinginventory.getPalletCode();
                    caseCode = existinginventory.getCaseCode();

                    inventory2.setBusinessPartnerCode(putAwayLine.getBusinessPartnerCode());
                    inventory2.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
                    inventory2.setPackBarcodes(PACK_BARCODE);
//                    if (putAwayLine.getBatchSerialNumber() != null) {
//                        inventory2.setPackBarcodes(putAwayLine.getBatchSerialNumber());
//                    } else {
//                        inventory2.setPackBarcodes(PACK_BARCODE);
//                    }
                    if (inventory2.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                        if (itemType != null) {
                            inventory2.setItemType(itemType.getItemType());
                            inventory2.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }
                    inventory2.setReferenceDocumentNo(refDocNumber);
                    inventory2.setReferenceOrderNo(refDocNumber);
                    inventory2.setManufacturerDate(putAwayLine.getManufacturerDate());
                    inventory2.setExpiryDate(putAwayLine.getExpiryDate());
                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
                    inventory2.setUpdatedOn(new Date());
                    inventory2.setUpdatedBy(loginUserId);
//                    if (!alreadyExecuted) {
                    InventoryV2 createdinventoryV2 = inventoryV2Repository.save(inventory2);
                    log.info("----existinginventory--createdInventoryV2--------> : " + createdinventoryV2);
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Existing Inventory---Error-----> : " + e.toString());
        }

        try {
            log.info("Create Inventory bin Class Id 1 Initiated: " + new Date());
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));

            inventory.setCompanyCodeId(companyCode);
            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);                // VAR_ID
            inventory.setVariantSubCode("1");            // VAR_SUB_ID
            inventory.setStorageMethod("1");            // STR_MTD
            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
            inventory.setReferenceField9(putAwayLine.getManufacturerName());
            inventory.setDescription(putAwayLine.getDescription());
            inventory.setReferenceField8(putAwayLine.getDescription());
            if (putAwayLine.getBatchSerialNumber() != null) {
                inventory.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());        // STR_NO
            } else {
                inventory.setBatchSerialNumber("1");        // STR_NO
                inventory.setPackBarcodes(PACK_BARCODE);
            }

            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(companyCode);
            storageBinPutAway.setPlantId(plantId);
            storageBinPutAway.setLanguageId(languageId);
            storageBinPutAway.setWarehouseId(warehouseId);
            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());

            StorageBinV2 storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
            log.info("storageBin: " + storageBin);

            if (storageBin != null) {
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setStorageSectionId(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                inventory.setBinClassId(storageBin.getBinClassId());
            }

            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
            inventory.setPlantDescription(putAwayLine.getPlantDescription());
            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());

            inventory.setPalletCode(palletCode);
            inventory.setCaseCode(caseCode);
            log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
            inventory.setStockTypeDescription(stockTypeDesc);
            log.info("StockTypeDescription: " + stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);
            InventoryV2 existingInventory = grLineService.getInventoryBinClassId1V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName,
                    putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(),
                    putAwayLine.getConfirmedStorageBin());

            // INV_QTY
            if (existingInventory != null) {
                Double ALLOC_QTY = existingInventory.getAllocatedQuantity() != null ? existingInventory.getAllocatedQuantity() : 0D;
                Double INV_QTY = existingInventory.getInventoryQuantity() != null ? existingInventory.getInventoryQuantity() : 0D;
                Double NO_OF_BAGS = existingInventory.getNoBags() != null ? existingInventory.getNoBags() : 0D;
                Double NEW_NO_OF_BAGS = putAwayLine.getNoBags() != null ? putAwayLine.getNoBags() : 0D;
                Double TOT_NO_OF_BAGS = NO_OF_BAGS + NEW_NO_OF_BAGS;
                log.info("Existing Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY, PA_CNF_QTY : "
                        + INV_QTY + ", " + ALLOC_QTY + ", " + existingInventory.getReferenceField4() + ", " + putAwayLine.getPutawayConfirmedQty() + ", " + NO_OF_BAGS);

//                double physicalQty = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                double physicalQty = putAwayLine.getPutawayConfirmedQty();

                INV_QTY = INV_QTY + physicalQty;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(TOT_NO_OF_BAGS);
                inventory.setBagSize(putAwayLine.getBagSize());
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                inventory.setAlternateUom(putAwayLine.getAlternateUom());
                log.info("New Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + TOT_NO_OF_BAGS);

                inventory.setUpdatedBy(loginUserId);
                inventory.setCreatedOn(existingInventory.getCreatedOn());
            }
            if (existingInventory == null) {
                Double ALLOC_QTY = 0D;
//                Double INV_QTY = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                Double INV_QTY = putAwayLine.getPutawayConfirmedQty();
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                Double TOT_NO_OF_BAGS = putAwayLine.getNoBags();
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(TOT_NO_OF_BAGS);
                inventory.setBagSize(putAwayLine.getBagSize());
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                inventory.setAlternateUom(putAwayLine.getAlternateUom());
                log.info("New Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + TOT_NO_OF_BAGS);

                inventory.setCreatedBy(loginUserId);
                inventory.setCreatedOn(new Date());
            }

            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, languageId, itemCode);
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }
            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
            inventory.setDeletionIndicator(0L);

            inventory.setUpdatedOn(new Date());
            inventory.setBatchDate(new Date());

            return inventory;
//            InventoryV2 createdinventory = null;
//            if (!alreadyExecuted) {
//                createdinventory = inventoryV2Repository.save(inventory);
//                alreadyExecuted = true;             //to ensure method executing only once
//                log.info("created inventory : executed" + createdinventory + " -----> " + alreadyExecuted);
//            }

//            return createdinventory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Creating Inventory");
        }
    }
//==============SPAREX========================
    /**
     * @param putAwayLineV2List putAwayLine List
     * @param loginUserID       userID
     */
    @Async("asyncExecutor")
    public void createInventoryAsyncNewV10(List<PutAwayLineV2> putAwayLineV2List, String loginUserID) {
        List<InventoryV2> inventoryV2List = new ArrayList<>();
        log.info("Inventory BinClassId 1 process started -----------------V10-----------> Value Size is {}", putAwayLineV2List.size());
        for (PutAwayLineV2 putAwayLineV2 : putAwayLineV2List) {
            inventoryV2List.add(createInventoryNonCBMNewV10(putAwayLineV2, loginUserID));
        }

        if (!inventoryV2List.isEmpty()) {
            log.info("Inventory BinClassId 1 process completed -----------------V10-----------> Value Size is {}", inventoryV2List.size());
            inventoryV2Repository.saveAll(inventoryV2List);
        }
    }

    //=============SPAREX================================
    /**
     * @param putAwayLine putAwayLine Input's
     * @return return
     */
    public synchronized InventoryV2 createInventoryNonCBMNewV10(PutAwayLineV2 putAwayLine, String loginUserId) {
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("SPAREX");

        String palletCode = null;
        String caseCode = null;
        String companyCode = putAwayLine.getCompanyCode();
        String plantId = putAwayLine.getPlantId();
        String warehouseId = putAwayLine.getWarehouseId();
        String itemCode = putAwayLine.getItemCode();
        String manufacturerName = putAwayLine.getManufacturerName();
        String refDocNumber = putAwayLine.getRefDocNumber();
        String languageId = putAwayLine.getLanguageId();

        try {
            log.info("Create V10 Inventory bin Class Id 1 Initiated: " + new Date());
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));

            inventory.setCompanyCodeId(companyCode);
            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);                // VAR_ID
            inventory.setVariantSubCode("1");            // VAR_SUB_ID
            inventory.setStorageMethod("1");            // STR_MTD
            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
            inventory.setReferenceField9(putAwayLine.getManufacturerName());
            inventory.setDescription(putAwayLine.getDescription());
            inventory.setReferenceField8(putAwayLine.getDescription());
            if (putAwayLine.getBatchSerialNumber() != null) {
                inventory.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());        // STR_NO
            } else {
                inventory.setBatchSerialNumber("1");        // STR_NO
                inventory.setPackBarcodes(PACK_BARCODE);
            }

            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(companyCode);
            storageBinPutAway.setPlantId(plantId);
            storageBinPutAway.setLanguageId(languageId);
            storageBinPutAway.setWarehouseId(warehouseId);
            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());
            storageBinPutAway.setStorageSectionId(putAwayLine.getStorageSectionId());

            StorageBinV2 storageBin = null;
            if(putAwayLine.getInboundOrderTypeId() == 2L){
                storageBin = storageBinService.getStorageBinByBinClassId1V10(warehouseId, putAwayLine.getConfirmedStorageBin(), 1L, companyCode, plantId, languageId);
                log.info("Bin --> " + storageBin);
            } else {
                storageBin = storageBinService.getStorageBinByBinClassIdV10(warehouseId, 10L, companyCode, plantId, languageId);
                log.info("storageBin: " + storageBin);
            }

            if (storageBin != null) {
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setStorageSectionId(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                inventory.setBinClassId(storageBin.getBinClassId());
            }

            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
            inventory.setPlantDescription(putAwayLine.getPlantDescription());
            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());

            inventory.setPalletCode(palletCode);
            inventory.setCaseCode(caseCode);
            log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
            inventory.setStockTypeDescription(stockTypeDesc);
            log.info("StockTypeDescription: " + stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);
            InventoryV2 existingInventory = grLineService.getInventoryBinClassId1V10(companyCode, plantId, languageId, warehouseId, itemCode,
                    putAwayLine.getBarcodeId(), putAwayLine.getConfirmedStorageBin(),putAwayLine.getInboundOrderTypeId());

            // INV_QTY
            if (existingInventory != null) {
                Double ALLOC_QTY = existingInventory.getAllocatedQuantity() != null ? existingInventory.getAllocatedQuantity() : 0D;
                Double INV_QTY = existingInventory.getInventoryQuantity() != null ? existingInventory.getInventoryQuantity() : 0D;
                log.info("Existing Inventory V10 ----> INV_QTY, ALLOC_QTY, TOT_QTY, PA_CNF_QTY : "
                        + INV_QTY + ", " + ALLOC_QTY + ", " + existingInventory.getReferenceField4() + ", " + putAwayLine.getPutawayConfirmedQty());

                double physicalQty = putAwayLine.getPutawayConfirmedQty();

                INV_QTY = INV_QTY + physicalQty;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                log.info("New Inventory V10 ----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY );

                inventory.setUpdatedBy(loginUserId);
                inventory.setCreatedOn(existingInventory.getCreatedOn());
                inventory.setReferenceField4(round(TOT_QTY));

                inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
                inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());

            }else {
                Double ALLOC_QTY = 0D;
                Double INV_QTY = putAwayLine.getPutawayConfirmedQty();
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                log.info("New Inventory V10 ----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY);

                inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
                inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());

                inventory.setCreatedBy(loginUserId);
                inventory.setCreatedOn(new Date());
            }

            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, languageId, itemCode);
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }

            inventory.setDeletionIndicator(0L);

            inventory.setUpdatedOn(new Date());
            inventory.setBatchDate(new Date());

            return inventory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Creating Inventory");
        }
    }

    //================================================BF===========================================

    /**
     * @param putAwayLineV2List putAwayLine List
     * @param loginUserID       userID
     */
    @Async("asyncExecutor")
    public void createInventoryAsyncProcessV9(List<PutAwayLineV2> putAwayLineV2List, String loginUserID) {

        log.info("PutAwayLine List ---------------------->{}, Before Inventory Creation --> ", putAwayLineV2List.size());
        List<InventoryV2> inventoryV2List = new ArrayList<>();
        for (PutAwayLineV2 putAwayLineV2 : putAwayLineV2List) {
            inventoryV2List.add(createInventoryNonCBMV9(putAwayLineV2, loginUserID));
        }

        if (!inventoryV2List.isEmpty()) {
            log.info("Inventory BinClassId --------->1 ---------V9---> Size is {} ", inventoryV2List.size());
            inventoryV2Repository.saveAll(inventoryV2List);
            log.info("Inventory Saved Values -----> " + inventoryV2List);
        }
    }

    //================================================BF===========================================

    /**
     * @param putAwayLine
     * @param loginUserId
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public InventoryV2 createInventoryNonCBMV9(PutAwayLineV2 putAwayLine, String loginUserId) {


        log.info("PutawayLine----->" + putAwayLine);
        String companyCode = putAwayLine.getCompanyCode();
        String plantId = putAwayLine.getPlantId();
        String languageId = putAwayLine.getLanguageId();
        String warehouseId = putAwayLine.getWarehouseId();
        String barCodeId = putAwayLine.getBarcodeId();
        String itemCode = putAwayLine.getItemCode();
        String manufacturerName = putAwayLine.getManufacturerName();
        String refDocNumber = putAwayLine.getRefDocNumber();
        String packBarcodeId = putAwayLine.getPackBarcodes();
        Double uomQty = null;
        alreadyExecuted = false;

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);

        try {
            if (putAwayLine.getInboundOrderTypeId().equals(1L) || putAwayLine.getInboundOrderTypeId().equals(4L)) {
                uomQty = stagingLineV2Repository.getUomQtyV9(companyCode, plantId, warehouseId, itemCode, "2", "3");
            }
            if (putAwayLine.getInboundOrderTypeId().equals(2L)) {
                uomQty = 60.D;
            }
            InventoryV2 createdinventory = null;
            log.info("BinClassID 1 create/Update Initiated---->alreadyExecuted---> " + new Date() + ", " + alreadyExecuted);
            InventoryV2 dbInventoryBinClassId3 = null;
            if (putAwayLine.getInboundOrderTypeId().equals(1L) || putAwayLine.getInboundOrderTypeId().equals(4L)) {
                dbInventoryBinClassId3 = inventoryService.getInventoryBinClassId3V9(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, barCodeId, 3L, putAwayLine.getPalletId());
                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
            }
            if (putAwayLine.getInboundOrderTypeId().equals(2L)) {
                dbInventoryBinClassId3 = inventoryService.getInventoryBinClassId3V9(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, barCodeId, 7L, putAwayLine.getPalletId());
                log.info("Create Inventory bin Class Id 7 Initiated: " + new Date());
            }
            log.info("dbInventoryBinClassId3---------->" + dbInventoryBinClassId3);
            if (dbInventoryBinClassId3 != null) {
                double physicalQty = putAwayLine.getPutawayConfirmedQty();
                double INV_QTY = dbInventoryBinClassId3.getInventoryQuantity() - physicalQty;
                log.info("INV_QTY-------------->" + INV_QTY);
                if (INV_QTY < 0) {
                    INV_QTY = 0;
                }

                if (INV_QTY >= 0) {
                    InventoryV2 inventory2 = new InventoryV2();
                    BeanUtils.copyProperties(dbInventoryBinClassId3, inventory2, CommonUtils.getNullPropertyNames(dbInventoryBinClassId3));
                    String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
                    inventory2.setStockTypeDescription(stockTypeDesc);
                    inventory2.setInventoryQuantity(round(INV_QTY));
                    inventory2.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
//                    inventory2.setReferenceField2(String.valueOf(putAwayLine.getQtyInCase()));
                    inventory2.setReferenceField2(String.valueOf(uomQty));
                    if (uomQty.equals(dbInventoryBinClassId3.getInventoryQuantity())) {
                        inventory2.setReferenceField7("1");
                    } else {
                        inventory2.setReferenceField7("0");
                    }
                    inventory2.setBusinessPartnerCode(putAwayLine.getBusinessPartnerCode());
                    inventory2.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
                    inventory2.setPackBarcodes(putAwayLine.getPackBarcodes());
                    if (inventory2.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                        if (itemType != null) {
                            inventory2.setItemType(itemType.getItemType());
                            inventory2.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }
                    inventory2.setCrateQty(putAwayLine.getOrderQty());
                    inventory2.setQtyInCreate(putAwayLine.getQtyInCreate());
                    inventory2.setQtyInCase(putAwayLine.getQtyInCase());
                    inventory2.setVehicleNo(putAwayLine.getVehicleNo());
                    inventory2.setReferenceDocumentNo(refDocNumber);
                    inventory2.setReferenceOrderNo(refDocNumber);
                    inventory2.setManufacturerDate(putAwayLine.getManufacturerDate());
                    inventory2.setColor(putAwayLine.getGoodsReceiptNo());
                    inventory2.setExpiryDate(putAwayLine.getExpiryDate());
                    inventory2.setCreatedOn(dbInventoryBinClassId3.getCreatedOn());
                    inventory2.setManufacturerCode(putAwayLine.getManufacturerCode());
                    inventory2.setManufacturerName(putAwayLine.getManufacturerName());
                    //getCustomerId
                    String customerId = imbasicdata1Repository.getCustomerIdV9(inventory2.getCompanyCodeId(), inventory2.getLanguageId(),
                            inventory2.getPlantId(), inventory2.getWarehouseId(), inventory2.getItemCode(), inventory2.getManufacturerName());
                    inventory2.setMaterialNo(customerId);
                    log.info("customerId---->" + customerId);
                    //BinDesc
                    String binDesc = inventoryV2Repository.getBinClassIdDecription(dbInventoryBinClassId3.getBinClassId(), dbInventoryBinClassId3.getCompanyCodeId(), dbInventoryBinClassId3.getPlantId(), putAwayLine.getLanguageId());
                    inventory2.setReferenceField10(binDesc);

                    inventory2.setUpdatedOn(new Date());
                    inventory2.setUpdatedBy(loginUserId);
                    InventoryV2 createdinventoryV2 = inventoryV2Repository.save(inventory2);
                    log.info("----existinginventory--createdInventoryV2--------> : " + createdinventoryV2);

                }
            }

            log.info("Inventory Bin Class ID 1 Creation Started -------------------------> ");
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);
            InventoryV2 dbInventoryBinClassId1 = inventoryService.getInventoryBinClassId1V9(companyCode, plantId, languageId,
                    warehouseId, itemCode, manufacturerName, barCodeId, putAwayLine.getPalletId());
            log.info("DBInventory1----->" + dbInventoryBinClassId1);
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));
            inventory.setCompanyCodeId(companyCode);

            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);
            inventory.setVariantSubCode("1");
            inventory.setStorageMethod("1");
            inventory.setBatchSerialNumber("1");
            inventory.setPackBarcodes(packBarcodeId);
            inventory.setDeletionIndicator(0L);
            inventory.setReferenceField8(putAwayLine.getDescription());
            inventory.setReferenceField9(putAwayLine.getManufacturerName());
//            //BinDesc
//            String binDesc = inventoryV2Repository.getBinClassIdDecription(dbInventoryBinClassId3.getBinClassId(), putAwayLine.getLanguageId());
//            inventory.setReferenceField10(binDesc);
            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
            inventory.setDescription(putAwayLine.getDescription());
            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
            inventory.setManufacturerCode(putAwayLine.getManufacturerCode());
            inventory.setManufacturerName(putAwayLine.getManufacturerName());

            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            StorageBinV2 storageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, warehouseId, putAwayLine.getConfirmedStorageBin());
            log.info("storageBin: {}", storageBin);

            if (storageBin != null) {
                inventory.setStorageBin(storageBin.getStorageBin());
                inventory.setBinClassId(storageBin.getBinClassId());
                //BinDesc
                String binDesc = inventoryV2Repository.getBinClassIdDecription(storageBin.getBinClassId(), storageBin.getCompanyCodeId(), storageBin.getPlantId(), putAwayLine.getLanguageId());
                inventory.setReferenceField10(binDesc);
//                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setStorageSectionId(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
//                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
            }

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
            inventory.setStockTypeDescription(stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);

            //getCustomerId
            String customerId = imbasicdata1Repository.getCustomerIdV9(inventory.getCompanyCodeId(), inventory.getLanguageId(),
                    inventory.getPlantId(), inventory.getWarehouseId(), inventory.getItemCode(), inventory.getManufacturerName());
            inventory.setMaterialNo(customerId);
            log.info("customerId----->" + customerId);

            if (dbInventoryBinClassId1 != null) {
                log.info("BinClassID Sum of values in BinClassId = 1");
                double INV_QTY = round1(dbInventoryBinClassId1.getInventoryQuantity() != null ? dbInventoryBinClassId1.getInventoryQuantity() : 0.0);
                double ALLOC_QTY = round1(dbInventoryBinClassId1.getAllocatedQuantity() != null ? dbInventoryBinClassId1.getAllocatedQuantity() : 0.0);
                INV_QTY = INV_QTY + (putAwayLine.getPutawayConfirmedQty() != null ? putAwayLine.getPutawayConfirmedQty() : 0.0);
//                INV_QTY = INV_QTY + (putAwayLine.getQtyInCase() != null ? putAwayLine.getQtyInCase() : 0.0);

                double CASE_QTY = (putAwayLine.getQtyInCase() != null ? putAwayLine.getQtyInCase() : 0.0) + (dbInventoryBinClassId1.getQtyInCase() != null ? dbInventoryBinClassId1.getQtyInCase() : 0.0);
                double CRATE_QTY = (putAwayLine.getQtyInCreate() != null ? putAwayLine.getQtyInCreate() : 0.0) +
                        (dbInventoryBinClassId1.getQtyInCreate() != null ? dbInventoryBinClassId1.getQtyInCreate() : 0.0);
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                inventory.setInventoryQuantity(INV_QTY);
                inventory.setAllocatedQuantity(ALLOC_QTY);
                inventory.setReferenceField4(TOT_QTY);
                inventory.setQtyInCreate(CRATE_QTY);
                inventory.setQtyInCase(CASE_QTY);

                inventory.setStorageBin(dbInventoryBinClassId1.getStorageBin());
//                inventory.setReferenceField2(String.valueOf(putAwayLine.getQtyInCase()));
                inventory.setReferenceField2(String.valueOf(uomQty));
                if (uomQty.equals(INV_QTY)) {
                    inventory.setReferenceField7("1");
                } else {
                    inventory.setReferenceField7("0");
                }
                inventory.setPalletCode(dbInventoryBinClassId1.getPalletCode());

                log.info("B1 - Updated Inventory - INV_QTY,ALLOC_QTY,TOT_QTY,CASE_QTY,CRATE_QTY,PIECE_QTY: {},{},{},{},{},{}", INV_QTY, ALLOC_QTY, TOT_QTY, CASE_QTY, CRATE_QTY);
            } else {
                log.info("BinClassID Sum of values in BinClassId ");
                Double INV_QTY = round1(putAwayLine.getPutawayConfirmedQty() != null ? putAwayLine.getPutawayConfirmedQty() : 0.0);
                Double ALLOC_QTY = 0.0;
//                INV_QTY = INV_QTY + PA_QTY;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                inventory.setInventoryQuantity(INV_QTY);
                inventory.setAllocatedQuantity(ALLOC_QTY);
                inventory.setReferenceField4(TOT_QTY);
                inventory.setQtyInCreate(putAwayLine.getQtyInCreate() != null ? putAwayLine.getQtyInCreate() : 0.0);
                inventory.setQtyInCase(putAwayLine.getQtyInCase() != null ? putAwayLine.getQtyInCase() : 0.0);
//                inventory.setReferenceField2(String.valueOf(putAwayLine.getQtyInCase()));
                inventory.setReferenceField2(String.valueOf(uomQty));
                if (uomQty.equals(INV_QTY)) {
                    inventory.setReferenceField7("1");
                } else {
                    inventory.setReferenceField7("0");
                }
                inventory.setPalletCode(putAwayLine.getPalletId());
                log.info("B1 - Updated Inventory - INV_QTY,ALLOC_QTY,TOT_QTY: {},{},{}", INV_QTY, ALLOC_QTY, TOT_QTY);
            }

            // INV_UOM
            inventory.setInventoryUom(putAwayLine.getPutAwayUom());
            inventory.setCreatedBy(loginUserId);
            inventory.setUpdatedBy(loginUserId);

            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }
            if (putAwayLine.getInboundOrderTypeId() != 11L) {
                if (inventory.getExpiryDate() != null) {

                    Date expiryDateAsDate = null;
                    // [SELF_LIFE || EXPIRY_DATE
                    try {
                        Long self_life = imbasicdata1Repository.getSelfLife(inventory.getItemCode(), inventory.getCompanyCodeId(), inventory.getPlantId(),
                                inventory.getLanguageId(), inventory.getWarehouseId(), inventory.getManufacturerName());

                        //For Remaining Days
                        Date currentDate = new Date();
                        Date expiryDate = inventory.getExpiryDate();

                        // Convert both dates to LocalDate
                        LocalDate localCurrentDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate localExpiryDate = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        // Calculate remaining days including today (+1)
                        long remainingDays = ChronoUnit.DAYS.between(localCurrentDate, localExpiryDate) + 1;
                        // Prevent negative remaining days (optional)
                        remainingDays = Math.max(remainingDays, 0);

                        inventory.setSelfLife(String.valueOf(self_life));
                        inventory.setRemainingDays(String.valueOf(remainingDays));
                        log.info("SelfLife, RemainingDays-----> " + self_life + remainingDays);
                        Long remainingPercentage = Math.round((double) remainingDays / self_life * 100);
                        log.info("Remainging SelfLife Percentage------>" + remainingPercentage);
                        inventory.setRemainingSelfLifePercentage(remainingPercentage);
                    } catch (Exception e) {
                        log.info("Remaining Self_life calculation error --- " + e.getMessage());
                    }
                }
            }

            //getContainerReceipt Date
            ContainerReceiptV2 containerReceiptV2 = containerReceiptRepository.getContainerReceipt(putAwayLine.getCompanyCode(), putAwayLine.getLanguageId(), putAwayLine.getPlantId(),
                    putAwayLine.getWarehouseId(), putAwayLine.getRefDocNumber());

            inventory.setVehicleReportingDate(containerReceiptV2.getCreatedOn());                 //containerReceiptCreatedDate
            inventory.setPalletId(containerReceiptV2.getCreatedBy());                             //containerReceiptCreatedBy
            inventory.setVehicleNo(putAwayLine.getGoodsReceiptNo());                              //GRNo
            inventory.setReferenceOrderNo(containerReceiptV2.getInvoiceNo());                     //ContainerReceiptInvoiceNo
            inventory.setVehicleUnloadingDate(new Date());
            inventory.setArticleNo(containerReceiptV2.getContainerReceiptNo());

            inventory.setItemGroup(containerReceiptV2.getReferenceField1());                    // itemGroup

            inventory.setReceivingVariance(putAwayLine.getReceivingVariance());
            inventory.setReferenceField6(putAwayLine.getReferenceField6());

            inventory.setMrp(putAwayLine.getMrp());                                //MRP
            inventory.setPriceSegment(putAwayLine.getAMSSupplierInvoiceNo());           //NetWeight
            inventory.setThreePLPartnerId(putAwayLine.getHsnCode());                    //GrossWeight
            inventory.setBrand(putAwayLine.getBrand());                              //TotalWeigh
            inventory.setOrigin(putAwayLine.getReferenceField4());                  //CustomerPallet
            inventory.setItemTypeDescription(containerReceiptV2.getReferenceField25());       //gate pass(manual)
            inventory.setManufacturerCode(putAwayLine.getManufacturerCode());
            inventory.setManufacturerName(putAwayLine.getManufacturerName());
//            inventory.setPalletCode(putAwayLine.getPalletId());
            inventory.setCreatedOn(new Date());
            inventory.setUpdatedOn(new Date());

            //StorageBin For Inventory
            if (inventory.getReferenceField7().equals("0")) {
                storageBinV2Repository.updateEmptyBinStatus(inventory.getStorageBin(), inventory.getCompanyCodeId(), inventory.getPlantId(), inventory.getWarehouseId(), 10L);
            } else {
                storageBinV2Repository.updateEmptyBinStatus(inventory.getStorageBin(), inventory.getCompanyCodeId(), inventory.getPlantId(), inventory.getWarehouseId(), 1L);
            }

            log.info("Inventory StorageBin---------> {}", inventory);

            if (!alreadyExecuted) {
                try {
                    createdinventory = inventory;
                    log.info("B1-created inventory : {}", createdinventory);
                } catch (Exception e1) {
                    log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e1.toString());
                    e1.printStackTrace();

                    // Inventory Error Handling
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }
            return createdinventory;
        } catch (Exception e) {
            // Exception Log
            e.printStackTrace();
            throw e;
        }
    }
}
