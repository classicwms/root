package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.inbound.transaction.model.report.FastSlowMovingDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;

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
    @Procedure(procedureName = "inboundline_status_update_new_proc")
    public void updateInboundLineStatusUpdateNewProc(
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

    InboundLineV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, Long lineNumber, String itemCode, Long statusId, Long deletionIndicator);

    //========================================Walkaroo==================================================================
    @Query(value = "select * from tblinboundline where ref_doc_no = :refDocNo and pre_ib_no = :preInboundNo \n" +
            "and c_id = :companyCode and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId \n" +
            "and status_id = :statusId and is_deleted = 0 ", nativeQuery = true)
    public List<InboundLineV2> getInboundLinesV3ForInboundConfirm(@Param("companyCode") String companyCode,
                                                                  @Param("plantId") String plantId,
                                                                  @Param("languageId") String languageId,
                                                                  @Param("warehouseId") String warehouseId,
                                                                  @Param("refDocNo") String refDocNo,
                                                                  @Param("preInboundNo") String preInboundNo,
                                                                  @Param("statusId") Long statusId);

    @Modifying
    @Query(value = "UPDATE tblinboundline set status_id = :statusId, status_text = :statusText where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber AND pre_ib_no = :preInboundNo " +
            "AND itm_code = :itemCode AND ib_line_no = :lineNo and is_deleted = 0", nativeQuery = true)
    void updateInboundLinePGI(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("warehouseId") String warehouseId,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("preInboundNo") String preInbound,
                              @Param("itemCode") String itemCode,
                              @Param("lineNo") Long lineNo,
                              @Param("statusId") Long statusId,
                              @Param("statusText") String statusText);

    @Modifying
    @Query(value = "UPDATE tblinboundline set status_id = :statusId, status_text = :statusText where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber " +
            "AND itm_code = :itemCode AND ib_line_no = :lineNo and is_deleted = 0", nativeQuery = true)
    void updateInboundLine(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("warehouseId") String warehouseId,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("itemCode") String itemCode,
                              @Param("lineNo") Long lineNo,
                              @Param("statusId") Long statusId,
                              @Param("statusText") String statusText);
    
    @Modifying
    @Query(value = "UPDATE tblinboundline set status_id = :statusId, status_text = :statusText, accept_qty = :acceptQty, var_qty = :varQty, " +
    		"ref_field_2 = :refField2 where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber " +
            "AND itm_code = :itemCode AND ib_line_no = :lineNo and is_deleted = 0", nativeQuery = true)
    void updateInboundLineV2(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("warehouseId") String warehouseId,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("itemCode") String itemCode,
                              @Param("lineNo") Long lineNo,
                              @Param("statusId") Long statusId,
                              @Param("statusText") String statusText,
                              @Param("acceptQty") Double acceptQty,
                              @Param("varQty") Double varQty,
                              @Param("refField2") String refField2);

    @Modifying
    @Query(value = "DELETE FROM tbliborder2 WHERE ref_document_no = :refDocNo AND reversal_flag IN ('X', 'x')", nativeQuery = true)
    void deleteByRefDocumentNoAndReversalFlag(@Param("refDocNo") String refDocumentNo);

    @Modifying
    @Query(value = "DELETE FROM tbliborderlines2 WHERE order_id = :orderId AND reversal_flag IN ('X', 'x')", nativeQuery = true)
    void deleteByOrderIdAndReversalFlag(@Param("orderId") String orderId);

    @Query(value = "select itm_code as itemCode,item_text as itemText, COALESCE(sum(dlv_qty),0) as deliveryQuantity \n" +
            "from tbloutboundline \n" +
            "where dlv_cnf_on between :fromDate and :toDate and wh_id = :warehouseId and dlv_qty is not null and dlv_qty > 0  \n" +
            "group by itm_code,item_text order by sum(dlv_qty) desc ", nativeQuery = true)
    List<FastSlowMovingDashboard.FastSlowMovingDashboardImpl> getFastSlowMovingDashboardData(@Param("warehouseId") String warehouseId,
                                                                                             @Param("fromDate") Date fromDate,
                                                                                             @Param("toDate") Date toDate);

}

