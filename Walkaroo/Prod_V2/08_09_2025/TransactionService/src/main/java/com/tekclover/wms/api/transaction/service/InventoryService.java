package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ParseException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auditlog.AuditLog;
import com.tekclover.wms.api.transaction.model.dto.ExcessConfirmation;
import com.tekclover.wms.api.transaction.model.dto.IInventory;
import com.tekclover.wms.api.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.errorlog.ErrorLog;
import com.tekclover.wms.api.transaction.model.impl.InventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.SearchInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.SearchInventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.transaction.repository.ErrorLogRepository;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.InventoryTransRepository;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.repository.specification.InventorySpecification;
import com.tekclover.wms.api.transaction.repository.specification.InventoryV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InventoryService extends BaseService {

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    StorageBinService storageBinService;

    //================================================================================================
    @Autowired
    InventoryV2Repository inventoryV2Repository;
    
    @Autowired
    InventoryTransRepository inventoryTransRepository;

    @Autowired
    ErrorLogRepository exceptionLogRepo;

//    boolean alreadyExecuted = true;
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
     *
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
            n.setReferenceField4(round(n.getInventoryQuantity() + n.getAllocatedQuantity()));
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

    /**
     * DeleteInventory
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBackParCode
     * @param refDocNumber
     * @param itemCode
     * @param loginUserID
     */
    public void deleteInventory(String companyCodeId, String plantId, String languageId, String warehouseId,
                                String packBackParCode, String refDocNumber, String itemCode, String loginUserID) {

        List<Inventory> dbInventory = inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndReferenceOrderNoAndDeletionIndicator(
                companyCodeId, languageId, plantId, warehouseId, itemCode, packBackParCode, refDocNumber, 0L);

        if (dbInventory != null) {
            for (Inventory inventory : dbInventory) {
                inventory.setDeletionIndicator(1L);
                inventory.setUpdatedBy(loginUserID);
                inventoryRepository.save(inventory);
            }
        } else {
            log.info("Error in deleting Id:  warehouseId:" + warehouseId +
                    ", companyCodeId :" + companyCodeId +
                    ", itemCode: " + itemCode +
                    ", refDocNumber: " + refDocNumber +
                    " doesn't exist.");
        }
    }

    /*
     * -------------------------Audit Log----------------------------------------------------------------
     */
    public void createAuditLogRecord(String companyCodeId, String plantId, String warehouseId,
                                     String loginUserID, String tableName, String objectName,
                                     String modifiedField, String oldValue, String newValue,
                                     String packBarcodes, String itemCode, String storageBin,
                                     Long stockTypeId, Long specialStockIndicatorId, String refDocNumber)
            throws InvocationTargetException, IllegalAccessException {

        AuditLog auditLog = new AuditLog();

        auditLog.setCompanyCode(companyCodeId);

        auditLog.setPlantID(plantId);

        auditLog.setWarehouseId(warehouseId);

        auditLog.setFiscalYear(DateUtils.getCurrentYear());

        auditLog.setObjectName(objectName);

        auditLog.setTableName(tableName);

        auditLog.setRefDocNumber(refDocNumber);

        // MOD_FIELD
        auditLog.setModifiedField(modifiedField);

        // OLD_VL
        auditLog.setOldValue(oldValue);

        // NEW_VL
        auditLog.setNewValue(newValue);

        // CTD_BY
        auditLog.setCreatedBy(loginUserID);

        // CTD_ON
        auditLog.setCreatedOn(new Date());

        // UTD_BY
        auditLog.setUpdatedBy(loginUserID);

        // UTD_ON
        auditLog.setUpdatedOn(new Date());

        auditLog.setReferenceField1(packBarcodes);
        auditLog.setReferenceField2(itemCode);
        auditLog.setReferenceField3(storageBin);
        auditLog.setReferenceField4(String.valueOf(stockTypeId));
        auditLog.setReferenceField5(String.valueOf(specialStockIndicatorId));

        auditLogService.createAuditLog(auditLog, loginUserID);
    }

    //============================================================V2=============================================================

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param binClassId
     * @return
     */
    public InventoryV2 getInventory(String companyCodeId, String plantId,
                                    String languageId, String warehouseId,
                                    String packBarcodes, String itemCode,
                                    String manufacturerName, Long binClassId) {
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceField1AndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        manufacturerName,
                        binClassId,
                        0L
                );

        if (inventory != null && !inventory.isEmpty()) {
            return inventory.get(0);
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @return
     */
    public IInventoryImpl getInventoryforExistingBin(String companyCodeId, String plantId,
                                                     String languageId, String warehouseId,
                                                     String packBarcodes, String itemCode,
                                                     String manufacturerName, Long binClassId) {
        IInventoryImpl getInventoryExistingBin =
                inventoryV2Repository.getInventoryforExistingBin(
                        companyCodeId,
                        languageId,
                        plantId,
                        warehouseId,
                        manufacturerName,
                        packBarcodes,
                        itemCode,
                        binClassId
                );

        if (getInventoryExistingBin != null) {
            return getInventoryExistingBin;
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
     * @return
     */
    public List<InventoryV2> getInventoryForOrderManagementV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                              String itemCode, Long stockTypeId, Long binClassId) {
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndAndStockTypeIdAndBinClassIdAndInventoryQuantityGreaterThanAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
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
    public List<IInventoryImpl> getInventoryForOrderManagementV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
//        List<InventoryV2> inventory =
//                inventoryV2Repository
//                .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndAndStockTypeIdAndBinClassIdAndManufacturerNameAndInventoryQuantityGreaterThanAndDeletionIndicatorOrderByInventoryIdDesc(
//                        languageId,
//                        companyCodeId,
//                        plantId,
//                        warehouseId,
//                        itemCode,
//                        stockTypeId,
//                        binClassId,
//                        manufacturerName,
//                        0D,
//                        0L
//                );

        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryForOrderManagement(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, stockTypeId, manufacturerName);
//        List<IInventoryImpl> inventory = inventoryV2Repository.
//                findInventoryForOMmt(companyCodeId, languageId, plantId, warehouseId, itemCode, binClassId, stockTypeId, manufacturerName);
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
	public List<IInventoryImpl> getInventoryForOrderManagementOrderByCtdOnV2(String companyCodeId, String plantId,
			String languageId, String warehouseId, String itemCode, Long stockTypeId, Long binClassId,
			String manufacturerName) {
		if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null || itemCode == null
				|| stockTypeId == null || binClassId == null || manufacturerName == null) {
			throw new BadRequestException(
					"Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
							+ companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode
							+ "," + stockTypeId + "," + binClassId + "," + manufacturerName);
		}

		List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryOmlOrderByCtdOnId(companyCodeId, plantId,
				languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId);
		return inventory;
	}
	
	public List<IInventoryImpl> getInventoryForOrderManagementOrderByCtdOnV3(String companyCodeId, String plantId,
			String languageId, String warehouseId, String itemCode, Long stockTypeId, Long binClassId,
			String manufacturerName, String barCodeId) {
		if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null || itemCode == null
				|| stockTypeId == null || binClassId == null || manufacturerName == null) {
			throw new BadRequestException(
					"Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
							+ companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode
							+ "," + stockTypeId + "," + binClassId + "," + manufacturerName);
		}

		List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryOmlOrderByCtdOnIdV3(companyCodeId, plantId,
				languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId, barCodeId);
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
    public List<IInventoryImpl> getInventoryForOrderManagementOrderByLevelIdV2(String companyCodeId, String plantId, String languageId, String warehouseId,
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
    public List<IInventory> getInventoryForOrderManagementGroupByLevelIdV2(String companyCodeId, String plantId, String languageId, String warehouseId,
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
     * @param manufacturerName
     * @return
     */
    public List<IInventory> getInventoryForOrderManagementByBatch(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        List<IInventory> inventory = inventoryV2Repository.findInventoryByBatch(companyCodeId, plantId, languageId, warehouseId, binClassId, itemCode, manufacturerName);
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
    public List<IInventoryImpl> getInventoryForOrderManagementGroupByLevelIdV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                               String itemCode, Long stockTypeId, Long binClassId, Long levelId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryOmlGroupByLevelId(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId, levelId);
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
	public List<IInventoryImpl> getInventoryForOrderManagementGroupByBarcodedIdV3(String companyCodeId, String plantId,
			String languageId, String warehouseId, String itemCode, Long stockTypeId, Long binClassId, Long levelId,
			String manufacturerName, String barcodeId) {
		if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null || itemCode == null
				|| stockTypeId == null || binClassId == null || manufacturerName == null) {
			throw new BadRequestException(
					"Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
							+ companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode
							+ "," + stockTypeId + "," + binClassId + "," + manufacturerName);
		}

		List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryOmlGroupByLevelIdV3(companyCodeId, plantId,
				languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId, levelId, barcodeId);
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
    public List<IInventoryImpl> getInventoryForOrderManagementGroupByBatch(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                               String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryOmlGroupByBatch(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId);
        return inventory;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param storageBin
     * @param stockTypeId
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV2GroupByBatch(String companyCodeId, String plantId, String languageId,
                                                                               String warehouseId, String itemCode, String manufacturerName,
                                                                               Long binClassId, String storageBin, Long stockTypeId) {
        List<IInventoryImpl> inventory =
                inventoryV2Repository.findInventoryOmlGroupByBatch(
                        companyCodeId,
                        languageId,
                        plantId,
                        warehouseId,
                        manufacturerName,
                        itemCode,
                        storageBin,
                        stockTypeId,
                        binClassId);
        return inventory;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @return
     */
    public List<IInventoryImpl> getInventoryForPutAwayCreate(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String itemCode, String manufacturerName, Long binClassId) {

        List<IInventoryImpl> inventory = inventoryV2Repository.
//                inventoryForPutAway(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId);
        inventoryForPutAwaytemp(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId);
        return inventory;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param binClassId
     * @return
     */
    public List<String> getInventoryBinForPutAwayCreate(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        String itemCode, Long binClassId) {

        List<String> inventory = inventoryV2Repository.
                inventoryForPutAwayCreate(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId);
        return inventory;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param referenceDocumentNo
     * @param itemCode
     * @return
     */
    public List<InventoryV2> getInventory(String companyCodeId, String plantId,
                                          String languageId, String warehouseId,
                                          String referenceDocumentNo, String itemCode) {
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        referenceDocumentNo,
                        itemCode,
                        0L
                );

        if (inventory == null) {
            throw new BadRequestException("The given Inventory ID : " +
                    ", warehouseId: " + warehouseId +
                    ", companyCodeId: " + companyCodeId +
                    ", plantId: " + plantId +
                    ", languageId: " + languageId +
                    ", referenceDocumentNo: " + referenceDocumentNo +
                    ", itemCode: " + itemCode +
                    " doesn't exist.");
        }
        return inventory;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param storageBin
     * @param manufacturerCode
     * @return
     */
    public InventoryV2 getInventory(String companyCode, String plantId, String languageId,
                                    String warehouseId, String packBarcodes, String itemCode,
                                    String storageBin, String manufacturerCode) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
        Optional<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        storageBin,
                        manufacturerCode,
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
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerCode
     * @return
     */
    public InventoryV2 getInventory(String companyCode, String plantId, String languageId,
                                    String warehouseId, String packBarcodes, String itemCode,
                                    String manufacturerCode) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode);
        Optional<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        manufacturerCode,
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
     * @param itemCode
     * @param binClassId
     * @return
     */
    public List<InventoryV2> getInventory(String companyCode, String plantId, String languageId, String warehouseId, String itemCode, Long binClassId) {

        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
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
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @return
     */
    public List<IInventoryImpl> getInventoryForInvMmt(String companyCode, String plantId, String languageId, String warehouseId,
                                                      String itemCode, String manufacturerName, Long binClassId) {

//        List<InventoryV2> inventory =
//                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
//                        languageId,
//                        companyCode,
//                        plantId,
//                        warehouseId,
//                        itemCode,
//                        manufacturerName,
//                        binClassId,
//                        0L
//                );
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
     * @param warehouseId
     * @param itemCode
     * @param binClassId
     * @return
     */
    public List<InventoryV2> getInventoryForInvMmt(String companyCode, String plantId, String languageId, String warehouseId, String itemCode, Long binClassId) {

        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        itemCode,
                        binClassId,
                        0L
                );
        if (inventory.isEmpty()) {
            return null;
        }
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @return
     */
    public List<InventoryV2> getInventoryV2(String companyCode, String plantId, String languageId, String warehouseId, String itemCode) {
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        itemCode,
                        0L
                );
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<IInventoryImpl> getInventoryForStockAdjustment(String companyCodeId, String plantId, String languageId,
                                                               String warehouseId, String itemCode, String manufacturerName) {
//        List<InventoryV2> inventory =
//                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndBinClassIdAndStockTypeIdAndDeletionIndicatorOrderByInventoryIdDesc(
//                        languageId,
//                        companyCode,
//                        plantId,
//                        warehouseId,
//                        itemCode,
//                        manufacturerName,
//                        1L,
//                        1L,
//                        0L
//                );
        List<IInventoryImpl> inventory =
                inventoryV2Repository.findInventoryForStockAdjustmentGroupByPackBarcode(
                        companyCodeId,
                        plantId,
                        languageId,
                        warehouseId,
                        itemCode,
                        1L,
                        1L,
                        manufacturerName);
        return inventory;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param referenceDocumentNo
     * @param stockTypeId
     * @return
     */
    public List<InventoryV2> getInventoryV2(String companyCode, String plantId, String languageId,
                                            String warehouseId, String referenceDocumentNo, Long stockTypeId) {
        List<InventoryV2> inventoryList =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentNoAndStockTypeIdAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        referenceDocumentNo,
                        stockTypeId,
                        0L);
        log.info("Inventory - stock type id to be updated: " + inventoryList);
        return inventoryList;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param referenceDocumentNo
     * @param stockTypeId
     * @return
     */
    public List<InventoryV2> getInventoryForInboundConfirmV2(String companyCode, String plantId, String languageId,
                                                             String warehouseId, String referenceDocumentNo, Long stockTypeId) {
        List<InventoryV2> inventoryList =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentNoAndStockTypeIdAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        referenceDocumentNo,
                        stockTypeId,
                        0L);
        log.info("Inventory - stock type id to be updated: " + inventoryList);
        if (inventoryList == null || inventoryList.isEmpty() || inventoryList.size() == 0) {
            log.info("inventory null ---> c_id, plant_id, lang_id, wh_id, ref_doc_no, stockTypeId: "+ companyCode + ", " + plantId + ", " + languageId + ", " + warehouseId + "," + referenceDocumentNo + ", " + stockTypeId);
            return null;
        }
//        if (inventoryList != null) {
//            for (InventoryV2 dbInventory : inventoryList) {
//                InventoryV2 newInventory = new InventoryV2();
//                newInventory.setInventoryId(System.currentTimeMillis());
//                BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
//                newInventory.setStockTypeId(1L);
//                String stockTypeDesc = getStockTypeDesc(companyCode,plantId,languageId,warehouseId,1L);
//                dbInventory.setStockTypeDescription(stockTypeDesc);
//                inventoryV2Repository.save(newInventory);
//                log.info("Inventory Updated to StockTypeId 1");
//            }
//        }
        return inventoryList;
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param referenceDocumentNo
     * @param stockTypeId
     */
//    public void updateInventoryStockTypeId(String companyCode, String plantId, String languageId,
//                                           String warehouseId, String referenceDocumentNo, Long stockTypeId) {
//        inventoryV2Repository.updateInventoryStockTypeId(warehouseId, companyCode, plantId, languageId, referenceDocumentNo, stockTypeId);
//    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param binClassId
     * @param storageBin
     * @param stockTypeId
     * @return
     */
    public List<InventoryV2> getInventoryForOrderMgmtV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        String itemCode, Long binClassId, String storageBin, Long stockTypeId) {
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndStorageBinAndStockTypeIdAndDeletionIndicatorAndInventoryQuantityGreaterThanOrderByInventoryQuantityAscInventoryIdDesc(
                        languageId,
                        companyCodeId,
                        plantId,
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
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param storageBin
     * @param stockTypeId
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, String manufacturerName, Long binClassId, String storageBin, Long stockTypeId) {
        List<IInventoryImpl> inventory =
                inventoryV2Repository.findInventoryOml(
                        companyCodeId,
                        languageId,
                        plantId,
                        warehouseId,
                        manufacturerName,
                        itemCode,
                        storageBin,
                        stockTypeId,
                        binClassId);
        return inventory;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param storageBin
     * @param stockTypeId
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV2OrderByLevelId(String companyCodeId, String plantId, String languageId,
                                                                               String warehouseId, String itemCode, String manufacturerName,
                                                                               Long binClassId, String storageBin, Long stockTypeId) {
        List<IInventoryImpl> inventory =
                inventoryV2Repository.findInventoryOmlOrderByLevelId(
                        companyCodeId,
                        languageId,
                        plantId,
                        warehouseId,
                        manufacturerName,
                        itemCode,
                        storageBin,
                        stockTypeId,
                        binClassId);
        return inventory;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param levelId
     * @param storageBin
     * @param stockTypeId
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV2GroupByLevelId(String companyCodeId, String plantId, String languageId,
                                                                               String warehouseId, String itemCode, String manufacturerName,
                                                                               Long binClassId, Long levelId, String storageBin, Long stockTypeId) {
        List<IInventoryImpl> inventory =
                inventoryV2Repository.findInventoryOmlGroupByLevelId(
                        companyCodeId,
                        languageId,
                        plantId,
                        warehouseId,
                        manufacturerName,
                        itemCode,
                        storageBin,
                        stockTypeId,
                        binClassId,
                        levelId);
        return inventory;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stSecIds
     * @return
     */
    public List<IInventory> getInventoryGroupByStorageBinV2(String companyCodeId, String plantId, String languageId,
                                                            String warehouseId, String itemCode, List<String> stSecIds) {
        List<IInventory> inventory = inventoryV2Repository.findInventoryGroupByStorageBinV2(companyCodeId, plantId, languageId, warehouseId, itemCode, stSecIds);
        return inventory;
    }

    /**
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param storageBin
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryV2(String companyCode, String plantId, String languageId,
                                      String warehouseId, String packBarcodes, String itemCode, String storageBin) {
        try {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
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
        log.info("getInventory record----------> : " + inventory.get(0));
        return inventory.get(0);
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
     * @param barcodeId
     * @param itemCode
     * @param binClassId
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryV3(String companyCode, String plantId, String languageId,
                                      String warehouseId, String barcodeId, String itemCode, Long binClassId) {
        try {
        	log.info("getInventory----------> : " + warehouseId + "," + barcodeId + "," + itemCode + "," + binClassId);
        	List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeIdAndItemCodeAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        barcodeId,
                        itemCode,
                        binClassId,
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
            throw new BadRequestException("Exception while Inventory Get : " + e);
        }
    }
    
    /**
     * 
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param barcodeId
     * @param itemCode
     * @param stBin
     * @param binClassId
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryV3(String companyCode, String plantId, String languageId,
                                      String warehouseId, String barcodeId, String itemCode, String stBin, Long binClassId) {
        try {
        	log.info("getInventory----------> : " + warehouseId + "," + barcodeId + "," + itemCode + "," + binClassId);
        	List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeIdAndItemCodeAndBinClassIdAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        barcodeId,
                        itemCode,
                        binClassId,
                        stBin,
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
            throw new BadRequestException("Exception while Inventory Get : " + e);
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param storageBin
     * @param manufacturerName
     * @return
     */
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
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param storageBin
     * @param manufacturerName
     * @return
     */
    public InventoryV2 getInventoryForQualityConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                       String packBarcodes, String itemCode, String storageBin, String manufacturerName) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        storageBin,
                        manufacturerName,
                        4L,
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
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public List<IInventoryImpl> getInventoryForPerpetualCountV2(String companyCode, String plantId, String languageId,
                                                                String warehouseId, String itemCode, String manufacturerName) {
        try {
        log.info("getInventory----------> : " + warehouseId + "," + manufacturerName + "," + itemCode + "," + plantId);
//        List<InventoryV2> inventory =
//                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicator(
//                        languageId,
//                        companyCode,
//                        plantId,
//                        warehouseId,
//                        itemCode,
//                        manufacturerName,
//                        1L,
//                        0L
//                );

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
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerName
     * @param storageBin
     * @param storageSectionId
     * @return
     */
    public InventoryV2 getInventoryForInhouseTransferV2(String companyCode, String plantId, String languageId, String warehouseId, String packBarcodes,
                                                        String itemCode, String manufacturerName, String storageBin, String storageSectionId) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
        InventoryV2 inventory =
                inventoryV2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStorageSectionIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        manufacturerName,
                        storageBin,
                        storageSectionId,
                        0L
                );
        if (inventory == null) {
            log.error("---------Inventory is null-----------");
            return null;
        }
        log.info("getInventory record----------> : " + inventory);
        return inventory;
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param storageSectionId
     * @return
     */
    public InventoryV2 getInventoryForImfInhouseTransferV2(String companyCode, String plantId, String languageId, String warehouseId, String packBarcodes,
                                                           String itemCode, String manufacturerName, Long binClassId, String storageSectionId) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + binClassId);
        InventoryV2 inventory =
                inventoryV2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndBinClassIdAndStorageSectionIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        manufacturerName,
                        binClassId,
                        storageSectionId,
                        0L
                );
        if (inventory == null) {
            log.error("---------Inventory is null-----------");
            return null;
        }
        log.info("getInventory record----------> : " + inventory);
        return inventory;
    }

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
     * @param batchSerialNumber
     * @return
     */
    public InventoryV2 getInventoryForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                        String packBarcodes, String itemCode, String manufacturerName, String storageBin, String batchSerialNumber) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
        InventoryV2 inventory =
                inventoryV2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndStorageBinAndBatchSerialNumberAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
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
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerName
     * @param storageBin
     * @param batchSerialNumber
     * @param storageSectionId
     * @return
     */
    public InventoryV2 getInventoryForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId, String packBarcodes,
                                                       String itemCode, String manufacturerName, String storageBin, String batchSerialNumber, String storageSectionId) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
        InventoryV2 inventory =
                inventoryV2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndStorageBinAndBatchSerialNumberAndStorageSectionIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        packBarcodes,
                        itemCode,
                        manufacturerName,
                        storageBin,
                        batchSerialNumber,
                        storageSectionId,
                        0L);
        if (inventory != null) {
            log.info("getInventory record----------> : " + inventory);
            return inventory;
        }
        log.info("getInventory record----------> : " + inventory);
        return null;
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
    public InventoryV2 getInventoryForAllocationV3(String companyCode, String plantId, String languageId, String warehouseId,
                                                   String packBarcodes, String itemCode, String manufacturerName, String storageBin, String barcodeId) {
        log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin + "," + 
        		 languageId+ "," +companyCode+ "," + plantId + "," +  manufacturerName+ "," +  barcodeId );
        List<InventoryV2> inventory =
                inventoryV2Repository
                        .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerNameAndBarcodeIdAndDeletionIndicatorOrderByInventoryIdDesc(
                                languageId,
                                companyCode,
                                plantId,
                                warehouseId,
                                packBarcodes,
                                itemCode,
                                storageBin,
                                manufacturerName,
                                barcodeId,
                                0L
                        );
        if (inventory.isEmpty()) {
            log.error("---------Inventory is null-----------");
            return null;
        }
        log.info("getInventory record----------> : " + inventory.get(0));
        return inventory.get(0);
    }
    
	public InventoryV2 getInventoryForAllocationV2(String companyCode, String plantId, String languageId,
			String warehouseId, String packBarcodes, String itemCode, String manufacturerName, String storageBin) {
		log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
		List<InventoryV2> inventory = inventoryV2Repository
				.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerNameAndDeletionIndicatorOrderByInventoryIdDesc(
						languageId, companyCode, plantId, warehouseId, packBarcodes, itemCode, storageBin,
						manufacturerName, 0L);
		if (inventory.isEmpty()) {
			log.error("---------Inventory is null-----------");
			return null;
		}
		log.info("getInventory record----------> : " + inventory.get(0));
		return inventory.get(0);
	}

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param storageSectionIds
     * @return
     */
    public List<InventoryV2> getInventoryForAdditionalBinsV2(String companyCode, String plantId, String languageId,
                                                             String warehouseId, String itemCode, List<String> storageSectionIds) {
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceField10InAndBinClassIdAndInventoryQuantityGreaterThan(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        itemCode,
                        storageSectionIds,
                        1L,
                        0D
                );
        return inventory;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @return
     */
    public List<IInventoryImpl> getInventoryV2ForAdditionalBinsV2(String companyCode, String plantId, String languageId,
                                                                  String warehouseId, String itemCode, String manufacturerName, Long binClassId) {
//        List<InventoryV2> inventory =
//                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceField10InAndBinClassIdAndInventoryQuantityGreaterThan(
//                        languageId,
//                        companyCode,
//                        plantId,
//                        warehouseId,
//                        itemCode,
//                        storageSectionIds,
//                        1L,
//                        0D
//                );

        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryForInventoryMovement(
                companyCode, plantId, languageId, warehouseId, itemCode, binClassId, manufacturerName);
        return inventory;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param storageSectionIds
     * @param stockTypeId
     * @return
     */
    public List<InventoryV2> getInventoryForAdditionalBinsForOB2V2(String companyCode, String plantId, String languageId, String warehouseId,
                                                                   String itemCode, List<String> storageSectionIds, Long stockTypeId) {
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceField10InAndStockTypeIdAndBinClassIdAndInventoryQuantityGreaterThan(
                        languageId,
                        companyCode,
                        plantId,
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
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param pickedPackCode
     * @param refDocNumber
     * @param manufacturerName
     * @return
     */
    public InventoryV2 getInventoryForPickUpLine(String companyCode, String plantId, String languageId, String warehouseId,
                                                 String itemCode, String pickedPackCode, String refDocNumber, String manufacturerName) {
        InventoryV2 inventory = inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndReferenceDocumentNoAndManufacturerNameAndDeletionIndicator(
                languageId,
                companyCode,
                plantId,
                warehouseId,
                itemCode,
                pickedPackCode,
                refDocNumber,
                manufacturerName,
                0L);
        if (inventory == null) {
            log.error("---------Inventory is null-----------");
            return null;
        }
        return inventory;
    }

    /**
     * @param itemCode
     * @param binClassId
     * @param warehouseId
     * @return
     */
    public List<InventoryV2> getInventoryForPutawayHeader(String itemCode, String manufacturerName, Long binClassId,
                                                          String companycode, String plantId, String languageId, String warehouseId) {
        log.info("itm_code,c_id,plant_id,lang_id,wh_id,bin_cl_id,mfr_name: " + itemCode + ", " + companycode + ", " + plantId + ", " + languageId + ", " + warehouseId + ", " + binClassId + ", " + manufacturerName);
        List<InventoryV2> stBinInventoryList =
                inventoryV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        companycode, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, 0L);
        if (stBinInventoryList == null || stBinInventoryList.isEmpty()) {
            // Exception Log
            createInventoryLog9(languageId, companycode, plantId, warehouseId, itemCode, manufacturerName, binClassId, "Inventory is null.");
            log.error("---------Inventory is null-----------");
            return null;
        }
        log.info("StBin Inventory List : " + stBinInventoryList);
        return stBinInventoryList;
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
     *
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

        if(searchInventory.getMaterialNo() != null && searchInventory.getMaterialNo().isEmpty()){
            searchInventory.setMaterialNo(null);
        }
        if(searchInventory.getPriceSegment() != null && searchInventory.getPriceSegment().isEmpty()){
            searchInventory.setPriceSegment(null);
        }
        if(searchInventory.getArticleNo() != null && searchInventory.getArticleNo().isEmpty()){
            searchInventory.setArticleNo(null);
        }
        if(searchInventory.getGender() != null && searchInventory.getGender().isEmpty()){
            searchInventory.setGender(null);
        }
        if(searchInventory.getColor() != null && searchInventory.getColor().isEmpty()){
            searchInventory.setColor(null);
        }
        if(searchInventory.getSize() != null && searchInventory.getSize().isEmpty()){
            searchInventory.setSize(null);
        }
        if(searchInventory.getNoPairs() != null && searchInventory.getNoPairs().isEmpty()) {
            searchInventory.setNoPairs(null);
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
                searchInventory.getPartnerCode(),
                searchInventory.getSpecialStockIndicatorId(),
                searchInventory.getItemType(),
                searchInventory.getMaterialNo(),
                searchInventory.getPriceSegment(),
                searchInventory.getArticleNo(),
                searchInventory.getGender(),
                searchInventory.getColor(),
                searchInventory.getSize(),
                searchInventory.getNoPairs(),
                searchInventory.getBinClassId());
        log.info("Inventory results: " + results.size() );
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
        try {
        InventoryV2 dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicatorOrderByInventoryIdDesc(
                companyCodeId, plantId, languageId, warehouseId, packBarcodes, itemCode, manufacturerName, storageBin, stockTypeId, specialStockIndicatorId, 0L);
        log.info("Inventory for Update: " + dbInventory);
        if(dbInventory != null) {
            InventoryV2 newInventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
            BeanUtils.copyProperties(updateInventory, newInventory, CommonUtils.getNullPropertyNames(updateInventory));
                if(newInventory.getStorageSectionId() == null) {
                    newInventory.setStorageSectionId(newInventory.getReferenceField10());
                }
            newInventory.setUpdatedBy(loginUserID);
            newInventory.setUpdatedOn(new Date());
//            newInventory.setInventoryId(System.currentTimeMillis());
            return inventoryV2Repository.save(newInventory);
        }
        return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param loginUserID
     * @return
     */
    public void updateInventoryForBarcodeV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String itemCode, String manufacturerName, String barcodeId, String loginUserID) {
        List<InventoryV2> dbInventoryList = inventoryV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 0L);
        log.info("Inventory for UpdateBarcodeId: " + dbInventoryList);
        if(dbInventoryList != null && !dbInventoryList.isEmpty()) {
            for (InventoryV2 dbInventory : dbInventoryList) {
                dbInventory.setBarcodeId(barcodeId);
                dbInventory.setUpdatedBy(loginUserID);
                dbInventory.setUpdatedOn(new Date());
                inventoryV2Repository.save(dbInventory);
            }
        }
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
        try {
        InventoryV2 dbInventory = new InventoryV2();
        log.info("newInventory : " + newInventory);
        BeanUtils.copyProperties(newInventory, dbInventory, CommonUtils.getNullPropertyNames(newInventory));
            if(dbInventory.getStorageSectionId() == null) {
                dbInventory.setStorageSectionId(dbInventory.getReferenceField10());
            }
        dbInventory.setDeletionIndicator(0L);
        dbInventory.setCreatedBy(loginUserID);
        dbInventory.setCreatedOn(new Date());
            dbInventory.setInventoryId(System.currentTimeMillis());
        return inventoryV2Repository.save(dbInventory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @return
     */
    public List<InventoryV2> getInventoryForDeleteV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                     String packBarcodes, String itemCode, String manufacturerName) {
        List<InventoryV2> inventory =
                inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
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
        return inventory;
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param packBarcodes
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<IInventoryImpl> getInventoryForDelete(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                      String packBarcodes, String itemCode, String manufacturerName) {
        List<IInventoryImpl> inventory =
                inventoryV2Repository.findInventoryForInventoryDelete(
                        companyCodeId,
                        plantId,
                        languageId,
                        warehouseId,
                        itemCode,
                        packBarcodes,
                        3L,
                        manufacturerName);
        return inventory;
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
     *
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
            newInventory.setInventoryQuantity(round(dbInventory.getInventoryQuantity() - grLine.getGoodReceiptQty()));
            newInventory.setReferenceField4(round(dbInventory.getReferenceField4() - grLine.getGoodReceiptQty()));
            newInventory.setCreatedOn(new Date());
            newInventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 7));
            inventoryV2Repository.save(newInventory);
        }
        return newInventory;
    }

    //===========================================Inventory_ExceptionLog================================================
    private void createInventoryLog9(String languageId, String companyCodeId, String plantId, String warehouseId,
                                     String itemCode, String manufacturerName, Long binClassId, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(itemCode);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCodeId);
        errorLog.setPlantId(plantId);
        errorLog.setWarehouseId(warehouseId);
        errorLog.setReferenceField1(itemCode);
        errorLog.setReferenceField2(manufacturerName);
        errorLog.setReferenceField3(String.valueOf(binClassId));
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    //==============================================Walkaroo======================================================

    /**
     *
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
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
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
     *
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
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryV3(String companyCode, String plantId, String languageId, String warehouseId,
                                      String packBarcodes, String itemCode, String barcodeId, String manufacturerName, String storageBin) {
        try {
            log.info("getInventory----------> : wh_id ---> " + warehouseId + ", pack_barcode ---> " + packBarcodes + ", itm_code ---> " + itemCode + ", st_bin ----> " + storageBin + ", barcode_id --->" + barcodeId + ", MFR_NAME ---> " +
                    manufacturerName + ", lang_id ----> " + languageId + ", c_id ----> " + companyCode + ", plant_id ----> " + plantId);
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @return
     */
//    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public synchronized InventoryV2 getInventoryV3(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName,
                                      String barcodeId, String storageBin, Long binClassId, Long stockTypeId) throws Exception {
        try {
			log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + barcodeId + "|" + storageBin + "|" + binClassId + "|" + stockTypeId);
			IInventoryImpl dbInventory = inventoryV2Repository.getInboundInventoryV3(companyCodeId, plantId, languageId, warehouseId, barcodeId, null,
			        itemCode, manufacturerName, PACK_BARCODE, storageBin, binClassId, stockTypeId);
			if(dbInventory != null) {
			    InventoryV2 inventory = new InventoryV2();
			    BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
			    return inventory;
			}
			return null;
		} catch (BeansException e) {
			e.printStackTrace();
			throw e;
		}
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, String manufacturerName, Long stockTypeId, Long binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.findInventoryForOrderManagementV3(
                companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, stockTypeId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.findInventoryForOrderManagementV3(
                    companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, STAGING_BIN_CLASS_ID, stockTypeId);
        }
        return inventoryList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementOrderByCreatedOnV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                                 String itemCode, String manufacturerName, Long stockTypeId, Long binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryOrderByCreatedOnV3(
                companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.getOMLInventoryOrderByCreatedOnV3(
                    companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, STAGING_BIN_CLASS_ID, stockTypeId);
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
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementOrderByExpiryDateV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                                  String itemCode, String manufacturerName, Long stockTypeId, Long binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryOrderByExpiryDateV3(
                companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.getOMLInventoryOrderByExpiryDateV3(
                    companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, STAGING_BIN_CLASS_ID, stockTypeId);
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
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementOrderByLevelIdV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                               String itemCode, String manufacturerName, Long stockTypeId, Long binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryOrderByLevelIdV3(
                companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.getOMLInventoryOrderByLevelIdV3(
                    companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, STAGING_BIN_CLASS_ID, stockTypeId);
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
     * @return
     */
    public List<IInventory> getInventoryForOrderManagementByBatchV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                    String itemCode, String manufacturerName, Long stockTypeId, Long binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventory> inventoryList = inventoryV2Repository.findInventoryByBatchV3(
                companyCodeId, plantId, languageId, warehouseId, binClassId, stockTypeId, itemCode, manufacturerName);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.findInventoryByBatchV3(
                    companyCodeId, plantId, languageId, warehouseId, STAGING_BIN_CLASS_ID, stockTypeId, itemCode, manufacturerName);
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
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementOrderByBatchV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                             String itemCode, String manufacturerName, Long stockTypeId, Long binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryOrderByBatchV3(
                companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.getOMLInventoryOrderByBatchV3(
                    companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, STAGING_BIN_CLASS_ID, stockTypeId);
        }
        return inventoryList;
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
     * @return
     */
    public List<IInventory> getInventoryForOrderManagementGroupByLevelIdV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                           String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventory> inventoryList = inventoryV2Repository.findInventoryGroupByLevelIdV3(
                companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.findInventoryGroupByLevelIdV3(
                    companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, STAGING_BIN_CLASS_ID);
        }
        return inventoryList;
    }

    /**
     * Inventory query for orderfullfillment -----BinClassId 1 and 10
     */
    public List<IInventory> getInventoryForOrderFullfillment(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                           String itemCode, Long stockTypeId, List<Long> binClassId, String manufacturerName) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventory> inventoryList = inventoryV2Repository.findInventoryListFFMO(
                companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, binClassId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.findInventoryListFFMO(
                    companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, stockTypeId, Collections.singletonList(3L));
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
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementLevelIdV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                        String itemCode, String manufacturerName, Long stockTypeId, Long binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryLevelIdV3(
                companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.getOMLInventoryLevelIdV3(
                    companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, STAGING_BIN_CLASS_ID, stockTypeId);
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
     * @param barcodeId
     * @param storageBin
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      String itemCode, String manufacturerName, String barcodeId, String storageBin) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + barcodeId + "|" + storageBin);
        IInventoryImpl dbInventory = inventoryV2Repository.getInventoryV3(
                companyCodeId, plantId, languageId, warehouseId, barcodeId, null, itemCode, manufacturerName, PACK_BARCODE, storageBin);
        if(dbInventory != null) {
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
            return inventory;
        }
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @return
     */
    public InventoryV2 getInventoryForPickUpLineV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String itemCode, String manufacturerName, String barcodeId, String storageBin) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + barcodeId + "|" + storageBin);
        IInventoryImpl dbInventory = inventoryV2Repository.getPickupLineInventoryV3(
                companyCodeId, plantId, languageId, warehouseId, barcodeId, null, itemCode, manufacturerName, PACK_BARCODE, storageBin);
        if (dbInventory != null) {
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
            return inventory;
        }
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @return
     */
    public InventoryV2 getInventoryForPickUpLineV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String itemCode, String manufacturerName, String barcodeId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + barcodeId);
        IInventoryImpl dbInventory = inventoryV2Repository.getPickupLineInventoryV3(
                companyCodeId, plantId, languageId, warehouseId, barcodeId, null, itemCode, manufacturerName, PACK_BARCODE, null);
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
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @return
     */
    public InventoryV2 getOutboundInventoryV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                              String itemCode, String manufacturerName, String barcodeId, String storageBin) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + barcodeId + "|" + storageBin);
        IInventoryImpl dbInventory = inventoryV2Repository.getOutboundInventoryV3(
                companyCodeId, plantId, languageId, warehouseId, barcodeId, null, itemCode, manufacturerName, PACK_BARCODE, storageBin);
        log.info("OutboundInventory : " + dbInventory);
        if (dbInventory != null) {
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
            return inventory;
        }
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @return
     */
    public InventoryV2 getOutboundReversalInventoryV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                      String itemCode, String manufacturerName, String barcodeId, String storageBin) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + barcodeId + "|" + storageBin);
        IInventoryImpl dbInventory = inventoryV2Repository.getOutboundReversalInventoryV3(
                companyCodeId, plantId, languageId, warehouseId, barcodeId, null, itemCode, manufacturerName, PACK_BARCODE, storageBin);
        log.info("OutboundInventory : " + dbInventory);
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
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId + "|" + stockTypeId);
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null || itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }

        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryForOrderManagement(
                companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, stockTypeId, manufacturerName);
        if(inventory == null || inventory.isEmpty()) {
            return inventoryV2Repository.findInventoryForOrderManagement(
                    companyCodeId, plantId, languageId, warehouseId, itemCode, STAGING_BIN_CLASS_ID, stockTypeId, manufacturerName);
        }
        return inventory;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param storageBin
     * @return
     */
    public Double getInventoryByStorageBinV3(String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + storageBin);
        return inventoryV2Repository.getInventoryBinStatusV3(companyCodeId, plantId, languageId, warehouseId, storageBin);
    }

    /**
     * @param putAwayLine
     * @param loginUserId
     * @return
     * @throws Exception
     */
//    @Async("asyncExecutor")
//    @Transactional
    public InventoryV2 createInventoryNonCBMV3(PutAwayLineV2 putAwayLine, String loginUserId) throws Exception {
        String companyCode = putAwayLine.getCompanyCode();
        String plantId = putAwayLine.getPlantId();
        String languageId = putAwayLine.getLanguageId();
        String warehouseId = putAwayLine.getWarehouseId();
        String barCodeId = putAwayLine.getBarcodeId();
        String itemCode = putAwayLine.getItemCode();
        String manufacturerName = putAwayLine.getManufacturerName();
        String refDocNumber = putAwayLine.getRefDocNumber();
        try {
            //BinClassId 3 reduce Inventory
            try {
				binClassId3InventoryReduction(companyCode, plantId, languageId, warehouseId, barCodeId, itemCode, 
						manufacturerName, refDocNumber, putAwayLine.getPutawayConfirmedQty(), loginUserId);
			} catch (Exception e) {
				log.error("Error on getting Inventory - binClassId3InventoryReduction : " + e.toString());
				e.printStackTrace();
			}

            // Validate HuSerial No/ Barcode Id for Uniqueness
//            validateInventoryBarcodeId(companyCode, plantId, languageId, warehouseId, barCodeId);

            InventoryV2 createdinventory = null;
            log.info("BinClassID 1 create/Update Initiated---->alreadyExecuted---> " + new Date());
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
            double PA_QTY = round(putAwayLine.getPutawayConfirmedQty());
            if(dbInventoryBinClassId1 == null) {
                // INV_QTY
                double INV_QTY = round(PA_QTY);
                inventory.setInventoryQuantity(INV_QTY);
                inventory.setAllocatedQuantity(0D);
                inventory.setReferenceField4(INV_QTY);      //Allocated Qty is zero for newInventory
                log.info("B1 New - Inventory - INV_QTY,TOT_QTY: {}", INV_QTY);
            } else {
                double INV_QTY = round(dbInventoryBinClassId1.getInventoryQuantity());
                double ALLOC_QTY = round(dbInventoryBinClassId1.getAllocatedQuantity());
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

            inventory.setCreatedOn(new Date());
            inventory.setUpdatedOn(new Date());
            try {
				createdinventory = inventoryV2Repository.save(inventory);
				log.info("B1-created inventory : {}", createdinventory);
            } catch (Exception e1) {
				log.error("--ERROR--createInventoryNonCBMV3 ----level1--inventory--error----> :" + e1.toString());
				e1.printStackTrace();
				
				// Inventory Error Handling
				InventoryTrans newInventoryTrans = new InventoryTrans();
				BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
				newInventoryTrans.setReRun(0L);	
				InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
				log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
			}
            return createdinventory;
        } catch (Exception e) {
            // Exception Log
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 
     */
	@Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT2M")
	public void updateErroredOutInventory () {
		List<InventoryTrans> inventoryTransList = inventoryTransRepository.findByReRun(0L);
		inventoryTransList.stream().forEach( it -> {
			log.info("----updateErroredOutInventory-->: " + it);
			inventoryV2Repository.updateInventory(it.getWarehouseId(),
					it.getPackBarcodes(), it.getItemCode(),
					it.getStorageBin(), it.getInventoryQuantity(), it.getAllocatedQuantity());
			it.setReRun(1L);
			inventoryTransRepository.save(it);		
			log.info("----updateInventoryTrans-is-done-->: " + it);
		});
	}

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param barcodeId
     * @param itemCode
     * @param manufacturerName
     * @param putAwayQty
     * @param loginUserId
     */
    public void binClassId3InventoryReduction(String companyCode, String plantId, String languageId, String warehouseId, String barcodeId,
                                              String itemCode, String manufacturerName, String refDocNumber, Double putAwayQty, String loginUserId) {
        InventoryV2 dbInventory = null;
		try {
			dbInventory = getInventoryV3(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, null, 3L, 1L);
		} catch (Exception e) {
			log.error("Error on getInventoryV3 : " + e.toString());;
			e.printStackTrace();
		}
		
        if (dbInventory != null) {
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));

            Double INV_QTY = round(dbInventory.getInventoryQuantity());
            Double ALLOC_QTY = round(dbInventory.getAllocatedQuantity());
            Double PA_QTY = round(putAwayQty);

            log.info("Before - B3-Inventory - INV_QTY,ALLOC_QTY,TOT_QTY,GrQty: {}, {}, {}, {}", INV_QTY, ALLOC_QTY, dbInventory.getReferenceField4(), PA_QTY);
            INV_QTY = INV_QTY - PA_QTY;
            INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
            double TOT_QTY = INV_QTY + ALLOC_QTY;

            inventory.setInventoryQuantity(INV_QTY);
            inventory.setAllocatedQuantity(ALLOC_QTY);
            inventory.setReferenceField4(TOT_QTY);
            log.info("After - B3-Inventory - INV_QTY,ALLOC_QTY,TOT_QTY: {}, {}, {}", INV_QTY, ALLOC_QTY, TOT_QTY);

            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }

            inventory.setReferenceDocumentNo(refDocNumber);
            inventory.setReferenceOrderNo(refDocNumber);
            inventory.setUpdatedBy(loginUserId);
            inventory.setCreatedOn(dbInventory.getCreatedOn());
            inventory.setUpdatedOn(new Date());
            try {
				InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
				log.info("BinClassId 3 created inventory[Existing] - reduced : {}", createdinventory);
            } catch (Exception e1) {
				log.error("--ERROR--schedulePostPickupLineProcessV2 ----level1--inventory--error----> :" + e1.toString());
				e1.printStackTrace();
				
				// Inventory Error Handling
				InventoryTrans newInventoryTrans = new InventoryTrans();
				BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
				newInventoryTrans.setReRun(0L);	
				InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
				log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
			}
        }
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param barcodeId
     * @param itemCode
     * @param manufacturerName
     * @param putAwayLine
     * @param loginUserId
     */
    public void binClassId1InventoryReduction(String companyCode, String plantId, String languageId, String warehouseId,
                                              String barcodeId, String itemCode, String manufacturerName, PutAwayLineV2 putAwayLine, String loginUserId) {
        InventoryV2 dbInventory = getInventoryV3(companyCode, plantId, languageId, warehouseId, PACK_BARCODE, itemCode, barcodeId, manufacturerName, putAwayLine.getConfirmedStorageBin());
        if (dbInventory != null) {
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));

            Double INV_QTY = round(dbInventory.getInventoryQuantity());
            Double ALLOC_QTY = round(dbInventory.getAllocatedQuantity());
            Double PA_QTY = round(putAwayLine.getPutawayConfirmedQty());

            log.info("Before - B1-Inventory - INV_QTY,ALLOC_QTY,TOT_QTY,GrQty: {}, {}, {}, {}", INV_QTY, ALLOC_QTY, dbInventory.getReferenceField4(), PA_QTY);
            INV_QTY = INV_QTY - PA_QTY;
            INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
            double TOT_QTY = INV_QTY + ALLOC_QTY;

            inventory.setInventoryQuantity(INV_QTY);
            inventory.setAllocatedQuantity(ALLOC_QTY);
            inventory.setReferenceField4(TOT_QTY);
            log.info("After - B1-Inventory - INV_QTY,ALLOC_QTY,TOT_QTY: {}, {}, {}", INV_QTY, ALLOC_QTY, TOT_QTY);

            if (putAwayLine.getBarcodeId() != null) {
                inventory.setBarcodeId(putAwayLine.getBarcodeId());
            }

            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }
            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
            inventory.setUpdatedBy(loginUserId);
            inventory.setCreatedOn(dbInventory.getCreatedOn());
            inventory.setUpdatedOn(new Date());
            
            try {
                InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
                log.info("-----binClassId1InventoryReduction--createdinventory--->: {}", createdinventory);
            } catch (Exception e1) {
				log.error("--ERROR--binClassId1InventoryReduction ----level1--inventory--error----> :" + e1.toString());
				e1.printStackTrace();
				
				// Inventory Error Handling
				InventoryTrans newInventoryTrans = new InventoryTrans();
				BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
				newInventoryTrans.setReRun(0L);	
				InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
				log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
            }
        }
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param barcodeId
     */
    public void validateInventoryBarcodeId(String companyCode, String plantId, String languageId, String warehouseId, String barcodeId) {
        Double inventoryExist = inventoryV2Repository.getDuplicateBarcodeInventoryStatusV3(companyCode, plantId, languageId, warehouseId, barcodeId);
        log.info("Duplicate Inventory Barcode Check : " + inventoryExist);
        if(inventoryExist != null && inventoryExist > 0) {
            throw new BadRequestException("Duplicate Inventory binClassId 1 for HU Serial No : " + barcodeId);
        }
    }

    /**
     * walkaroo inventory for stock movement report - item_group by barcodeId
     * @param searchInventory
     * @return
     * @throws Exception
     */
    public List<IInventoryImpl> findInventoryV3(SearchInventoryV2 searchInventory) throws Exception {

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
            List<Long> binClassIds = Arrays.asList(1L);
            searchInventory.setBinClassId(binClassIds);
        }
        if (searchInventory.getDescription() == null || searchInventory.getDescription().isEmpty()) {
            searchInventory.setDescription(null);
        }
        if (searchInventory.getPartnerCode() == null || searchInventory.getPartnerCode().isEmpty()) {
            searchInventory.setPartnerCode(null);
        }

        if(searchInventory.getMaterialNo() != null && searchInventory.getMaterialNo().isEmpty()){
            searchInventory.setMaterialNo(null);
        }
        if(searchInventory.getPriceSegment() != null && searchInventory.getPriceSegment().isEmpty()){
            searchInventory.setPriceSegment(null);
        }
        if(searchInventory.getArticleNo() != null && searchInventory.getArticleNo().isEmpty()){
            searchInventory.setArticleNo(null);
        }
        if(searchInventory.getGender() != null && searchInventory.getGender().isEmpty()){
            searchInventory.setGender(null);
        }
        if(searchInventory.getColor() != null && searchInventory.getColor().isEmpty()){
            searchInventory.setColor(null);
        }
        if(searchInventory.getSize() != null && searchInventory.getSize().isEmpty()){
            searchInventory.setSize(null);
        }
        if(searchInventory.getNoPairs() != null && searchInventory.getNoPairs().isEmpty()) {
            searchInventory.setNoPairs(null);
        }

        List<IInventoryImpl> results = inventoryV2Repository.findInventoryV3(
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
                searchInventory.getPartnerCode(),
                searchInventory.getSpecialStockIndicatorId(),
                searchInventory.getItemType(),
                searchInventory.getMaterialNo(),
                searchInventory.getPriceSegment(),
                searchInventory.getArticleNo(),
                searchInventory.getGender(),
                searchInventory.getColor(),
                searchInventory.getSize(),
                searchInventory.getNoPairs(),
                searchInventory.getBinClassId());
        log.info("Inventory results: " + results.size() );
        return results;
    }

    /**
     *
     * @param stagingLine
     * @param loginUserId
     */
    @Transactional
    public void createInventoryNonCBMStagingLineV2(StagingLineEntityV2 stagingLine, String loginUserId) throws Exception {

        String companyCode = stagingLine.getCompanyCode();
        String plantId = stagingLine.getPlantId();
        String languageId = stagingLine.getLanguageId();
        String warehouseId = stagingLine.getWarehouseId();
        String barCodeId = stagingLine.getBarcodeId();
        String itemCode = stagingLine.getItemCode();
        String manufacturerName = stagingLine.getManufacturerName();
        try {
            InventoryV2 createdinventory = null;
            InventoryV2 dbInventory = getInventoryV3(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, barCodeId, null, 3L, 1L);
            if (dbInventory != null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));

                Double INV_QTY = getQuantity(dbInventory.getInventoryQuantity());
                Double GR_QTY = getQuantity(stagingLine.getOrderQty());

                log.info("Before - B3-Inventory - INV_QTY,TOT_QTY,OrderQty: {}, {}, {}, {}", INV_QTY, dbInventory.getReferenceField4(), GR_QTY);
                INV_QTY = round(INV_QTY + GR_QTY);

                inventory.setInventoryQuantity(INV_QTY);
                inventory.setAllocatedQuantity(0D);
                inventory.setReferenceField4(INV_QTY);

                if (stagingLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(stagingLine.getBarcodeId());
                }

                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }
                inventory.setReferenceDocumentNo(stagingLine.getRefDocNumber());
                inventory.setReferenceOrderNo(stagingLine.getRefDocNumber());
                inventory.setUpdatedBy(loginUserId);
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("BinClassId 3 created inventory[Existing] : {}", createdinventory);
            }

            if (dbInventory == null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(stagingLine, inventory, CommonUtils.getNullPropertyNames(stagingLine));
                inventory.setCompanyCodeId(companyCode);

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);
                inventory.setVariantSubCode("1");
                inventory.setStorageMethod("1");
                if (stagingLine.getBatchSerialNumber() != null) {
                    inventory.setBatchSerialNumber(stagingLine.getBatchSerialNumber());
                    inventory.setPackBarcodes(stagingLine.getBatchSerialNumber());
                } else {
                    inventory.setBatchSerialNumber("1");
                    inventory.setPackBarcodes(PACK_BARCODE);
                }
                inventory.setBinClassId(3L);
                inventory.setDeletionIndicator(0L);
                inventory.setReferenceField8(stagingLine.getItemDescription());
                inventory.setReferenceField9(stagingLine.getManufacturerName());
                inventory.setManufacturerCode(stagingLine.getManufacturerName());
                inventory.setDescription(stagingLine.getItemDescription());
                inventory.setReferenceDocumentNo(stagingLine.getRefDocNumber());
                inventory.setReferenceOrderNo(stagingLine.getRefDocNumber());

                log.info("StorageBin get and set from Master -------------BinClassId ----> " + 3L);
                // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
                StorageBinV2 storageBin = storageBinService.getStorageBinByBinClassIdV2(warehouseId, 3L, companyCode, plantId, languageId);
                log.info("storageBin: {}", storageBin);

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
                inventory.setPalletId(stagingLine.getPalletId());

                // INV_QTY
                double INV_QTY = round(stagingLine.getOrderQty());
                inventory.setInventoryQuantity(INV_QTY);
                inventory.setReferenceField4(INV_QTY);      //Allocated Qty is zero for bin Class Id 3
                log.info("New - Inventory - INV_QTY,TOT_QTY: {}", INV_QTY);

                // INV_UOM
                inventory.setInventoryUom(stagingLine.getOrderUom());
                inventory.setCreatedBy(loginUserId);
                inventory.setUpdatedBy(loginUserId);

                if (inventory.getItemType() == null) {
                    IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                    if (itemType != null) {
                        inventory.setItemType(itemType.getItemType());
                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                    }
                }

                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("B3 created inventory : {}", createdinventory);
            }
        } catch (Exception e) {
            // Exception Log
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 
     * @param excessConfirmations
     * @param loginUserID
     * @return
     */
    public List<InventoryV2> createInventoryv3(List<ExcessConfirmation> excessConfirmations, String loginUserID) {

        List<InventoryV2> inventoryList = new ArrayList<>();
        InventoryV2 createdinventory = null;
        for (ExcessConfirmation excessConfirmation : excessConfirmations) {
            String companyCode = excessConfirmation.getCompanyCode();
            String plantId = excessConfirmation.getPlantId();
            String languageId = excessConfirmation.getLanguageId();
            String warehouseId = excessConfirmation.getWarehouseId();
            String barCodeId = excessConfirmation.getBarcodeId();

            try {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(excessConfirmation, inventory, CommonUtils.getNullPropertyNames(excessConfirmation));
                inventory.setCompanyCodeId(companyCode);

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);
                inventory.setVariantSubCode("1");
                inventory.setStorageMethod("1");

                inventory.setBatchSerialNumber("1");
                inventory.setPackBarcodes(PACK_BARCODE);

                inventory.setBinClassId(8L);
                inventory.setDeletionIndicator(0L);
                inventory.setBarcodeId(barCodeId);
//                inventory.setReferenceField8(stagingLine.getItemDescription());
//                inventory.setReferenceField9(stagingLine.getManufacturerName());
//                inventory.setManufacturerCode(stagingLine.getManufacturerName());
//                inventory.setDescription(stagingLine.getItemDescription());
//                inventory.setReferenceDocumentNo(stagingLine.getRefDocNumber());
//                inventory.setReferenceOrderNo(stagingLine.getRefDocNumber());

                // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
                log.info("StorageBin get and set from Master -------------BinClassId ----> " + 8L);
                StorageBinV2 storageBin = storageBinService.getStorageBinByBinClassIdV2(warehouseId, 8L, companyCode, plantId, languageId);
                log.info("storageBin: {}", storageBin);

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
//                inventory.setPalletId(stagingLine.getPalletId());

                // INV_QTY
                double INV_QTY = round(excessConfirmation.getQuantity());
                inventory.setInventoryQuantity(INV_QTY);
                inventory.setReferenceField4(INV_QTY);      //Allocated Qty is zero for bin Class Id 3
                log.info("New - Inventory - INV_QTY,TOT_QTY: {}", INV_QTY);

                // INV_UOM
//                inventory.setInventoryUom(stagingLine.getOrderUom());
                inventory.setCreatedBy(loginUserID);
                inventory.setUpdatedBy(loginUserID);

//                if (inventory.getItemType() == null) {
//                    IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
//                    if (itemType != null) {
//                        inventory.setItemType(itemType.getItemType());
//                        inventory.setItemTypeDescription(itemType.getItemTypeDescription());
//                    }
//                }

                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                createdinventory = inventoryV2Repository.save(inventory);
                log.info("B3 created inventory : {}", createdinventory);
            } catch (Exception e) {
                // Exception Log
                e.printStackTrace();
                throw e;
            }
            inventoryList.add(createdinventory);
        }
        return inventoryList;
    }
    
    //----------------------------------
    /**
     * 
     * @param barcodeId
     * @param binClassId
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryV3 (String barcodeId, Long binClassId) {
        try {
        	log.info("-----getInventoryV3----------> : " + barcodeId + "," + binClassId);
        	List<InventoryV2> inventory = inventoryV2Repository.findByBarcodeIdAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        barcodeId,
                        binClassId,
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
            throw new BadRequestException("Exception while Inventory Get : " + e);
        }
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
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementFullfillment(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                        String itemCode, String manufacturerName, Long stockTypeId, List<Long> binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryFullFillment(
                companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.getOMLInventoryFullFillment(
                    companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, Collections.singletonList(3L), stockTypeId);
        }
        return inventoryList;
    }

    // Get Inventory for OrderFullfillment
    public List<IInventoryImpl> getInventoryOrderFullfillment(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                               String itemCode, String manufacturerName, Long stockTypeId, List<Long> binClassId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryFullFillment(
                companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId);
        if(inventoryList == null || inventoryList.isEmpty()) {
            return inventoryV2Repository.getOMLInventoryFullFillment(
                    companyCodeId, plantId, languageId, warehouseId, null, null, itemCode, manufacturerName, PACK_BARCODE, Collections.singletonList(3L), stockTypeId);
        }
        return inventoryList;
    }
}
