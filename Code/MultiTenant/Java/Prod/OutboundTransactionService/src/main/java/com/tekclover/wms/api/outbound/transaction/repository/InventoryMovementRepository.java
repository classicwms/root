package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.PerpetualLineEntityImpl;
import com.tekclover.wms.api.outbound.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.outbound.transaction.model.inventory.InventoryMovement;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@Transactional
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement,Long>,
		JpaSpecificationExecutor<InventoryMovement>, StreamableJpaSpecificationRepository<InventoryMovement> {
	
	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize", value="1000"))
	public List<InventoryMovement> findAll();
	
	/**
	 * 
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param movementType
	 * @param submovementType
	 * @param palletCode
	 * @param caseCode
	 * @param packBarcodes
	 * @param itemCode
	 * @param variantCode
	 * @param variantSubCode
	 * @param batchSerialNumber
	 * @param movementDocumentNo
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<InventoryMovement> 
		findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeAndSubmovementTypeAndPalletCodeAndCaseCodeAndPackBarcodesAndItemCodeAndVariantCodeAndVariantSubCodeAndBatchSerialNumberAndMovementDocumentNoAndDeletionIndicator(
				String languageId, String companyCodeId, String plantId, String warehouseId, Long movementType, Long submovementType, String palletCode, String caseCode, String packBarcodes, String itemCode, Long variantCode, String variantSubCode, String batchSerialNumber, String movementDocumentNo, Long deletionIndicator);

	public Optional<InventoryMovement> findByMovementType(Long movementType);
	
	public List<InventoryMovement> findByMovementTypeInAndSubmovementTypeInAndCreatedOnBetween(List<Long> movementType, 
			List<Long> submovementType, Date dateFrom, Date dateTo);
	
	/**
	 * 
	 * @param warehouseId
	 * @param itemCode
	 * @param startDate
	 * @param endDate
	 * @param movementType
	 * @param submovementType
	 * @return
	 */
	public List<InventoryMovement> findByWarehouseIdAndItemCodeAndCreatedOnBetweenAndMovementTypeAndSubmovementTypeInOrderByCreatedOnAsc (
			String warehouseId, String itemCode, Date startDate, Date endDate, Long movementType, List<Long> submovementType);

	@Query(value = "Select \n" +
			" im.lang_id as languageId,im.c_id as companyCodeId, im.plant_id as plantId, im.wh_id as warehouseId, im.itm_code as itemCode, \n " +
			" im.st_bin as storageBin, im.stck_typ_id as stockTypeId,im.sp_st_ind_id as specialStockIndicator, im.pack_barcode as packBarcodes,\n" +
			" (COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0)) as inventoryQuantity, i.inv_uom as inventoryUom, \n" + 
			" i.ref_field_8 as itemDesc,i.ref_field_9 as manufacturerPartNo,i.ref_field_10 as storageSectionId\n" +
			" from tblinventorymovement as im join tblinventory as i on \n" +
			" im.c_id = i.c_id and im.lang_id = i.lang_id and im.plant_id = i.plant_id and i.is_deleted = 0\n" +
			" and im.wh_id = i.wh_id and im.pack_barcode = i.pack_barcode and im.st_bin = i.st_bin and im.itm_code = i.itm_code \n" +
			" where \n" +
			" im.MVT_TYP_ID in :movementTypeId \n" +
			" and im.SUB_MVT_TYP_ID in :subMovementTypeId \n" +
			" and im.IM_CTD_ON between :fromDate and :toDate \n" +
			" group by im.lang_id,im.c_id,im.plant_id,im.wh_id,im.itm_code,im.st_bin,im.stck_typ_id,im.sp_st_ind_id,im.pack_barcode,\n" +
			" i.inv_uom ,i.ref_field_8,i.ref_field_9,i.ref_field_10,(COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0))", nativeQuery = true)
	public List<PerpetualLineEntityImpl> getRecordsForRunPerpetualCount (
			@Param(value = "movementTypeId") List<Long> movementTypeId,
			@Param(value = "subMovementTypeId") List<Long> subMovementTypeId,
			@Param(value = "fromDate") Date fromDate,
			@Param(value = "toDate") Date toDate);

	@Transactional
	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize",value="100"))
	@Query(value = "Select \n" +
			" im.lang_id as languageId,im.c_id as companyCodeId, im.plant_id as plantId, im.wh_id as warehouseId, im.itm_code as itemCode, \n " +
			" im.st_bin as storageBin, im.stck_typ_id as stockTypeId,im.sp_st_ind_id as specialStockIndicator, im.pack_barcode as packBarcodes,\n" +
			" (COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0)) as inventoryQuantity, i.inv_uom as inventoryUom, \n" +
			" i.ref_field_8 as itemDesc,i.ref_field_9 as manufacturerPartNo,i.ref_field_10 as storageSectionId\n" +
			" from tblinventorymovement as im join tblinventory as i on \n" +
			" im.c_id = i.c_id and im.lang_id = i.lang_id and im.plant_id = i.plant_id and i.is_deleted = 0\n" +
			" and im.wh_id = i.wh_id and im.pack_barcode = i.pack_barcode and im.st_bin = i.st_bin and im.itm_code = i.itm_code \n" +
			" where \n" +
			" im.MVT_TYP_ID in :movementTypeId \n" +
			" and im.SUB_MVT_TYP_ID in :subMovementTypeId \n" +
			" and im.IM_CTD_ON between :fromDate and :toDate \n" +
			" group by im.lang_id,im.c_id,im.plant_id,im.wh_id,im.itm_code,im.st_bin,im.stck_typ_id,im.sp_st_ind_id,im.pack_barcode,\n" +
			" i.inv_uom ,i.ref_field_8,i.ref_field_9,i.ref_field_10,(COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0))", nativeQuery = true)
	Stream<PerpetualLineEntityImpl> getRecordsForRunPerpetualCountStream (
			@Param(value = "movementTypeId") List<Long> movementTypeId,
			@Param(value = "subMovementTypeId") List<Long> subMovementTypeId,
			@Param(value = "fromDate") Date fromDate,
			@Param(value = "toDate") Date toDate);

	@Transactional
	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize",value="100"))
	@Query(value = "Select \n" +
			" im.lang_id as languageId,im.c_id as companyCodeId, im.plant_id as plantId, im.wh_id as warehouseId, im.itm_code as itemCode, \n " +
			" im.st_bin as storageBin, im.stck_typ_id as stockTypeId,im.sp_st_ind_id as specialStockIndicator, im.pack_barcode as packBarcodes,\n" +
			" (COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0)) as inventoryQuantity, i.inv_uom as inventoryUom, \n" +
			" i.ref_field_8 as itemDesc,i.ref_field_9 as manufacturerPartNo,i.ref_field_10 as storageSectionId\n" +
			" from tblinventorymovement as im join tblinventory as i on \n" +
			" im.c_id = i.c_id and im.lang_id = i.lang_id and im.plant_id = i.plant_id and i.is_deleted = 0\n" +
			" and im.wh_id = i.wh_id and im.pack_barcode = i.pack_barcode and im.st_bin = i.st_bin and im.itm_code = i.itm_code \n" +
			" where \n" +
			" im.MVT_TYP_ID in :movementTypeId \n" +
			" and im.SUB_MVT_TYP_ID in :subMovementTypeId \n" +
			" and im.IM_CTD_ON between :fromDate and :toDate \n" +
			" group by im.lang_id,im.c_id,im.plant_id,im.wh_id,im.itm_code,im.st_bin,im.stck_typ_id,im.sp_st_ind_id,im.pack_barcode,\n" +
			" i.inv_uom ,i.ref_field_8,i.ref_field_9,i.ref_field_10,(COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0))", nativeQuery = true)
	List<PerpetualLineEntityImpl> getRecordsForRunPerpetualCountV8 (
			@Param(value = "movementTypeId") List<Long> movementTypeId,
			@Param(value = "subMovementTypeId") List<Long> subMovementTypeId,
			@Param(value = "fromDate") Date fromDate,
			@Param(value = "toDate") Date toDate);

	public Optional<InventoryMovement> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeAndSubmovementTypeAndPackBarcodesAndItemCodeAndBatchSerialNumberAndMovementDocumentNoAndDeletionIndicator(
			String languageId, String companyCode, String plantId, String warehouseId, Long movementType,
			Long submovementType, String packBarcodes, String itemCode, String batchSerialNumber,
			String movementDocumentNo, long l);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Query (value = "SELECT SUM(MVT_QTY) AS SUM_MVTQTY_VALUE FROM tblinventorymovement \r\n"
	+ " WHERE ITM_CODE IN :itemCode AND MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 AND IS_DELETED = 0 \r\n"
	+ " AND IM_CTD_ON BETWEEN :dateFrom AND :dateTo GROUP BY ITM_CODE", nativeQuery = true)
	public Double findSumOfMvtQty (@Param(value = "itemCode") List<String> itemCode,
			@Param(value = "dateFrom") Date dateFrom,
			@Param(value = "dateTo") Date dateTo);

	//Description
	@Query(value = "Select tc.c_text as companyDesc,\n" +
			"tp.plant_text as plantDesc,\n" +
			"tw.wh_text as warehouseDesc from \n" +
			"tblcompanyid tc\n" +
			"join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id \n" +
			"join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id = tc.lang_id and tw.plant_id = tp.plant_id \n" +
			"where\n" +
			"tc.lang_id IN (:languageId) and \n" +
			"tc.c_id IN (:companyCodeId) and \n" +
			"tp.plant_id IN(:plantId) and \n" +
			"tw.wh_id IN (:warehouseId) and \n" +
			"tc.is_deleted = 0 and \n" +
			"tp.is_deleted = 0 and \n" +
			"tw.is_deleted = 0 ", nativeQuery = true)
    IKeyValuePair getDescription(@Param(value = "languageId") String languageId,
                                 @Param(value = "companyCodeId") String companyCodeId,
                                 @Param(value = "plantId") String plantId,
                                 @Param(value = "warehouseId") String warehouseId);

    List<InventoryMovement> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<InventoryMovement> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndReferenceNumberAndDeletionIndicator(
			String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String referenceNumber, Long deletionIndicator);

	@Query(value = "select il.wh_id as warehouseId, il.itm_code as itemCode, 'StockAdjustment' as documentType, il.ref_doc_no as documentNumber, "
			+ " il.c_id as companyCodeId, il.plant_id as plantId, il.lang_id as languageId, il.im_ctd_on as confirmedOn, "
			+ " il.c_text as companyDescription,il.plant_text as plantDescription,il.wh_text as warehouseDescription, "
			+ " COALESCE(il.mvt_qty,0) as movementQty, il.text as itemText ,il.mfr_part as manufacturerSKU from tblinventorymovement il "
			+ "WHERE il.ITM_CODE in (:itemCode) AND MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 AND "
			+ "(COALESCE(:manufacturerName, null) IS NULL OR (il.MFR_PART IN (:manufacturerName))) and \n"
			+ "il.C_ID in (:companyCodeId) AND il.PLANT_ID in (:plantId) AND il.LANG_ID in (:languageId) AND il.WH_ID in (:warehouseId) "
			+ " AND il.is_deleted = 0 AND il.IM_CTD_ON between :fromDate and :toDate ", nativeQuery = true)
	public List<StockMovementReportImpl> findStockAdjustmentForStockMovement(@Param("itemCode") List<String> itemCode,
                                                                             @Param("manufacturerName") List<String> manufacturerName,
                                                                             @Param("warehouseId") List<String> warehouseId,
                                                                             @Param("companyCodeId") List<String> companyCodeId,
                                                                             @Param("plantId") List<String> plantId,
                                                                             @Param("languageId") List<String> languageId,
                                                                             @Param("fromDate") Date fromDate,
                                                                             @Param("toDate") Date toDate);
}