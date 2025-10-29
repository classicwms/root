package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
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
public interface InboundLineV2Repository extends JpaRepository<InboundLineV2, Long>,
        JpaSpecificationExecutor<InboundLineV2>, StreamableJpaSpecificationRepository<InboundLineV2> {

    @Query(value = "select \n" +
            "* \n" +
            "from \n" +
            "tblinboundline \n" +
            "where \n" +
            "c_id IN (:companyCode) and \n" +
            "lang_id IN (:languageId) and \n" +
            "plant_id IN(:plantId) and \n" +
            "wh_id IN (:warehouseId) and \n" +
            "ib_line_no IN (:lineNo) and \n" +
            "itm_code IN (:itemCode) and \n" +
            "pre_ib_no IN (:preInboundNo) and \n" +
            "ref_doc_no IN (:refDocNumber) and \n" +
            "is_deleted = 0", nativeQuery = true)
    InboundLineV2 getInboundLineV2(
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "lineNo") Long lineNo,
            @Param(value = "preInboundNo") String preInboundNo,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "companyCode") String companyCode,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "refDocNumber") String refDocNumber
    );

    InboundLineV2 findByWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
            String warehouseId, String refDocNumber, String preInboundNo, Long lineNo, String itemCode, Long deletionIndicator);

    Optional<InboundLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String refDocNumber, String preInboundNo, Long lineNo, String itemCode, Long deletionIndicator);

    @Query(value="select (COALESCE(il.accept_qty,0) + COALESCE(il.damage_qty,0)) as quantity \n" +
            "from tblinboundline il where il.itm_code = :itemCode and il.IB_LINE_NO = :lineNo and il.ref_doc_no = :refDocNo and \n" +
            "il.PRE_IB_NO = :preInboundNo and il.wh_id = :warehouseId and il.IS_DELETED = 0 " ,nativeQuery=true)
    public Double getQuantityByRefDocNoAndPreInboundNoAndLineNoAndItemCode(@Param("itemCode") String itemCode,
                                                                           @Param ("refDocNo") String refDocNo,
                                                                           @Param ("preInboundNo") String preInboundNo,
                                                                           @Param ("lineNo") Long lineNo,
                                                                           @Param ("warehouseId") String warehouseId);

    List<InboundLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String refDocNumber, String preInboundNo, Long deletionIndicator);

    List<InboundLineV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndReferenceField1AndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, String referenceField1, Long statusId, Long deletionIndicator);

    List<InboundLineV2> findByRefDocNumberAndDeletionIndicator(String refDocNumber, Long deletionIndicator);

    List<InboundLineV2> findByRefDocNumberAndCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
            String refDocNumber, String companyCode, String plantId, String languageId, String warehouseId, Long deletionIndicator);

    List<InboundLineV2> findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<InboundLineV2> findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    List<InboundLineV2> findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndStatusIdAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long statusId, Long deletionIndicator);

    @Query(value = "select il.wh_id as warehouseId, il.itm_code as itemCode, 'InBound' as documentType ,il.ref_doc_no as documentNumber, il.partner_code as partnerCode, "
            + " il.c_id as companyCodeId,il.plant_id as plantId,il.lang_id as languageId, il.ib_cnf_on as confirmedOn,"
            + " il.c_text as companyDescription,il.plant_text as plantDescription,il.status_text as statusDescription,il.wh_text as warehouseDescription, "
            + " (COALESCE(il.accept_qty,0) + COALESCE(il.damage_qty,0)) as movementQty, il.text as itemText ,il.mfr_name as manufacturerSKU from tblinboundline il "
//            + " join tblimbasicdata1 im on il.itm_code = im.itm_code "
            + "WHERE il.ITM_CODE in (:itemCode) AND il.is_deleted = 0  AND "
//            + "im.WH_ID in (:warehouseId) AND "
            + "(COALESCE(:manufacturerName, null) IS NULL OR (il.MFR_NAME IN (:manufacturerName))) and \n"
            + "il.C_ID in (:companyCodeId) AND il.PLANT_ID in (:plantId) AND il.LANG_ID in (:languageId) AND il.WH_ID in (:warehouseId) AND il.status_id in (:statusId) "
            + " AND (il.accept_qty is not null OR il.damage_qty is not null) AND il.IB_CNF_ON between :fromDate and :toDate ",
            nativeQuery = true)
    public List<StockMovementReportImpl> findInboundLineForStockMovement(@Param("itemCode") List<String> itemCode,
                                                                         @Param("manufacturerName") List<String> manufacturerName,
                                                                         @Param("warehouseId") List<String> warehouseId,
                                                                         @Param("companyCodeId") List<String> companyCodeId,
                                                                         @Param("plantId") List<String> plantId,
                                                                         @Param("languageId") List<String> languageId,
                                                                         @Param("statusId") List<Long> statusId,
                                                                         @Param("fromDate") Date fromDate,
                                                                         @Param("toDate") Date toDate);

    @Query(value = "Select top 1 PA_CNF_ON from tblputawayline where ref_doc_no = :refDocNo and itm_code = :itemCode " +
            "and c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId " +
            "and mfr_name = :manufacturerName order by PA_CNF_ON DESC",
            nativeQuery = true)
    public Date findDateFromPutawayLine(@Param("refDocNo") String refDocNo, @Param("itemCode") String itemCode,
                                        @Param("manufacturerName") String manufacturerName,
                                        @Param("warehouseId") String warehouseId,
                                        @Param("companyCodeId") String companyCodeId,
                                        @Param("plantId") String plantId,
                                        @Param("languageId") String languageId);

    @Modifying
    @Query(value = "Update tblinboundline set STATUS_ID = :statusId, STATUS_TEXT = :statusText, UTD_ON = getDate(), IB_CNF_ON = getDate() \n" +
            "WHERE C_ID = :companyCode AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNo AND \n" +
            "PRE_IB_NO = :preInboundNo AND ITM_CODE = :itemCode AND MFR_NAME = :mfrName AND IB_LINE_NO = :lineNo AND IS_DELETED = 0 ", nativeQuery = true)
    void updateInboundLineStatus(@Param("statusId") Long statusId, @Param("statusText") String statusText,
                                 @Param("companyCode") String companyCode, @Param("plantId") String plantId, @Param("languageId") String languageId,
                                 @Param("warehouseId") String warehouseId, @Param("refDocNo") String refDocNo, @Param("preInboundNo") String preInboundNo,
                                 @Param("itemCode") String itemCode, @Param("mfrName") String mfrName, @Param("lineNo") Long lineNo);

    List<InboundLineV2> findByRefDocNumberAndCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
            String refDocNumber, String companyCode, String plantId, String languageId, String warehouseId, Long statusId, Long deletionIndicator);

    List<InboundLineV2> findByRefDocNumberAndPreInboundNoAndCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
            String refDocNumber, String preInboundNo, String companyCode, String plantId, String languageId, String warehouseId, Long statusId, Long deletionIndicator);



    @Transactional
    @Procedure(procedureName = "grheader_status_update_proc")
    public void updateGrheaderStatusUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("goodsReceiptNo") String goodsReceiptNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedOn") Date updatedOn
    );

    @Transactional
    @Procedure(procedureName = "inboundline_status_update_ib_cnf_proc")
    public void updateInboundLineStatusUpdateInboundConfirmProc(
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

    @Modifying
    @Query(value = " UPDATE tblinboundline SET STATUS_ID   = :statusId, STATUS_TEXT = :statusDescription, REF_FIELD_2 = 'TRUE', IB_CNF_ON   = :updatedOn, IB_CNF_BY   = :updatedBy \n" +
            "WHERE IS_DELETED = 0 AND STATUS_ID <> 24 AND ITM_CODE = :itemCode AND MFR_NAME = :manufacturerName AND C_ID = :companyCodeId \n" +
            "AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber \n" +
            "AND PRE_IB_NO = :preInboundNo AND IB_LINE_NO = :lineNumber", nativeQuery = true)
    void updateInboundLineStatusUpdateInboundConfirmIndividualItem(
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("lineNumber") Long lineNumber);


    @Query(value = "select * from tblinboundline where ref_doc_no = :refDocNo and pre_ib_no = :preInboundNo \n" +
            "and c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId \n" +
            "and status_id = :statusId and ref_field_2 = 'true' and is_deleted = 0 ", nativeQuery = true)
    public List<InboundLineV2> getInboundLinesV2ForInboundConfirm(@Param("companyCode") String companyCode,
                                                                  @Param("plantId") String plantId,
                                                                  @Param("languageId") String languageId,
                                                                  @Param("warehouseId") String warehouseId,
                                                                  @Param("refDocNo") String refDocNo,
                                                                  @Param("preInboundNo") String preInboundNo,
                                                                  @Param("statusId") Long statusId);

    @Query(value = "select count(ref_doc_no) from tblinboundline where ref_doc_no = :refDocNo and pre_ib_no = :preInboundNo \n" +
            "and c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId \n" +
            "and status_id = :statusId and ref_field_2 = 'true' and is_deleted = 0 ", nativeQuery = true)
    public Long getInboundLinesV2CountForInboundConfirmWithStatusId(@Param("companyCode") String companyCode,
                                                                    @Param("plantId") String plantId,
                                                                    @Param("languageId") String languageId,
                                                                    @Param("warehouseId") String warehouseId,
                                                                    @Param("refDocNo") String refDocNo,
                                                                    @Param("preInboundNo") String preInboundNo,
                                                                    @Param("statusId") Long statusId);
    @Query(value = "select count(ref_doc_no) from tblinboundline where ref_doc_no = :refDocNo and pre_ib_no = :preInboundNo \n" +
            "and c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId \n" +
            "and is_deleted = 0 ", nativeQuery = true)
    public Long getInboundLinesV2CountForInboundConfirm(@Param("companyCode") String companyCode,
                                                        @Param("plantId") String plantId,
                                                        @Param("languageId") String languageId,
                                                        @Param("warehouseId") String warehouseId,
                                                        @Param("refDocNo") String refDocNo,
                                                        @Param("preInboundNo") String preInboundNo);

}

