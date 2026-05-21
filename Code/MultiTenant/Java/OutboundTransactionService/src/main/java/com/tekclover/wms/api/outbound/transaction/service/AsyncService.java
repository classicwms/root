package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.InventoryV2;
import com.tekclover.wms.api.outbound.transaction.model.mnc.AddInhouseTransferHeader;
import com.tekclover.wms.api.outbound.transaction.model.mnc.InhouseTransferHeaderEntity;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.AssignPickerV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.outbound.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.outbound.transaction.repository.*;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AsyncService extends BaseService {

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    InhouseTransferHeaderService inHouseTransferHeaderService;

    @Autowired
    PickupLineService pickuplineService;

    @Autowired
    PickupHeaderService pickupheaderService;

    @Autowired
    OrderManagementLineService ordermangementlineService;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    InventoryTransRepository inventoryTransRepository;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

//    @Async("asyncTaskExecutor")
//    public void processPickupLineAsync(List<AddPickupLine> newPickupLine, String loginUserID) throws Exception {
//        if (newPickupLine == null || newPickupLine.isEmpty()) {
//            log.info("There are no PickupLines to be Processed");
//            return;
//        }
//
//        try {
//            log.info("AddPickupLine -----> {}", newPickupLine);
//            DataBaseContextHolder.setCurrentDb("MT");
//            String routingDb = dbConfigRepository.getDbName(String.valueOf(newPickupLine.get(0).getCompanyCodeId()), newPickupLine.get(0).getPlantId(), newPickupLine.get(0).getWarehouseId());
//            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb(routingDb);
//
//            List<PickupLineV2> createdPickupLine = null;
//            if (routingDb != null) {
//                switch (routingDb) {
//                    case "FAHAHEEL":
//                    case "AUTO_LAP":
//                        createdPickupLine = pickuplineService.createPickupLineNonCBMV2(newPickupLine, loginUserID);
//                        break;
//                    case "NAMRATHA":
//                        createdPickupLine = pickuplineService.createPickupLineNonCBMV4(newPickupLine, loginUserID);
//                        break;
//                    case "REEFERON":
//                        createdPickupLine = pickuplineService.createPickupLineV5(newPickupLine, loginUserID);
//                        break;
//                    case "KNOWELL":
//                        createdPickupLine = pickuplineService.createPickupLineNonCBMV7(newPickupLine, loginUserID);
//                        break;
//                }
//            }
//            log.info("CreatedPickupLine through Async ------> {}", createdPickupLine);
//
//        } finally {
//            DataBaseContextHolder.clear();
//        }
//    }
//    @Async("asyncTaskExecutor")
    public void processPickupLineAsync(List<AddPickupLine> newPickupLine, String loginUserID) throws Exception {

        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("NAMRATHA");

            try {
                for (AddPickupLine pickupLineV2 : newPickupLine) {
                    statusDescription = getStatusDescription(57L, "EN");
                    int pickupHeader = pickupHeaderV2Repository.updatePickupHeaderStatusUpdateV4(pickupLineV2.getCompanyCodeId(), pickupLineV2.getPlantId(), pickupLineV2.getLanguageId(),
                            pickupLineV2.getWarehouseId(), pickupLineV2.getRefDocNumber(), pickupLineV2.getPreOutboundNo(), pickupLineV2.getItemCode(),
                            pickupLineV2.getManufacturerName(), pickupLineV2.getPartnerCode(), pickupLineV2.getPickupNumber(), pickupLineV2.getLineNumber(), 57L,
                            statusDescription, loginUserID, new Date());
                    log.info("PickupHeader Status Updated to 57 for PickupLineV2 through Async ------> {}", pickupHeader);
                }
            } catch (Exception e) {
                log.error("Error while updating PickupHeader status to 57 for PickupLineV2 through Async ------> {}", e.toString());
                e.printStackTrace();
            }
            processPickupLine(newPickupLine, loginUserID);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /**
     *
     * @param newPickupLine pickupLine
     * @param loginUserID userID
     * @throws Exception
     */
    @Async("asyncTaskExecutor")
    public void processPickupLine(List<AddPickupLine> newPickupLine, String loginUserID) throws Exception {

        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("NAMRATHA");

            List<PickupLineV2> createdPickupLine = pickuplineService.createPickupLineNonCBMV4(newPickupLine, loginUserID);
            log.info("createdPickupLine through ASYNC -----> {}", createdPickupLine);
            getInventoryForMatchingBarcodeIdV4(createdPickupLine, loginUserID);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//    @Async("asyncTaskExecutor")
    public void getInventoryForMatchingBarcodeIdV4(List<PickupLineV2> dbPickupLineList, String loginUserID) throws Exception {
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("NAMRATHA");
        log.info("V4 Inventory Async Process started ");
        try {
            for (PickupLineV2 pickupLineV2 : dbPickupLineList) {
                modifyInventoryForMatchingBarcodeIdV4(pickupLineV2.getCompanyCodeId(), pickupLineV2.getPlantId(), pickupLineV2.getLanguageId(), pickupLineV2.getWarehouseId(),
                        pickupLineV2.getItemCode(), pickupLineV2.getRefDocNumber(), pickupLineV2, loginUserID);
            }
            log.info("V4 Inventory Async Process Completded ");
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param refDocNumber
     * @param dbPickupLine
     * @param loginUserID
     */
    public void modifyInventoryForMatchingBarcodeIdV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                      String itemCode, String refDocNumber, PickupLineV2 dbPickupLine, String loginUserID) {

        InventoryV2 inventory = inventoryService.getOutboundInventoryV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                dbPickupLine.getManufacturerName(), dbPickupLine.getBarcodeId(),
                dbPickupLine.getPickedStorageBin(), dbPickupLine.getAlternateUom());
        log.info("inventory record queried: " + inventory);
        if (inventory != null) {
            if (dbPickupLine.getAllocatedQty() > 0D) {
                try {
                    log.info("UOM,ALTUOM: " + dbPickupLine.getPickUom() + "|" + dbPickupLine.getAlternateUom());
                    double[] inventoryQty = null;
                    inventoryQty = calculateInventoryV6(dbPickupLine.getAllocatedQty(), dbPickupLine.getPickConfirmQty(), dbPickupLine.getNoBags(), inventory.getInventoryQuantity(), inventory.getAllocatedQuantity());
                    if (inventoryQty != null && inventoryQty.length > 3) {
                        inventory.setInventoryQuantity(inventoryQty[0]);
                        inventory.setAllocatedQuantity(inventoryQty[1]);
                        inventory.setReferenceField4(inventoryQty[2]);
                        //                        inventory.setNoBags(inventoryQty[3]);
                    }

                    if (inventory.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(companyCodeId, plantId, languageId, warehouseId, itemCode);
                        if (itemType != null) {
                            inventory.setItemType(itemType.getItemType());
                            inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }

                    InventoryV2 inventoryV2 = new InventoryV2();
                    BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));

                    log.info("Bag Size -----------------> {}", dbPickupLine.getBagSize());
                    log.info("Bag Size Inventory -----------------> {}", inventoryV2.getBagSize());
                    log.info("Inventory Quantity -----------------> {}", inventoryV2.getInventoryQuantity());

                    if (dbPickupLine.getBagSize() > inventoryV2.getInventoryQuantity()) {
                        log.info("Opened Case");
                        inventoryV2.setLoosePack(true);
                    } else if (inventoryV2.getBagSize() > inventoryV2.getInventoryQuantity()) {
                        log.info("Opened Case");
                        inventoryV2.setLoosePack(true);
                    } else {
                        log.info("Closed Case");
                        inventoryV2.setLoosePack(false);
                    }

                    inventoryV2.setReferenceDocumentNo(refDocNumber);
                    inventoryV2.setReferenceOrderNo(refDocNumber);
                    inventoryV2.setUpdatedOn(new Date());
                    try {
                        inventoryV2 = inventoryV2Repository.save(inventoryV2);
                        pickupLineV2Repository.updateExpDate(inventoryV2.getCompanyCodeId(), inventoryV2.getPlantId(), inventoryV2.getLanguageId(),
                                inventoryV2.getWarehouseId(), inventoryV2.getReferenceDocumentNo(), inventoryV2.getItemCode(), inventoryV2.getBarcodeId(),
                                inventoryV2.getExpiryDate());
                        log.info("PickupLine Exp_Date Updated ---------------> " + inventoryV2.getBarcodeId());

                        log.info("-----Inventory2 updated-------: " + inventoryV2);
                    } catch (Exception e) {
                        log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                        e.printStackTrace();
                        InventoryTrans newInventoryTrans = new InventoryTrans();
                        BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                        newInventoryTrans.setReRun(0L);
                        InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                        log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                    }

                    if (inventory.getReferenceField4() == 0) {
                        // Setting up statusId = 0
                        try {
                            // Check whether Inventory has record or not for that storageBin
                            Double inventoryByStBin = inventoryService.getInventoryByStorageBinV4(companyCodeId, plantId, languageId, warehouseId, inventory.getStorageBin());
                            if (inventoryByStBin == null) {
                                // Setting up statusId = 0
                                pickuplineService.updateStorageBinEmptyStatus(companyCodeId, plantId, languageId, warehouseId, inventory.getStorageBin(), loginUserID);
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

            if (dbPickupLine.getAllocatedQty() == null || dbPickupLine.getAllocatedQty() == 0D) {
                try {
                    log.info("UOM,ALTUOM: " + dbPickupLine.getPickUom() + "|" + dbPickupLine.getAlternateUom());
                    double[] inventoryQty = null;
                    if (dbPickupLine.getPickUom().equalsIgnoreCase(dbPickupLine.getAlternateUom())) {
                        inventoryQty = calculateUOMInventory(dbPickupLine.getAllocatedQty(), dbPickupLine.getPickConfirmQty(), dbPickupLine.getNoBags(), inventory.getInventoryQuantity(), inventory.getAllocatedQuantity());
                    } else {
                        inventoryQty = calculateInventoryV6(dbPickupLine.getAllocatedQty(), dbPickupLine.getPickConfirmQty(), dbPickupLine.getNoBags(), inventory.getInventoryQuantity(), inventory.getAllocatedQuantity());
                    }
                    if (inventoryQty != null && inventoryQty.length > 3) {
                        inventory.setInventoryQuantity(inventoryQty[0]);
                        inventory.setAllocatedQuantity(inventoryQty[1]);
                        inventory.setReferenceField4(inventoryQty[2]);
                        //                        inventory.setNoBags(inventoryQty[3]);
                    }

                    if (inventory.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(companyCodeId, plantId, languageId, warehouseId, itemCode);
                        if (itemType != null) {
                            inventory.setItemType(itemType.getItemType());
                            inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }

                    InventoryV2 newInventoryV2 = new InventoryV2();
                    BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));

                    log.info("Bag Size -----------------> {}", dbPickupLine.getBagSize());
                    log.info("Bag Size Inventory -----------------> {}", newInventoryV2.getBagSize());
                    log.info("Inventory Quantity -----------------> {}", newInventoryV2.getInventoryQuantity());

                    if (dbPickupLine.getBagSize() > newInventoryV2.getInventoryQuantity()) {
                        log.info("Opened Case");
                        newInventoryV2.setLoosePack(true);
                    } else if (newInventoryV2.getBagSize() > newInventoryV2.getInventoryQuantity()) {
                        log.info("Opened Case");
                        newInventoryV2.setLoosePack(true);
                    } else {
                        log.info("Closed Case");
                        newInventoryV2.setLoosePack(false);
                    }

                    newInventoryV2.setReferenceDocumentNo(refDocNumber);
                    newInventoryV2.setReferenceOrderNo(refDocNumber);
                    newInventoryV2.setUpdatedOn(new Date());
                    try {
                        InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                        log.info("InventoryV2 created : " + createdInventoryV2);

                        pickupLineV2Repository.updateExpDate(createdInventoryV2.getCompanyCodeId(), createdInventoryV2.getPlantId(), createdInventoryV2.getLanguageId(),
                                createdInventoryV2.getWarehouseId(), createdInventoryV2.getReferenceDocumentNo(), createdInventoryV2.getItemCode(), createdInventoryV2.getBarcodeId(),
                                createdInventoryV2.getExpiryDate());
                        log.info("PickupLine Exp_Date Updated -------------------------> V4");
                        if (createdInventoryV2.getReferenceField4() == 0) {
                            //-------------------------------------------------------------------
                            // PASS PickedConfirmedStBin, WH_ID to inventory
                            // 	If inv_qty && alloc_qty is zero or null then do the below logic.
                            //-------------------------------------------------------------------
                            // Check whether Inventory has record or not for that storageBin
                            Double inventoryByStBin = inventoryService.getInventoryByStorageBinV4(companyCodeId, plantId, languageId, warehouseId, inventory.getStorageBin());
                            if (inventoryByStBin == null) {
                                // Setting up statusId = 0
                                pickuplineService.updateStorageBinEmptyStatus(companyCodeId, plantId, languageId, warehouseId, inventory.getStorageBin(), loginUserID);
                            }
                        }
                    } catch (Exception e) {
                        log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                        e.printStackTrace();
//                        InventoryTrans newInventoryTrans = new InventoryTrans();
//                        BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
//                        newInventoryTrans.setReRun(0L);
//                        InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
//                        log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                    }
                } catch (Exception e1) {
                    log.error("Inventory cum StorageBin update: Error :" + e1.toString());
                    e1.printStackTrace();
                }
            }
        }
    }

    @Async("asyncTaskExecutor")
    public void processInhouseTransferHeaderAsync(AddInhouseTransferHeader newInHouseTransferHeader, String loginUserID) throws Exception {
        if (newInHouseTransferHeader == null) {
            log.info("There are no InhouseTransferHeader to be Processed");
            return;
        }

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newInHouseTransferHeader.getCompanyCodeId(), newInHouseTransferHeader.getPlantId(), newInHouseTransferHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            InhouseTransferHeaderEntity transferHeaderEntity = null;

            if (routingDb != null){
                switch (routingDb){
                    case "REEFERON":
                        transferHeaderEntity = inHouseTransferHeaderService.createInHouseTransferHeaderV5(newInHouseTransferHeader, loginUserID);
                        break;
                    case "KNOWELL":
                        transferHeaderEntity= inHouseTransferHeaderService.createInHouseTransferHeaderV2(newInHouseTransferHeader, loginUserID);
                        break;
                    default:
                        transferHeaderEntity= inHouseTransferHeaderService.createInHouseTransferHeaderV2(newInHouseTransferHeader, loginUserID);
                }
            }

            log.info("createdInHouseTransferHeader through ASYNC -----> {}", transferHeaderEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//    @Async("asyncTaskExecutor")
//    public void processPatchAssignedPickerIdInPickupHeaderAsync(List<PickupHeaderV2> updatePickupHeaderList) {
//        if (updatePickupHeaderList == null) {
//            log.error("There are no updatePickUpHeaderList to be Processed");
//            return;
//        }
//
//        try {
//            log.info("updatePickupHeaderList ------> {}", updatePickupHeaderList);
//            String routingDb = null;
//            for (PickupHeaderV2 updatepickupHeaderlist : updatePickupHeaderList) {
//                DataBaseContextHolder.setCurrentDb("MT");
//                routingDb = dbConfigRepository.getDbName(updatepickupHeaderlist.getCompanyCodeId(), updatepickupHeaderlist.getPlantId(), updatepickupHeaderlist.getWarehouseId());
//                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//                DataBaseContextHolder.clear();
//                DataBaseContextHolder.setCurrentDb(routingDb);
//            }
//
//            List<PickupHeaderV2> updatedPickupHeader = new ArrayList<>();
//            if (routingDb != null) {
//                switch (routingDb) {
//                    case "NAMRATHA":
//                        updatedPickupHeader =
//                                pickupheaderService.patchAssignedPickerIdInPickupHeaderV6(updatePickupHeaderList);
//                        break;
//                    case "KNOWELL":
//                        updatedPickupHeader =
//                                pickupheaderService.patchAssignedPickerIdInPickupHeaderV2(updatePickupHeaderList);
//                        break;
//                    case "FAHAHEEL":
//                        updatedPickupHeader =
//                                pickupheaderService.patchAssignedPickerIdInPickupHeaderV2(updatePickupHeaderList);
//                        break;
//                }
//            }
//
//            log.info("UpdatedPickupHeader completed through Async ----> {}", updatedPickupHeader);
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            DataBaseContextHolder.clear();
//        }
//    }

    @Async("asyncTaskExecutor")
    public void assignPickerAsync(List<AssignPickerV2> assignPicker, String assignedPickerId, String loginUserID) {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(assignPicker.get(0).getCompanyCodeId(), assignPicker.get(0).getPlantId(),
                    assignPicker.get(0).getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<OrderManagementLineV2> updatupdatedOrderManagementLine = null;
            if (routingDb != null) {
                switch (routingDb) {
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        updatupdatedOrderManagementLine = ordermangementlineService.doAssignPickerV2(assignPicker,assignedPickerId,loginUserID);
                        break;
                    case "REEFERON":
                        updatupdatedOrderManagementLine = ordermangementlineService.doAssignPickerV5(assignPicker, assignedPickerId, loginUserID);
                        break;
                    case "NAMRATHA":
                        updatupdatedOrderManagementLine = ordermangementlineService.doAssignPickerV2(assignPicker, assignedPickerId, loginUserID);
                        break;
                    case "KNOWELL":
                        updatupdatedOrderManagementLine = ordermangementlineService.doAssignPickerV7(assignPicker, assignedPickerId, loginUserID);
                        break;
                }
            }
            log.info("OrderManagementLine AssignPicker completed through Async ----> {}", updatupdatedOrderManagementLine);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
