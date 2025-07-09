package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLineEntityImpl;
import com.tekclover.wms.api.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
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
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long>,
        JpaSpecificationExecutor<InventoryMovement>, StreamableJpaSpecificationRepository<InventoryMovement> {

    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
    public List<InventoryMovement> findAll();

    /**
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
     * @param warehouseId
     * @param itemCode
     * @param startDate
     * @param endDate
     * @param movementType
     * @param submovementType
     * @return
     */
    public List<InventoryMovement> findByWarehouseIdAndItemCodeAndCreatedOnBetweenAndMovementTypeAndSubmovementTypeInOrderByCreatedOnAsc(
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
    public List<PerpetualLineEntityImpl> getRecordsForRunPerpetualCount(
            @Param(value = "movementTypeId") List<Long> movementTypeId,
            @Param(value = "subMovementTypeId") List<Long> subMovementTypeId,
            @Param(value = "fromDate") Date fromDate,
            @Param(value = "toDate") Date toDate);

    @Transactional
    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "100"))
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
    Stream<PerpetualLineEntityImpl> getRecordsForRunPerpetualCountStream(
            @Param(value = "movementTypeId") List<Long> movementTypeId,
            @Param(value = "subMovementTypeId") List<Long> subMovementTypeId,
            @Param(value = "fromDate") Date fromDate,
            @Param(value = "toDate") Date toDate);

    public Optional<InventoryMovement> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeAndSubmovementTypeAndPackBarcodesAndItemCodeAndBatchSerialNumberAndMovementDocumentNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, Long movementType,
            Long submovementType, String packBarcodes, String itemCode, String batchSerialNumber,
            String movementDocumentNo, long l);

    public Optional<InventoryMovement> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeAndSubmovementTypeAndPackBarcodesAndItemCodeAndManufacturerNameAndMovementDocumentNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, Long movementType,
            Long submovementType, String packBarcodes, String itemCode, String manufacturerName,
            String movementDocumentNo, Long deletionIndicator);

    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = "SELECT SUM(MVT_QTY) AS SUM_MVTQTY_VALUE FROM tblinventorymovement \r\n"
            + " WHERE ITM_CODE IN :itemCode AND MVT_TYP_ID = 4 AND SUB_MVT_TYP_ID = 1 AND IS_DELETED = 0 \r\n"
            + " AND IM_CTD_ON BETWEEN :dateFrom AND :dateTo GROUP BY ITM_CODE", nativeQuery = true)
    public Double findSumOfMvtQty(@Param(value = "itemCode") List<String> itemCode,
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

//	@Transactional
//	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize",value="100"))
//	@Query(value = "Select \n" +
//			" im.lang_id as languageId,im.c_id as companyCodeId, im.plant_id as plantId, im.wh_id as warehouseId, im.itm_code as itemCode, \n " +
//			" im.st_bin as storageBin, im.stck_typ_id as stockTypeId,im.sp_st_ind_id as specialStockIndicator, im.pack_barcode as packBarcodes,\n" +
//			" (COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0)) as inventoryQuantity, i.inv_uom as inventoryUom, \n" +
//			" i.ref_field_8 as itemDesc,i.ref_field_9 as manufacturerPartNo,i.ref_field_10 as storageSectionId\n" +
//			" from tblinventorymovement as im join tblinventory as i on \n" +
//			" im.c_id = i.c_id and im.lang_id = i.lang_id and im.plant_id = i.plant_id and i.is_deleted = 0\n" +
//			" and im.wh_id = i.wh_id and im.pack_barcode = i.pack_barcode and im.st_bin = i.st_bin and im.itm_code = i.itm_code \n" +
//			" where \n" +
//			" im.MVT_TYP_ID in :movementTypeId \n" +
//			" and im.wh_id = :warehouseId \n" +
//			" and im.SUB_MVT_TYP_ID in :subMovementTypeId \n" +
//			" and (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (im.IM_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null)))\n" +
//			" group by im.lang_id,im.c_id,im.plant_id,im.wh_id,im.itm_code,im.st_bin,im.stck_typ_id,im.sp_st_ind_id,im.pack_barcode,\n" +
//			" i.inv_uom ,i.ref_field_8,i.ref_field_9,i.ref_field_10,(COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0))", nativeQuery = true)
//	List<PerpetualLineEntityImpl> getRecordsForRunPerpetualCountStreamV2 (
//			@Param(value = "movementTypeId") List<Long> movementTypeId,
//			@Param(value = "subMovementTypeId") List<Long> subMovementTypeId,
//			@Param(value = "warehouseId") List<String> warehouseId,
//			@Param(value = "fromDate") Date fromDate,
//			@Param(value = "toDate") Date toDate);

//	@Transactional
//	@QueryHints(@javax.persistence.QueryHint(name="org.hibernate.fetchSize",value="100"))
//	@Query(value = "select i.lang_id as languageId,i.c_id as companyCodeId, i.plant_id as plantId, i.wh_id as warehouseId, i.itm_code as itemCode, \n" +
//			"i.st_bin as storageBin, i.stck_typ_id as stockTypeId,i.sp_st_ind_id as specialStockIndicator, i.pack_barcode as packBarcodes,\n" +
//			"(COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0)) as inventoryQuantity, pk.pick_uom as inventoryUom,\n" +
//			"i.ref_field_8 as itemDesc,i.ref_field_9 as manufacturerPartNo,i.ref_field_10 as storageSectionId\n" +
//			"from tblinventory i join tblpickupline pk on \n" +
//			"i.c_id = pk.c_id and i.lang_id = pk.lang_id and i.plant_id = pk.plant_id and i.is_deleted = 0\n" +
//			"and i.wh_id = pk.wh_id and i.pack_barcode = pk.pick_pack_barcode and i.st_bin = pk.pick_st_bin and i.itm_code = pk.itm_code\n" +
//			"where (COALESCE(:companyCodeId, null) IS NULL OR (pk.c_id IN (:companyCodeId))) and \n" +
//			" (COALESCE(:plantId, null) IS NULL OR (pk.plant_id IN (:plantId))) and \n" +
//			" (COALESCE(:languageId, null) IS NULL OR (pk.lang_id IN (:languageId))) and \n" +
//			" (COALESCE(:warehouseId, null) IS NULL OR (pk.wh_id IN (:warehouseId))) and \n" +
//			" (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (pk.pick_ctd_on between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null)))\n" +
//			" group by i.lang_id,i.c_id,i.plant_id,i.wh_id,i.itm_code,i.st_bin,i.stck_typ_id,i.sp_st_ind_id,i.pack_barcode,\n" +
//			" pk.pick_uom ,i.ref_field_8,i.ref_field_9,i.ref_field_10,(COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0))", nativeQuery = true)
//	List<PerpetualLineEntityImpl> getRecordsForRunPerpetualCountStreamOutbound (
//			@Param(value = "companyCodeId") List<String> companyCodeId,
//			@Param(value = "plantId") List<String> plantId,
//			@Param(value = "languageId") List<String> languageId,
//			@Param(value = "warehouseId") List<String> warehouseId,
//			@Param(value = "fromDate") Date fromDate,
//			@Param(value = "toDate") Date toDate);

    @Transactional
    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    @Query(value = "SELECT INV_ID as inventoryId, LANG_ID as languageId, C_ID as companyCodeId, PLANT_ID as plantId, WH_ID as warehouseId, \n" +
            " PAL_CODE as palletCode, CASE_CODE as caseCode, PACK_BARCODE as packBarcodes, ITM_CODE as itemCode, VAR_ID as variantCode, VAR_SUB_ID as variantSubCode, \n" +
            " STR_NO as batchSerialNumber, ST_BIN as storageBin, STCK_TYP_ID as stockTypeId, SP_ST_IND_ID as specialStockIndicatorId, REF_ORD_NO as referenceOrderNo, STR_MTD as approvalLevel , \n" +
            " BIN_CL_ID as binClassId, TEXT as itemDesc, INV_QTY as inventoryQuantity, ALLOC_QTY as allocatedQuantity, INV_UOM as inventoryUom, MFR_DATE as manufacturerDate, EXP_DATE as expiryDate, IS_DELETED as deletionIndicator, REF_FIELD_1 as referenceField1, REF_FIELD_2 as referenceField2, \n" +
            " REF_FIELD_3 as referenceField3, REF_FIELD_4 as referenceField4, REF_FIELD_5 as referenceField5, REF_FIELD_6 as referenceField6, REF_FIELD_7 as referenceField7, REF_FIELD_8 as referenceField8, REF_FIELD_9 as referenceField9, REF_FIELD_10 as storageSectionId , \n" +
            " IU_CTD_BY as createdBy, IU_CTD_ON as createdOn, UTD_BY as updatedBy, UTD_ON as updatedOn, MFR_CODE as manufacturerCode, BARCODE_ID as barcodeId, CBM as cbm, level_id as levelId, tpl_partner_id as threePLPartnerId, tpl_partner_text as threePLPartnerText, CBM_UNIT as cbmUnit, CBM_PER_QTY as cbmPerQuantity, MFR_NAME as manufacturerName, \n" +
            " ORIGIN as origin, BRAND as brand, REF_DOC_NO as referenceDocumentNo, C_TEXT as companyDescription, PLANT_TEXT as plantDescription, WH_TEXT as warehouseDescription, STCK_TYP_TEXT as stockTypeDescription, STATUS_TEXT as statusDescription, TPL_CBM as threePLCbm, TPL_UOM as threePLUom, RATE as rate, \n" +
            " CURRENCY as currency, TPL_CBM_PER_QTY as threePLCbmPerQty, TPL_RATE_PER_QTY as threePLRatePerQty, TOTAL_TPL_CBM as totalThreePLCbm, TOTAL_RATE as totalRate FROM tblinventory \n" +
            " where (COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            " (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            " (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            " (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
//            " (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (iu_ctd_on between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null))) and \n" +
            " inv_id in (select max(it.inv_id) inventoryId from tblinventory it \n" +
            " join tblputawayline pt on it.c_id = pt.c_id and it.lang_id = pt.lang_id and it.plant_id = pt.plant_id and it.is_deleted = 0 \n" +
            " and it.wh_id = pt.wh_id and it.itm_code = pt.itm_code " +
//            "and it.st_bin = pt.prop_st_bin \n" +
            " where (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (pa_ctd_on between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null))) \n" +
            " group by it.c_id, it.plant_id, it.wh_id, it.itm_code, it.st_bin) and  ref_field_4 >0 ", nativeQuery = true)
    List<PerpetualLineEntityImpl> getRecordsForRunPerpetualCountStreamInbound(
            @Param(value = "companyCodeId") List<String> companyCodeId,
            @Param(value = "plantId") List<String> plantId,
            @Param(value = "languageId") List<String> languageId,
            @Param(value = "warehouseId") List<String> warehouseId,
            @Param(value = "fromDate") Date fromDate,
            @Param(value = "toDate") Date toDate);


    @Transactional
    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    @Query(value = "SELECT INV_ID as inventoryId, LANG_ID as languageId, C_ID as companyCodeId, PLANT_ID as plantId, WH_ID as warehouseId, \n" +
            " PAL_CODE as palletCode, CASE_CODE as caseCode, PACK_BARCODE as packBarcodes, ITM_CODE as itemCode, VAR_ID as variantCode, VAR_SUB_ID as variantSubCode, \n" +
            " STR_NO as batchSerialNumber, ST_BIN as storageBin, STCK_TYP_ID as stockTypeId, SP_ST_IND_ID as specialStockIndicatorId, REF_ORD_NO as referenceOrderNo, STR_MTD as approvalLevel , \n" +
            " BIN_CL_ID as binClassId, TEXT as itemDesc, INV_QTY as inventoryQuantity, ALLOC_QTY as allocatedQuantity, INV_UOM as inventoryUom, MFR_DATE as manufacturerDate, EXP_DATE as expiryDate, IS_DELETED as deletionIndicator, REF_FIELD_1 as referenceField1, REF_FIELD_2 as referenceField2, \n" +
            " REF_FIELD_3 as referenceField3, REF_FIELD_4 as referenceField4, REF_FIELD_5 as referenceField5, REF_FIELD_6 as referenceField6, REF_FIELD_7 as referenceField7, REF_FIELD_8 as referenceField8, REF_FIELD_9 as referenceField9, REF_FIELD_10 as storageSectionId , \n" +
            " IU_CTD_BY as createdBy, IU_CTD_ON as createdOn, UTD_BY as updatedBy, UTD_ON as updatedOn, MFR_CODE as manufacturerCode, BARCODE_ID as barcodeId, CBM as cbm, level_id as levelId, tpl_partner_id as threePLPartnerId, tpl_partner_text as threePLPartnerText, CBM_UNIT as cbmUnit, CBM_PER_QTY as cbmPerQuantity, MFR_NAME as manufacturerName, \n" +
            " ORIGIN as origin, BRAND as brand, REF_DOC_NO as referenceDocumentNo, C_TEXT as companyDescription, PLANT_TEXT as plantDescription, WH_TEXT as warehouseDescription, STCK_TYP_TEXT as stockTypeDescription, STATUS_TEXT as statusDescription, TPL_CBM as threePLCbm, TPL_UOM as threePLUom, RATE as rate, \n" +
            " CURRENCY as currency, TPL_CBM_PER_QTY as threePLCbmPerQty, TPL_RATE_PER_QTY as threePLRatePerQty, TOTAL_TPL_CBM as totalThreePLCbm, TOTAL_RATE as totalRate FROM tblinventory \n" +
            " where (COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            " (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            " (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            " (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
//            " (COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))) and \n" +
//            " (COALESCE(:storageBin, null) IS NULL OR (st_bin IN (:storageBin))) and \n" +
           // " (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (iu_ctd_on between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null))) and \n" +
            " inv_id in (select max(it.inv_id) inventoryId from tblinventory it \n" +
            " join tblpickupline pt on it.c_id = pt.c_id and it.lang_id = pt.lang_id and it.plant_id = pt.plant_id and it.is_deleted = 0 \n" +
            " and it.wh_id = pt.wh_id and it.itm_code = pt.itm_code and it.st_bin = pt.pick_st_bin \n" +
            " where (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (pick_ctd_on between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null))) \n" +
            "group by it.c_id, it.plant_id, it.wh_id, it.itm_code, it.st_bin) and  ref_field_4 >0 ", nativeQuery = true)
    List<PerpetualLineEntityImpl> getRecordsForRunPerpetualCountStreamOutbound(
            @Param(value = "companyCodeId") List<String> companyCodeId,
            @Param(value = "plantId") List<String> plantId,
            @Param(value = "languageId") List<String> languageId,
            @Param(value = "warehouseId") List<String> warehouseId,
            @Param(value = "fromDate") Date fromDate,
            @Param(value = "toDate") Date toDate);

}