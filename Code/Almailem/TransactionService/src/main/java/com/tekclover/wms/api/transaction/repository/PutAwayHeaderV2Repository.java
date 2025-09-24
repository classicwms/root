package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.impl.PutAwayHeaderImpl;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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
public interface PutAwayHeaderV2Repository extends JpaRepository<PutAwayHeaderV2, Long>,
        JpaSpecificationExecutor<PutAwayHeaderV2>, StreamableJpaSpecificationRepository<PutAwayHeaderV2> {


    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String putAwayNumber, Long deletionIndicator);

    Optional<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndGoodsReceiptNoAndPalletCodeAndCaseCodeAndPackBarcodesAndPutAwayNumberAndProposedStorageBinAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String goodsReceiptNo, String palletCode, String caseCode,
            String packBarcodes, String putAwayNumber, String proposedStorageBin, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String refDocNumber, List<Long> statusIds, Long deletionIndicator);

    List<PutAwayHeaderV2> findByWarehouseIdAndStatusIdAndInboundOrderTypeIdInAndDeletionIndicator(
            String warehouseId, Long statusId, List<Long> orderTypeId, Long deletionIndicator);

    @Query(value = "SELECT * from tblputawayheader \n"
            + "where pa_no = :putAwayNumber and is_deleted = 0", nativeQuery = true)
    public PutAwayHeaderV2 getPutAwayHeaderV2(@Param(value = "putAwayNumber") String putAwayNumber);

    PutAwayHeaderV2 findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPutAwayNumberAndDeletionIndicator(
            String companyId, String plantId, String warehouseId, String languageId, String putAwayNumber, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String refDocNumber, String preInboundNumber, Long deletionIndicator);

    PutAwayHeaderV2 findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPreInboundNoAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId,
            String preInboundNo, String refDocNumber, String packBarcodes, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndManufacturerNameAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId,
            String itemCode, String manufacturerName, Long statusId, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId, Long statusId, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
            String companyCodeId, String plantId, String warehouseId, String languageId, String itemCode, String manufacturerName, Long statusId, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
            String companyCodeId, String plantId, String warehouseId, String languageId, Long statusId, Long deletionIndicator);

    PutAwayHeaderV2 findByPutAwayNumberAndDeletionIndicator(String putAwayNumber, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, List<Long> statusIds, Long deletionIndicator);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, List<Long> statusIds, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<PutAwayHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    @Query(value = "SELECT COUNT(*) FROM tblputawayheader WHERE LANG_ID ='EN' AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n"
            + "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND STATUS_ID IN (19) AND IS_DELETED = 0", nativeQuery = true)
    public long getPutawayHeaderCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("preInboundNo") String preInboundNo,
            @Param("refDocNumber") String refDocNumber);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PutAwayHeaderV2 ib SET ib.statusId = :statusId, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCodeId = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updatePutAwayHeaderStatus(@Param("warehouseId") String warehouseId,
                                   @Param("companyCode") String companyCode,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("statusId") Long statusId,
                                   @Param("statusDescription") String statusDescription);

    List<PutAwayHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String putawayNumber, Long deletionIndicator);

    PutAwayHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preInboundNo,
            String refDocNumber, String putAwayNumber, Long deletionIndicator);

    PutAwayHeaderV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndReferenceField5AndManufacturerNameAndReferenceField9AndDeletionIndicatorOrderByCreatedBy(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preInboundNo,
            String refDocNumber, String itemCode, String manufacturerName, String lineNumber, Long deletionIndicator);

    List<PutAwayHeaderV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndReferenceField5AndManufacturerNameAndReferenceField9AndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preInboundNo,
            String refDocNumber, String itemCode, String manufacturerName, String lineNumber, Long statusId, Long deletionIndicator);

    @Transactional
    @Procedure(procedureName = "paheader_status_update_ib_cnf_proc")
    public void updatepaheaderStatusUpdateInboundConfirmProc(
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

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 and bin_cl_id in (1,7) and \n"
            + "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n"
            + "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n"
            + "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n"
            + "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n"
            + "(COALESCE(:manufacturerName, null) IS NULL OR (MFR_NAME IN (:manufacturerName))) and \n"
            + "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode)))  \n"
            + "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "select ref_field_4,c_id,plant_id,lang_id,wh_id,itm_code,mfr_name into #tblinvqty from tblinventory \n"
            + "where is_deleted=0 and bin_cl_id in (1,7) and \n"
            + "inv_id in (select inventoryId from #inv) \n"

            + "select \n"
            + "LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PRE_IB_NO preInboundNo, \n"
            + "REF_DOC_NO refDocNumber, \n"
            + "GR_NO goodsReceiptNo, \n"
            + "IB_ORD_TYP_ID inboundOrderTypeId, \n"
            + "PAL_CODE palletCode, \n"
            + "CASE_CODE caseCode, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "PA_NO putAwayNumber, \n"
            + "PROP_ST_BIN proposedStorageBin, \n"
            + "PA_QTY putAwayQuantity, \n"
            + "PA_UOM putAwayUom, \n"
            + "STR_TYP_ID strategyTypeId, \n"
            + "ST_NO strategyNo, \n"
            + "PROP_HE_NO proposedHandlingEquipment, \n"
            + "ASS_USER_ID assignedUserId, \n"
            + "STATUS_ID statusId, \n"
            + "QTY_TYPE quantityType, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
            + "REF_FIELD_4 referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "IS_DELETED deletionIndicator, \n"
            + "PA_CTD_BY createdBy, \n"
            + "PA_CTD_ON createdOn, \n"
            + "PA_UTD_BY updatedBy, \n"
            + "PA_UTD_ON updatedOn, \n"
            + "PA_CNF_BY confirmedBy, \n"
            + "PA_CNF_ON confirmedOn, \n"
            + "(select sum(ref_field_4) from #tblinvqty where itm_code=ph.ref_field_5 and mfr_name=ph.mfr_name \n"
            + "and plant_id=ph.plant_id and c_id=ph.c_id and wh_id=ph.wh_id and lang_id=ph.lang_id \n"
            + "group by itm_code,mfr_name,plant_id,wh_id,c_id,lang_id) inventoryQuantity, \n"
            + "BARCODE_ID barcodeId, \n"
            + "MFR_DATE manufacturerDate, \n"
            + "EXP_DATE expiryDate, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "ORD_QTY orderQty, \n"
            + "CBM cbm, \n"
            + "CBM_UNIT cbmUnit, \n"
            + "CBM_QTY cbmQuantity, \n"
            + "APP_STATUS approvalStatus, \n"
            + "REMARK remark, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription, \n"
            + "ACTUAL_PACK_BARCODE actualPackBarcodes, \n"
            + "MIDDLEWARE_ID middlewareId, \n"
            + "MIDDLEWARE_TABLE middlewareTable, \n"
            + "MANUFACTURER_FULL_NAME manufacturerFullName, \n"
            + "REF_DOC_TYPE referenceDocumentType, \n"
            + "TRANSFER_ORDER_DATE transferOrderDate, \n"
            + "IS_COMPLETED isCompleted, \n"
            + "IS_CANCELLED isCancelled, \n"
            + "M_UPDATED_ON mUpdatedOn, \n"
            + "SOURCE_BRANCH_CODE sourceBranchCode, \n"
            + "SOURCE_COMPANY_CODE sourceCompanyCode, \n"
            + "LEVEL_ID levelId \n"
            + "from tblputawayheader ph \n"
            + "where \n"
            + "(COALESCE(:companyCodeId, null) IS NULL OR (ph.c_id IN (:companyCodeId))) and \n"
            + "(COALESCE(:languageId, null) IS NULL OR (ph.lang_id IN (:languageId))) and \n"
            + "(COALESCE(:plantId, null) IS NULL OR (ph.plant_id IN (:plantId))) and \n"
            + "(COALESCE(:warehouseId, null) IS NULL OR (ph.wh_id IN (:warehouseId))) and \n"
            + "(COALESCE(:manufacturerName, null) IS NULL OR (ph.MFR_NAME IN (:manufacturerName))) and \n"
            + "(COALESCE(:itemCode, null) IS NULL OR (ph.REF_FIELD_5 IN (:itemCode))) and \n"
            + "(COALESCE(:refDocNumber, null) IS NULL OR (ph.REF_DOC_NO IN (:refDocNumber))) and \n"
            + "(COALESCE(:preInboundNo, null) IS NULL OR (ph.PRE_IB_NO IN (:preInboundNo))) and \n"
            + "(COALESCE(:packBarcodes, null) IS NULL OR (ph.PACK_BARCODE IN (:packBarcodes))) and \n"
            + "(COALESCE(:putAwayNumber, null) IS NULL OR (ph.PA_NO IN (:putAwayNumber))) and \n"
            + "(COALESCE(:proposedStorageBin, null) IS NULL OR (ph.PROP_ST_BIN IN (:proposedStorageBin))) and \n"
            + "(COALESCE(:proposedHandlingEquipment, null) IS NULL OR (ph.PROP_HE_NO IN (:proposedHandlingEquipment))) and \n"
            + "(COALESCE(:statusId, null) IS NULL OR (ph.STATUS_ID IN (:statusId))) and \n"
            + "(COALESCE(:inboundOrderTypeId, null) IS NULL OR (ph.IB_ORD_TYP_ID IN (:inboundOrderTypeId))) and \n"
            + "(COALESCE(:createdBy, null) IS NULL OR (ph.PA_CTD_BY IN (:createdBy))) and \n"
            + "(COALESCE(:barcodeId, null) IS NULL OR (ph.BARCODE_ID IN (:barcodeId))) and \n"
            + "(COALESCE(:manufacturerCode, null) IS NULL OR (ph.MFR_CODE IN (:manufacturerCode))) and \n"
            + "(COALESCE(:origin, null) IS NULL OR (ph.ORIGIN IN (:origin))) and \n"
            + "(COALESCE(:brand, null) IS NULL OR (ph.BRAND IN (:brand))) and \n"
            + "(COALESCE(:approvalStatus, null) IS NULL OR (ph.APP_STATUS IN (:approvalStatus))) and \n"
            + "(COALESCE(CONVERT(VARCHAR(255), :startCreatedOn), null) IS NULL OR (ph.PA_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startCreatedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endCreatedOn), null))) and\n"
            + "ph.is_deleted=0", nativeQuery = true)
    List<PutAwayHeaderImpl> findPutAwayHeader(@Param("companyCodeId") List<String> companyCodeId,
                                              @Param("plantId") List<String> plantId,
                                              @Param("languageId") List<String> languageId,
                                              @Param("warehouseId") List<String> warehouseId,
                                              @Param("itemCode") List<String> itemCode,
                                              @Param("manufacturerName") List<String> manufacturerName,
                                              @Param("refDocNumber") List<String> refDocNumber,
                                              @Param("preInboundNo") List<String> preInboundNo,
                                              @Param("packBarcodes") List<String> packBarcodes,
                                              @Param("putAwayNumber") List<String> putAwayNumber,
                                              @Param("proposedStorageBin") List<String> proposedStorageBin,
                                              @Param("proposedHandlingEquipment") List<String> proposedHandlingEquipment,
                                              @Param("createdBy") List<String> createdBy,
                                              @Param("barcodeId") List<String> barcodeId,
                                              @Param("manufacturerCode") List<String> manufacturerCode,
                                              @Param("origin") List<String> origin,
                                              @Param("brand") List<String> brand,
                                              @Param("approvalStatus") List<String> approvalStatus,
                                              @Param("statusId") List<Long> statusId,
                                              @Param("inboundOrderTypeId") List<Long> inboundOrderTypeId,
                                              @Param("startCreatedOn") Date startCreatedOn,
                                              @Param("endCreatedOn") Date endCreatedOn);


    @Modifying
    @Query(value = " UPDATE PAH SET PAH.STATUS_ID = :statusId, \n " +
            "PAH.STATUS_TEXT = :statusDescription, \n" +
            "PAH.PA_CNF_BY = :updatedBy, \n" +
            "PAH.PA_CNF_ON = :updatedOn FROM tblputawayheader PAH " +
            "INNER JOIN \n" +
            "(SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_IB_NO, IB_LINE_NO, ITM_CODE, MFR_NAME \n" +
            "FROM tblinboundline \n" +
            "WHERE IS_DELETED = 0 \n" +
            "AND REF_FIELD_2 = 'TRUE' AND STATUS_ID = :statusId AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId \n" +
            "AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo) X ON PAH.C_ID = X.C_ID AND PAH.PLANT_ID = X.PLANT_ID \n" +
            "AND PAH.LANG_ID = X.LANG_ID AND PAH.WH_ID = X.WH_ID AND PAH.REF_DOC_NO = X.REF_DOC_NO AND PAH.PRE_IB_NO = X.PRE_IB_NO \n" +
            "AND PAH.REF_FIELD_5 = X.ITM_CODE AND PAH.MFR_NAME = X.MFR_NAME AND PAH.REF_FIELD_9 = X.IB_LINE_NO \n" +
            "AND PAH.IS_DELETED = 0 ", nativeQuery = true)
    void updatePutawayHeader(@Param("companyCodeId") String companyCodeId,
                             @Param("plantId") String plantId,
                             @Param("languageId") String languageId,
                             @Param("warehouseId") String warehouseId,
                             @Param("refDocNumber") String refDocNumber,
                             @Param("preInboundNo") String preInboundNo,
                             @Param("statusId") Long statusId,
                             @Param("statusDescription") String statusDescription,
                             @Param("updatedBy") String updatedBy,
                             @Param("updatedOn") Date updatedOn);
}