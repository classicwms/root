package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auditlog.AuditLog;
import com.tekclover.wms.api.transaction.model.dto.IInventory;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.impl.InventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.SearchInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.specification.InventorySpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InventoryService extends BaseService {
	
	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private InventoryMovementRepository inventoryMovementRepository;

	@Autowired
	private AuditLogService auditLogService;

	/**
	 * getInventorys
	 * @return
	 */
	public List<Inventory> getInventorys () {
		List<Inventory> inventoryList =  inventoryRepository.findAll();
		inventoryList = inventoryList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return inventoryList;
	}
	
	/**
	 * getInventory
	 * @param stockTypeId
	 * @return
	 */
	public Inventory getInventory (String warehouseId, String packBarcodes, String itemCode, String storageBin, Long stockTypeId, 
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
						); ///XXXXXXXXXXXXXXXXXXXXX
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
	 * @param warehouseId
	 * @param packBarcodes
	 * @param itemCode
	 * @param binClassId
	 * @return
	 */
	public Inventory getInventory (String warehouseId, String packBarcodes, String itemCode, Long binClassId) {
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
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param packBarcodes
	 * @param binClassId
	 * @return
	 */
	public List<Inventory> getInventoryForDeliveryConfirmtion (String warehouseId, String itemCode, 
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
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param stockTypeId
	 * @return
	 */
	public List<Inventory> getInventoryForStockReport (String warehouseId, String itemCode, Long stockTypeId) {
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
	 * 
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
	 * 
	 * @param warehouseId
	 * @param packBarcodes
	 * @param itemCode
	 * @return
	 */
	public List<Inventory> getInventoryForDelete (String warehouseId, String packBarcodes, String itemCode) {
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
	 * 
	 * @param warehouseId
	 * @param palletCode
	 * @param caseCode
	 * @param packBarcodes
	 * @param itemCode
	 * @return
	 */
	public List<Inventory> getInventory (String warehouseId, String palletCode, String caseCode, String packBarcodes, 
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
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param palletCode
	 * @param packBarcodes
	 * @param storageBin
	 * @return
	 */
	public List<Inventory> getInventoryForTransfers (String warehouseId, String itemCode, String palletCode, String packBarcodes, String storageBin) {
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
	 * 
	 * @param warehouseId
	 * @param packBarcodes
	 * @param itemCode
	 * @param storageBin
	 * @return
	 */
	public Inventory getInventory (String warehouseId, String packBarcodes, String itemCode, String storageBin) {
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
			log.error ("---------Inventory is null-----------");
			return null;
		}
		log.info("getInventory record----------> : " + inventory.get());
		return inventory.get();
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param storageBin
	 * @return
	 */
	public Inventory getInventoryByStorageBin (String warehouseId, String storageBin) {
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
			log.error ("---------Inventory is null-----------");
			return null;
		}
		return inventory.get();
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param binClassId
	 * @return
	 */
	public List<Inventory> getInventory (String warehouseId, String itemCode, Long binClassId) {
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
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @return
	 */
	public List<Inventory> getInventory (String warehouseId, String itemCode) {
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
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param binClassId
	 * @param storageBin
	 * @param stockTypeId
	 * @return
	 */
	public List<Inventory> getInventoryForOrderMgmt (String warehouseId, String itemCode, Long binClassId, String storageBin, Long stockTypeId) {
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
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param stSecIds
	 * @return
	 */
	public List<IInventory> getInventoryGroupByStorageBin (String warehouseId, String itemCode, List<String> stSecIds) {
		List<IInventory> inventory = inventoryRepository.findInventoryGroupByStorageBin(warehouseId, itemCode, stSecIds);
		return inventory;
	}
	
	/**
	 * 
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
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param storageSectionIds
	 * @param stockTypeId
	 * @return
	 */
	public List<Inventory> getInventoryForAdditionalBinsForOB2 (String warehouseId, String itemCode, List<String> storageSectionIds, Long stockTypeId) {
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
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param storageBin
	 * @param stockTypeId
	 * @return
	 */
	public List<Long> getInventoryQtyCount (String warehouseId, String itemCode, List<String> storageBin, 
			Long stockTypeId) {
		List<Long> inventory = inventoryRepository.findInventoryQtyCount(warehouseId, itemCode, storageBin, stockTypeId);
		return inventory;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @return
	 */
	public Double getInventoryQtyCount (String warehouseId, String itemCode) {
		Double inventoryQty = inventoryRepository.getInventoryQtyCount(warehouseId, itemCode);
		return inventoryQty;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param stBins
	 * @return
	 */
	public List<Inventory> getInventoryByStorageBin (String warehouseId, List<String> stBins) {
		List<Inventory> inventory = inventoryRepository.findByWarehouseIdAndStorageBinIn(warehouseId, stBins);
		return inventory;
	}
	
	/**
	 * 
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
	 * 
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
			if (n.getInventoryQuantity() == null) { n.setInventoryQuantity(0D);}
			if (n.getAllocatedQuantity() == null) { n.setAllocatedQuantity(0D);}
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
	 * 
	 * @param searchInventory
	 * @return
	 * @throws ParseException
	 */
	public List<Inventory> getQuantityValidatedInventory(SearchInventory searchInventory)
			throws ParseException {
		InventorySpecification spec = new InventorySpecification(searchInventory);
		List<Inventory> results = inventoryRepository.findAll(spec);
		if(!results.isEmpty()){
			results = results.stream().filter(x->  (x.getInventoryQuantity() != null && x.getInventoryQuantity() != 0) && (x.getAllocatedQuantity() != null && x.getAllocatedQuantity() != 0) ).collect(Collectors.toList());
		}
		return results;
	}
	
	/**
	 * createInventory
	 * @param newInventory
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Inventory createInventory (AddInventory newInventory, String loginUserID) 
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
	 *
	 * @param warehouseId
	 * @param packBarcodes
	 * @param itemCode
	 * @param storageBin
	 * @param stockTypeId
	 * @param specialStockIndicatorId
	 * @param loginUserID
	 * @param updateInventory
	 * @param dbInventory 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Inventory updateInventory (String warehouseId, String packBarcodes, String itemCode, String storageBin, 
			Long stockTypeId, Long specialStockIndicatorId, UpdateInventory updateInventory, Inventory dbInventory, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {

//		Inventory dbInventory = getInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId);

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

		if(updateInventory.getInventoryQuantity() == null) {
			updateInventory.setInventoryQuantity(0D);
		}
		if(updateInventory.getAllocatedQuantity() == null) {
			updateInventory.setAllocatedQuantity(0D);
		}
		if(dbInventory.getAllocatedQuantity() == null) {
			dbInventory.setAllocatedQuantity(0D);
		}
		if(dbInventory.getInventoryQuantity() == null) {
			dbInventory.setInventoryQuantity(0D);
		}

		Double newTotalQuantity = updateInventory.getInventoryQuantity() + updateInventory.getAllocatedQuantity();
		Double dbTotalQuantity = dbInventory.getInventoryQuantity() + dbInventory.getAllocatedQuantity();
		log.info("newTotalQuantity: "+ newTotalQuantity + "dbTotalQuantity: " + dbTotalQuantity);
		if(newTotalQuantity != dbTotalQuantity && dbInventory.getBinClassId() == 1) {

			InventoryMovement dbInventoryMovement = new InventoryMovement();

			String movementDocumentNumber;
			if(inventoryRepository.findMovementDocumentNo() != null) {
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
			dbInventoryMovement.setManufacturerPartNo(dbInventory.getReferenceField9()); //Inventory Ref_Field_9 - ManufacturePartNo
			dbInventoryMovement.setStorageBin(dbInventory.getStorageBin());
			dbInventoryMovement.setStorageMethod(dbInventory.getStorageMethod());
			dbInventoryMovement.setDescription(dbInventory.getReferenceField8());
			dbInventoryMovement.setStockTypeId(dbInventory.getStockTypeId());
			dbInventoryMovement.setSpecialStockIndicator(dbInventory.getSpecialStockIndicatorId());
			if(newTotalQuantity < dbTotalQuantity) {
				dbInventoryMovement.setMovementQtyValue("N");
				log.info("MovementQtyValue: "+ dbInventoryMovement.getMovementQtyValue());
			} else {
				dbInventoryMovement.setMovementQtyValue("P");
				log.info("MovementQtyValue: "+ dbInventoryMovement.getMovementQtyValue());
			}
			dbInventoryMovement.setMovementQty(newTotalQuantity-dbTotalQuantity);
			dbInventoryMovement.setBalanceOHQty(newTotalQuantity);
			dbInventoryMovement.setInventoryUom(dbInventory.getInventoryUom());
			dbInventoryMovement.setDeletionIndicator(dbInventory.getDeletionIndicator());
			dbInventoryMovement.setCreatedBy(dbInventory.getUpdatedBy());
			dbInventoryMovement.setCreatedOn(new Date());
			log.info("Inventory Movement: "+ dbInventoryMovement);
			inventoryMovementRepository.save(dbInventoryMovement);
		}

		BeanUtils.copyProperties(updateInventory, dbInventory, CommonUtils.getNullPropertyNames(updateInventory));
		dbInventory.setUpdatedBy(loginUserID);
		dbInventory.setUpdatedOn(new Date());
		return inventoryRepository.save(dbInventory);
	}
	
	/**
	 * 
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
	public Inventory updateInventory (String warehouseId, String packBarcodes, String itemCode, String storageBin, 
			Long stockTypeId, Long specialStockIndicatorId, UpdateInventory updateInventory, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Inventory dbInventory = getInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId);

		if(updateInventory.getInventoryQuantity() == null) {
			updateInventory.setInventoryQuantity(0D);
		}
		if(updateInventory.getAllocatedQuantity() == null) {
			updateInventory.setAllocatedQuantity(0D);
		}
		
		if(dbInventory.getAllocatedQuantity() == null) {
			dbInventory.setAllocatedQuantity(0D);
		}
		if(dbInventory.getInventoryQuantity() == null) {
			dbInventory.setInventoryQuantity(0D);
		}

		Double newTotalQuantity = updateInventory.getInventoryQuantity() + updateInventory.getAllocatedQuantity();
		Double dbTotalQuantity = dbInventory.getInventoryQuantity() + dbInventory.getAllocatedQuantity();
		log.info("newTotalQuantity: "+ newTotalQuantity + "dbTotalQuantity: " + dbTotalQuantity);
		if(newTotalQuantity != dbTotalQuantity && dbInventory.getBinClassId() == 1) {

			InventoryMovement dbInventoryMovement = new InventoryMovement();

			String movementDocumentNumber;
			if(inventoryRepository.findMovementDocumentNo() != null) {
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
			dbInventoryMovement.setMovementDocumentNo(movementDocumentNumber);
			dbInventoryMovement.setManufacturerPartNo(dbInventory.getReferenceField9()); //Inventory Ref_Field_9 - ManufacturePartNo
			dbInventoryMovement.setStorageBin(dbInventory.getStorageBin());
			dbInventoryMovement.setStorageMethod(dbInventory.getStorageMethod());
			dbInventoryMovement.setDescription(dbInventory.getReferenceField8());
			dbInventoryMovement.setStockTypeId(dbInventory.getStockTypeId());
			dbInventoryMovement.setSpecialStockIndicator(dbInventory.getSpecialStockIndicatorId());
			if(newTotalQuantity < dbTotalQuantity) {
				dbInventoryMovement.setMovementQtyValue("N");
				log.info("MovementQtyValue: "+ dbInventoryMovement.getMovementQtyValue());
			} else {
				dbInventoryMovement.setMovementQtyValue("P");
				log.info("MovementQtyValue: "+ dbInventoryMovement.getMovementQtyValue());
			}
			dbInventoryMovement.setMovementQty(newTotalQuantity-dbTotalQuantity);
			dbInventoryMovement.setBalanceOHQty(newTotalQuantity);
			dbInventoryMovement.setInventoryUom(dbInventory.getInventoryUom());
			dbInventoryMovement.setDeletionIndicator(dbInventory.getDeletionIndicator());
			dbInventoryMovement.setCreatedBy(dbInventory.getUpdatedBy());
			dbInventoryMovement.setCreatedOn(new Date());
			log.info("Inventory Movement: "+ dbInventoryMovement);
			inventoryMovementRepository.save(dbInventoryMovement);
		}

		BeanUtils.copyProperties(updateInventory, dbInventory, CommonUtils.getNullPropertyNames(updateInventory));
		dbInventory.setUpdatedBy(loginUserID);
		dbInventory.setUpdatedOn(new Date());
		return inventoryRepository.save(dbInventory);
	}
	
	/**
	 * deleteInventory
	 * @param stockTypeId
	 */
	public void deleteInventory (String warehouseId, String packBarcodes, String itemCode, String storageBin, Long stockTypeId, 
			Long specialStockIndicatorId) {
		Inventory inventory = getInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId);
		if ( inventory != null) {
			inventory.setDeletionIndicator(1L);
			inventoryRepository.save(inventory);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + stockTypeId);
		}
	}

	/**
	 * 
	 * @param warehouseId
	 * @param packBarcodes
	 * @param itemCode
	 */ 
	public boolean deleteInventory(String warehouseId, String packBarcodes, String itemCode) {
		try {
			List<Inventory> inventoryList = getInventoryForDelete (warehouseId, packBarcodes, itemCode);
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
	
	/*
	 * -------------------------Audit Log----------------------------------------------------------------
	 */
	public void createAuditLogRecord(String companyCodeId, String plantId, String warehouseId,
									 String loginUserID, String tableName, String objectName,
									 String modifiedField, String oldValue, String newValue,
									 String packBarcodes, String itemCode,String storageBin,
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
}
