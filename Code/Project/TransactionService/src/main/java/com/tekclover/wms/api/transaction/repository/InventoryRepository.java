package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.dto.IInventory;
import com.tekclover.wms.api.transaction.model.impl.InventoryImpl;
import com.tekclover.wms.api.transaction.model.impl.StockReportImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;

@Repository
@Transactional
public interface InventoryRepository extends PagingAndSortingRepository<Inventory,Long>, JpaSpecificationExecutor<Inventory> {
	
	String UPGRADE_SKIPLOCKED = "-2";
	
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
	@Transactional
	@Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
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
	
	// referenceField10
	public List<Inventory> findByWarehouseIdAndItemCodeAndBinClassIdAndReferenceField10InAndDeletionIndicator(String warehouseId, 
			String itemCode, Long binClassId, List<String> storageSectionIds, Long deletionIndicator);

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
	 * @param deletionIndicator
	 * @return
	 */
	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPalletCodeAndCaseCodeAndPackBarcodesAndItemCodeAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String palletCode,
			String caseCode, String packBarcodes, String itemCode, Long deletionIndicator);

	// SQL Query for getting Inventory
	@Query (value = "SELECT INV_QTY AS inventoryQty, INV_UOM AS inventoryUom FROM tblinventory "
			+ "WHERE WH_ID = :warehouseId AND PACK_BARCODE = :packbarCode AND "
			+ "ITM_CODE = :itemCode AND ST_BIN = :storageBin", nativeQuery = true)
	public IInventory findInventoryForPeriodicRun (
			@Param(value = "warehouseId") String warehouseId,
			@Param(value = "itemCode") String itemCode,
			@Param(value = "storageBin") String storageBin,
			@Param(value = "packbarCode") String packbarCode);
	
	@Transactional
	@Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
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
	
	@Query (value = "SELECT SUM(INV_QTY) FROM tblinventory \r\n"
			+ " WHERE WH_ID = :warehouseId AND ITM_CODE = :itemCode AND \r\n"
			+ " BIN_CL_ID = 1 \r\n"
			+ " GROUP BY ITM_CODE", nativeQuery = true)
	public Double getInventoryQtyCount (
			@Param(value = "warehouseId") String warehouseId,
			@Param(value = "itemCode") String itemCode);

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
	
	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndStorageBinAndStockTypeIdAndCreatedOnAndDeletionIndicatorAndInventoryQuantityGreaterThan(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode, Long binClassId,
			String storageBin, Long stockTypeId, Date createdOn, long l, double d);
	
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

	@Transactional
	@Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
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

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndStorageBinAndStockTypeIdAndDeletionIndicatorAndInventoryQuantityGreaterThanOrderByInventoryQuantity(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode, Long binClassId,
			String storageBin, Long stockTypeId, long l, double d);
	
	//-------------STRATEGY-APPROACH-START--------------------------------------------------------------------
	@Query (value = "SELECT ST_BIN AS storageBin, SUM(INV_QTY) AS inventoryQty FROM tblinventory \r\n"
			+ "WHERE WH_ID = :warehouseId and ITM_CODE = :itemCode AND BIN_CL_ID = 1 AND STCK_TYP_ID = 1 \r\n"
			+ "AND REF_FIELD_10 IN :storageSecIds AND IS_DELETED = 0 \r\n"
			+ "GROUP BY ST_BIN \r\n"
			+ "HAVING SUM(INV_QTY) > 0 \r\n"
			+ "ORDER BY ST_BIN, SUM(INV_QTY)", nativeQuery = true)
	public List<IInventory> findInventoryGroupByStorageBin (
			@Param(value = "warehouseId") String warehouseId,
			@Param(value = "itemCode") String itemCode,
			@Param(value = "storageSecIds") List<String> storageSecIds);
	
	@Query (value = "SELECT IU_CTD_ON AS createdOn, ITM_CODE AS itemCode, SUM(INV_QTY) AS inventoryQty, ST_BIN AS storageBin\r\n"
			+ "FROM tblinventory WHERE WH_ID = :warehouseId and ITM_CODE = :itemCode \r\n"
			+ "AND BIN_CL_ID = 1 AND STCK_TYP_ID = 1 \r\n"
			+ "AND REF_FIELD_10 IN :storageSecIds AND IS_DELETED = 0 \r\n"
			+ "GROUP BY ITM_CODE, IU_CTD_ON, ST_BIN\r\n"
			+ "HAVING SUM(INV_QTY) > 0 \r\n"
			+ "ORDER BY IU_CTD_ON, SUM(INV_QTY)", nativeQuery = true)
	public List<IInventory> findInventoryGroupByCreatedOn (
			@Param(value = "warehouseId") String warehouseId,
			@Param(value = "itemCode") String itemCode,
			@Param(value = "storageSecIds") List<String> storageSecIds);
	
	//-------------STRATEGY-APPROACH-END---------------------------------------------------------------------

	public Optional<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageBinAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String storageBin, long l);
	
	@Query (value = "SELECT SUM (INV_QTY) AS INV_QTY, SUM(ALLOC_QTY) AS ALLOC_QTY\r\n"
			+ "	  FROM tblinventory \r\n"
			+ "	  WHERE WH_ID = :wahrehouseId AND st_bin = :stBin AND IS_DELETED = 0 \r\n"
			+ "	  GROUP BY st_bin", nativeQuery = true)
	public Double[] findInventoryQtyByStBin (
			@Param(value = "warehouseId") String warehouseId,
			@Param(value = "stBin") String stBin);

	public List<Inventory> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndAndStockTypeIdAndBinClassIdAndInventoryQuantityGreaterThanAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
			Long stockTypeId, Long binClassId, Double invQty, Long deletionIndicator);
	@Transactional
//	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize",value="100"))
	@Query(value = "select \n" +
			"pl.ref_doc_no asnNumber, \n" +
			"iv.LANG_ID languageId, \n" +
			"iv.C_ID companyCodeId, \n" +
			"iv.PLANT_ID plantId, \n" +
			"iv.WH_ID warehouseId, \n" +
			"iv.PAL_CODE palletCode, \n" +
			"iv.CASE_CODE caseCode, \n" +
			"iv.PACK_BARCODE packBarcodes, \n" +
			"iv.ITM_CODE itemCode, \n" +
			"iv.VAR_ID variantCode, \n" +
			"iv.VAR_SUB_ID variantSubCode, \n" +
			"iv.STR_NO batchSerialNumber, \n" +
			"iv.ST_BIN storageBin, \n" +
			"iv.STCK_TYP_ID stockTypeId, \n" +
			"iv.SP_ST_IND_ID specialStockIndicatorId, \n" +
			"iv.REF_ORD_NO referenceOrderNo, \n" +
			"iv.STR_MTD storageMethod, \n" +
			"iv.BIN_CL_ID binClassId, \n" +
			"iv.TEXT description, \n" +
			"iv.INV_QTY inventoryQuantity, \n" +
			"iv.ALLOC_QTY allocatedQuantity, \n" +
			"iv.INV_UOM inventoryUom, \n" +
			"DATEADD(HOUR,3,iv.MFR_DATE) manufacturerDate, \n" +
			"DATEADD(HOUR,3,iv.EXP_DATE) expiryDate, \n" +
			"iv.IS_DELETED deletionIndicator, \n" +
			"iv.REF_FIELD_1 referenceField1, \n" +
			"iv.REF_FIELD_2 referenceField2, \n" +
			"iv.REF_FIELD_3 referenceField3, \n" +
			"COALESCE(iv.INV_QTY,0)+COALESCE(iv.ALLOC_QTY,0) referenceField4, \n" +
			"iv.REF_FIELD_5 referenceField5, \n" +
			"iv.REF_FIELD_6 referenceField6, \n" +
			"iv.REF_FIELD_7 referenceField7, \n" +
			"iv.REF_FIELD_8 referenceField8, \n" +
			"iv.REF_FIELD_9 referenceField9, \n" +
			"iv.REF_FIELD_10 referenceField10, \n" +
			"iv.IU_CTD_BY createdBy, \n" +
			"DATEADD(HOUR,3,iv.IU_CTD_ON) createdOn, \n" +
			"iv.UTD_BY updatedBy, \n" +
			"iv.UTD_ON updatedOn \n" +
			"from \n" +
			"tblinventory iv \n" +
			"left join tblputawayline pl on pl.pack_barcode = iv.pack_barcode and pl.itm_code = iv.itm_code and pl.prop_st_bin = iv.st_bin \n" +
			"where \n" +
			"(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
			"(COALESCE(:packBarcodes, null) IS NULL OR (iv.pack_barcode IN (:packBarcodes))) and \n" +
			"(COALESCE(:itemCode, null) IS NULL OR (iv.itm_code IN (:itemCode))) and \n" +
			"(COALESCE(:storageBin, null) IS NULL OR (iv.st_bin IN (:storageBin))) and \n" +
			"(COALESCE(:storageSectionId, null) IS NULL OR (iv.ref_field_10 IN (:storageSectionId))) and \n" +
			"(COALESCE(:stockTypeId, null) IS NULL OR (iv.stck_typ_id IN (:stockTypeId))) and \n" +
			"(COALESCE(:specialStockIndicatorId, null) IS NULL OR (iv.sp_st_ind_id IN (:specialStockIndicatorId))) and\n" +
			"(COALESCE(:binClassId, null) IS NULL OR (iv.bin_cl_id IN (:binClassId))) and\n" +
			"(COALESCE(:description, null) IS NULL OR (iv.text IN (:description))) and iv.is_deleted = 0", nativeQuery = true)
	List<InventoryImpl> findInventory (
//	Stream<InventoryImpl> findInventory (
			@Param(value = "warehouseId") List<String> warehouseId,
			@Param(value = "packBarcodes") List<String> packBarcodes,
			@Param(value = "itemCode") List<String> itemCode,
			@Param(value = "storageBin") List<String> storageBin,
			@Param(value = "storageSectionId") List<String> storageSectionId,
			@Param(value = "stockTypeId") List<Long> stockTypeId,
			@Param(value = "specialStockIndicatorId") List<Long> specialStockIndicatorId,
			@Param(value = "binClassId") List<Long> binClassId,
			@Param(value = "description") String description);

	@Query(value = "select \n" +
			"cast(max(mvt_doc_no) as numeric)+1 movementDocumentNo \n" +
			"from \n" +
			"tblinventorymovement iv \n" +
			"where \n" +
			"iv.MVT_TYP_ID = 4 and iv.SUB_MVT_TYP_ID = 1 and STR_NO = 1  and iv.is_deleted = 0", nativeQuery = true)
	String findMovementDocumentNo();
}