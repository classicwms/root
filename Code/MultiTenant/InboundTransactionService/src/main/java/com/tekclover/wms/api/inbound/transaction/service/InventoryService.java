package com.tekclover.wms.api.inbound.transaction.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.dto.*;
import com.tekclover.wms.api.inbound.transaction.model.impl.InventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.*;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.SearchInventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.repository.specification.InventorySpecification;
import com.tekclover.wms.api.inbound.transaction.repository.specification.InventoryV2Specification;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ParseException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InventoryService extends BaseService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    MastersService mastersService;

    @Autowired
    private ImBasicData1Repository imbasicdata1Repository;

    //================================================================================================
    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    StorageBinService storageBinService;
    @Autowired
    private ErrorLogRepository exceptionLogRepo;
    boolean alreadyExecuted = true;
    @Autowired
    private InventoryTransRepository inventoryTransRepository;

    private static final long SELF_LIFE_DAYS = 180;
    //================================================================================================

    /**
     * getInventorys
     *
     * @return
     */
    public List<Inventory> getInventorys() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        inventoryList = inventoryList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return inventoryList;
    }

    /**
     * getInventory
     *
     * @param stockTypeId
     * @return
     */
    public Inventory getInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin, Long stockTypeId,
                                  Long specialStockIndicatorId) {
        Optional<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        storageBin,
                        stockTypeId,
                        specialStockIndicatorId,
                        0L
                );
        if (inventory.isEmpty()) {
            throw new BadRequestException("The given Inventory ID : " +
                    ", warehouseId: " + warehouseId +
                    ", packBarcodes: " + packBarcodes +
                    ", itemCode: " + itemCode +
                    ", storageBin: " + storageBin +
                    ", stockTypeId: " + stockTypeId +
                    ", specialStockIndicatorId: " + specialStockIndicatorId +
                    " doesn't exist.");
        }
        return inventory.get();
    }


    /**
     * @param putAwayLine
     * @param loginUserId
     * @return
     * @throws Exception
     */
    @Transactional
    public InventoryV2 createInventoryNonCBMV5(PutAwayLineV2 putAwayLine, String loginUserId) throws Exception {

        log.info("PutawayLine----->" + putAwayLine);
        String companyCode = putAwayLine.getCompanyCode();
        String plantId = putAwayLine.getPlantId();
        String languageId = putAwayLine.getLanguageId();
        String warehouseId = putAwayLine.getWarehouseId();
        String barCodeId = putAwayLine.getBarcodeId();
        String itemCode = putAwayLine.getItemCode();
        String manufacturerName = putAwayLine.getManufacturerName();
        String refDocNumber = putAwayLine.getRefDocNumber();
        alreadyExecuted = false;
        try {

            //BinClassId 3 reduce Inventory
//            binClassId3InventoryReduction(companyCode, plantId, languageId, warehouseId, barCodeId, itemCode, manufacturerName, refDocNumber, putAwayLine.getInventoryQuantity(), loginUserId);

            //Validate HuSerial No/ Barcode Id for Uniqueness
//            validateInventoryBarcodeId(companyCode, plantId, languageId, warehouseId, barCodeId);

            InventoryV2 createdinventory = null;
            log.info("BinClassID 1 create/Update Initiated---->alreadyExecuted---> " + new Date() + ", " + alreadyExecuted);
            InventoryV2 dbInventoryBinClassId1 = getInventoryV3(companyCode, plantId, languageId, warehouseId, PACK_BARCODE, itemCode, barCodeId, manufacturerName, putAwayLine.getConfirmedStorageBin());
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));
            inventory.setCompanyCodeId(companyCode);

            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);
            inventory.setVariantSubCode("1");
            inventory.setStorageMethod("1");
            inventory.setBatchSerialNumber("1");
            inventory.setPackBarcodes(PACK_BARCODE);
            inventory.setDeletionIndicator(0L);
            inventory.setReferenceField8(putAwayLine.getDescription());
            inventory.setReferenceField9(putAwayLine.getManufacturerName());
            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
            inventory.setDescription(putAwayLine.getDescription());
            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());

            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            StorageBinV2 storageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, warehouseId, putAwayLine.getConfirmedStorageBin());
            log.info("storageBin: {}", storageBin);

            if (storageBin != null) {
                inventory.setStorageBin(storageBin.getStorageBin());
                inventory.setBinClassId(storageBin.getBinClassId());
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setStorageSectionId(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
            }

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
            inventory.setStockTypeDescription(stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);
//            double PA_QTY = round1(putAwayLine.getPutawayConfirmedQty());
            double PA_QTY = round1(putAwayLine.getInventoryQuantity());
            if (dbInventoryBinClassId1 == null) {
                // INV_QTY
                double INV_QTY = round1(PA_QTY);
                inventory.setInventoryQuantity(INV_QTY);
                inventory.setAllocatedQuantity(0D);
                inventory.setReferenceField4(INV_QTY);      //Allocated Qty is zero for newInventory
                log.info("B1 New - Inventory - INV_QTY,TOT_QTY: {}", INV_QTY);
            } else {
//                double INV_QTY = round1(dbInventoryBinClassId1.getInventoryQuantity());
                double INV_QTY = round1(dbInventoryBinClassId1.getInventoryQuantity());
                double ALLOC_QTY = round1(dbInventoryBinClassId1.getAllocatedQuantity());
                INV_QTY = INV_QTY + PA_QTY;
                double TOT_QTY = INV_QTY + ALLOC_QTY;
                inventory.setInventoryQuantity(INV_QTY);
                inventory.setAllocatedQuantity(ALLOC_QTY);
                inventory.setReferenceField4(TOT_QTY);
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

            inventory.setCrateQty(putAwayLine.getOrderQty());
            inventory.setCreatedOn(new Date());
            inventory.setUpdatedOn(new Date());
            inventory.setQtyInCreate(putAwayLine.getQtyInCreate());
            inventory.setQtyInPiece(putAwayLine.getQtyInPiece());
            inventory.setQtyInCase(putAwayLine.getQtyInCase());
            inventory.setVehicleNo(putAwayLine.getVehicleNo());
            inventory.setVehicleUnloadingDate(putAwayLine.getVehicleUnloadingDate());
            inventory.setVehicleReportingDate(putAwayLine.getVehicleReportingDate());
            inventory.setReceivingVariance(putAwayLine.getReceivingVariance());
            if (!alreadyExecuted) {
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
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


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param barcodeId
     * @param manufacturerName
     * @param storageBin
     * @return
     */
    public InventoryV2 getInventoryV3(String companyCode, String plantId, String languageId, String warehouseId,
                                      String packBarcodes, String itemCode, String barcodeId, String manufacturerName, String storageBin) {
        try {
            log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
            List<InventoryV2> inventory =
                    inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndBarcodeIdAndManufacturerNameAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
                            languageId,
                            companyCode,
                            plantId,
                            warehouseId,
                            packBarcodes,
                            itemCode,
                            barcodeId,
                            manufacturerName,
                            storageBin,
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
            throw new BadRequestException("Error While Inventory Get : " + e);
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param barcodeId
     * @param manufacturerName
     * @param storageBin
     * @param batchSerialNumber
     * @return
     */
    public InventoryV2 getInventoryForInboundConfirmV3(String companyCode, String plantId, String languageId, String warehouseId,
                                                       String packBarcodes, String itemCode, String barcodeId, String manufacturerName,
                                                       String storageBin, String batchSerialNumber) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
        InventoryV2 inventory =
                inventoryV2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndBarcodeIdAndManufacturerCodeAndStorageBinAndBatchSerialNumberAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        barcodeId,
                        manufacturerName,
                        storageBin,
                        batchSerialNumber,
                        0L);
        if (inventory != null) {
            log.info("getInventory record----------> : " + inventory);
            return inventory;
        }
        log.info("getInventory record----------> : " + inventory);
        return null;
    }

    /**
     * getInventory
     *
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param binClassId
     * @return
     */
    public Inventory getInventory(String warehouseId, String packBarcodes, String itemCode, Long binClassId) {
        Optional<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndBinClassIdAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        binClassId,
                        0L
                );
        if (inventory != null) {
            return inventory.get();
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param packBarcodes
     * @param binClassId
     * @return
     */
    public List<Inventory> getInventoryForDeliveryConfirmtion(String warehouseId, String itemCode,
                                                              String packBarcodes, Long binClassId) {
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndBinClassIdAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        itemCode,
                        packBarcodes,
                        binClassId,
                        0L
                );
        if (inventory != null) {
            return inventory;
        }
        return null;
    }


    /**
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @return
     */
    public List<Inventory> getInventoryForStockReport(String warehouseId, String itemCode, Long stockTypeId) {
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndAndStockTypeIdAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        itemCode,
                        stockTypeId,
                        0L
                );
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param binClassId
     * @return
     */
    public List<Inventory> getInventoryForOrderManagement(String warehouseId, String itemCode, Long stockTypeId, Long binClassId) {
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndAndStockTypeIdAndBinClassIdAndInventoryQuantityGreaterThanAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        itemCode,
                        stockTypeId,
                        binClassId,
                        0D,
                        0L
                );
        return inventory;
    }

    /**
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @return
     */
    public List<Inventory> getInventoryForDelete(String warehouseId, String packBarcodes, String itemCode) {
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        0L
                );
        return inventory;
    }

    /**
     * @param warehouseId
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param itemCode
     * @return
     */
    public List<Inventory> getInventory(String warehouseId, String palletCode, String caseCode, String packBarcodes,
                                        String itemCode) {
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPalletCodeAndCaseCodeAndPackBarcodesAndItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        palletCode,
                        caseCode,
                        packBarcodes,
                        itemCode,
                        0L
                );
        if (inventory.isEmpty()) {
            throw new BadRequestException("The given Inventory ID : " +
                    ", warehouseId: " + warehouseId +
                    ", palletCode: " + palletCode +
                    ", caseCode: " + caseCode +
                    ", packBarcodes: " + packBarcodes +
                    ", itemCode: " + itemCode +
                    " doesn't exist.");
        }
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param palletCode
     * @param packBarcodes
     * @param storageBin
     * @return
     */
    public List<Inventory> getInventoryForTransfers(String warehouseId, String itemCode, String palletCode, String packBarcodes, String storageBin) {
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPalletCodeAndPackBarcodesAndStorageBinAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        itemCode,
                        palletCode,
                        packBarcodes,
                        storageBin,
                        0L
                );

        if (!inventory.isEmpty()) {
            return inventory;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param storageBin
     * @return
     */
    public Inventory getInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
        Optional<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        storageBin,
                        0L
                );
        if (inventory.isEmpty()) {
            log.error("---------Inventory is null-----------");
            return null;
        }
        log.info("getInventory record----------> : " + inventory.get());
        return inventory.get();
    }

    /**
     * @param warehouseId
     * @param storageBin
     * @return
     */
    public Inventory getInventoryByStorageBin(String warehouseId, String storageBin) {
        Optional<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageBinAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        storageBin,
                        0L
                );
        if (inventory.isEmpty()) {
            log.error("---------Inventory is null-----------");
            return null;
        }
        return inventory.get();
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param binClassId
     * @return
     */
    public List<Inventory> getInventory(String warehouseId, String itemCode, Long binClassId) {
        Warehouse warehouse = getWarehouse(warehouseId);
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(
                        warehouse.getLanguageId(),
                        warehouse.getCompanyCode(),
                        warehouse.getPlantId(),
                        warehouseId,
                        itemCode,
                        binClassId,
                        0L
                );
        if (inventory.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader ID : " +
                    ", warehouseId: " + warehouseId +
                    ", itemCode: " + itemCode +
                    ", binClassId: " + binClassId +
                    " doesn't exist.");
        }
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @return
     */
    public List<Inventory> getInventory(String warehouseId, String itemCode) {
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        itemCode,
                        0L
                );
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param binClassId
     * @param storageBin
     * @param stockTypeId
     * @return
     */
    public List<Inventory> getInventoryForOrderMgmt(String warehouseId, String itemCode, Long binClassId, String storageBin, Long stockTypeId) {
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndStorageBinAndStockTypeIdAndDeletionIndicatorAndInventoryQuantityGreaterThanOrderByInventoryQuantity(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        itemCode,
                        binClassId,
                        storageBin,
                        stockTypeId,
                        0L,
                        0D
                );
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param stSecIds
     * @return
     */
    public List<IInventory> getInventoryGroupByStorageBin(String warehouseId, String itemCode, List<String> stSecIds) {
        List<IInventory> inventory = inventoryRepository.findInventoryGroupByStorageBin(warehouseId, itemCode, stSecIds);
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param storageSectionIds
     * @return
     */
    public List<Inventory> getInventoryForAdditionalBins(String warehouseId, String itemCode, List<String> storageSectionIds) {
        Warehouse warehouse = getWarehouse(warehouseId);
        log.info("InventoryForAdditionalBins ID : warehouseId: " + warehouseId + ", itemCode: " + itemCode + ", storageSectionIds: " + storageSectionIds);
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceField10InAndBinClassIdAndInventoryQuantityGreaterThan(
                        warehouse.getLanguageId(),
                        warehouse.getCompanyCode(),
                        warehouse.getPlantId(),
                        warehouseId,
                        itemCode,
                        storageSectionIds,
                        1L,
                        0D
                );
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param storageSectionIds
     * @param stockTypeId
     * @return
     */
    public List<Inventory> getInventoryForAdditionalBinsForOB2(String warehouseId, String itemCode, List<String> storageSectionIds, Long stockTypeId) {
        Warehouse warehouse = getWarehouse(warehouseId);
        List<Inventory> inventory =
                inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceField10InAndStockTypeIdAndBinClassIdAndInventoryQuantityGreaterThan(
                        warehouse.getLanguageId(),
                        warehouse.getCompanyCode(),
                        warehouse.getPlantId(),
                        warehouseId,
                        itemCode,
                        storageSectionIds,
                        stockTypeId,
                        1L,
                        0D
                );

        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param storageBin
     * @param stockTypeId
     * @return
     */
    public List<Long> getInventoryQtyCount(String warehouseId, String itemCode, List<String> storageBin,
                                           Long stockTypeId) {
        List<Long> inventory = inventoryRepository.findInventoryQtyCount(warehouseId, itemCode, storageBin, stockTypeId);
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @return
     */
    public Double getInventoryQtyCount(String warehouseId, String itemCode) {
        Double inventoryQty = inventoryRepository.getInventoryQtyCount(warehouseId, itemCode);
        return inventoryQty;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param manufacturerName
     * @param itemCode
     * @return
     */
    public Double getInventoryQtyCountV2(String companyCodeId, String plantId, String languageId, String warehouseId, String manufacturerName, String itemCode) {
        Double inventoryQty = inventoryV2Repository.getInventoryQtyCountV2(companyCodeId, plantId, languageId, warehouseId, manufacturerName, itemCode);
        return inventoryQty;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param manufacturerName
     * @param itemCode
     * @return
     */
    public Double getInventoryQtyCountForInvMmt(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String manufacturerName, String itemCode) {
        Double inventoryQty = inventoryV2Repository.getInventoryQtyCountForInvMmt(companyCodeId, plantId, languageId, warehouseId, manufacturerName, itemCode);
        return inventoryQty;
    }

    /**
     * @param warehouseId
     * @param stBins
     * @return
     */
    public List<Inventory> getInventoryByStorageBin(String warehouseId, List<String> stBins) {
        List<Inventory> inventory = inventoryRepository.findByWarehouseIdAndStorageBinIn(warehouseId, stBins);
        return inventory;
    }

    /**
     * @param sortBy
     * @param pageSize
     * @param pageNo
     * @param searchInventory
     * @return
     * @throws ParseException
     */
    public Page<Inventory> findInventory(SearchInventory searchInventory, Integer pageNo, Integer pageSize, String sortBy)
            throws ParseException {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        InventorySpecification spec = new InventorySpecification(searchInventory);
        Page<Inventory> results = inventoryRepository.findAll(spec, paging);
        return results;
    }

    /**
     * @param searchInventory
     * @return
     * @throws ParseException
     */
//	public List<Inventory> findInventory(SearchInventory searchInventory)
//			throws ParseException {
//		InventorySpecification spec = new InventorySpecification(searchInventory);
//		List<Inventory> results = inventoryRepository.findAll(spec);
//		return results;
//	}
    public List<Inventory> findInventory(SearchInventory searchInventory)
            throws ParseException {
        InventorySpecification spec = new InventorySpecification(searchInventory);
        List<Inventory> results = inventoryRepository.findAll(spec);
        results.stream().forEach(n -> {
            if (n.getInventoryQuantity() == null) {
                n.setInventoryQuantity(0D);
            }
            if (n.getAllocatedQuantity() == null) {
                n.setAllocatedQuantity(0D);
            }
            n.setReferenceField4(n.getInventoryQuantity() + n.getAllocatedQuantity());
        });
        return results;
    }

    @Transactional
    public List<InventoryImpl> findInventoryNew(SearchInventory searchInventory)
            throws ParseException {

        List<InventoryImpl> results = inventoryRepository.findInventory(
                searchInventory.getWarehouseId(),
                searchInventory.getPackBarcodes(),
                searchInventory.getItemCode(),
                searchInventory.getStorageBin(),
                searchInventory.getStorageSectionId(),
                searchInventory.getStockTypeId(),
                searchInventory.getSpecialStockIndicatorId(),
                searchInventory.getBinClassId(),
                searchInventory.getDescription()
        );
//		return results.collect(Collectors.toList());
        return results;
    }

    /**
     * @param searchInventory
     * @return
     * @throws ParseException
     */
    public List<Inventory> getQuantityValidatedInventory(SearchInventory searchInventory)
            throws ParseException {
        InventorySpecification spec = new InventorySpecification(searchInventory);
        List<Inventory> results = inventoryRepository.findAll(spec);
        if (!results.isEmpty()) {
            results = results.stream().filter(x -> (x.getInventoryQuantity() != null && x.getInventoryQuantity() != 0) && (x.getAllocatedQuantity() != null && x.getAllocatedQuantity() != 0)).collect(Collectors.toList());
        }
        return results;
    }

    /**
     * createInventory
     *
     * @param newInventory
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Inventory createInventory(AddInventory newInventory, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Inventory dbInventory = new Inventory();
        log.info("newInventory : " + newInventory);
        BeanUtils.copyProperties(newInventory, dbInventory, CommonUtils.getNullPropertyNames(newInventory));
        dbInventory.setDeletionIndicator(0L);
        dbInventory.setCreatedBy(loginUserID);
        dbInventory.setCreatedOn(new Date());
        return inventoryRepository.save(dbInventory);
    }

    /**
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param storageBin
     * @param stockTypeId
     * @param specialStockIndicatorId
     * @param loginUserID
     * @param updateInventory
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Inventory updateInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin,
                                     Long stockTypeId, Long specialStockIndicatorId, UpdateInventory updateInventory, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        Inventory dbInventory = getInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId);

        //* ------------------------------------Audit Log----------------------------------------------------------------
        // PALLET_CODE
		/*if (updateInventory.getPalletCode() != null && updateInventory.getPalletCode() != dbInventory.getPalletCode()) {
//			log.info("Inserting Audit log for PALLET_CODE");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
								warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"PAL_CODE", dbInventory.getPalletCode(), updateInventory.getPalletCode(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// CASE_CODE
		if (updateInventory.getCaseCode() != null && updateInventory.getCaseCode() != dbInventory.getCaseCode()) {
//			log.info("Inserting Audit log for CASE_CODE");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
								warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"CASE_CODE", dbInventory.getCaseCode(), updateInventory.getCaseCode(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// VAR_ID
		if (updateInventory.getVariantCode() != null && updateInventory.getVariantCode() != dbInventory.getVariantCode()) {
//			log.info("Inserting Audit log for VAR_ID");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
								warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"VAR_ID", String.valueOf(dbInventory.getVariantCode()), String.valueOf(updateInventory.getVariantCode()),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// VAR_SUB_ID
		if (updateInventory.getVariantSubCode() != null && updateInventory.getVariantSubCode() != dbInventory.getVariantSubCode()) {
//			log.info("Inserting Audit log for VAR_SUB_ID");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"VAR_SUB_ID", dbInventory.getVariantSubCode(), updateInventory.getVariantSubCode(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// STR_NO
		if (updateInventory.getBatchSerialNumber() != null && updateInventory.getBatchSerialNumber() != dbInventory.getBatchSerialNumber()) {
//			log.info("Inserting Audit log for STR_NO");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"STR_NO", dbInventory.getBatchSerialNumber(), updateInventory.getBatchSerialNumber(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// STCK_TYP_ID
		if (updateInventory.getStockTypeId() != null && updateInventory.getStockTypeId() != dbInventory.getStockTypeId()) {
//			log.info("Inserting Audit log for STCK_TYP_ID");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"STCK_TYP_ID", String.valueOf(dbInventory.getStockTypeId()), String.valueOf(updateInventory.getStockTypeId()),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// REF_ORD_NO
		if (updateInventory.getReferenceOrderNo() != null && updateInventory.getReferenceOrderNo() != dbInventory.getReferenceOrderNo()) {
//			log.info("Inserting Audit log for REF_ORD_NO");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"REF_ORD_NO", dbInventory.getReferenceOrderNo(), updateInventory.getReferenceOrderNo(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// STR_MTD
		if (updateInventory.getStorageMethod() != null && updateInventory.getStorageMethod() != dbInventory.getStorageMethod()) {
//			log.info("Inserting Audit log for STR_MTD");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"STR_MTD", String.valueOf(dbInventory.getStorageMethod()), updateInventory.getStorageMethod(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// BIN_CL_ID
		if (updateInventory.getBinClassId() != null && updateInventory.getBinClassId() != dbInventory.getBinClassId()) {
//			log.info("Inserting Audit log for BIN_CL_ID");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"BIN_CL_ID", String.valueOf(dbInventory.getBinClassId()), String.valueOf(updateInventory.getBinClassId()),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// TEXT
		if (updateInventory.getDescription() != null && updateInventory.getDescription() != dbInventory.getDescription()) {
//			log.info("Inserting Audit log for TEXT");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"TEXT", dbInventory.getDescription(), updateInventory.getDescription(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// INV_QTY
		if (updateInventory.getInventoryQuantity() != null && updateInventory.getInventoryQuantity() != dbInventory.getInventoryQuantity()) {
//			log.info("Inserting Audit log for INV_QTY");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"INV_QTY", String.valueOf(dbInventory.getInventoryQuantity()), String.valueOf(updateInventory.getInventoryQuantity()),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// ALLOC_QTY
		if (updateInventory.getAllocatedQuantity() != null && updateInventory.getAllocatedQuantity() != dbInventory.getAllocatedQuantity()) {
//			log.info("Inserting Audit log for ALLOC_QTY");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"ALLOC_QTY", String.valueOf(dbInventory.getAllocatedQuantity()), String.valueOf(updateInventory.getAllocatedQuantity()),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// INV_UOM
		if (updateInventory.getInventoryUom() != null && updateInventory.getInventoryUom() != dbInventory.getInventoryUom()) {
//			log.info("Inserting Audit log for INV_UOM");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"INV_UOM", dbInventory.getInventoryUom(), updateInventory.getInventoryUom(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// MFR_DATE
		if (updateInventory.getManufacturerDate() != null && updateInventory.getManufacturerDate() != dbInventory.getManufacturerDate()) {
//			log.info("Inserting Audit log for MFR_DATE");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"MFR_DATE", String.valueOf(dbInventory.getManufacturerDate()), String.valueOf(updateInventory.getManufacturerDate()),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// EXP_DATE
		if (updateInventory.getExpiryDate() != null && updateInventory.getExpiryDate() != dbInventory.getExpiryDate()) {
//			log.info("Inserting Audit log for EXP_DATE");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"EXP_DATE", String.valueOf(dbInventory.getExpiryDate()), String.valueOf(updateInventory.getExpiryDate()),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// REF_FIELD_5
		if (updateInventory.getReferenceField5() != null && updateInventory.getReferenceField5() != dbInventory.getReferenceField5()) {
//			log.info("Inserting Audit log for REF_FIELD_5");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"REF_FIELD_5", dbInventory.getReferenceField5(), updateInventory.getReferenceField5(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// REF_FIELD_6
		if (updateInventory.getReferenceField6() != null && updateInventory.getReferenceField6() != dbInventory.getReferenceField6()) {
//			log.info("Inserting Audit log for REF_FIELD_6");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"REF_FIELD_6", dbInventory.getReferenceField6(), updateInventory.getReferenceField6(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// REF_FIELD_7
		if (updateInventory.getReferenceField7() != null && updateInventory.getReferenceField7() != dbInventory.getReferenceField7()) {
//			log.info("Inserting Audit log for REF_FIELD_7");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"REF_FIELD_7", dbInventory.getReferenceField7(), updateInventory.getReferenceField7(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// REF_FIELD_8
		if (updateInventory.getReferenceField8() != null && updateInventory.getReferenceField8() != dbInventory.getReferenceField8()) {
//			log.info("Inserting Audit log for REF_FIELD_8");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"REF_FIELD_8", dbInventory.getReferenceField8(), updateInventory.getReferenceField8(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// REF_FIELD_9
		if (updateInventory.getReferenceField9() != null && updateInventory.getReferenceField9() != dbInventory.getReferenceField9()) {
//			log.info("Inserting Audit log for REF_FIELD_9");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"REF_FIELD_9", dbInventory.getReferenceField9(), updateInventory.getReferenceField9(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}

		// REF_FIELD_10
		if (updateInventory.getReferenceField10() != null && updateInventory.getReferenceField10() != dbInventory.getReferenceField10()) {
//			log.info("Inserting Audit log for REF_FIELD_10");
			createAuditLogRecord(dbInventory.getCompanyCodeId(), dbInventory.getPlantId(),
					warehouseId, loginUserID, "INVENTORY", "INVENTORY",
					"REF_FIELD_10", dbInventory.getReferenceField10(), updateInventory.getReferenceField10(),
					packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId,"");
		}*/
        //* --------------------------------------------------------------------------------------------------------------------------------------*//

        /*--------------------------------------Inventory Movement Create ---------------------------------------------------------*/
		/* Inventory Movement will be created only when change in total quantity
			i.e., INV_QTY+ALLOC_QTY and binClassId == 1; otherwise no record should be created */

        if (updateInventory.getInventoryQuantity() == null) {
            updateInventory.setInventoryQuantity(0D);
        }
        if (updateInventory.getAllocatedQuantity() == null) {
            updateInventory.setAllocatedQuantity(0D);
        }
        if (dbInventory.getAllocatedQuantity() == null) {
            dbInventory.setAllocatedQuantity(0D);
        }
        if (dbInventory.getInventoryQuantity() == null) {
            dbInventory.setInventoryQuantity(0D);
        }

        Double newTotalQuantity = updateInventory.getInventoryQuantity() + updateInventory.getAllocatedQuantity();
        Double dbTotalQuantity = dbInventory.getInventoryQuantity() + dbInventory.getAllocatedQuantity();
        log.info("newTotalQuantity: " + newTotalQuantity + "dbTotalQuantity: " + dbTotalQuantity);
        if (!newTotalQuantity.equals(dbTotalQuantity) && dbInventory.getBinClassId() == 1) {

            InventoryMovement dbInventoryMovement = new InventoryMovement();

            String movementDocumentNumber;
            if (inventoryRepository.findMovementDocumentNo() != null) {
                movementDocumentNumber = inventoryRepository.findMovementDocumentNo();
            } else {
                movementDocumentNumber = "1";
            }

            dbInventoryMovement.setLanguageId(dbInventory.getLanguageId());
            dbInventoryMovement.setCompanyCodeId(dbInventory.getCompanyCodeId());
            dbInventoryMovement.setPlantId(dbInventory.getPlantId());
            dbInventoryMovement.setWarehouseId(dbInventory.getWarehouseId());
            dbInventoryMovement.setMovementType(4L);
            dbInventoryMovement.setSubmovementType(1L);
            dbInventoryMovement.setPalletCode(dbInventory.getPalletCode());
            dbInventoryMovement.setCaseCode(dbInventory.getCaseCode());
            dbInventoryMovement.setPackBarcodes(dbInventory.getPackBarcodes());
            dbInventoryMovement.setItemCode(dbInventory.getItemCode());
            dbInventoryMovement.setVariantCode(dbInventory.getVariantCode());
            dbInventoryMovement.setVariantSubCode(dbInventory.getVariantSubCode());
            dbInventoryMovement.setBatchSerialNumber("1");
//			dbInventoryMovement.setMovementDocumentNo("1");
            dbInventoryMovement.setMovementDocumentNo(movementDocumentNumber);
            dbInventoryMovement.setManufacturerName(dbInventory.getReferenceField9()); //Inventory Ref_Field_9 - ManufacturePartNo
            dbInventoryMovement.setStorageBin(dbInventory.getStorageBin());
            dbInventoryMovement.setStorageMethod(dbInventory.getStorageMethod());
            dbInventoryMovement.setDescription(dbInventory.getReferenceField8());
            dbInventoryMovement.setStockTypeId(dbInventory.getStockTypeId());
            dbInventoryMovement.setSpecialStockIndicator(dbInventory.getSpecialStockIndicatorId());
            if (newTotalQuantity < dbTotalQuantity) {
                dbInventoryMovement.setMovementQtyValue("N");
                log.info("MovementQtyValue: " + dbInventoryMovement.getMovementQtyValue());
            } else {
                dbInventoryMovement.setMovementQtyValue("P");
                log.info("MovementQtyValue: " + dbInventoryMovement.getMovementQtyValue());
            }
            dbInventoryMovement.setMovementQty(newTotalQuantity - dbTotalQuantity);
            dbInventoryMovement.setBalanceOHQty(newTotalQuantity);
            dbInventoryMovement.setInventoryUom(dbInventory.getInventoryUom());
//			dbInventoryMovement.setRefDocNumber("");
            dbInventoryMovement.setDeletionIndicator(dbInventory.getDeletionIndicator());
            dbInventoryMovement.setCreatedBy(dbInventory.getUpdatedBy());
            dbInventoryMovement.setCreatedOn(new Date());
            log.info("Inventory Movement: " + dbInventoryMovement);
            inventoryMovementRepository.save(dbInventoryMovement);
        }

        BeanUtils.copyProperties(updateInventory, dbInventory, CommonUtils.getNullPropertyNames(updateInventory));
        dbInventory.setUpdatedBy(loginUserID);
        dbInventory.setUpdatedOn(new Date());
        return inventoryRepository.save(dbInventory);
    }

    /**
     * deleteInventory
     *
     * @param stockTypeId
     */
    public void deleteInventory(String warehouseId, String packBarcodes, String itemCode, String storageBin, Long stockTypeId,
                                Long specialStockIndicatorId) {
        Inventory inventory = getInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId);
        if (inventory != null) {
            inventory.setDeletionIndicator(1L);
            inventoryRepository.save(inventory);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + stockTypeId);
        }
    }

    /**
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     */
    public boolean deleteInventory(String warehouseId, String packBarcodes, String itemCode) {
        try {
            List<Inventory> inventoryList = getInventoryForDelete(warehouseId, packBarcodes, itemCode);
            log.info("inventoryList : " + inventoryList);
            if (inventoryList != null) {
                for (Inventory inventory : inventoryList) {
                    inventoryRepository.delete(inventory);
                    log.info("inventory deleted.");
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new EntityNotFoundException("Error in deleting Id: " + e.toString());
        }
    }

    //============================================================V2=============================================================


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForInvMmt(String companyCode, String plantId, String languageId, String warehouseId,
                                                      String itemCode, String manufacturerName, Long binClassId) {

        List<IInventoryImpl> inventory = inventoryV2Repository.
                findInventoryForInventoryMovement(companyCode, plantId, languageId, warehouseId, itemCode, binClassId, manufacturerName);

        if (inventory == null || inventory.isEmpty()) {
            return null;
//            throw new BadRequestException("The given PreInboundHeader ID : " +
//                    ", warehouseId: " + warehouseId +
//                    ", itemCode: " + itemCode +
//                    ", binClassId: " + binClassId +
//                    " doesn't exist.");
        }
        return inventory;
    }


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<InventoryV2> getInventoryForInhouseTransferV2(String companyCode, String plantId, String languageId,
                                                              String warehouseId, String itemCode, String manufacturerName) {
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        itemCode,
                        manufacturerName,
                        0L
                );
        return inventory;
    }


    /**
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
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryForInhouseTransferV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                        String packBarcodes, String itemCode, String manufacturerName, String storageBin) {
        try {
            log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
            List<InventoryV2> inventory =
                    inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
                            languageId,
                            companyCode,
                            plantId,
                            warehouseId,
                            packBarcodes,
                            itemCode,
                            manufacturerName,
                            storageBin,
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
            throw new BadRequestException("Error While Inventory Get : " + e);
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


    /**
     * @param searchInventory
     * @return
     * @throws ParseException
     */
    public List<InventoryV2> findInventoryV2(SearchInventoryV2 searchInventory)
            throws ParseException {
        InventoryV2Specification spec = new InventoryV2Specification(searchInventory);
        List<InventoryV2> results = inventoryV2Repository.findAll(spec);
        return results;
    }

    /**
     * @param searchInventory
     * @return
     * @throws ParseException
     */
    public List<IInventoryImpl> findInventoryNewV2(SearchInventoryV2 searchInventory)
            throws ParseException {

        log.info("SearchInventory Input: " + searchInventory);
        if (searchInventory.getCompanyCodeId() == null || searchInventory.getCompanyCodeId().isEmpty()) {
            searchInventory.setCompanyCodeId(null);
        }
        if (searchInventory.getPlantId() == null || searchInventory.getPlantId().isEmpty()) {
            searchInventory.setPlantId(null);
        }
        if (searchInventory.getLanguageId() == null || searchInventory.getLanguageId().isEmpty()) {
            searchInventory.setLanguageId(null);
        }
        if (searchInventory.getWarehouseId() == null || searchInventory.getWarehouseId().isEmpty()) {
            searchInventory.setWarehouseId(null);
        }
        if (searchInventory.getReferenceDocumentNo() == null || searchInventory.getReferenceDocumentNo().isEmpty()) {
            searchInventory.setReferenceDocumentNo(null);
        }
        if (searchInventory.getBarcodeId() == null || searchInventory.getBarcodeId().isEmpty()) {
            searchInventory.setBarcodeId(null);
        }
        if (searchInventory.getManufacturerCode() == null || searchInventory.getManufacturerCode().isEmpty()) {
            searchInventory.setManufacturerCode(null);
        }
        if (searchInventory.getManufacturerName() == null || searchInventory.getManufacturerName().isEmpty()) {
            searchInventory.setManufacturerName(null);
        }
        if (searchInventory.getPackBarcodes() == null || searchInventory.getPackBarcodes().isEmpty()) {
            searchInventory.setPackBarcodes(null);
        }
        if (searchInventory.getItemCode() == null || searchInventory.getItemCode().isEmpty()) {
            searchInventory.setItemCode(null);
        }
        if (searchInventory.getStorageBin() == null || searchInventory.getStorageBin().isEmpty()) {
            searchInventory.setStorageBin(null);
        }
        if (searchInventory.getStockTypeId() == null || searchInventory.getStockTypeId().isEmpty()) {
            searchInventory.setStockTypeId(null);
        }
        if (searchInventory.getStorageSectionId() == null || searchInventory.getStorageSectionId().isEmpty()) {
            searchInventory.setStorageSectionId(null);
        }
        if (searchInventory.getSpecialStockIndicatorId() == null || searchInventory.getSpecialStockIndicatorId().isEmpty()) {
            searchInventory.setSpecialStockIndicatorId(null);
        }
        if (searchInventory.getLevelId() == null || searchInventory.getLevelId().isEmpty()) {
            searchInventory.setLevelId(null);
        }
        if (searchInventory.getBinClassId() == null || searchInventory.getBinClassId().isEmpty()) {
            searchInventory.setBinClassId(null);
        }
        if (searchInventory.getDescription() == null || searchInventory.getDescription().isEmpty()) {
            searchInventory.setDescription(null);
        }


        List<IInventoryImpl> results = inventoryV2Repository.findInventoryNew(
                searchInventory.getCompanyCodeId(),
                searchInventory.getLanguageId(),
                searchInventory.getPlantId(),
                searchInventory.getWarehouseId(),
                searchInventory.getReferenceDocumentNo(),
                searchInventory.getBarcodeId(),
                searchInventory.getManufacturerCode(),
                searchInventory.getManufacturerName(),
                searchInventory.getPackBarcodes(),
                searchInventory.getItemCode(),
                searchInventory.getStorageBin(),
                searchInventory.getDescription(),
                searchInventory.getStockTypeId(),
                searchInventory.getStorageSectionId(),
                searchInventory.getLevelId(),
                searchInventory.getSpecialStockIndicatorId(),
                searchInventory.getBinClassId());
        log.info("Inventory results: " + results.size());
        return results;
    }


    /**
     * @param searchInventory
     * @return
     * @throws ParseException
     */
    public List<IInventoryImpl> findInventoryV4(SearchInventoryV2 searchInventory) throws Exception {

        log.info("SearchInventory Input: " + searchInventory);
        if (searchInventory.getCompanyCodeId() == null || searchInventory.getCompanyCodeId().isEmpty()) {
            searchInventory.setCompanyCodeId(null);
        }
        if (searchInventory.getPlantId() == null || searchInventory.getPlantId().isEmpty()) {
            searchInventory.setPlantId(null);
        }
        if (searchInventory.getLanguageId() == null || searchInventory.getLanguageId().isEmpty()) {
            searchInventory.setLanguageId(null);
        }
        if (searchInventory.getWarehouseId() == null || searchInventory.getWarehouseId().isEmpty()) {
            searchInventory.setWarehouseId(null);
        }
        if (searchInventory.getReferenceDocumentNo() == null || searchInventory.getReferenceDocumentNo().isEmpty()) {
            searchInventory.setReferenceDocumentNo(null);
        }
        if (searchInventory.getBarcodeId() == null || searchInventory.getBarcodeId().isEmpty()) {
            searchInventory.setBarcodeId(null);
        }
        if (searchInventory.getManufacturerCode() == null || searchInventory.getManufacturerCode().isEmpty()) {
            searchInventory.setManufacturerCode(null);
        }
        if (searchInventory.getManufacturerName() == null || searchInventory.getManufacturerName().isEmpty()) {
            searchInventory.setManufacturerName(null);
        }
        if (searchInventory.getPackBarcodes() == null || searchInventory.getPackBarcodes().isEmpty()) {
            searchInventory.setPackBarcodes(null);
        }
        if (searchInventory.getItemCode() == null || searchInventory.getItemCode().isEmpty()) {
            searchInventory.setItemCode(null);
        }
        if (searchInventory.getStorageBin() == null || searchInventory.getStorageBin().isEmpty()) {
            searchInventory.setStorageBin(null);
        }
        if (searchInventory.getStockTypeId() == null || searchInventory.getStockTypeId().isEmpty()) {
            searchInventory.setStockTypeId(null);
        }
        if (searchInventory.getStorageSectionId() == null || searchInventory.getStorageSectionId().isEmpty()) {
            searchInventory.setStorageSectionId(null);
        }
        if (searchInventory.getSpecialStockIndicatorId() == null || searchInventory.getSpecialStockIndicatorId().isEmpty()) {
            searchInventory.setSpecialStockIndicatorId(null);
        }
        if (searchInventory.getLevelId() == null || searchInventory.getLevelId().isEmpty()) {
            searchInventory.setLevelId(null);
        }
        if (searchInventory.getItemType() != null && searchInventory.getItemType().isEmpty()) {
            searchInventory.setItemType(null);
        }
        if (searchInventory.getBinClassId() == null || searchInventory.getBinClassId().isEmpty()) {
            searchInventory.setBinClassId(null);
        }
        if (searchInventory.getDescription() == null || searchInventory.getDescription().isEmpty()) {
            searchInventory.setDescription(null);
        }
        if (searchInventory.getPartnerCode() == null || searchInventory.getPartnerCode().isEmpty()) {
            searchInventory.setPartnerCode(null);
        }

        if (searchInventory.getMaterialNo() != null && searchInventory.getMaterialNo().isEmpty()) {
            searchInventory.setMaterialNo(null);
        }
        if (searchInventory.getPriceSegment() != null && searchInventory.getPriceSegment().isEmpty()) {
            searchInventory.setPriceSegment(null);
        }
        if (searchInventory.getArticleNo() != null && searchInventory.getArticleNo().isEmpty()) {
            searchInventory.setArticleNo(null);
        }
        if (searchInventory.getGender() != null && searchInventory.getGender().isEmpty()) {
            searchInventory.setGender(null);
        }
        if (searchInventory.getColor() != null && searchInventory.getColor().isEmpty()) {
            searchInventory.setColor(null);
        }
        if (searchInventory.getSize() != null && searchInventory.getSize().isEmpty()) {
            searchInventory.setSize(null);
        }
        if (searchInventory.getNoPairs() != null && searchInventory.getNoPairs().isEmpty()) {
            searchInventory.setNoPairs(null);
        }
        if (searchInventory.getAlternateUom() != null && searchInventory.getAlternateUom().isEmpty()) {
            searchInventory.setAlternateUom(null);
        }
        List<IInventoryImpl> results = inventoryV2Repository.findInventoryV4(
                searchInventory.getCompanyCodeId(),
                searchInventory.getPlantId(),
                searchInventory.getLanguageId(),
                searchInventory.getWarehouseId(),
                searchInventory.getBarcodeId(),
                searchInventory.getManufacturerCode(),
                searchInventory.getManufacturerName(),
                searchInventory.getPackBarcodes(),
                searchInventory.getItemCode(),
                searchInventory.getStorageBin(),
                searchInventory.getDescription(),
                searchInventory.getStockTypeId(),
                searchInventory.getStorageSectionId(),
                searchInventory.getLevelId(),
                searchInventory.getPartnerCode(),
                searchInventory.getSpecialStockIndicatorId(),
                searchInventory.getItemType(),
                searchInventory.getSize(),
                searchInventory.getBinClassId(),
                searchInventory.getAlternateUom());
        log.info("Inventory results: " + results.size());
        return results;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param storageBin
     * @param stockTypeId
     * @param specialStockIndicatorId
     * @param updateInventory
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InventoryV2 updateInventoryV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                         String packBarcodes, String itemCode, String manufacturerName,
                                         String storageBin, Long stockTypeId, Long specialStockIndicatorId,
                                         InventoryV2 updateInventory, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InventoryV2 dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicatorOrderByInventoryIdDesc(
                companyCodeId, plantId, languageId, warehouseId, packBarcodes, itemCode, manufacturerName, storageBin, stockTypeId, specialStockIndicatorId, 0L);
        log.info("Inventory for Update: " + dbInventory);
        if (dbInventory != null) {
            InventoryV2 newInventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
            BeanUtils.copyProperties(updateInventory, newInventory, CommonUtils.getNullPropertyNames(updateInventory));
            newInventory.setUpdatedBy(loginUserID);
            newInventory.setUpdatedOn(new Date());
            return inventoryV2Repository.save(newInventory);
        }
        return null;
    }


    /**
     * @param newInventory
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InventoryV2 createInventoryV2(InventoryV2 newInventory, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InventoryV2 dbInventory = new InventoryV2();
        log.info("newInventory : " + newInventory);
        BeanUtils.copyProperties(newInventory, dbInventory, CommonUtils.getNullPropertyNames(newInventory));
        dbInventory.setDeletionIndicator(0L);
        dbInventory.setCreatedBy(loginUserID);
        dbInventory.setCreatedOn(new Date());
        return inventoryV2Repository.save(dbInventory);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public InventoryV2 getInventoryForReversalV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String packBarcodes, String itemCode, String manufacturerName) {
        InventoryV2 inventory =
                inventoryV2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        manufacturerName,
                        3L,
                        0L
                );
//        log.info("Inventory: " + inventory);
        return inventory;
    }


    /**
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     */
    public boolean deleteInventoryV2(String companyCodeId, String plantId, String languageId, String warehouseId, Long stockTypeId, Long specialStockIndicatorId,
                                     String packBarcodes, String itemCode, String manufacturerName, String storageBin, String loginUserId) {
        try {
            List<InventoryV2> inventoryList = inventoryV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, packBarcodes, itemCode, manufacturerName, storageBin, stockTypeId, specialStockIndicatorId, 0L);
            log.info("inventoryList for Delete: " + inventoryList);
            if (inventoryList != null) {
                for (InventoryV2 inventory : inventoryList) {
                    inventory.setDeletionIndicator(1L);
                    inventory.setUpdatedOn(new Date());
                    inventory.setUpdatedBy(loginUserId);
                    inventoryV2Repository.save(inventory);
//                    inventoryV2Repository.delete(inventory);
                    log.info("inventory deleted.");
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new EntityNotFoundException("Error in deleting Id: " + e.toString());
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param grLine
     * @return
     */
    public InventoryV2 deleteInventoryInvoiceCancellation(String companyCodeId, String plantId, String languageId,
                                                          String warehouseId, GrLineV2 grLine) {
        InventoryV2 dbInventory =
                inventoryV2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        "99999",
                        grLine.getItemCode(),
                        grLine.getManufacturerName(),
                        3L,
                        0L
                );
        log.info("Inventory - Cancellation : " + dbInventory);
        InventoryV2 newInventory = new InventoryV2();
        if (dbInventory != null) {
            BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
            newInventory.setInventoryQuantity(dbInventory.getInventoryQuantity() - grLine.getGoodReceiptQty());
            newInventory.setReferenceField4(dbInventory.getReferenceField4() - grLine.getGoodReceiptQty());
            newInventory.setCreatedOn(new Date());
            inventoryV2Repository.save(newInventory);
        }
        return newInventory;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForPutAwayCreate(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String itemCode, String manufacturerName, Long binClassId) {

        List<IInventoryImpl> inventory = inventoryV2Repository.
//                inventoryForPutAway(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId);
        inventoryForPutAwaytemp(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId);
        return inventory;
    }

    @Transactional
    public InventoryV2 createInventoryNonCBMV2(GrLineV2 createdGRLine) {

        try {
            InventoryV2 dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                    createdGRLine.getCompanyCode(),
                    createdGRLine.getPlantId(),
                    createdGRLine.getLanguageId(),
                    createdGRLine.getWarehouseId(),
                    createdGRLine.getItemCode(),
                    createdGRLine.getManufacturerName(),
                    "99999", 3L, 0L);

            InventoryV2 createdinventory = null;

            if (dbInventory != null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
                inventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty());
                log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                Double totalQty = 0D;
                if (inventory.getReferenceField4() != null) {
                    totalQty = inventory.getReferenceField4() + createdGRLine.getGoodReceiptQty();
                }
                if (inventory.getReferenceField4() == null) {
                    totalQty = createdGRLine.getGoodReceiptQty();
                }
                inventory.setReferenceField4(totalQty);
                log.info("Total Inventory Qty : " + totalQty);
                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory[Existing] : " + createdinventory);
                } catch (Exception e) {
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }

            if (dbInventory == null) {

                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(createdGRLine, inventory, CommonUtils.getNullPropertyNames(createdGRLine));

                inventory.setCompanyCodeId(createdGRLine.getCompanyCode());

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);
                inventory.setVariantSubCode("1");
                inventory.setStorageMethod("1");
                inventory.setBatchSerialNumber("1");
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setBinClassId(3L);
                inventory.setDeletionIndicator(0L);
                inventory.setManufacturerCode(createdGRLine.getManufacturerName());
                inventory.setManufacturerName(createdGRLine.getManufacturerName());

                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
                log.info("StorageBin input : companyCode --> " + createdGRLine.getCompanyCode() + ", plantId ---> " + createdGRLine.getPlantId() +
                        ", languageId ---> " + createdGRLine.getLanguageId() + ", warehouseId ---> " + createdGRLine.getWarehouseId());
                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                StorageBin storageBin = mastersService.getStorageBin(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(), 3L, authTokenForMastersService.getAccess_token());
                log.info("storageBin: " + storageBin);
                inventory.setStorageBin(storageBin.getStorageBin());

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(createdGRLine.getCompanyCode());
                imBasicData.setPlantId(createdGRLine.getPlantId());
                imBasicData.setLanguageId(createdGRLine.getLanguageId());
                imBasicData.setWarehouseId(createdGRLine.getWarehouseId());
                imBasicData.setItemCode(createdGRLine.getItemCode());
                imBasicData.setManufacturerName(createdGRLine.getManufacturerName());
                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImbasicData1 : " + itemCodeCapacityCheck);

                if (itemCodeCapacityCheck != null) {
                    inventory.setReferenceField8(itemCodeCapacityCheck.getDescription());
                    inventory.setReferenceField9(itemCodeCapacityCheck.getManufacturerPartNo());
                    inventory.setDescription(itemCodeCapacityCheck.getDescription());
                }
                if (storageBin != null) {
                    inventory.setReferenceField10(storageBin.getStorageSectionId());
                    inventory.setReferenceField5(storageBin.getAisleNumber());
                    inventory.setReferenceField6(storageBin.getShelfId());
                    inventory.setReferenceField7(storageBin.getRowId());
                    inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                }

                // STCK_TYP_ID
                inventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), 1L);
                inventory.setStockTypeDescription(stockTypeDesc);

                // SP_ST_IND_ID
                inventory.setSpecialStockIndicatorId(1L);

                // INV_QTY
                if (dbInventory != null) {
                    inventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty());
                    log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(inventory.getInventoryQuantity());
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                if (dbInventory == null) {
                    inventory.setInventoryQuantity(createdGRLine.getGoodReceiptQty());
                    log.info("Inventory Qty = gr_qty: " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(inventory.getInventoryQuantity());
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                //packbarcode
                /*
                 * Hardcoding Packbarcode as 99999
                 */
//            inventory.setPackBarcodes(createdGRLine.getPackBarcodes());
                inventory.setPackBarcodes("99999");
                inventory.setReferenceField1(createdGRLine.getPackBarcodes());

                // INV_UOM
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setCreatedBy(createdGRLine.getCreatedBy());

                //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());

                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory : " + createdinventory);
                } catch (Exception e) {
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
//            createGrLineLog7(createdGRLine, e.toString());

            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param alternateUom
     * @param bagSize
     * @param binClassId
     * @return
     */
    public synchronized List<String> getPutAwayHeaderCreateInventoryV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                          String itemCode, String manufacturerName, String alternateUom, Double bagSize, Long binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + bagSize + "|" + binClassId);
        return inventoryV2Repository.getPutAwayHeaderCreateInventoryV4(companyCodeId, plantId, languageId, warehouseId,
                null, null, itemCode, manufacturerName,
                null, binClassId, null);
    }

    /**
     * @param createdGRLine
     * @return
     */
    public InventoryV2 createInventoryNonCBMV4(String companyCode, String plantId, String languageId,
                                               String warehouseId, String itemCode, String manufacturerName,
                                               String refDocNumber, GrLineV2 createdGRLine) {
        try {
            InventoryV2 createdinventory = null;
            InventoryV2 dbInventory = getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName,
                    createdGRLine.getBarcodeId(), createdGRLine.getAlternateUom());

            if (dbInventory != null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));

                Double INV_QTY = dbInventory.getInventoryQuantity() != null ? dbInventory.getInventoryQuantity() : 0D;
                Double ALLOC_QTY = dbInventory.getAllocatedQuantity() != null ? dbInventory.getAllocatedQuantity() : 0D;

                log.info("Before - Inventory - INV_QTY,ALLOC_QTY,TOT_QTY,GrQty: " + INV_QTY + ", " + ALLOC_QTY + ", " + dbInventory.getReferenceField4() + ", " + createdGRLine.getGoodReceiptQty() + ", " + dbInventory.getNoBags());
                double physicalQty = createdGRLine.getGoodReceiptQty();
                INV_QTY = INV_QTY + physicalQty;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;

                double BAG_SIZE = dbInventory.getBagSize() != null ? dbInventory.getBagSize() : 0D;
                double NO_OF_BAGS = TOT_QTY / BAG_SIZE;
                double CASE_QTY = createdGRLine.getBagSize() / NO_OF_BAGS;

                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setCaseQty(round(CASE_QTY));
                if (inventory.getBagSize() > inventory.getInventoryQuantity()) {
                    inventory.setLoosePack(true);
                } else {
                    inventory.setLoosePack(false);
                }
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(roundUp(NO_OF_BAGS));
                inventory.setBagSize(createdGRLine.getBagSize());
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setAlternateUom(createdGRLine.getAlternateUom());
                log.info("After - Inventory - INV_QTY,ALLOC_QTY,TOT_QTY: " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + inventory.getNoBags());
                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setManufacturerDate(createdGRLine.getManufacturerDate());
                inventory.setExpiryDate(createdGRLine.getExpiryDate());
                inventory.setBusinessPartnerCode(createdGRLine.getBusinessPartnerCode());
                inventory.setReferenceDocumentNo(refDocNumber);
                inventory.setReferenceOrderNo(refDocNumber);
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory[Existing] : " + createdinventory);
                } catch (Exception e1) {
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }


            if (dbInventory == null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(createdGRLine, inventory, CommonUtils.getNullPropertyNames(createdGRLine));
                inventory.setCompanyCodeId(companyCode);

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);
                inventory.setVariantSubCode("1");
                inventory.setStorageMethod("1");
                if (createdGRLine.getBatchSerialNumber() != null) {
                    inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                    inventory.setPackBarcodes(createdGRLine.getBatchSerialNumber());
                } else {
                    inventory.setBatchSerialNumber("1");
                    inventory.setPackBarcodes(PACK_BARCODE);
                }
                inventory.setBinClassId(3L);
                inventory.setDeletionIndicator(0L);
                inventory.setManufacturerCode(manufacturerName);
                inventory.setManufacturerName(manufacturerName);
                inventory.setReferenceField8(createdGRLine.getItemDescription());
                inventory.setReferenceField9(manufacturerName);
                inventory.setDescription(createdGRLine.getItemDescription());

                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
                StorageBinV2 storageBin = storageBinService.getStorageBinByBinClassIdV2(warehouseId, 3L, companyCode, plantId, languageId);
                log.info("storageBin: " + storageBin);

                if (storageBin != null) {
                    inventory.setStorageBin(storageBin.getStorageBin());
                    inventory.setReferenceField10(storageBin.getStorageSectionId());
                    inventory.setStorageSectionId(storageBin.getStorageSectionId());
                    inventory.setReferenceField5(storageBin.getAisleNumber());
                    inventory.setReferenceField6(storageBin.getShelfId());
                    inventory.setReferenceField7(storageBin.getRowId());
                    inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                }

                // STCK_TYP_ID
                inventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
                inventory.setStockTypeDescription(stockTypeDesc);

                // SP_ST_IND_ID
                inventory.setSpecialStockIndicatorId(1L);

//                double physicalQty = getQuantity(createdGRLine.getGoodReceiptQty(), createdGRLine.getBagSize());
                double physicalQty = createdGRLine.getGoodReceiptQty();

                Double INV_QTY = physicalQty;
                Double ALLOC_QTY = 0D;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;

                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(createdGRLine.getNoBags());
                inventory.setBagSize(createdGRLine.getBagSize());
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setAlternateUom(createdGRLine.getAlternateUom());
                log.info("New - Inventory - INV_QTY,ALLOC_QTY,TOT_QTY: " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + createdGRLine.getNoBags());
                /*
                 * Hardcoding Packbarcode as 99999
                 */
                inventory.setReferenceField1(createdGRLine.getPackBarcodes());

                // INV_UOM
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setCreatedBy(createdGRLine.getCreatedBy());

                //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
                inventory.setReferenceDocumentNo(refDocNumber);
                inventory.setReferenceOrderNo(refDocNumber);
                inventory.setBusinessPartnerCode(createdGRLine.getBusinessPartnerCode());
                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }

                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory : " + createdinventory);
                } catch (Exception e1) {
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
//            createGrLineLog7(createdGRLine, e.toString());

            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barCodeId
     * @param alternateUom
     * @return
     */
    public synchronized InventoryV2 getInventoryBinClassId3V4(String companyCode, String plantId, String languageId, String warehouseId,
                                                 String itemCode, String manufacturerName, String barCodeId, String alternateUom) {
        InventoryV2 dbInventoryV2 = new InventoryV2();
        IInventoryImpl inventory = inventoryV2Repository.getInboundInventoryV4(companyCode, plantId, languageId, warehouseId, barCodeId, null,
                itemCode, manufacturerName, PACK_BARCODE, null, 3L, alternateUom);
        if (inventory != null) {
            BeanUtils.copyProperties(inventory, dbInventoryV2, CommonUtils.getNullPropertyNames(inventory));
            return dbInventoryV2;
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @param alternateUom
     * @return
     */
    public synchronized InventoryV2 getOutboundInventoryV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                              String itemCode, String manufacturerName, String barcodeId, String storageBin, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName +
                "|" + alternateUom + "|" + barcodeId + "|" + storageBin);
        IInventoryImpl dbInventory = inventoryV2Repository.getOutboundInventoryV4(companyCodeId, plantId, languageId, warehouseId, barcodeId, null,
                itemCode, manufacturerName, PACK_BARCODE, storageBin, alternateUom);
        if (dbInventory == null) {
            dbInventory = inventoryV2Repository.getOutboundInventoryV4(companyCodeId, plantId, languageId, warehouseId, barcodeId, null,
                    itemCode, manufacturerName, PACK_BARCODE, storageBin, null);
        }
        if (dbInventory != null) {
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
            return inventory;
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
                                                                 String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.findInventoryForOrderManagementV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, stockTypeId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.findInventoryForOrderManagementV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, stockTypeId, null);
        }
        return inventoryList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByCreatedOnV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                                 String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByCreatedOn(companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByCreatedOn(companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
        }
        return inventoryList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByExpiryDateV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                                  String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByExpiryDate(companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByExpiryDate(companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
        }
        return inventoryList;
    }

    public synchronized List<IInventoryImpl> getInventoryForOrderManagementLevelAsscIdV6(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                            String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
                                                                            String alternateUom, Long levelId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId + "|" + levelId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryLevelAsscIdV6(
                companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom, levelId);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryLevelIdV6(
                    companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null, levelId);
        }
        return inventoryList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param storageBin
     * @param alternateUom
     * @return
     */
    public synchronized InventoryV2 getInventoryV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      String itemCode, String manufacturerName, String barcodeId, String storageBin, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName +
                "|" + alternateUom + "|" + barcodeId + "|" + storageBin);
        IInventoryImpl dbInventory = inventoryV2Repository.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, barcodeId, null,
                itemCode, manufacturerName, PACK_BARCODE, storageBin, alternateUom);
        if (dbInventory == null) {
            dbInventory = inventoryV2Repository.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, barcodeId, null,
                    itemCode, manufacturerName, PACK_BARCODE, storageBin, null);
        }
        if (dbInventory != null) {
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
            return inventory;
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventory> getInventoryForOrderManagementGroupByLevelIdV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                           String itemCode, Long stockTypeId, Long binClassId, String manufacturerName, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventory> inventoryList = inventoryV2Repository.findInventoryGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                manufacturerName, stockTypeId, binClassId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.findInventoryGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stockTypeId, binClassId, null);
        }
        return inventoryList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @param levelId
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementLevelIdV6(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                        String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
                                                                        String alternateUom, Long levelId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId + "|" + levelId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryLevelIdV6(
                companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom, levelId);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryLevelAsscIdV6(
                    companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null, levelId);
        }
        return inventoryList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByLevelIdV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                               String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByLevelId(companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByLevelId(companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
        }
        return inventoryList;
    }


    @Scheduled(cron = "0 0 0 * * ?")  // Every day at 12 AM
    @Transactional
    public void updateShelfLifeDaily() {
        DataBaseContextHolder.setCurrentDb("REEFERON");
        int updatedCount = inventoryRepository.updateShelfLife();
        log.info("Updated shelf life for " + updatedCount + " records at " + java.time.LocalDateTime.now());
    }

}