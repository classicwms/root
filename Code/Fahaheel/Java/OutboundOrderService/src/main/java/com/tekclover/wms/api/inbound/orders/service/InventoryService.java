package com.tekclover.wms.api.inbound.orders.service;


import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.dto.IInventory;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.repository.InventoryV2Repository;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.tekclover.wms.api.inbound.orders.service.BaseService.PACK_BARCODE;
import static com.tekclover.wms.api.inbound.orders.service.BaseService.STAGING_BIN_CLASS_ID;

@Service
@Slf4j
public class InventoryService {
//
    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryV2(String companyCode, String plantId, String languageId, String warehouseId,
                                      String packBarcodes, String itemCode, String storageBin, String manufacturerName) {
        try {
            log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
            List<InventoryV2> inventory =
                    inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerNameAndDeletionIndicatorOrderByInventoryIdDesc(
                            languageId,
                            companyCode,
                            plantId,
                            warehouseId,
                            packBarcodes,
                            itemCode,
                            storageBin,
                            manufacturerName,
                            0L
                    );
            if (inventory.isEmpty()) {
                log.error("---------Inventory is null-----------");
                return null;
            }
            log.info("getInventory record----------> : " + inventory.get(0));
            return inventory.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception while Inventory get : " + e);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        log.info("Parameter inputs: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryForOrderManagement(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, stockTypeId, manufacturerName);
        return inventory;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByCtdOnV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                             String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryOmlOrderByCtdOnId(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId);
        return inventory;
    }

    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByLevelIdV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                               String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryOmlOrderByLevelId(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId);
        return inventory;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @return
     */
    public synchronized List<IInventory> getInventoryForOrderManagementGroupByLevelIdV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                           String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        List<IInventory> inventory = inventoryV2Repository.findInventoryGroupByLevelId(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId);
        return inventory;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param levelId
     * @param manufacturerName
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementGroupByLevelIdV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                               String itemCode, Long stockTypeId, Long binClassId, Long levelId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryOmlGroupByLevelId(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId, levelId);
        return inventory;
    }


//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param referenceDocumentNo
//     * @param itemCode
//     * @return
//     */
//    public List<InventoryV2> getInventory(String companyCodeId, String plantId,
//                                          String languageId, String warehouseId,
//                                          String referenceDocumentNo, String itemCode) {
//        List<InventoryV2> inventory =
//                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentNoAndItemCodeAndDeletionIndicator(
//                        languageId,
//                        companyCodeId,
//                        plantId,
//                        warehouseId,
//                        referenceDocumentNo,
//                        itemCode,
//                        0L
//                );
//
//        if (inventory == null) {
//            throw new BadRequestException("The given Inventory ID : " +
//                    ", warehouseId: " + warehouseId +
//                    ", companyCodeId: " + companyCodeId +
//                    ", plantId: " + plantId +
//                    ", languageId: " + languageId +
//                    ", referenceDocumentNo: " + referenceDocumentNo +
//                    ", itemCode: " + itemCode +
//                    " doesn't exist.");
//        }
//        return inventory;
//    }
//
//    /**
//     * @param companyCode
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param packBarcodes
//     * @param itemCode
//     * @param storageBin
//     * @param manufacturerCode
//     * @return
//     */
//    public InventoryV2 getInventory(String companyCode, String plantId, String languageId,
//                                    String warehouseId, String packBarcodes, String itemCode,
//                                    String storageBin, String manufacturerCode) {
//        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
//        Optional<InventoryV2> inventory =
//                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerCodeAndDeletionIndicator(
//                        languageId,
//                        companyCode,
//                        plantId,
//                        warehouseId,
//                        packBarcodes,
//                        itemCode,
//                        storageBin,
//                        manufacturerCode,
//                        0L
//                );
//        if (inventory.isEmpty()) {
//            log.error("---------Inventory is null-----------");
//            return null;
//        }
//        log.info("getInventory record----------> : " + inventory.get());
//        return inventory.get();
//    }
//
//    /**
//     * @param companyCode
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param packBarcodes
//     * @param itemCode
//     * @param manufacturerCode
//     * @return
//     */
//    public InventoryV2 getInventory(String companyCode, String plantId, String languageId,
//                                    String warehouseId, String packBarcodes, String itemCode,
//                                    String manufacturerCode) {
//        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode);
//        Optional<InventoryV2> inventory =
//                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndDeletionIndicator(
//                        languageId,
//                        companyCode,
//                        plantId,
//                        warehouseId,
//                        packBarcodes,
//                        itemCode,
//                        manufacturerCode,
//                        0L
//                );
//        if (inventory.isEmpty()) {
//            log.error("---------Inventory is null-----------");
//            return null;
//        }
//        log.info("getInventory record----------> : " + inventory.get());
//        return inventory.get();
//    }
//
//    /**
//     * @param companyCode
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @return
//     */
//    public List<InventoryV2> getInventoryV5(String companyCode, String plantId, String languageId,
//                                    String warehouseId, String itemCode) {
//        log.info("getInventory----------> : " + warehouseId );
//        List<InventoryV2> inventory =
//                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
//                        languageId,
//                        companyCode,
//                        plantId,
//                        warehouseId,
//                        itemCode,
//                        0L
//                );
//        if (inventory.isEmpty()) {
//            log.error("---------Inventory is null-----------");
//            return null;
//        }
//        log.info("getInventory record----------> : " + inventory);
//        return inventory;
//    }
//
    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerName
     * @param storageBin
     * @return
     */
    public InventoryV2 getInventoryForAllocationV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                   String packBarcodes, String itemCode, String manufacturerName, String storageBin) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
        List<InventoryV2> inventory =
                inventoryV2Repository
                        .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerNameAndDeletionIndicatorOrderByInventoryIdDesc(
                                languageId,
                                companyCode,
                                plantId,
                                warehouseId,
                                packBarcodes,
                                itemCode,
                                storageBin,
                                manufacturerName,
                                0L
                        );
        if (inventory.isEmpty()) {
            log.error("---------Inventory is null-----------");
            return null;
        }
        log.info("getInventory record----------> : " + inventory.get(0));
        return inventory.get(0);
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param storageBin
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryByStorageBinV2(String companyCode, String plantId, String languageId, String warehouseId, String storageBin) {
        try {
            List<InventoryV2> inventory =
                    inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
                            languageId,
                            companyCode,
                            plantId,
                            warehouseId,
                            storageBin,
                            0L
                    );
            if (inventory.isEmpty()) {
                log.error("---------Inventory is null-----------");
                return null;
            }
            return inventory.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception while Inventory Get : " + e);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param manufacturerName
     * @param itemCode
     * @return
     */
    public synchronized Double getInventoryQtyCountForInvMmt(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String manufacturerName, String itemCode) {
        Double inventoryQty = inventoryV2Repository.getInventoryQtyCountForInvMmt(companyCodeId, plantId, languageId, warehouseId, manufacturerName, itemCode);
        return inventoryQty;
    }

//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param stockTypeId
//     * @param binClassId
//     * @param manufacturerName
//     * @param alternateUom
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                 String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
//                                                                 String alternateUom) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.findInventoryForOrderManagementV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, stockTypeId, alternateUom);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.findInventoryForOrderManagementV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, stockTypeId, null);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param stockTypeId
//     * @param binClassId
//     * @param manufacturerName
//     * @param alternateUom
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementV7(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                 String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
//                                                                 String alternateUom) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.findInventoryForOrderManagementV7(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, stockTypeId, alternateUom);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.findInventoryForOrderManagementV7(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, stockTypeId, null);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @param alternateUom
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByCreatedOnV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                                 String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByCreatedOn(companyCodeId, plantId, languageId, warehouseId, null, null,
//                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByCreatedOn(companyCodeId, plantId, languageId, warehouseId, null, null,
//                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @param alternateUom
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByExpiryDateV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                                  String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByExpiryDate(companyCodeId, plantId, languageId, warehouseId, null, null,
//                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByExpiryDate(companyCodeId, plantId, languageId, warehouseId, null, null,
//                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
//        }
//        return inventoryList;
//    }
//
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @param alternateUom
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByLevelIdV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                               String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByLevelId(companyCodeId, plantId, languageId, warehouseId, null, null,
//                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByLevelId(companyCodeId, plantId, languageId, warehouseId, null, null,
//                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @param alternateUom
//     * @return
//     */
//    public synchronized List<IInventory> getInventoryForOrderManagementByBatchV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                    String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
//        List<IInventory> inventoryList = inventoryV2Repository.findInventoryByBatchV4(companyCodeId, plantId, languageId, warehouseId, binClassId,
//                stockTypeId, itemCode, manufacturerName, alternateUom);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.findInventoryByBatchV4(companyCodeId, plantId, languageId, warehouseId, binClassId,
//                    stockTypeId, itemCode, manufacturerName, null);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @param alternateUom
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByBatchV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                             String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName +
//                "|" + alternateUom + "|" + binClassId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByBatch(companyCodeId, plantId, languageId, warehouseId, null, null,
//                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByBatch(companyCodeId, plantId, languageId, warehouseId, null, null,
//                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param stockTypeId
//     * @param binClassId
//     * @param manufacturerName
//     * @param alternateUom
//     * @return
//     */
//    public synchronized List<IInventory> getInventoryForOrderManagementGroupByLevelIdV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                           String itemCode, Long stockTypeId, Long binClassId, String manufacturerName, String alternateUom) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
//        List<IInventory> inventoryList = inventoryV2Repository.findInventoryGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                manufacturerName, stockTypeId, binClassId, alternateUom);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.findInventoryGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    manufacturerName, stockTypeId, binClassId, null);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @param alternateUom
//     * @param levelId
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementLevelIdV6(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                        String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
//                                                                        String alternateUom, Long levelId) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId + "|" + levelId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryLevelIdV6(
//                companyCodeId, plantId, languageId, warehouseId, null, null,
//                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom, levelId);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.getOMLInventoryLevelAsscIdV6(
//                    companyCodeId, plantId, languageId, warehouseId, null, null,
//                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null, levelId);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @param alternateUom
//     * @param levelId
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementLevelAsscIdV6(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                            String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
//                                                                            String alternateUom, Long levelId) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId + "|" + levelId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryLevelAsscIdV6(
//                companyCodeId, plantId, languageId, warehouseId, null, null,
//                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom, levelId);
//        if (inventoryList == null || inventoryList.isEmpty()) {
//            inventoryList = inventoryV2Repository.getOMLInventoryLevelIdV6(
//                    companyCodeId, plantId, languageId, warehouseId, null, null,
//                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null, levelId);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param barcodeId
//     * @param batchSerialNumber
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @param storageBin
//     * @param alternateUom
//     * @param bagSize
//     * @return
//     */
//    public synchronized IInventoryImpl getInventoryV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                         String itemCode, String barcodeId, String batchSerialNumber, String manufacturerName,
//                                         Long stockTypeId, Long binClassId, String storageBin, String alternateUom, Double bagSize) {
//        String packBarCode = batchSerialNumber != null ? batchSerialNumber : PACK_BARCODE;
//        log.info(companyCodeId + "|" + plantId + "|"+ languageId + "|"+ warehouseId + "|"+ itemCode + "|"+ manufacturerName + "|"+ alternateUom + "|"+ bagSize + "|"+ binClassId + "|"+ storageBin + "|"+ barcodeId + "|"+ packBarCode);
//        return inventoryV2Repository.getOMLInventoryV4(
//                companyCodeId, plantId, languageId, warehouseId, barcodeId, batchSerialNumber, itemCode, manufacturerName,
//                packBarCode, storageBin, binClassId, stockTypeId, alternateUom);
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param storageBin
//     * @param alternateUom
//     * @return
//     */
//    public synchronized InventoryV2 getInventoryV4(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                      String itemCode, String manufacturerName, String barcodeId, String storageBin, String alternateUom) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName +
//                "|" + alternateUom + "|" + barcodeId + "|" + storageBin);
//        IInventoryImpl dbInventory = inventoryV2Repository.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, barcodeId, null,
//                itemCode, manufacturerName, PACK_BARCODE, storageBin, alternateUom);
//        if(dbInventory == null) {
//            dbInventory = inventoryV2Repository.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, barcodeId, null,
//                    itemCode, manufacturerName, PACK_BARCODE, storageBin, null);
//        }
//        if(dbInventory != null) {
//            InventoryV2 inventory = new InventoryV2();
//            BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
//            return inventory;
//        }
//        return null;
//    }
//
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param stockTypeId
//     * @param binClassId
//     * @param manufacturerName
//     * @return
//     */
//    public synchronized List<IInventory> getInventoryForOrderManagementGroupByLevelIdV5(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                           String itemCode, Long stockTypeId, Long binClassId, String manufacturerName,
//                                                                           String barcodeId) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId + "|" + barcodeId);
//        List<IInventory> inventoryList = inventoryV2Repository.findInventoryGroupByLevelIdV5(
//                companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId, barcodeId);
//        if(inventoryList == null || inventoryList.isEmpty()) {
//            return inventoryV2Repository.findInventoryGroupByLevelIdV5(
//                    companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, STAGING_BIN_CLASS_ID, barcodeId);
//        }
//        return inventoryList;
//    }
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param stockTypeId
//     * @param binClassId
//     * @param manufacturerName
//     * @return
//     */
//    public List<IInventory> getRemainingSelfLife(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                            String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
//        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
//                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
//            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
//                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
//        }
//
//        List<IInventory> inventory = inventoryV2Repository.findInventoryForRemainingDaysV5(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId);
//        return inventory;
//    }
//
//
//
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementLevelIdV5(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                        String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
//                                                                        String barcodeId) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryLevelIdV5(
//                companyCodeId, plantId, languageId, warehouseId, barcodeId, null, itemCode, manufacturerName, null, binClassId, stockTypeId);
//        if(inventoryList == null || inventoryList.isEmpty()) {
//            return inventoryV2Repository.getOMLInventoryLevelIdV5(
//                    companyCodeId, plantId, languageId, warehouseId, barcodeId, null, itemCode, manufacturerName, null, STAGING_BIN_CLASS_ID, stockTypeId);
//        }
//        return inventoryList;
//    }
//
//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param stockTypeId
//     * @param binClassId
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByLevelIdV5(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                               String itemCode, String manufacturerName, Long stockTypeId, Long binClassId) {
//        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
//        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryOrderByLevelIdV5(
//                companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId);
//        if(inventoryList == null || inventoryList.isEmpty()) {
//            return inventoryV2Repository.getOMLInventoryOrderByLevelIdV5(
//                    companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, STAGING_BIN_CLASS_ID, stockTypeId);
//        }
//        return inventoryList;
//    }
//
//
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param stockTypeId
//     * @param binClassId
//     * @param manufacturerName
//     * @return
//     */
//    public List<IInventory> getInventoryForOrderManagementGroupByExpiryDate(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                             String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
//        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
//                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
//            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
//                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
//        }
//
//        List<IInventory> inventory = inventoryV2Repository.findInventoryGroupIdExpiryV5(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId);
//        return inventory;
//    }
//
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param stockTypeId
//     * @param binClassId
//     * @param expiryDate
//     * @param manufacturerName
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementGroupByExpiryDate(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                                String itemCode, Long stockTypeId, Long binClassId, Date expiryDate, String manufacturerName)throws ParseException {
//        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
//                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
//            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
//                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
//        }
//        Date fromDate = null;
//        Date toDate = null;
//        if (expiryDate != null) {
//            Date[] dates = DateUtils.addTimeToDatesForSearch(expiryDate,expiryDate);
//            fromDate = dates[0];
//            toDate = dates[1];
//        }
//        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryOmlGroupByExpiryDateV5(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId, fromDate, toDate);
//        return inventory;
//    }
//
//
//    /**
//     *
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @param binClassId
//     * @param barcodeId
//     * @param storageBin
//     * @param stockTypeId
//     * @return
//     */
//    public synchronized List<IInventoryImpl> getInventoryForOrderManagementV2GroupByBarcodeIdV5(String companyCodeId, String plantId, String languageId,
//                                                                                 String warehouseId, String itemCode, String manufacturerName,
//                                                                                 Long binClassId, String barcodeId, String storageBin, Long stockTypeId) {
//        List<IInventoryImpl> inventory =
//                inventoryV2Repository.findInventoryOmlGroupByBarcodeIdV5(
//                        companyCodeId,
//                        languageId,
//                        plantId,
//                        warehouseId,
//                        manufacturerName,
//                        itemCode,
//                        storageBin,
//                        stockTypeId,
//                        binClassId,
//                        barcodeId);
//        return inventory;
//    }

    /**
     * Perpetual for Fahaheel
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public  synchronized List<IInventoryImpl> getInventoryForPerpetualCountV2(String companyCode, String plantId, String languageId,
                                                                String warehouseId, String itemCode, String manufacturerName) {
        try {
            log.info("getInventory----------> : " + warehouseId + "," + manufacturerName + "," + itemCode + "," + plantId);

            List<IInventoryImpl> inventory =
                    inventoryV2Repository.findInventoryForPerpertual(
                            companyCode,
                            plantId,
                            languageId,
                            warehouseId,
                            itemCode,
                            //                        1L,
                            manufacturerName);
            if (inventory == null || inventory.isEmpty()) {
                log.error("---------Inventory is null-----------");
                return null;
            }
            log.info("getInventory record----------> : " + inventory.size());
            return inventory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception while Inventory Get : " + e);
        }
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public List<IInventoryImpl> getStockAdjustmentInventory(String companyCode, String plantId, String languageId, String warehouseId,
                                                            String itemCode, String manufacturerName, Long binClassId) {
        try {
            return inventoryV2Repository.
                    inventoryForStockCount(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception while Inventory Get : " + e);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param packBarcodes
     * @param binClassId
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryForStockAdjustmentDamageV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                              String itemCode, String packBarcodes, Long binClassId, String manufacturerName) {
        try {
            InventoryV2 inventory =
                    inventoryV2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndBinClassIdAndManufacturerNameAndDeletionIndicatorOrderByInventoryIdDesc(
                            languageId,
                            companyCodeId,
                            plantId,
                            warehouseId,
                            itemCode,
                            packBarcodes,
                            binClassId,
                            manufacturerName,
                            0L
                    );
            if (inventory != null) {
                log.info("InventoryForStockAdjustmentDamageV2: " + inventory);
                return inventory;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception while Inventory Get : " + e);
        }
    }

}
