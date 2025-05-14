package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface GrLineV2Repository extends JpaRepository<GrLineV2, Long>, JpaSpecificationExecutor<GrLineV2>,
        StreamableJpaSpecificationRepository<GrLineV2> {


    List<GrLineV2> findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
            String goodsReceiptNo, String itemCode, Long lineNo, String languageId,
            String companyCode, String plantId, String refDocNumber, String packBarcodes,
            String warehouseId, String preInboundNo, String caseCode, Long deletionIndicator);

    Optional<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String goodsReceiptNo, String palletCode, String caseCode,
            String packBarcodes, Long lineNo, String itemCode, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String preInboundNo, String refDocNumber, String packBarcodes,
            Long lineNo, String itemCode, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String refDocNumber, String packBarcodes, String warehouseId,
            String preInboundNo, String caseCode, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            Long lineNo, String itemCode, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String refDocNumber, String packBarcodes, List<Long> statusId, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndItemCodeAndManufacturerNameAndLineNoAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber,
            String packBarcodes, String itemCode, String manufacturerName, Long lineNo, Long deletionIndicator);
    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndItemCodeAndManufacturerNameAndLineNoAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber,
            String packBarcodes, String itemCode, String manufacturerName, Long lineNo, String preInboundNo, Long deletionIndicator);

    List<GrLineV2> findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndCreatedOnAndDeletionIndicator(
            String goodsReceiptNo, String itemCode, Long lineNo,
            String languageId, String companyCode, String plantId,
            String refDocNumber, String packBarcodes, String warehouseId,
            String preInboundNo, String caseCode, Date createdOn, Long deletionIndicator);

    List<GrLineV2> findByCompanyCodeAndLanguageIdAndBranchCodeAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String languageId, String branchCode, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String preInboundNumber, String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    GrLineV2 findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, String packBarcodes,
            String warehouseId, String preInboundNo, String itemCode, String manufacturerName, Long deletionIndicator);

    List<GrLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<GrLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Query(value = "select count(*) \n" +
            "from tblgrline where c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and \n" +
            "wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and PRE_IB_NO = :preInboundNo and is_deleted = 0 and STATUS_ID = :statusId",nativeQuery = true)
    public Long getGrLineStatus17Count(@Param("companyCode") String companyCode,
                                       @Param("plantId") String plantId,
                                       @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("refDocNumber") String refDocNumber,
                                       @Param("preInboundNo") String preInboundNo,
                                       @Param("statusId") Long statusId);

    List<GrLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndWarehouseIdAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, String warehouseId,
            String preInboundNo, String itemCode, String manufacturerName, Long lineNumber, Long deletionIndicator);

    @Query(value = "select sum(gr_qty) \n" +
            "from tblgrline where c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and \n" +
            "wh_id = :warehouseId and REF_DOC_NO = :refDocNumber and PRE_IB_NO = :preInboundNo and \n" +
            "gr_no = :goodsReceiptNo and pal_code = :palletCode and case_code = :caseCode and \n" +
            "itm_code = :itemCode and mfr_name = :manufacturerName and \n" +
            "is_deleted = 0 and ib_line_no = :lineNo \n"+
            "group by itm_code,mfr_name,pre_ib_no,ref_doc_no,gr_no,pal_code,case_code,ib_line_no,lang_id,wh_id,plant_id,c_id ",nativeQuery = true)
    public Double getGrLineQuantity(@Param("companyCode") String companyCode,
                                  @Param("plantId") String plantId,
                                  @Param("languageId") String languageId,
                                  @Param("warehouseId") String warehouseId,
                                  @Param("refDocNumber") String refDocNumber,
                                  @Param("preInboundNo") String preInboundNo,
                                  @Param("goodsReceiptNo") String goodsReceiptNo,
                                  @Param("palletCode") String palletCode,
                                  @Param("caseCode") String caseCode,
                                  @Param("itemCode") String itemCode,
                                  @Param("manufacturerName") String manufacturerName,
                                  @Param("lineNo") Long lineNo);

    @Query(value = "SELECT \n" +
            "LANG_ID languageId, \n" +
            "C_ID companyCode, \n" +
            "PLANT_ID plantId, \n" +
            "WH_ID warehouseId, \n" +
            "PRE_IB_NO preInboundNo, \n" +
            "REF_DOC_NO refDocNumber, \n" +
            "GR_NO goodsReceiptNo, \n" +
            "PAL_CODE palletCode, \n" +
            "CASE_CODE caseCode, \n" +
            "PACK_BARCODE packBarcodes, \n" +
            "IB_LINE_NO lineNumber, \n" +
            "ITM_CODE itemCode, \n" +
            "IB_ORD_TYP_ID inboundOrderTypeId, \n" +
            "VAR_ID variantCode, \n" +
            "VAR_SUB_ID variantSubCode, \n" +
            "STR_NO batchSerialNumber, \n" +
            "STCK_TYP_ID stockTypeId, \n" +
            "SP_ST_IND_ID specialStockIndicatorId, \n" +
            "ST_MTD storageMethod, \n" +
            "STATUS_ID statusId, \n" +
            "PARTNER_CODE businessPartnerCode, \n" +
            "CONT_NO containerNo, \n" +
            "INV_NO invoiceNo, \n" +
            "ITEM_TEXT itemDescription, \n" +
            "MFR_PART manufacturerPartNo, \n" +
            "HSN_CODE hsnCode, \n" +
            "VAR_TYP variantType, \n" +
            "SPEC_ACTUAL specificationActual, \n" +
            "ITM_BARCODE itemBarcode, \n" +
            "ORD_QTY orderQty, \n" +
            "ORD_UOM orderUom, \n" +
            "GR_QTY goodReceiptQty, \n" +
            "GR_UOM grUom, \n" +
            "ACCEPT_QTY acceptedQty, \n" +
            "DAMAGE_QTY damageQty, \n" +
            "QTY_TYPE quantityType, \n" +
            "ASS_USER_ID assignedUserId, \n" +
            "PAWAY_HE_NO putAwayHandlingEquipment, \n" +
            "PA_CNF_QTY confirmedQty, \n" +
            "REM_QTY remainingQty, \n" +
            "REF_ORD_NO referenceOrderNo, \n" +
            "REF_ORD_QTY referenceOrderQty, \n" +
            "CROSS_DOCK_ALLOC_QTY crossDockAllocationQty, \n" +
            "MFR_DATE manufacturerDate, \n" +
            "EXP_DATE expiryDate, \n" +
            "STR_QTY storageQty, \n" +
            "REMARK remark, \n" +
            "REF_FIELD_1 referenceField1, \n" +
            "REF_FIELD_2 referenceField2, \n" +
            "REF_FIELD_3 referenceField3, \n" +
            "REF_FIELD_4 referenceField4, \n" +
            "REF_FIELD_5 referenceField5, \n" +
            "REF_FIELD_6 referenceField6, \n" +
            "REF_FIELD_7 referenceField7, \n" +
            "REF_FIELD_8 referenceField8, \n" +
            "REF_FIELD_9 referenceField9, \n" +
            "REF_FIELD_10 referenceField10, \n" +
            "IS_DELETED deletionIndicator, \n" +
            "GR_CTD_BY createdBy, \n" +
            "(select top 1 gr_ctd_on from tblgrheader gh  \n" +
            "where gh.gr_no=gl.gr_no and gh.ref_doc_no=gl.ref_doc_no and gh.pre_ib_no = gl.pre_ib_no and \n" +
            "gh.c_id=gl.c_id and gh.plant_id=gl.plant_id and gh.lang_id=gl.lang_id and gh.wh_id=gl.wh_id) createdOn, \n" +
            "GR_UTD_BY updatedBy, \n" +
            "GR_UTD_ON updatedOn, \n" +
            "GR_CNF_BY confirmedBy, \n" +
            "GR_CNF_ON confirmedOn, \n" +
            "INV_QTY inventoryQuantity, \n" +
            "BARCODE_ID barcodeId, \n" +
            "CBM cbm, \n" +
            "CBM_UNIT cbmUnit, \n" +
            "MFR_CODE manufacturerCode, \n" +
            "MFR_NAME manufacturerName, \n" +
            "ORIGIN origin, \n" +
            "BRAND brand, \n" +
            "REJ_TYPE rejectType, \n" +
            "REJ_REASON rejectReason, \n" +
            "CBM_QTY cbmQuantity, \n" +
            "C_TEXT companyDescription, \n" +
            "PLANT_TEXT plantDescription, \n" +
            "WH_TEXT warehouseDescription, \n" +
            "ST_BIN_INTM interimStorageBin, \n" +
            "STATUS_TEXT statusDescription, \n" +
            "PURCHASE_ORDER_NUMBER purchaseOrderNumber, \n" +
            "MIDDLEWARE_ID middlewareId, \n" +
            "MIDDLEWARE_HEADER_ID middlewareHeaderId, \n" +
            "MIDDLEWARE_TABLE middlewareTable, \n" +
            "MANUFACTURER_FULL_NAME manufacturerFullName, \n" +
            "REF_DOC_TYPE referenceDocumentType, \n" +
            "BRANCH_CODE branchCode, \n" +
            "TRANSFER_ORDER_NO transferOrderNo, \n" +
            "IS_COMPLETED isCompleted \n" +
            "from tblgrline gl \n" +
            "where \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (gl.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (gl.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (gl.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (gl.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:refDocNumber, null) IS NULL OR (gl.ref_doc_no IN (:refDocNumber))) and \n" +
            "(COALESCE(:preInboundNo, null) IS NULL OR (gl.pre_ib_no IN (:preInboundNo))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (gl.pack_barcode IN (:packBarcodes))) and \n" +
            "(COALESCE(:lineNo, null) IS NULL OR (gl.ib_line_no IN (:lineNo))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (gl.itm_code IN (:itemCode))) and \n" +
            "(COALESCE(:caseCode, null) IS NULL OR (gl.case_code IN (:caseCode))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (gl.mfr_name IN (:manufacturerName))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (gl.barcode_id IN (:barcodeId))) and \n" +
            "(COALESCE(:statusId, null) IS NULL OR (gl.status_id IN (:statusId))) and \n" +
            "(COALESCE(:manufacturerCode, null) IS NULL OR (gl.mfr_code IN (:manufacturerCode))) and\n" +
            "(COALESCE(:origin, null) IS NULL OR (gl.origin IN (:origin))) and\n" +
            "(COALESCE(:interimStorageBin, null) IS NULL OR (gl.ST_BIN_INTM IN (:interimStorageBin))) and\n" +
            "(COALESCE(:brand, null) IS NULL OR (gl.BRAND IN (:brand))) and\n" +
            "(COALESCE(:rejectType, null) IS NULL OR (gl.REJ_TYPE IN (:rejectType))) and\n" +
            "(COALESCE(:rejectReason, null) IS NULL OR (gl.REJ_REASON IN (:rejectReason))) and\n" +
            "(COALESCE(:inboundOrderTypeId, null) IS NULL OR (gl.IB_ORD_TYP_ID IN (:inboundOrderTypeId))) and \n" +
            "(COALESCE(CONVERT(VARCHAR(255), :startDate), null) IS NULL OR (gl.GR_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) ",nativeQuery = true)
            public List<GrLineImpl> findGrLine(@Param("companyCodeId") List<String> companyCodeId,
                                               @Param("plantId") List<String> plantId,
                                               @Param("languageId") List<String> languageId,
                                               @Param("warehouseId") List<String> warehouseId,
                                               @Param("refDocNumber") List<String> refDocNumber,
                                               @Param("preInboundNo") List<String> preInboundNo,
                                               @Param("packBarcodes") List<String> packBarcodes,
                                               @Param("lineNo") List<Long> lineNo,
                                               @Param("itemCode") List<String> itemCode,
                                               @Param("caseCode") List<String> caseCode,
                                               @Param("manufacturerName") List<String> manufacturerName,
                                               @Param("barcodeId") List<String> barcodeId,
                                               @Param("statusId") List<Long> statusId,
                                               @Param("manufacturerCode") List<String> manufacturerCode,
                                               @Param("origin") List<String> origin,
                                               @Param("interimStorageBin") List<String> interimStorageBin,
                                               @Param("brand") List<String> brand,
                                               @Param("rejectType") List<String> rejectType,
                                               @Param("rejectReason") List<String> rejectReason,
                                               @Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate,
                                               @Param("inboundOrderTypeId") List<Long> inboundOrderTypeId);


    @Transactional
    @Procedure(procedureName = "grline_status_update_ib_cnf_proc")
    public void updateGrLineStatusUpdateInboundConfirmProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

    // Get LeadTime for GrLine Release
    @Query(value = "SELECT \n" +
            "RIGHT('0' + CAST(DATEDIFF(DAY, tgrh.gr_ctd_on, :nDate) AS VARCHAR(2)), 2) AS diffDays, \n" +
            "RIGHT('0' + CAST(DATEDIFF(HOUR, tgrh.gr_ctd_on, :nDate) % 24 AS VARCHAR(2)), 2) AS diffHours, \n" +
            "RIGHT('0' + CAST(DATEDIFF(MINUTE, tgrh.gr_ctd_on, :nDate) % 60 AS VARCHAR(2)), 2) AS diffMinutes, \n" +
            "RIGHT('0' + CAST(DATEDIFF(SECOND, tgrh.gr_ctd_on, :nDate) % 60 AS VARCHAR(2)), 2) AS diffSeconds \n" +
            "from tblgrheader tgrh \n" +
            "where \n" +
            "tgrh.lang_Id IN (:languageId) and \n" +
            "tgrh.c_id IN (:companyCodeId) and \n" +
            "tgrh.plant_Id IN (:plantId) and \n" +
            "tgrh.gr_no IN (:goodsReceiptNo) and \n" +
            "tgrh.wh_id IN (:warehouseId) and \n" +
            "tgrh.is_deleted = 0", nativeQuery = true)
    GrLineImpl getLeadTime(@Param(value = "languageId") String languageId,
                           @Param(value = "companyCodeId") String companyCodeId,
                           @Param(value = "plantId") String plantId,
                           @Param(value = "warehouseId") String warehouseId,
                           @Param(value = "goodsReceiptNo") String goodsReceiptNo,
                           @Param(value = "nDate") Date nDate);

    @Query(value = "select top 1 partner_nm from tblbusinesspartner where partner_code =:partnerCode and is_Deleted = 0", nativeQuery = true)
     String getPartnerName(@Param("partnerCode") String partnerCode);

}