package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.report.ContainerReceiptInboundImpl;
import com.tekclover.wms.api.inbound.transaction.model.report.InboundReceiptConfirm;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import com.tekclover.wms.api.inbound.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
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

    @Query(value = "select (COALESCE(il.accept_qty,0) + COALESCE(il.damage_qty,0)) as quantity \n" +
            "from tblinboundline il where il.itm_code = :itemCode and il.IB_LINE_NO = :lineNo and il.ref_doc_no = :refDocNo and \n" +
            "il.PRE_IB_NO = :preInboundNo and il.wh_id = :warehouseId and il.IS_DELETED = 0 ", nativeQuery = true)
    public Double getQuantityByRefDocNoAndPreInboundNoAndLineNoAndItemCode(@Param("itemCode") String itemCode,
                                                                           @Param("refDocNo") String refDocNo,
                                                                           @Param("preInboundNo") String preInboundNo,
                                                                           @Param("lineNo") Long lineNo,
                                                                           @Param("warehouseId") String warehouseId);

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

    @Modifying(clearAutomatically = true)
    @Query("UPDATE InboundLineV2 ib SET ib.statusId = :statusId, ib.confirmedBy = :confirmedBy, ib.confirmedOn = :confirmedOn, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId and ib.statusId = 20")
    void updateInboundLineStatus(@Param("warehouseId") String warehouseId,
                                 @Param("companyCode") String companyCode,
                                 @Param("plantId") String plantId,
                                 @Param("languageId") String languageId,
                                 @Param("refDocNumber") String refDocNumber,
                                 @Param("statusId") Long statusId,
                                 @Param("statusDescription") String statusDescription,
                                 @Param("confirmedBy") String confirmedBy,
                                 @Param("confirmedOn") Date confirmedOn);

    List<InboundLineV2> findByRefDocNumberAndCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
            String refDocNumber, String companyCode, String plantId, String languageId, String warehouseId, Long statusId, Long deletionIndicator);

    List<InboundLineV2> findByRefDocNumberAndPreInboundNoAndCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
            String refDocNumber, String preInboundNo, String companyCode, String plantId, String languageId, String warehouseId, Long statusId, Long deletionIndicator);

    @Transactional
    @Procedure(procedureName = "inboundline_status_update_proc")
    public void updateInboundLineStatusUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("itmCode") String itmCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("lineNumber") Long lineNumber,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedOn") Date updatedOn
    );

    @Transactional
    @Procedure(procedureName = "amghara_inboundline_status_update_new_proc")
    public void updateInboundLineStatusUpdateNewProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("lineNumber") Long lineNumber,
            @Param("itmCode") String itmCode,
            @Param("mfrName") String mfrName,
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

    @Transactional
    @Procedure(procedureName = "inboundline_status_update_ib_cnf_individual_proc")
    public void updateInboundLineStatusUpdateInboundConfirmIndividualItemProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("lineNumber") Long lineNumber,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

    InboundLineV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, Long lineNo, String itemCode, String manufacturerName, Long deletionIndicator);


    @Query(value = "select * from tblinboundline where ref_doc_no = :refDocNo \n" +
            "and c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId \n" +
            "and status_id = :statusId and status_id != :cnfStatusId and ref_field_2 = 'true' and is_deleted = 0 ", nativeQuery = true)
    public List<InboundLineV2> getInboundLinesV2ForInboundConfirm(@Param("companyCode") String companyCode,
                                                                  @Param("plantId") String plantId,
                                                                  @Param("languageId") String languageId,
                                                                  @Param("warehouseId") String warehouseId,
                                                                  @Param("refDocNo") String refDocNo,
                                                                  @Param("statusId") Long statusId,
                                                                  @Param("cnfStatusId") Long cnfStatusId);

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

    InboundLineV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, Long lineNo, String itemCode, String manufacturerName, Long statusId, Long deletionIndicator);



    @Modifying
    @Query(value = "UPDATE tblinboundline SET STATUS_ID = :statusId \r\n"
            + " WHERE IB_LINE_NO = :lineNo and ITM_CODE = :itemCode and ref_doc_no = :refDocNumber and pre_ib_no = :preInboundNo \n" +
            "and c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId \n" +
            "and is_deleted = 0 ",nativeQuery = true)
    void updateStatusId(@Param(value = "statusId") Long statusId,
                        @Param(value = "warehouseId") String warehouseId,
                        @Param(value = "lineNo") Long lineNo,
                        @Param(value = "preInboundNo") String preInboundNo,
                        @Param(value = "itemCode") String itemCode,
                        @Param(value = "companyCode") String companyCode,
                        @Param(value = "plantId") String plantId,
                        @Param(value = "languageId") String languageId,
                        @Param(value = "refDocNumber") String refDocNumber
    );
    @Transactional
    @Procedure(procedureName = "inboundline_status_update_new_proc")
    public void updateInboundLineStatusUpdateNewProcV5(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedOn") Date updatedOn
    );

    @Modifying(clearAutomatically = true)
    @Query(value = "update tblinboundline set is_deleted = 1 where REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo ",nativeQuery = true)
    void softDeleteByRefDocNo(@Param("refDocNumber") String refDocNumber,
                              @Param("preInboundNo") String preInboundNo);

    @Modifying
    @Query(value = "update tblinboundline set status_id = :statusId , status_text = :statusText, act_accept_qty =:actualAcceptQty, ACCEPT_QTY =:acceptQty, VAR_QTY =:varianceQty \n" +
            "where c_id = :companyId and lang_id = :languageId and plant_id = :plantId and \n " +
            "wh_id =:warehouseId and ref_doc_no = :refDocNo and pre_ib_no = :preInboundNo and ib_line_no = :lineNo and itm_code = :itemCode", nativeQuery = true)
    void updateInboundLineStatusAndQuantity(@Param("companyId") String companyId,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("warehouseId") String warehouseId,
                                            @Param("refDocNo") String refDocNo,
                                            @Param("preInboundNo") String preInboundNo,
                                            @Param("lineNo") Long lineNo,
                                            @Param("itemCode") String itemCode,
                                            @Param("statusId") Long statusId,
                                            @Param("statusText") String statusText,
                                            @Param("actualAcceptQty") Double actualAcceptQty,
                                            @Param("acceptQty") Double acceptQty,
                                            @Param("varianceQty") Double varianceQty);

    @Modifying
    @Query(value = "update tblinboundline set status_id = :statusId , status_text = :statusText \n" +
            "where c_id = :companyId and lang_id = :languageId and plant_id = :plantId and \n " +
            "wh_id =:warehouseId and ref_doc_no = :refDocNo and pre_ib_no = :preInboundNo and ib_line_no = :lineNo and itm_code = :itemCode", nativeQuery = true)
    void updateInboundLineStatus(@Param("companyId") String companyId,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("warehouseId") String warehouseId,
                                            @Param("refDocNo") String refDocNo,
                                            @Param("preInboundNo") String preInboundNo,
                                            @Param("lineNo") Long lineNo,
                                            @Param("itemCode") String itemCode,
                                            @Param("statusId") Long statusId,
                                            @Param("statusText") String statusText);

    @Modifying
    @Query(value = "update tblinboundline set status_id = :statusId , status_text = :statusText \n" +
            "where c_id = :companyId and lang_id = :languageId and plant_id = :plantId and \n " +
            "wh_id =:warehouseId and ref_doc_no = :refDocNo and pre_ib_no = :preInboundNo and ib_line_no = :lineNo and itm_code = :itemCode", nativeQuery = true)
    void updateInboundLineStatusV7(@Param("companyId") String companyId,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("refDocNo") String refDocNo,
                                   @Param("preInboundNo") String preInboundNo,
                                   @Param("lineNo") Long lineNo,
                                   @Param("itemCode") String itemCode,
                                   @Param("statusId") Long statusId,
                                   @Param("statusText") String statusText);

    @Modifying
    @Query(value = "UPDATE tblinboundline SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, REF_FIELD_2 = 'TRUE', " +
            " IB_CNF_ON = :updatedOn, IB_CNF_BY = :updatedBy " +
            " WHERE \n " +
            " IS_DELETED = 0 AND status_id <> 24 AND ITM_CODE = :itemCode AND MFR_NAME = :manufacturerName AND \n " +
            " C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \n " +
            " REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND IB_LINE_NO = :lineNumber", nativeQuery = true)
    public void updateConfirmIndividualItemV10(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("lineNumber") Long lineNumber,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn);

    void deleteByCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndRefDocNumberAndLineNoAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId, String itemCode,String refDocNumber, Long lineNo, String preInboundNo, Long deletionIndicator);

    void deleteByCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId, String itemCode,String refDocNumber, String preInboundNo, Long deletionIndicator);




    @Modifying
    @Query(value =
            "UPDATE tblinboundline SET " +
                    "ACCEPT_QTY = ISNULL(ACCEPT_QTY, 0) + :acceptedQty, \n" +
                    "VAR_QTY = COALESCE(:varQty, VAR_QTY), \n" +
                    "DAMAGE_QTY = ISNULL(DAMAGE_QTY, 0) + :damageQty, \n" +
                    "STATUS_ID = COALESCE(:statusId, STATUS_ID), \n" +
                    "QTY_IN_CASE = COALESCE(:qtyInCase, QTY_IN_CASE), \n" +
                    "MFR_DATE = COALESCE(:manufacturerDate, MFR_DATE), \n" +
                    "EXP_DATE = COALESCE(:expiryDate, EXP_DATE), \n" +
                    "COLOR = COALESCE(:color, COLOR), \n" +
                    "STATUS_TEXT = COALESCE(:statusDescription, STATUS_TEXT), \n" +
                    "GENDER = COALESCE(:gender, GENDER), \n" +
                    "REF_FIELD_3 = COALESCE(:origin, REF_FIELD_3) \n" +
                    "WHERE c_id = :companyCodeId \n" +
                    "AND plant_id = :plantId \n" +
                    "AND wh_id = :warehouseId \n" +
                    "AND ref_doc_no = :refDocNo \n" +
                    "AND PRE_IB_NO = :preInboundNo \n" +
                    "AND PARENT_PRODUCTION_ORDER_NO = :parentNo \n" +
                    "AND ITM_CODE = :itmCode \n" +
                    "AND ISNULL(ACCEPT_QTY, 0) < Ord_qty \n" +
                    "AND is_deleted = 0",
            nativeQuery = true)
    void updateInboundLineDetailsV9(@Param("acceptedQty") Double acceptedQty,
                                    @Param("varQty") Double varQty,
                                    @Param("damageQty") Double damageQty,
                                    @Param("statusId") Long statusId,
                                    @Param("qtyInCase") Double qtyInCase,
                                    @Param("manufacturerDate") Date manufacturerDate,
                                    @Param("expiryDate") Date expiryDate,
                                    @Param("statusDescription") String statusDescription,
                                    @Param("gender") String gender,
                                    @Param("color") String color,
                                    @Param("companyCodeId") String companyCodeId,
                                    @Param("plantId") String plantId,
                                    @Param("warehouseId") String warehouseId,
                                    @Param("refDocNo") String refDocNo,
                                    @Param("preInboundNo") String preInboundNo,
                                    @Param("parentNo") String parentProductionNo,
                                    @Param("itmCode") String itmCode,
                                    @Param("origin") String origin);


    @Query(value = "select * from tblinboundline where ref_doc_no = :refDocNo and PRE_IB_NO = :preInboundNo and WH_ID = :warehouseId and PLANT_ID = :plantId and \n " +
            "C_ID = :companyId AND LANG_ID = :languageId AND is_deleted = 0 ", nativeQuery = true)
    List<InboundLineV2> findInboundLineForReportV5(@Param("refDocNo") String refDocNo, @Param("preInboundNo") String preInboundNo,
                                                   @Param("warehouseId") String warehouseId, @Param("plantId") String plantId,
                                                   @Param("companyId") String companyId, @Param("languageId") String languageId);


    @Query(value = "select sum(ord_qty) orderQty, ITM_CODE itemCode, BARCODE_ID barcodeId, MFR_DATE manufacturerDate, EXP_DATE expiryDate, max(MFR_NAME) manufacturerName, sum(ACCEPT_QTY) acceptedQty, " +
            "sum(DAMAGE_QTY) damageQty, sum(NO_BAGS) noBags, max(TEXT) description, ROUND(sum(ord_qty) - (sum(ACCEPT_QTY) + sum(DAMAGE_QTY)), 2) as missingQty from tblinboundline where ref_doc_no = :refDocNo and PRE_IB_NO = :preInboundNo and WH_ID = :warehouseId and PLANT_ID = :plantId and \n " +
            "C_ID = :companyId AND LANG_ID = :languageId AND is_deleted = 0 group by ITM_CODE, BARCODE_ID, MFR_DATE, EXP_DATE ", nativeQuery = true)
    List<InboundReceiptConfirm> findInboundLineV9(@Param("refDocNo") String refDocNo, @Param("preInboundNo") String preInboundNo,
                                                  @Param("warehouseId") String warehouseId, @Param("plantId") String plantId,
                                                  @Param("companyId") String companyId, @Param("languageId") String languageId);

    @Query(value = "select sum(ord_qty) orderQty, ITM_CODE itemCode, max(MFR_NAME) manufacturerName, sum(ACCEPT_QTY) acceptedQty, " +
            "sum(DAMAGE_QTY) damageQty, sum(NO_BAGS) noBags, max(TEXT) description, ROUND(sum(ord_qty) - (sum(ACCEPT_QTY) + sum(DAMAGE_QTY)), 2) as missingQty from tblinboundline where ref_doc_no = :refDocNo and PRE_IB_NO = :preInboundNo and WH_ID = :warehouseId and PLANT_ID = :plantId and \n " +
            "C_ID = :companyId AND LANG_ID = :languageId AND is_deleted = 0 group by ITM_CODE ", nativeQuery = true)
    List<InboundReceiptConfirm> findInboundLineV5(@Param("refDocNo") String refDocNo, @Param("preInboundNo") String preInboundNo,
                                                  @Param("warehouseId") String warehouseId, @Param("plantId") String plantId,
                                                  @Param("companyId") String companyId, @Param("languageId") String languageId);


    @Query(value = "select cr.CONT_REC_NO as containerReceiptNo, cr.INV_NO as invoiceNo, cr.CONT_NO as containerNo, cr.CONT_TYP as containerType, \n" +
            "cr.CASE_NO as numberOfCases, cr.PAL_QTY as numberOfPallets, cr.ORIGIN as origin, cr.PARTNER_CODE as partnerCode, cr.STATUS_ID as statusId, \n" +
            "cr.STATUS_TEXT as statusDescription, cr.c_id as companyCodeId, cr.plant_id as plantId, cr.lang_id as languageId, cr.wh_id as warehouseId, \n" +
            "ib.PRE_IB_NO as preInboundNo, ib.ITM_CODE AS itemCode, ib.HSN_CODE as hsnCode, ib.MRP as mrp, ib.ORD_QTY as orderQty, ib.ORD_UOM as orderUom, \n" +
            "ib.ACCEPT_QTY as acceptedQty, ib.DAMAGE_QTY as damageQty, ib.PA_CNF_QTY as putawayConfirmedQty, ib.VAR_QTY as varianceQty, ib.EXP_DATE as expiryDate, ib.MATERIAL_NO as inventoryOwner, \n" +
            "ib.IB_ORD_TYP_ID as inboundOrderTypeId, ib.BARCODE_ID as barcodeId, ib.GENDER as gender, ib.IB_LINE_NO as [lineNo], ib.[TEXT] as description, ib.PARENT_PRODUCTION_ORDER_NO as parentProductionOrderNo, \n" +
            "ib.QTY_IN_CASE as qtyInCase, ib.QTY_IN_CREATE as qtyInCreate, ib.MFR_DATE as manufacturerDate, cr.REF_FIELD_1 as referenceField1, \n" +
            "cr.REF_FIELD_2 as referenceField2, cr.REF_FIELD_3 as referenceField3 , cr.REF_FIELD_4 as referenceField4, \n" +
            "cr.REF_FIELD_5 as referenceField5, cr.REF_FIELD_6 as referenceField6, cr.REF_FIELD_7 as referenceField7, cr.REF_FIELD_8 as referenceField8, \n" +
            "cr.REF_FIELD_9 as referenceField9, cr.REF_FIELD_10 as referenceField10, cr.REF_FIELD_11 as referenceField11, cr.REF_FIELD_12 as referenceField12, \n" +
            "cr.REF_FIELD_13 as referenceField13, cr.REF_FIELD_14 as referenceField14, cr.REF_FIELD_15 as referenceField15, cr.REF_FIELD_16 as referenceField16, \n" +
            "cr.REF_FIELD_17 as referenceField17 , cr.REF_FIELD_18 as referenceField18, cr.REF_FIELD_19 as referenceField19 , cr.REF_FIELD_20 as referenceField20, \n" +
            "ib.REF_FIELD_1 as ibReferenceField1, ib.REF_FIELD_2 as ibReferenceField2, ib.REF_FIELD_3 as ibReferenceField3, ib.REF_FIELD_4 as ibReferenceField4, ib.REF_FIELD_5 as ibReferenceField5, \n" +
            "ib.REF_FIELD_6 as ibReferenceField6, ib.REF_FIELD_7 as ibReferenceField7, ib.REF_FIELD_8 as ibReferenceField8, ib.REF_FIELD_9 as ibReferenceField9, ib.REF_FIELD_10 as ibReferenceField10 \n" +
            "from tblinboundline ib JOIN tblcontainerreceipt cr ON ib.c_id = cr.c_id and ib.lang_id = cr.lang_id and \n" +
            "ib.plant_id = cr.plant_id and ib.wh_id = cr.wh_id and ib.ref_doc_no = cr.inv_no where ib.c_id IN (:companyCode) and ib.plant_id IN (:plantId) and ib.lang_id IN (:languageId) and \n " +
            "(COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (ib.IB_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null))) and \n " +
            "ib.wh_id IN (:warehouseId) and (COALESCE(:refDocNumber, null) IS NULL OR (ib.ref_doc_no IN (:refDocNumber))) and (COALESCE(:inventoryOwner, null) IS NULL OR ib.MATERIAL_NO IN (:inventoryOwner)) and ib.is_deleted = 0 and cr.is_deleted = 0 \n ", nativeQuery = true)
    List<ContainerReceiptInboundImpl> getInboundLineContainerReceipt(@Param(value = "warehouseId") List<String> warehouseId,
                                                                     @Param(value = "companyCode") List<String> companyCode,
                                                                     @Param(value = "plantId") List<String> plantId,
                                                                     @Param(value = "languageId") List<String> languageId,
                                                                     @Param(value = "refDocNumber") List<String> refDocNumber,
                                                                     @Param(value = "inventoryOwner") List<String> inventoryOwner,
                                                                     @Param("fromDate") Date fromDate,
                                                                     @Param("toDate") Date toDate);

    @Query(value = "\n" +
            "Select ib.ITM_CODE, ib.BARCODE_ID, SUM(ib.ACCEPT_QTY) as totalAcceptQty, MAX(ib.REF_DOC_NO) as orderNo,\n" +
            "MAX(au.UOM_QTY) AS uomQty,\n" +
            "   CEILING(\n" +
            "    SUM(ISNULL(ib.ACCEPT_QTY, 0) + ISNULL(ib.DAMAGE_QTY, 0)) * 1.0\n" +
            "    / NULLIF(MAX(au.UOM_QTY), 0)\n" +
            "  ) AS numberOfPallets,\n" +
            "  SUM(ISNULL(ib.ACCEPT_QTY, 0) + ISNULL(ib.DAMAGE_QTY, 0)) as totalReceivedQty, \n" + /// /////----------------
            "  MAX(cr.CONT_REC_NO) AS containerReceiptNo,\n" +
            "  MAX(cr.INV_NO) AS invoiceNo, \n" +
            "    MAX(cr.CONT_NO) AS containerNo, \n" +
            "    MAX(cr.CONT_TYP) AS containerType, \n" +
            "    MAX(cr.CASE_NO) AS numberOfCases, \n" +
            "    MAX(ib.REF_FIELD_3) AS origin, \n" +
//            "    MAX(cr.REF_FIELD_30) AS partnerCode, \n" +
            "    MAX(bp.PARTNER_NM) AS partnerCode,\n" +
            "    MAX(cr.STATUS_ID) AS statusId, \n" +
            "    MAX(cr.STATUS_TEXT) AS statusDescription,\n" +
            "  MAX(cr.c_id) AS companyCodeId, \n" +
            "    MAX(cr.plant_id) AS plantId, \n" +
            "    MAX(cr.lang_id) AS languageId, \n" +
            "    MAX(cr.wh_id) AS warehouseId, \n" +
            "            \n" +
            "    ib.ITM_CODE AS itemCode, \n" +
            "    ib.BARCODE_ID AS barcodeId, \n" +
            "            \n" +
            "    MAX(ib.PRE_IB_NO) AS preInboundNo, \n" +
            "    MAX(ib.HSN_CODE) AS hsnCode, \n" +
            "    MAX(ib.MRP) AS mrp, \n" +
            "    SUM(ib.ORD_QTY) AS orderQty, \n" + /// -----------
            "    MAX(ib.ORD_UOM) AS orderUom, \n" +
            "            \n" +
            "    SUM(ib.ACCEPT_QTY) AS acceptedQty, \n" +
            "    SUM(ib.DAMAGE_QTY) AS damageQty, \n" +
            "    SUM(ib.PA_CNF_QTY) AS putawayConfirmedQty, \n" +
            "    SUM(ib.VAR_QTY) AS varianceQty, \n" +
            "            \n" +
            "    MAX(ib.EXP_DATE) AS expiryDate, \n" +
            "    MAX(ib.MFR_DATE) AS manufacturerDate, \n" +
            "    MAX(ib.MATERIAL_NO) AS inventoryOwner, \n" +
            "    MAX(ib.IB_ORD_TYP_ID) AS inboundOrderTypeId, \n" +
            "    MAX(ib.GENDER) AS [gender], \n" +
            "    MAX(ib.IB_LINE_NO) AS [lineNo], \n" +
            "    MAX(ib.[TEXT]) AS [description], \n" +
            "    MAX(ib.PARENT_PRODUCTION_ORDER_NO) AS parentProductionOrderNo, \n" +
            "            \n" +
            "    MAX(ib.QTY_IN_CASE) AS qtyInCase, \n" +
            "    MAX(ib.QTY_IN_CREATE) AS qtyInCreate, \n" +
            "            \n" +
            "    MAX(cr.REF_FIELD_1) AS referenceField1, \n" +
            "    MAX(cr.REF_FIELD_2) AS referenceField2, \n" +
            "    MAX(cr.REF_FIELD_3) AS referenceField3, \n" +
            "    MAX(cr.REF_FIELD_4) AS referenceField4, \n" +
            "    MAX(cr.REF_FIELD_5) AS referenceField5, \n" +
            "    MAX(cr.REF_FIELD_6) AS referenceField6, \n" +
            "    MAX(cr.REF_FIELD_7) AS referenceField7, \n" +
            "    MAX(cr.REF_FIELD_8) AS referenceField8, \n" +
            "    MAX(cr.REF_FIELD_9) AS referenceField9, \n" +
            "    MAX(cr.REF_FIELD_10) AS referenceField10, \n" +
            "    MAX(cr.REF_FIELD_11) AS referenceField11, \n" +
            "    MAX(cr.REF_FIELD_12) AS referenceField12, \n" +
            "    MAX(cr.REF_FIELD_13) AS referenceField13, \n" +
            "    MAX(cr.REF_FIELD_14) AS referenceField14, \n" +
            "    MAX(cr.REF_FIELD_15) AS referenceField15, \n" +
            "    MAX(cr.REF_FIELD_16) AS referenceField16, \n" +
            "    MAX(cr.REF_FIELD_17) AS referenceField17, \n" +
            "    MAX(cr.REF_FIELD_18) AS referenceField18, \n" +
            "    MAX(cr.REF_FIELD_19) AS referenceField19, \n" +
            "    MAX(cr.REF_FIELD_20) AS referenceField20, \n" +
            "    MAX(cr.REF_FIELD_25) AS referenceField25, \n" +
            "    MAX(cr.REF_FIELD_30) AS referenceField30, \n" +
            "    MAX(cr.REF_FIELD_27) AS referenceField27, \n" +
            "    MAX(cr.CTD_BY) AS createdBy, \n" +
            "            \n" +
            "    MAX(ib.REF_FIELD_1) AS ibReferenceField1, \n" +
            "    MAX(ib.REF_FIELD_2) AS ibReferenceField2, \n" +
            "    MAX(ib.REF_FIELD_3) AS ibReferenceField3, \n" +
            "    MAX(ib.REF_FIELD_4) AS ibReferenceField4, \n" +
            "    MAX(ib.REF_FIELD_5) AS ibReferenceField5, \n" +
            "    MAX(ib.REF_FIELD_6) AS ibReferenceField6, \n" +
            "    MAX(ib.REF_FIELD_7) AS ibReferenceField7, \n" +
            "    MAX(ib.REF_FIELD_8) AS ibReferenceField8, \n" +
            "    MAX(ib.REF_FIELD_9) AS ibReferenceField9, \n" +
            "    MAX(ib.REF_FIELD_10) AS ibReferenceField10 \n" +
            "FROM tblinboundline ib\n" +
            "JOIN tblcontainerreceipt cr on\n" +
            " ib.c_id = cr.c_id\n" +
            " AND ib.lang_id = cr.lang_id\n" +
            " AND ib.plant_id = cr.plant_id\n" +
            " AND ib.wh_id = cr.wh_id\n" +
            " AND ib.ref_doc_no = cr.inv_no\n" +
            "\n" +
            "LEFT JOIN tblbusinesspartner bp ON \n" +
            "    bp.C_ID = cr.c_id \n" +
            "    AND bp.PLANT_ID = cr.plant_id \n" +
            "    AND bp.WH_ID = cr.wh_id \n" +
            "    AND bp.LANG_ID = cr.lang_id \n" +
            "    AND bp.PARTNER_TYP = 4 \n" +
            "    AND bp.REF_FIELD_5 = cr.REF_FIELD_30 \n" +
            " LEFT JOIN (\n" +
            "    SELECT \n" +
            "        ITM_CODE,\n" +
            "        MAX(UOM_QTY) AS UOM_QTY\n" +
            "    FROM tblimalternateuom\n" +
            "    GROUP BY ITM_CODE\n" +
            ") au ON au.ITM_CODE = ib.ITM_CODE\n" +
            "\n" +
            " WHERE (COALESCE(:companyCode, NULL) IS NULL OR ib.c_id IN (:companyCode)) \n" +
            "              AND (COALESCE(:plantId, NULL) IS NULL OR ib.plant_id IN (:plantId)) \n" +
            "              AND (COALESCE(:languageId, NULL) IS NULL OR ib.lang_id IN (:languageId)) \n" +
            "              AND (COALESCE(:warehouseId, NULL) IS NULL OR ib.wh_id IN (:warehouseId)) \n" +
            "              AND (COALESCE(:refDocNumber, NULL) IS NULL OR ib.ref_doc_no IN (:refDocNumber)) \n" +
            "              AND (COALESCE(:barcodeId, NULL) IS NULL OR ib.BARCODE_ID IN (:barcodeId)) \n" +
            "              AND (COALESCE(:itemCode, NULL) IS NULL OR ib.ITM_CODE IN (:itemCode)) \n" +
            "              AND (COALESCE(:inventoryOwner, NULL) IS NULL OR ib.MATERIAL_NO IN (:inventoryOwner)) \n" +
            "     AND (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (ib.CTD_ON between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null))) \n " +
            "              AND ib.is_deleted = 0 \n" +
            "              AND cr.is_deleted = 0\n" +
            "group by ib.ref_doc_no, ib.ITM_CODE, ib.BARCODE_ID, \n" +
            "ib.PARENT_PRODUCTION_ORDER_NO;", nativeQuery = true)
    List<ContainerReceiptInboundImpl> getInboundLineContainerReceiptReport(@Param(value = "warehouseId") List<String> warehouseId,
                                                                           @Param(value = "companyCode") List<String> companyCode,
                                                                           @Param(value = "plantId") List<String> plantId,
                                                                           @Param(value = "languageId") List<String> languageId,
                                                                           @Param(value = "refDocNumber") List<String> refDocNumber,
                                                                           @Param(value = "barcodeId") List<String> barcodeId,
                                                                           @Param(value = "itemCode") List<String> itemCode,
                                                                           @Param(value = "inventoryOwner") List<String> inventoryOwner,
                                                                           @Param(value = "fromDate") Date fromDate,
                                                                           @Param(value = "toDate") Date toDate);

    @Modifying
    @Query(value =
            "UPDATE tblinboundline SET " +
                    "ACCEPT_QTY = ISNULL(ACCEPT_QTY, 0) + :acceptedQty, \n" +
                    "VAR_QTY = COALESCE(:varQty, VAR_QTY), \n" +
                    "DAMAGE_QTY = COALESCE(:damageQty, DAMAGE_QTY), \n" +
                    "STATUS_ID = COALESCE(:statusId, STATUS_ID), \n" +
                    "QTY_IN_CASE = COALESCE(:qtyInCase, QTY_IN_CASE), \n" +
                    "MFR_DATE = COALESCE(:manufacturerDate, MFR_DATE), \n" +
                    "EXP_DATE = COALESCE(:expiryDate, EXP_DATE), \n" +
                    "COLOR = COALESCE(:color, COLOR), \n" +
                    "STATUS_TEXT = COALESCE(:statusDescription, STATUS_TEXT), \n" +
                    "GENDER = COALESCE(:gender, GENDER) \n" +
                    "WHERE c_id = :companyCodeId \n" +
                    "AND plant_id = :plantId \n" +
                    "AND wh_id = :warehouseId \n" +
                    "AND ref_doc_no = :refDocNo \n" +
                    "AND PRE_IB_NO = :preInboundNo \n" +
                    "AND PARENT_PRODUCTION_ORDER_NO = :parentNo \n" +
                    "AND ITM_CODE = :itmCode \n" +
                    "AND is_deleted = 0",
            nativeQuery = true)
    void updateInboundLineDetailsCrossDockV9(@Param("acceptedQty") Double acceptedQty,
                                             @Param("varQty") Double varQty,
                                             @Param("damageQty") Double damageQty,
                                             @Param("statusId") Long statusId,
                                             @Param("qtyInCase") Double qtyInCase,
                                             @Param("manufacturerDate") Date manufacturerDate,
                                             @Param("expiryDate") Date expiryDate,
                                             @Param("statusDescription") String statusDescription,
                                             @Param("gender") String gender,
                                             @Param("color") String color,
                                             @Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("refDocNo") String refDocNo,
                                             @Param("preInboundNo") String preInboundNo,
                                             @Param("parentNo") String parentProductionNo,
                                             @Param("itmCode") String itmCode);
}

