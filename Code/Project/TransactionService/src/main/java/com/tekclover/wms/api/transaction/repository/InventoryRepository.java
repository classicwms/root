package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.dto.IInventory;
import com.tekclover.wms.api.transaction.model.impl.StockReportImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;

@Repository
@Transactional
public interface InventoryRepository extends PagingAndSortingRepository<Inventory,Long>, JpaSpecificationExecutor<Inventory> {
	
	public List<Inventory> findAll();
	
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
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<Inventory> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, 
				String warehouseId, String packBarcodes, String itemCode, String storageBin, 
				Long stockTypeId, Long specialStockIndicatorId, Long deletionIndicator);

	/*
	 * Pass WH_ID/ITM_CODE in INVENTORY table and Fetch ST_BIN values where PUTAWAY_BLOCK and 
	 * PICK_BLOCK are not Null and insert the 1st value. (for WH_ID=111, fetch ST_BIN values of ST_SEC_ID= ZT)
	 */
	public List<Inventory> findByWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(String warehouseId, 
			String itemCode, Long binClassId, Long deletionIndicator);

	// WH_ID/PACK_BARCODE/ITM_CODE/BIN_CL_ID=3 in INVENTORY table
	public Optional<Inventory> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndBinClassIdAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, String packBarcodes, 
			String itemCode, Long binClassId, Long deletionIndicator);

	/**
	 * 
	 * @param languageId
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param palletCode
	 * @param caseCode
	 * @param packBarcodes
	 * @param itemCode
	 * @param l
	 * @return
	 */
	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPalletCodeAndCaseCodeAndPackBarcodesAndItemCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String palletCode,
			String caseCode, String packBarcodes, String itemCode, Long deletionIndicator);

	// SQL Query for getting Inventory
	// select inv_qty, INV_UOM from tblinventory where WH_ID=110 and PACK_BARCODE=202201200892 and ITM_CODE=0203011053 and ST_BIN='GG1GL09C02'
	@Query (value = "SELECT INV_QTY AS inventoryQty, INV_UOM AS inventoryUom FROM tblinventory "
			+ "WHERE WH_ID = :warehouseId AND PACK_BARCODE = :packbarCode AND "
			+ "ITM_CODE = :itemCode AND ST_BIN = :storageBin", nativeQuery = true)
	public IInventory findInventoryForPeriodicRun (
			@Param(value = "warehouseId") String warehouseId,
			@Param(value = "itemCode") String itemCode,
			@Param(value = "storageBin") String storageBin,
			@Param(value = "packbarCode") String packbarCode);
	
	public Optional<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String packBarcodes,
			String itemCode, String storageBin, Long deletionIndicator);
	
	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode, Long deletionIndicator);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPalletCodeAndPackBarcodesAndStorageBinAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
			String palletCode, String packBarcodes, String storageBin, Long deletionIndicator);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPalletCodeAndPackBarcodesAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
			String palletCode, String packBarcodes, Long deletionIndicator);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPalletCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
			String palletCode, Long deletionIndicator);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndStorageBinAndStockTypeIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode, Long binClassId,
			String storageBin, Long stockTypeId, Long deletionIndicator);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndStorageBinAndAllocatedQuantityAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
			String storageBin, Double allocatedQty, Long deletionIndicator);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
			Long binClassId, Long deletionIndicator);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, 
			String packBarcodes, String itemCode, Long deletionIndicator);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndStorageBinAndStockTypeIdAndDeletionIndicatorAndInventoryQuantityGreaterThan(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode, Long binClassId,
			String storageBin, Long stockTypeId, Long l, Double d);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndStorageBinAndStockTypeIdAndBinClassIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
			String storageBin, Long stockTypeId, Long binClassId, Long deletionIndicator);

	public Optional<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndBinClassIdNotAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String packBarcodes,
			String itemCode, Long binClassId, Long deletionIndicator);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndAndStockTypeIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
			Long stockTypeId, Long l);
	
	/*
	 * Reports
	 */
	@Query (value = "SELECT SUM(INV_QTY) FROM tblinventory \r\n"
			+ " WHERE WH_ID = :warehouseId AND ITM_CODE = :itemCode AND \r\n"
			+ " ST_BIN IN :storageBin AND STCK_TYP_ID = :stockTypeId AND BIN_CL_ID = 1 \r\n"
			+ " GROUP BY ITM_CODE", nativeQuery = true)
	public List<Long> findInventoryQtyCount (
			@Param(value = "warehouseId") String warehouseId,
			@Param(value = "itemCode") String itemCode,
			@Param(value = "storageBin") List<String> storageBin,
			@Param(value = "stockTypeId") Long stockTypeId);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndBinClassIdAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
			String packBarcodes, Long binClassId, long l);

	public List<Inventory> findByWarehouseIdAndStorageBinIn(String warehouseId, List<String> storageBin); 
	public List<Inventory> findByWarehouseId(String warehouseId);
	public Page<Inventory> findByWarehouseIdAndDeletionIndicator(String warehouseId, Long delFlag, Pageable pageable);

	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize", value="100"))
	public Page<Inventory> findByWarehouseIdInAndDeletionIndicator(List<String> warehouseId, Long delFlag, Pageable pageable);
	
	public Page<Inventory> findByWarehouseIdInAndDeletionIndicatorAndItemCode(List<String> warehouseId, Long delFlag, String itemCode, Pageable pageable);
	
	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize", value="1000"))
	public List<Inventory> findByWarehouseIdInAndDeletionIndicator(List<String> warehouseId, Long delFlag);

	@Query (value = "select itemCode,warehouseId,manufacturerSKU,itemText,onHandQty,damageQty,holdQty,(COALESCE(onHandQty ,0) + COALESCE(damageQty,0) + COALESCE(holdQty,0)) as availableQty from \r\n"
			+ "(select i.itm_code as itemCode,i.wh_id as warehouseId ,im.mfr_part as manufacturerSKU , im.text as itemText , \r\n"
			+ "(case \r\n"
			+ "WHEN :stockTypeText in ('ALL','ON HAND') THEN (select sum(CASE WHEN inv_qty > 0 THEN inv_qty ELSE 0 END) + sum(COALESCE(alloc_qty,0)) from tblinventory where wh_id IN (:warehouseIds) and itm_code = i.itm_code and stck_typ_id = 1 and IS_DELETED = 0 and ref_field_10 in ('ZB', 'ZG', 'ZC', 'ZT'))\r\n"
			+ "ELSE 0\r\n"
			+ "END ) as onHandQty,\r\n"
			+ "(case \r\n"
			+ "WHEN :stockTypeText in ('ALL','ON HAND') THEN (select sum(CASE WHEN inv_qty > 0 THEN inv_qty ELSE 0 END) from tblinventory where wh_id IN (:warehouseIds) and itm_code = i.itm_code and stck_typ_id = 1 and IS_DELETED = 0 and ref_field_10 in ('ZD'))\r\n"
			+ "WHEN :stockTypeText = 'DAMAGED' THEN (select sum(CASE WHEN inv_qty > 0 THEN inv_qty ELSE 0 END) from tblinventory where wh_id IN (:warehouseIds) and itm_code = i.itm_code and IS_DELETED = 0 and ref_field_10 in ('ZD'))\r\n"
			+ "ELSE 0\r\n"
			+ "END ) as damageQty,\r\n"
			+ "(case \r\n"
			+ "WHEN :stockTypeText in ('ALL','ON HAND') THEN (select sum(CASE WHEN inv_qty > 0 THEN inv_qty ELSE 0 END) from tblinventory where wh_id IN (:warehouseIds) and itm_code = i.itm_code and stck_typ_id = 7 and IS_DELETED = 0 and ref_field_10 in ('ZB', 'ZG', 'ZC', 'ZT'))\r\n"
			+ "WHEN :stockTypeText = 'HOLD' THEN (select sum(CASE WHEN inv_qty > 0 THEN inv_qty ELSE 0 END) from tblinventory where wh_id IN (:warehouseIds) and itm_code = i.itm_code and stck_typ_id = 7 and IS_DELETED = 0and ref_field_10 in ('ZB', 'ZG', 'ZC', 'ZT'))\r\n"
			+ "ELSE 0\r\n"
			+ "END ) as holdQty\r\n"
			+ "from tblinventory i \r\n"
			+ "join tblimbasicdata1 im on i.itm_code = im.itm_code\r\n"
			+ "where (:itemText IS NULL or (i.ref_field_8 = :itemText)) \r\n"
			+ "AND i.wh_id IN (:warehouseIds) \r\n"
			+ "AND (COALESCE(:itemCodes, null) IS NULL OR (i.itm_code IN (:itemCodes))) AND i.IS_DELETED = 0 \r\n"
			+ "group by i.itm_code ,i.wh_id , im.mfr_part , im.text) as X", nativeQuery = true)
	public List<StockReportImpl> getAllStockReport (
			@Param(value = "warehouseIds") List<String> warehouseId,
			@Param(value = "itemCodes") List<String> itemCode,
			@Param(value = "itemText") String itemText,
			@Param(value = "stockTypeText") String stockTypeText);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceField10InAndBinClassIdAndInventoryQuantityGreaterThan(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
			List<String> storageSectionIds, long l, Double m);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceField10InAndStockTypeIdAndBinClassIdAndInventoryQuantityGreaterThan(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
			List<String> storageSectionIds, Long stockTypeId, long l, Double m);
}