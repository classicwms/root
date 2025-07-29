package com.tekclover.wms.api.inbound.transaction.service;


import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    StorageBinService storageBinService;

    boolean alreadyExecuted = true;


    /**
     *
     * @param putAwayLineV2List putAwayLine List
     * @param loginUserID userID
     */
    @Async("asyncExecutor")
    public void createInventoryAsyncProcessV4(List<PutAwayLineV2> putAwayLineV2List, String loginUserID) {
        putAwayLineV2List.stream().forEach(putAwayLineV2 -> {
            createInventoryNonCBMV4(putAwayLineV2, loginUserID);
        });
    }

    /**
     * @param putAwayLine putAwayLine Input's
     * @return return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void createInventoryNonCBMV4(PutAwayLineV2 putAwayLine, String loginUserId) {
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("NAMRATHA");

        alreadyExecuted = false;
        log.info("Create Inventory Initiated ---> alreadyExecuted ---> " + new Date() + ", " + alreadyExecuted);
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
                    if (putAwayLine.getBatchSerialNumber() != null) {
                        inventory2.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                    } else {
                        inventory2.setPackBarcodes(PACK_BARCODE);
                    }
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
                    if (!alreadyExecuted) {
                        InventoryV2 createdinventoryV2 = inventoryV2Repository.save(inventory2);
                        log.info("----existinginventory--createdInventoryV2--------> : " + createdinventoryV2);
                    }
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

            InventoryV2 createdinventory = null;
            if (!alreadyExecuted) {
                createdinventory = inventoryV2Repository.save(inventory);
                alreadyExecuted = true;             //to ensure method executing only once
                log.info("created inventory : executed" + createdinventory + " -----> " + alreadyExecuted);
            }

//            return createdinventory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Creating Inventory");
        }
    }


}
