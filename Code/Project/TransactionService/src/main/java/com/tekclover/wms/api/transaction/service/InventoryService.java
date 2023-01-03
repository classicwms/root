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

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.dto.IInventory;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.SearchInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.specification.InventorySpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InventoryService extends BaseService {
	
	@Autowired
	private InventoryRepository inventoryRepository;

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
	 * @param packBarcodes
	 * @param itemCode
	 * @param binClassId
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
	 * @param caseCode
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
	 * @param storageBin
	 * @return
	 */
	public List<IInventory> getInventoryGroupByStorageBin (String warehouseId, String itemCode, List<String> stSecIds) {
		List<IInventory> inventory = inventoryRepository.findInventoryGroupByStorageBin(warehouseId, itemCode, stSecIds);
		return inventory;
	}
	
	/**
	 * Pass the selected WH_ID/ITM_CODE/ALLOC_QTY=0 for OB_ORD_TYP_ID = 0,1,3 
	 * @param warehouseId
	 * @param itemCode
	 * @param allocatedQty
	 * @return
	 */
//	public List<Inventory> getInventoryForAdditionalBins (String warehouseId, String itemCode, String storageBin) {
//		Warehouse warehouse = getWarehouse(warehouseId);
//		log.info("InventoryForAdditionalBins ID : warehouseId: " + warehouseId
//				+ ", itemCode: " + itemCode
//				+ ", storageBin: " + storageBin
//				);
//		List<Inventory> inventory = 
//				inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndStorageBinAndStockTypeIdAndBinClassIdAndDeletionIndicator(
//						warehouse.getLanguageId(),
//						warehouse.getCompanyCode(),
//						warehouse.getPlantId(),
//						warehouseId, 
//						itemCode, 
//						storageBin,
//						1L,
//						1L,
//						0L
//						);
//		return inventory;
//	}
	
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
	 * @param storageBin
	 * @param allocatedQty
	 * @param stockTypeId
	 * @param specialStockIndicatorId
	 * @return
	 */
//	public List<Inventory> getInventoryForAdditionalBinsForOB2 (String warehouseId, String itemCode, String storageBin, 
//			Long stockTypeId) {
//		Warehouse warehouse = getWarehouse(warehouseId);
//		List<Inventory> inventory = 
//				inventoryRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndStorageBinAndStockTypeIdAndBinClassIdAndDeletionIndicator(
//						warehouse.getLanguageId(),
//						warehouse.getCompanyCode(),
//						warehouse.getPlantId(),
//						warehouseId, 
//						itemCode, 
//						storageBin,
//						stockTypeId,
//						1L,
//						0L
//						);
// 
//		return inventory;
//	}
	
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
	 * @param searchInventoryMovement
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
	public List<Inventory> findInventory(SearchInventory searchInventory) 
			throws ParseException {
		InventorySpecification spec = new InventorySpecification(searchInventory);
		List<Inventory> results = inventoryRepository.findAll(spec);
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
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
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
	public Inventory updateInventory (String warehouseId, String packBarcodes, String itemCode, String storageBin, 
			Long stockTypeId, Long specialStockIndicatorId, 
			UpdateInventory updateInventory) 
			throws IllegalAccessException, InvocationTargetException {
		Inventory dbInventory = getInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId);
		BeanUtils.copyProperties(updateInventory, dbInventory, CommonUtils.getNullPropertyNames(updateInventory));
		return inventoryRepository.save(dbInventory);
	}
	
	/**
	 * deleteInventory
	 * @param loginUserID 
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
	 * @param palletCode
	 * @param caseCode
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
	 * -------------------------REPORTS----------------------------------------------------------------
	 */
	
}
