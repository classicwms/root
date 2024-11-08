package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.tekclover.wms.api.transaction.model.impl.StockMovementReportImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLineEntityImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;

@Repository
@Transactional
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement,Long>, JpaSpecificationExecutor<InventoryMovement> {
	
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

	@Query(value = "select im.wh_id as warehouseId, im.itm_code as itemCode, 'Adjustment' as documentType, im.mvt_doc_no as documentNumber, im.ref_field_6 as customerCode, \n"
			+ " convert(varchar, im.im_ctd_on, 105) as createdOn, convert(varchar, im.im_ctd_on, 8) as createdTime, im.im_ctd_on as confirmedOn, \n"
			+ " im.mvt_qty as movementQty, im.text as itemText, im.mfr_part as manufacturerSKU from tblinventorymovement im \n"
			+ " WHERE im.ITM_CODE in (:itemCode) AND im.WH_ID in (:warehouseId) AND \n"
			+ " (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (im.IM_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null)))\n"
			+ " AND im.MVT_QTY <> 0 AND im.MVT_TYP_ID = 4 AND im.SUB_MVT_TYP_ID = 1 AND im.IS_DELETED = 0", nativeQuery = true)
	public List<StockMovementReportImpl> findStockMovement(@Param("itemCode") List<String> itemCode,
														   @Param("warehouseId") List<String> warehouseId,
														   @Param("fromDate") Date fromDate,
														   @Param("toDate") Date toDate);
}