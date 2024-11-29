package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2Stream;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Repository
@Transactional
public interface OutboundHeaderV2Repository extends JpaRepository<OutboundHeaderV2, Long>,
        JpaSpecificationExecutor<OutboundHeaderV2>,
        StreamableJpaSpecificationRepository<OutboundHeaderV2> {

    String UPGRADE_SKIPLOCKED = "-2";

    OutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndRefDocNumberAndWarehouseIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String refDocNumber, String warehouseId, Long deletionIndicator);

    OutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String refDocNumber, Long deletionIndicator);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = UPGRADE_SKIPLOCKED)})
    OutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String referenceField2, Long deletionIndicator);

    OutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long deletionIndicator);

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OutboundHeader ob SET ob.statusId = :statusId \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.refDocNumber = :refDocNumber")
    public void updateOutboundHeaderStatusAs47(@Param("companyCodeId") String companyCodeId,
                                               @Param("plantId") String plantId,
                                               @Param("languageId") String languageId,
                                               @Param("warehouseId") String warehouseId,
                                               @Param("refDocNumber") String refDocNumber,
                                               @Param("statusId") Long statusId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OutboundHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo")
    public void updateOutboundHeaderStatusV2(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("languageId") String languageId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("refDocNumber") String refDocNumber,
                                             @Param("preOutboundNo") String preOutboundNo,
                                             @Param("statusId") Long statusId,
                                             @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value ="Update tbloutboundheader SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, DLV_CNF_ON = :deliveryConfirmedOn \r\n "
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND \r\n"
            + "LANG_ID = :languageId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo", nativeQuery = true)
    public void updateOutboundHeaderStatusNewV2(@Param("companyCodeId") String companyCodeId,
                                                @Param("plantId") String plantId,
                                                @Param("languageId") String languageId,
                                                @Param("warehouseId") String warehouseId,
                                                @Param("refDocNumber") String refDocNumber,
                                                @Param("preOutboundNo") String preOutboundNo,
                                                @Param("statusId") Long statusId,
                                                @Param("statusDescription") String statusDescription,
                                                @Param("deliveryConfirmedOn") Date deliveryConfirmedOn);

//    @Transactional
//    @Procedure(procedureName = "outbound_header_update_proc")
//    void updateOutboundHeaderUpdateProc(
//            @Param("companyCodeId") String companyCodeId,
//            @Param("plantId") String plantId,
//            @Param("languageId") String languageId,
//            @Param("warehouseId") String warehouseId,
//            @Param("refDocNumber") String refDocNumber,
//            @Param("statusId") Long statusId,
//            @Param("statusDescription") String statusDescription,
//            @Param("deliveryConfirmedOn") Date deliveryConfirmedOn
//    );

    OutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPickListNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String pickListNumber, Long deletionIndicator);


//    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
//    @Query(value =
//                    "SELECT SUM(ORD_QTY) sOrdQty,REF_DOC_NO,PRE_OB_NO,C_ID,PLANT_ID,LANG_ID,WH_ID into #pobl FROM tblpreoutboundline WHERE is_deleted = 0 and \n" +
//                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) AND\n" +
//                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
//                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
//                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
//                    "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) \n" +
//                    "GROUP BY REF_DOC_NO,PRE_OB_NO,C_ID,PLANT_ID,LANG_ID,WH_ID\n" +
//
//                    "SELECT COUNT(ORD_QTY) cOrdQty,REF_DOC_NO,PRE_OB_NO,C_ID,PLANT_ID,LANG_ID,WH_ID into #cntpobl FROM tblpreoutboundline WHERE is_deleted = 0 and \n" +
//                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) AND\n" +
//                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
//                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
//                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
//                    "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) \n" +
//                    "GROUP BY REF_DOC_NO,PRE_OB_NO,C_ID,PLANT_ID,LANG_ID,WH_ID\n" +
//
//                    "select \n" +
//                    "oh.c_id companyCodeId, oh.lang_id languageId, oh.partner_code partnerCode, \n" +
//                    "oh.plant_id plantId, oh.pre_ob_no preOutboundNo,oh.ref_doc_no refDocNumber,oh.wh_id warehouseId, \n" +
//                    "oh.dlv_ctd_by createdBy, \n" +
////                    "DATEADD(HOUR,3,oh.dlv_ctd_on) createdOn, \n" +
//                    "oh.dlv_ctd_on createdOn, \n" +
//                    "oh.is_deleted deletionIndicator, \n" +
//                    "oh.dlv_cnf_by deliveryConfirmedBy, \n" +
////                    "DATEADD(HOUR,3,oh.dlv_cnf_on) deliveryConfirmedOn, \n" +
//                    "oh.dlv_cnf_on deliveryConfirmedOn, \n" +
//                    "oh.dlv_ord_no deliveryOrderNo, oh.ob_ord_typ_id outboundOrderTypeId, \n" +
////                    "DATEADD(HOUR,3,oh.ref_doc_date) refDocDate, \n" +
//                    "oh.ref_doc_date refDocDate, \n" +
//                    "oh.ref_doc_typ referenceDocumentType,oh.remark remarks, \n" +
////                    "DATEADD(HOUR,3,oh.req_del_date) requiredDeliveryDate,\n" +
//                    "oh.req_del_date requiredDeliveryDate,\n" +
//                    "oh.dlv_rev_by reversedBy, \n" +
////                    "DATEADD(HOUR,3,oh.dlv_rev_on) reversedOn, \n" +
//                    "oh.dlv_rev_on reversedOn, \n" +
//                    "oh.status_id statusId,oh.dlv_utd_by updatedBy, \n" +
////                    "DATEADD(HOUR,3,oh.dlv_utd_on) updatedOn,\n" +
//                    "oh.dlv_utd_on updatedOn,\n" +
//                    "oh.INVOICE_NO invoiceNumber,\n" +
//                    "oh.C_TEXT companyDescription,\n" +
//                    "oh.PLANT_TEXT plantDescription,\n" +
//                    "oh.WH_TEXT warehouseDescription,\n" +
//                    "oh.STATUS_TEXT statusDescription,\n" +
//                    "oh.MIDDLEWARE_ID middlewareId,\n" +
//                    "oh.MIDDLEWARE_TABLE middlewareTable,\n" +
//                    "oh.SALES_ORDER_NUMBER salesOrderNumber,\n" +
//                    "oh.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
//                    "oh.PICK_LIST_NUMBER pickListNumber,\n" +
////                    "DATEADD(HOUR,3,oh.INVOICE_DATE) invoiceDate,\n" +
//                    "oh.INVOICE_DATE invoiceDate,\n" +
//                    "oh.DELIVERY_TYPE deliveryType,\n" +
//                    "oh.CUSTOMER_ID customerId,\n" +
//                    "oh.CUSTOMER_NAME customerName,\n" +
//                    "oh.TARGET_BRANCH_CODE targetBranchCode,\n" +
//                    "oh.ADDRESS address,\n" +
//                    "oh.PHONE_NUMBER phoneNumber,\n" +
//                    "oh.ALTERNATE_NO alternateNo,\n" +
//                    "oh.TOKEN_NUMBER tokenNumber,\n" +
//                    "oh.STATUS status,\n" +
//                    "oh.CUSTOMER_TYPE customerType,\n" +
//                    "oh.ref_field_1 referenceField1,oh.ref_field_2 referenceField2,oh.ref_field_3 referenceField3, \n" +
//                    "oh.ref_field_4 referenceField4,oh.ref_field_5 referenceField5,oh.ref_field_6 referenceField6,\n" +
//                    "(CASE WHEN sum(dlv_qty) is not null THEN sum(dlv_qty) ELSE 0 END) as referenceField7,\n" +
//                    "COUNT(CASE WHEN dlv_qty is not null and dlv_qty > 0 THEN dlv_qty ELSE NULL END) as referenceField8,\n" +
//                    "(select sOrdQty from #pobl pobl1 where pobl1.REF_DOC_NO = oh.REF_DOC_NO AND pobl1.PRE_OB_NO = oh.pre_ob_no \n" +
//                            "AND pobl1.c_id = oh.c_id AND pobl1.plant_id = oh.plant_id AND pobl1.lang_id = oh.lang_id AND pobl1.wh_id = oh.wh_id ) as referenceField9,\n" +
//                    "(select cOrdQty from #cntpobl pobl2 where pobl2.REF_DOC_NO = oh.REF_DOC_NO AND pobl2.PRE_OB_NO = oh.pre_ob_no \n" +
//                            "AND pobl2.c_id = oh.c_id AND pobl2.plant_id = oh.plant_id AND pobl2.lang_id = oh.lang_id AND pobl2.wh_id = oh.wh_id ) as referenceField10 \n" +
//                    "from tbloutboundheader oh\n" +
//                    "join tbloutboundline ol on ol.ref_doc_no = oh.ref_doc_no and ol.pre_ob_no = oh.pre_ob_no and ol.c_id = oh.c_id and ol.plant_id = oh.plant_id and ol.wh_id = oh.wh_id and ol.lang_id = oh.lang_id\n" +
//                    "where ol.ref_field_2 is null and oh.is_deleted = 0 and \n" +
//                    "(COALESCE(:companyCodeId, null) IS NULL OR (oh.c_id IN (:companyCodeId))) and \n" +
//                    "(COALESCE(:plantId, null) IS NULL OR (oh.plant_id IN (:plantId))) and \n" +
//                    "(COALESCE(:languageId, null) IS NULL OR (oh.lang_id IN (:languageId))) and \n" +
//                    "(COALESCE(:warehouseId, null) IS NULL OR (oh.wh_id IN (:warehouseId))) and \n" +
//                    "(COALESCE(:refDocNo, null) IS NULL OR (oh.ref_doc_no IN (:refDocNo))) and \n" +
//                    "(COALESCE(:preOutboundNo, null) IS NULL OR (oh.pre_ob_no IN (:preOutboundNo))) and \n" +
//                    "(COALESCE(:partnerCode, null) IS NULL OR (oh.partner_code IN (:partnerCode))) and \n" +
//                    "(COALESCE(:targetBranchCode, null) IS NULL OR (oh.target_branch_code IN (:targetBranchCode))) and \n" +
//                    "(COALESCE(:outboundOrderTypeId, null) IS NULL OR (oh.ob_ord_typ_id IN (:outboundOrderTypeId))) and \n" +
//                    "(COALESCE(:statusId, null) IS NULL OR (oh.status_id IN (:statusId))) and \n" +
//                    "(COALESCE(:soType, null) IS NULL OR (oh.ref_field_1 IN (:soType))) and\n" +
//                    "(COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) IS NULL OR (oh.REQ_DEL_DATE between COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :endRequiredDeliveryDate), null))) and\n" +
//                    "(COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) IS NULL OR (oh.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endDeliveryConfirmedOn), null))) and\n" +
//                    "(COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) IS NULL OR (oh.DLV_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) and COALESCE(CONVERT(VARCHAR(255), :endOrderDate), null)))\n" +
//                    "group by oh.c_id , oh.lang_id, oh.partner_code, oh.plant_id, oh.pre_ob_no,oh.ref_doc_no ,oh.wh_id,oh.dlv_ctd_by,oh.dlv_ctd_on,oh.is_deleted,oh.dlv_cnf_by,oh.dlv_cnf_on,\n" +
//                    "oh.dlv_ord_no, oh.ob_ord_typ_id,oh.ref_doc_date,oh.ref_doc_typ,oh.remark,oh.req_del_date,oh.dlv_rev_by,oh.dlv_rev_on,oh.status_id,oh.dlv_utd_by,oh.dlv_utd_on,\n" +
//                    "oh.ref_field_1,oh.ref_field_2,oh.ref_field_3,oh.ref_field_4,oh.ref_field_5,oh.ref_field_6,\n" +
//                    "oh.INVOICE_NO,oh.C_TEXT,oh.PLANT_TEXT,oh.WH_TEXT,oh.STATUS_TEXT,\n" +
//                    "oh.MIDDLEWARE_ID,oh.MIDDLEWARE_TABLE,oh.REF_DOC_TYP,oh.SALES_ORDER_NUMBER,\n" +
//                    "oh.SALES_INVOICE_NUMBER,oh.PICK_LIST_NUMBER,oh.INVOICE_DATE,oh.DELIVERY_TYPE,\n" +
//                    "oh.CUSTOMER_ID,oh.CUSTOMER_NAME,oh.ADDRESS,oh.PHONE_NUMBER,oh.ALTERNATE_NO,oh.STATUS,oh.TOKEN_NUMBER,oh.TARGET_BRANCH_CODE,oh.CUSTOMER_TYPE, \n" +
//                    "ol.ref_doc_no , ol.c_id , ol.lang_id, ol.plant_id, ol.wh_id, ol.pre_ob_no, ol.partner_code ", nativeQuery = true)
//    Stream<OutboundHeaderV2Stream> findAllOutBoundHeader(
//            @Param(value = "companyCodeId") List<String> companyCodeId,
//            @Param(value = "plantId") List<String> plantId,
//            @Param(value = "languageId") List<String> languageId,
//            @Param(value = "warehouseId") List<String> warehouseId,
//            @Param(value = "refDocNo") List<String> refDocNo,
//            @Param(value = "preOutboundNo") List<String> preOutboundNo,
//            @Param(value = "partnerCode") List<String> partnerCode,
//            @Param(value = "targetBranchCode") List<String> targetBranchCode,
//            @Param(value = "outboundOrderTypeId") List<Long> outboundOrderTypeId,
//            @Param(value = "statusId") List<Long> statusId,
//            @Param(value = "soType") List<String> soType,
//            @Param(value = "startRequiredDeliveryDate") Date startRequiredDeliveryDate,
//            @Param(value = "endRequiredDeliveryDate") Date endRequiredDeliveryDate,
//            @Param(value = "startDeliveryConfirmedOn") Date startDeliveryConfirmedOn,
//            @Param(value = "endDeliveryConfirmedOn") Date endDeliveryConfirmedOn,
//            @Param(value = "startOrderDate") Date startOrderDate,
//            @Param(value = "endOrderDate") Date endOrderDate);

//    @Query(value =
//            "SELECT SUM(ORD_QTY) sOrdQty,REF_DOC_NO,PRE_OB_NO,C_ID,PLANT_ID,LANG_ID,WH_ID into #pobl FROM tblpreoutboundline WHERE is_deleted = 0 and \n" +
//                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) AND\n" +
//                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
//                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
//                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
//                    "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) \n" +
//                    "GROUP BY REF_DOC_NO,PRE_OB_NO,C_ID,PLANT_ID,LANG_ID,WH_ID\n" +
//
//                    "SELECT COUNT(ORD_QTY) cOrdQty,REF_DOC_NO,PRE_OB_NO,C_ID,PLANT_ID,LANG_ID,WH_ID into #cntpobl FROM tblpreoutboundline WHERE is_deleted = 0 and \n" +
//                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) AND\n" +
//                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
//                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
//                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
//                    "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) \n" +
//                    "GROUP BY REF_DOC_NO,PRE_OB_NO,C_ID,PLANT_ID,LANG_ID,WH_ID\n" +
//
//                    "select \n" +
//                    "oh.c_id companyCodeId, oh.lang_id languageId, oh.partner_code partnerCode, \n" +
//                    "oh.plant_id plantId, oh.pre_ob_no preOutboundNo,oh.ref_doc_no refDocNumber,oh.wh_id warehouseId, \n" +
//                    "oh.dlv_ctd_by createdBy, \n" +
////                    "DATEADD(HOUR,3,oh.dlv_ctd_on) createdOn, \n" +
//                    "oh.dlv_ctd_on createdOn, \n" +
//                    "oh.is_deleted deletionIndicator, \n" +
//                    "oh.dlv_cnf_by deliveryConfirmedBy, \n" +
////                    "DATEADD(HOUR,3,oh.dlv_cnf_on) deliveryConfirmedOn, \n" +
//                    "oh.dlv_cnf_on deliveryConfirmedOn, \n" +
//                    "oh.dlv_ord_no deliveryOrderNo, oh.ob_ord_typ_id outboundOrderTypeId, \n" +
////                    "DATEADD(HOUR,3,oh.ref_doc_date) refDocDate, \n" +
//                    "oh.ref_doc_date refDocDate, \n" +
//                    "oh.ref_doc_typ referenceDocumentType,oh.remark remarks, \n" +
////                    "DATEADD(HOUR,3,oh.req_del_date) requiredDeliveryDate,\n" +
//                    "oh.req_del_date requiredDeliveryDate,\n" +
//                    "oh.dlv_rev_by reversedBy, \n" +
////                    "DATEADD(HOUR,3,oh.dlv_rev_on) reversedOn, \n" +
//                    "oh.dlv_rev_on reversedOn, \n" +
//                    "oh.status_id statusId,oh.dlv_utd_by updatedBy, \n" +
////                    "DATEADD(HOUR,3,oh.dlv_utd_on) updatedOn,\n" +
//                    "oh.dlv_utd_on updatedOn,\n" +
//                    "oh.INVOICE_NO invoiceNumber,\n" +
//                    "oh.C_TEXT companyDescription,\n" +
//                    "oh.PLANT_TEXT plantDescription,\n" +
//                    "oh.WH_TEXT warehouseDescription,\n" +
//                    "oh.STATUS_TEXT statusDescription,\n" +
//                    "oh.MIDDLEWARE_ID middlewareId,\n" +
//                    "oh.MIDDLEWARE_TABLE middlewareTable,\n" +
//                    "oh.SALES_ORDER_NUMBER salesOrderNumber,\n" +
//                    "oh.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
//                    "oh.PICK_LIST_NUMBER pickListNumber,\n" +
//                    "oh.INVOICE_DATE invoiceDate,\n" +
//                    "oh.DELIVERY_TYPE deliveryType,\n" +
//                    "oh.CUSTOMER_ID customerId,\n" +
//                    "oh.CUSTOMER_NAME customerName,\n" +
//                    "oh.TARGET_BRANCH_CODE targetBranchCode,\n" +
//                    "oh.ADDRESS address,\n" +
//                    "oh.PHONE_NUMBER phoneNumber,\n" +
//                    "oh.ALTERNATE_NO alternateNo,\n" +
//                    "oh.TOKEN_NUMBER tokenNumber,\n" +
//                    "oh.STATUS status,\n" +
//                    "oh.CUSTOMER_TYPE customerType,\n" +
//                    "oh.ref_field_1 referenceField1,oh.ref_field_2 referenceField2,oh.ref_field_3 referenceField3, \n" +
//                    "oh.ref_field_4 referenceField4,oh.ref_field_5 referenceField5,oh.ref_field_6 referenceField6,\n" +
//                    "(CASE WHEN sum(dlv_qty) is not null THEN sum(dlv_qty) ELSE 0 END) as referenceField7,\n" +
//                    "COUNT(CASE WHEN dlv_qty is not null and dlv_qty > 0 THEN dlv_qty ELSE NULL END) as referenceField8,\n" +
//                    "(select sOrdQty from #pobl pobl1 where pobl1.REF_DOC_NO = oh.REF_DOC_NO and pobl1.PRE_OB_NO = oh.PRE_OB_NO \n" +
//                        "and pobl1.c_id = oh.c_id and pobl1.plant_id = oh.plant_id and pobl1.lang_id = oh.lang_id and pobl1.wh_id = oh.wh_id) as referencefield9,\n" +
//                    "(SELECT COUNT(*) FROM tblpickupline WHERE REF_DOC_NO = oh.ref_doc_no AND PRE_OB_NO = oh.pre_ob_no AND STATUS_ID = 50 AND IS_DELETED = 0 \n" +
//                        "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id) AS countOfPickedLine, \n " +
//                    "(SELECT SUM(PICK_CNF_QTY) FROM tblpickupline WHERE REF_DOC_NO = oh.ref_doc_no AND PRE_OB_NO = oh.pre_ob_no AND IS_DELETED = 0 \n" +
//                        "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id) AS sumOfPickedQty, \n " +
////                    "(select cPckCnfQty from #cntpil pil1 where pil1.REF_DOC_NO = oh.REF_DOC_NO AND STATUS_ID = 50 AND IS_DELETED = 0) as countOfPickedLine,\n" +
////                    "(select sPckCnfQty from #sumpil pil2 where pil2.REF_DOC_NO = oh.REF_DOC_NO AND IS_DELETED = 0 ) as sumOfPickedQty,\n" +
//                    "(select cOrdQty from #cntpobl pobl2 where pobl2.REF_DOC_NO = oh.REF_DOC_NO AND pobl2.PRE_OB_NO = oh.pre_ob_no \n" +
//                        "AND pobl2.c_id = oh.c_id AND pobl2.plant_id = oh.plant_id AND pobl2.lang_id = oh.lang_id AND pobl2.wh_id = oh.wh_id) as referenceField10 \n" +
////                    "SUM(ORD_QTY) as referenceField9,\n" +
////                    "count(ord_qty) as referenceField10 \n" +
//                    "from tbloutboundheader oh\n" +
//                    "join tbloutboundline ol on ol.ref_doc_no = oh.ref_doc_no and ol.pre_ob_no = oh.pre_ob_no and ol.c_id = oh.c_id and ol.plant_id = oh.plant_id and ol.wh_id = oh.wh_id and ol.lang_id = oh.lang_id \n" +
//                    "where ol.ref_field_2 is null and oh.is_deleted = 0 and \n" +
//                    "(COALESCE(:companyCodeId, null) IS NULL OR (oh.c_id IN (:companyCodeId))) and \n" +
//                    "(COALESCE(:plantId, null) IS NULL OR (oh.plant_id IN (:plantId))) and \n" +
//                    "(COALESCE(:languageId, null) IS NULL OR (oh.lang_id IN (:languageId))) and \n" +
//                    "(COALESCE(:warehouseId, null) IS NULL OR (oh.wh_id IN (:warehouseId))) and \n" +
//                    "(COALESCE(:refDocNo, null) IS NULL OR (oh.ref_doc_no IN (:refDocNo))) and \n" +
//                    "(COALESCE(:preOutboundNo, null) IS NULL OR (oh.pre_ob_no IN (:preOutboundNo))) and \n" +
//                    "(COALESCE(:partnerCode, null) IS NULL OR (oh.partner_code IN (:partnerCode))) and \n" +
//                    "(COALESCE(:targetBranchCode, null) IS NULL OR (oh.target_branch_code IN (:targetBranchCode))) and \n" +
//                    "(COALESCE(:outboundOrderTypeId, null) IS NULL OR (oh.ob_ord_typ_id IN (:outboundOrderTypeId))) and \n" +
//                    "(COALESCE(:statusId, null) IS NULL OR (oh.status_id IN (:statusId))) and \n" +
//                    "(COALESCE(:soType, null) IS NULL OR (oh.ref_field_1 IN (:soType))) and\n" +
//                    "(COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) IS NULL OR (oh.REQ_DEL_DATE between COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :endRequiredDeliveryDate), null))) and\n" +
//                    "(COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) IS NULL OR (oh.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endDeliveryConfirmedOn), null))) and\n" +
//                    "(COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) IS NULL OR (oh.DLV_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) and COALESCE(CONVERT(VARCHAR(255), :endOrderDate), null)))\n" +
//                    "group by oh.c_id , oh.lang_id, oh.partner_code, oh.plant_id, oh.pre_ob_no,oh.ref_doc_no ,oh.wh_id,oh.dlv_ctd_by,oh.dlv_ctd_on,oh.is_deleted,oh.dlv_cnf_by,oh.dlv_cnf_on,\n" +
//                    "oh.dlv_ord_no, oh.ob_ord_typ_id,oh.ref_doc_date,oh.ref_doc_typ,oh.remark,oh.req_del_date,oh.dlv_rev_by,oh.dlv_rev_on,oh.status_id,oh.dlv_utd_by,oh.dlv_utd_on,\n" +
//                    "oh.ref_field_1,oh.ref_field_2,oh.ref_field_3,oh.ref_field_4,oh.ref_field_5,oh.ref_field_6,\n" +
//                    "oh.INVOICE_NO,oh.C_TEXT,oh.PLANT_TEXT,oh.WH_TEXT,oh.STATUS_TEXT,\n" +
//                    "oh.MIDDLEWARE_ID,oh.MIDDLEWARE_TABLE,oh.REF_DOC_TYP,oh.SALES_ORDER_NUMBER,\n" +
//                    "oh.SALES_INVOICE_NUMBER,oh.PICK_LIST_NUMBER,oh.INVOICE_DATE,oh.DELIVERY_TYPE,\n" +
//                    "oh.CUSTOMER_ID,oh.CUSTOMER_NAME,oh.ADDRESS,oh.PHONE_NUMBER,oh.ALTERNATE_NO,oh.STATUS,oh.TOKEN_NUMBER,oh.TARGET_BRANCH_CODE,oh.CUSTOMER_TYPE,\n" +
//                    "ol.ref_doc_no , ol.c_id , ol.lang_id, ol.plant_id, ol.wh_id, ol.pre_ob_no, ol.partner_code", nativeQuery = true)
//    List<OutboundHeaderV2Stream> findAllOutBoundHeaderForRFD(
//            @Param(value = "companyCodeId") List<String> companyCodeId,
//            @Param(value = "plantId") List<String> plantId,
//            @Param(value = "languageId") List<String> languageId,
//            @Param(value = "warehouseId") List<String> warehouseId,
//            @Param(value = "refDocNo") List<String> refDocNo,
//            @Param(value = "preOutboundNo") List<String> preOutboundNo,
//            @Param(value = "partnerCode") List<String> partnerCode,
//            @Param(value = "targetBranchCode") List<String> targetBranchCode,
//            @Param(value = "outboundOrderTypeId") List<Long> outboundOrderTypeId,
//            @Param(value = "statusId") List<Long> statusId,
//            @Param(value = "soType") List<String> soType,
//            @Param(value = "startRequiredDeliveryDate") Date startRequiredDeliveryDate,
//            @Param(value = "endRequiredDeliveryDate") Date endRequiredDeliveryDate,
//            @Param(value = "startDeliveryConfirmedOn") Date startDeliveryConfirmedOn,
//            @Param(value = "endDeliveryConfirmedOn") Date endDeliveryConfirmedOn,
//            @Param(value = "startOrderDate") Date startOrderDate,
//            @Param(value = "endOrderDate") Date endOrderDate);

    @Query(value =
                    "SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,REF_DOC_NO,PRE_OB_NO,DLV_QTY,ORD_QTY,STATUS_ID into #obl FROM tbloutboundline WHERE is_deleted = 0 and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) AND\n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
                    "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) \n" +

                    "SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,REF_DOC_NO,PRE_OB_NO,PICK_CNF_QTY,STATUS_ID into #pil FROM tblpickupline WHERE is_deleted = 0 and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) AND\n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
                    "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) \n" +

                    "select \n" +
                    "oh.c_id companyCodeId, oh.lang_id languageId, oh.partner_code partnerCode, \n" +
                    "oh.plant_id plantId, oh.pre_ob_no preOutboundNo,oh.ref_doc_no refDocNumber,oh.wh_id warehouseId, \n" +
                    "oh.dlv_ctd_by createdBy, \n" +
                    "oh.dlv_ctd_on createdOn, \n" +
                    "oh.is_deleted deletionIndicator, \n" +
                    "oh.dlv_cnf_by deliveryConfirmedBy, \n" +
                    "oh.dlv_cnf_on deliveryConfirmedOn, \n" +
                    "oh.dlv_ord_no deliveryOrderNo, oh.ob_ord_typ_id outboundOrderTypeId, \n" +
                    "oh.ref_doc_date refDocDate, \n" +
                    "oh.ref_doc_typ referenceDocumentType,oh.remark remarks, \n" +
                    "oh.req_del_date requiredDeliveryDate,\n" +
                    "oh.dlv_rev_by reversedBy, \n" +
                    "oh.dlv_rev_on reversedOn, \n" +
                    "oh.status_id statusId,oh.dlv_utd_by updatedBy, \n" +
                    "oh.dlv_utd_on updatedOn,\n" +
                    "oh.INVOICE_NO invoiceNumber,\n" +
                    "oh.C_TEXT companyDescription,\n" +
                    "oh.PLANT_TEXT plantDescription,\n" +
                    "oh.WH_TEXT warehouseDescription,\n" +
                    "oh.STATUS_TEXT statusDescription,\n" +
                    "oh.MIDDLEWARE_ID middlewareId,\n" +
                    "oh.MIDDLEWARE_TABLE middlewareTable,\n" +
                    "oh.SALES_ORDER_NUMBER salesOrderNumber,\n" +
                    "oh.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
                    "oh.PICK_LIST_NUMBER pickListNumber,\n" +
                    "oh.INVOICE_DATE invoiceDate,\n" +
                    "oh.DELIVERY_TYPE deliveryType,\n" +
                    "oh.CUSTOMER_ID customerId,\n" +
                    "oh.CUSTOMER_NAME customerName,\n" +
                    "oh.TARGET_BRANCH_CODE targetBranchCode,\n" +
                    "oh.ADDRESS address,\n" +
                    "oh.PHONE_NUMBER phoneNumber,\n" +
                    "oh.ALTERNATE_NO alternateNo,\n" +
                    "oh.TOKEN_NUMBER tokenNumber,\n" +
                    "oh.STATUS status,\n" +
                    "oh.CUSTOMER_TYPE customerType,\n" +
                    "oh.CONSIGNMENT consignment,\n" +
                    "oh.TRANS_NM transportName,\n" +
                    "oh.ref_field_1 referenceField1,oh.ref_field_2 referenceField2,oh.ref_field_3 referenceField3, \n" +
                    "oh.ref_field_4 referenceField4,oh.ref_field_5 referenceField5,oh.ref_field_6 referenceField6,\n" +

                    "COALESCE((select sum(DLV_QTY) totQty from #obl where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no \n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id and STATUS_ID = 59),0) \n" +
                    "as referenceField7, \n"+

                    "(select COUNT(PRE_OB_NO) from #obl \n" +
                    "where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no \n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id and STATUS_ID = 59) \n" +
                    "as referenceField8, \n"+

                    "COALESCE((select sum(ORD_QTY) totQty from #obl where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no \n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id),0) \n" +
                    "as referenceField9, \n"+

                    "(select COUNT(PRE_OB_NO) from #obl \n" +
                    "where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no \n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id) \n" +
                    "as referenceField10, \n"+

                    "COALESCE((select sum(PICK_CNF_QTY) totQty from #pil where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no AND STATUS_ID in (50,57)\n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id),0) \n" +
                    "as sumOfPickedQty, \n"+

                    "(select COUNT(PRE_OB_NO) from #pil \n" +
                    "where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no AND STATUS_ID in (50,57)\n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id) \n" +
                    "as countOfPickedLine \n"+

                    "from tbloutboundheader oh\n" +
                    "where oh.is_deleted = 0 and \n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (oh.c_id IN (:companyCodeId))) and \n" +
                    "(COALESCE(:plantId, null) IS NULL OR (oh.plant_id IN (:plantId))) and \n" +
                    "(COALESCE(:languageId, null) IS NULL OR (oh.lang_id IN (:languageId))) and \n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (oh.wh_id IN (:warehouseId))) and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (oh.ref_doc_no IN (:refDocNo))) and \n" +
                    "(COALESCE(:preOutboundNo, null) IS NULL OR (oh.pre_ob_no IN (:preOutboundNo))) and \n" +
                    "(COALESCE(:partnerCode, null) IS NULL OR (oh.partner_code IN (:partnerCode))) and \n" +
                    "(COALESCE(:targetBranchCode, null) IS NULL OR (oh.target_branch_code IN (:targetBranchCode))) and \n" +
                    "(COALESCE(:outboundOrderTypeId, null) IS NULL OR (oh.ob_ord_typ_id IN (:outboundOrderTypeId))) and \n" +
                    "(COALESCE(:statusId, null) IS NULL OR (oh.status_id IN (:statusId))) and \n" +
                    "(COALESCE(:soType, null) IS NULL OR (oh.ref_field_1 IN (:soType))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) IS NULL OR (oh.REQ_DEL_DATE between COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :endRequiredDeliveryDate), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) IS NULL OR (oh.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endDeliveryConfirmedOn), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) IS NULL OR (oh.DLV_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) and COALESCE(CONVERT(VARCHAR(255), :endOrderDate), null)))" , nativeQuery = true)
    List<OutboundHeaderV2Stream> findAllOutBoundHeaderForRFD(
            @Param(value = "companyCodeId") List<String> companyCodeId,
            @Param(value = "plantId") List<String> plantId,
            @Param(value = "languageId") List<String> languageId,
            @Param(value = "warehouseId") List<String> warehouseId,
            @Param(value = "refDocNo") List<String> refDocNo,
            @Param(value = "preOutboundNo") List<String> preOutboundNo,
            @Param(value = "partnerCode") List<String> partnerCode,
            @Param(value = "targetBranchCode") List<String> targetBranchCode,
            @Param(value = "outboundOrderTypeId") List<Long> outboundOrderTypeId,
            @Param(value = "statusId") List<Long> statusId,
            @Param(value = "soType") List<String> soType,
            @Param(value = "startRequiredDeliveryDate") Date startRequiredDeliveryDate,
            @Param(value = "endRequiredDeliveryDate") Date endRequiredDeliveryDate,
            @Param(value = "startDeliveryConfirmedOn") Date startDeliveryConfirmedOn,
            @Param(value = "endDeliveryConfirmedOn") Date endDeliveryConfirmedOn,
            @Param(value = "startOrderDate") Date startOrderDate,
            @Param(value = "endOrderDate") Date endOrderDate);

    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
    @Query(value =
                    "SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,REF_DOC_NO,PRE_OB_NO,DLV_QTY,ORD_QTY,STATUS_ID into #obl FROM tbloutboundline WHERE is_deleted = 0 and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) AND\n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
                    "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) \n" +

                    "select \n" +
                    "oh.c_id companyCodeId, oh.lang_id languageId, oh.partner_code partnerCode, \n" +
                    "oh.plant_id plantId, oh.pre_ob_no preOutboundNo,oh.ref_doc_no refDocNumber,oh.wh_id warehouseId, \n" +
                    "oh.dlv_ctd_by createdBy, \n" +
                    "oh.dlv_ctd_on createdOn, \n" +
                    "oh.is_deleted deletionIndicator, \n" +
                    "oh.dlv_cnf_by deliveryConfirmedBy, \n" +
                    "oh.dlv_cnf_on deliveryConfirmedOn, \n" +
                    "oh.dlv_ord_no deliveryOrderNo, oh.ob_ord_typ_id outboundOrderTypeId, \n" +
                    "oh.ref_doc_date refDocDate, \n" +
                    "oh.ref_doc_typ referenceDocumentType,oh.remark remarks, \n" +
                    "oh.req_del_date requiredDeliveryDate,\n" +
                    "oh.dlv_rev_by reversedBy, \n" +
                    "oh.dlv_rev_on reversedOn, \n" +
                    "oh.status_id statusId,oh.dlv_utd_by updatedBy, \n" +
                    "oh.dlv_utd_on updatedOn,\n" +
                    "oh.INVOICE_NO invoiceNumber,\n" +
                    "oh.C_TEXT companyDescription,\n" +
                    "oh.PLANT_TEXT plantDescription,\n" +
                    "oh.WH_TEXT warehouseDescription,\n" +
                    "oh.STATUS_TEXT statusDescription,\n" +
                    "oh.MIDDLEWARE_ID middlewareId,\n" +
                    "oh.MIDDLEWARE_TABLE middlewareTable,\n" +
                    "oh.SALES_ORDER_NUMBER salesOrderNumber,\n" +
                    "oh.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
                    "oh.PICK_LIST_NUMBER pickListNumber,\n" +
                    "oh.INVOICE_DATE invoiceDate,\n" +
                    "oh.DELIVERY_TYPE deliveryType,\n" +
                    "oh.CUSTOMER_ID customerId,\n" +
                    "oh.CUSTOMER_NAME customerName,\n" +
                    "oh.TARGET_BRANCH_CODE targetBranchCode,\n" +
                    "oh.ADDRESS address,\n" +
                    "oh.PHONE_NUMBER phoneNumber,\n" +
                    "oh.ALTERNATE_NO alternateNo,\n" +
                    "oh.TOKEN_NUMBER tokenNumber,\n" +
                    "oh.STATUS status,\n" +
                    "oh.CUSTOMER_TYPE customerType,\n" +
                    "oh.ref_field_1 referenceField1,oh.ref_field_2 referenceField2,oh.ref_field_3 referenceField3, \n" +
                    "oh.ref_field_4 referenceField4,oh.ref_field_5 referenceField5,oh.ref_field_6 referenceField6,\n" +

                    "COALESCE((select sum(DLV_QTY) totQty from #obl where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no \n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id and STATUS_ID = 59),0) \n" +
                    "as referenceField7, \n"+

                    "(select COUNT(PRE_OB_NO) from #obl \n" +
                    "where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no \n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id and STATUS_ID = 59) \n" +
                    "as referenceField8, \n"+

                    "COALESCE((select sum(ORD_QTY) totQty from #obl where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no \n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id),0) \n" +
                    "as referenceField9, \n"+

                    "(select COUNT(PRE_OB_NO) from #obl \n" +
                    "where REF_DOC_NO = oh.REF_DOC_NO AND PRE_OB_NO = oh.pre_ob_no \n" +
                    "AND c_id = oh.c_id AND plant_id = oh.plant_id AND lang_id = oh.lang_id AND wh_id = oh.wh_id) \n" +
                    "as referenceField10 \n"+

                    "from tbloutboundheader oh\n" +
                    "where oh.is_deleted = 0 and \n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (oh.c_id IN (:companyCodeId))) and \n" +
                    "(COALESCE(:plantId, null) IS NULL OR (oh.plant_id IN (:plantId))) and \n" +
                    "(COALESCE(:languageId, null) IS NULL OR (oh.lang_id IN (:languageId))) and \n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (oh.wh_id IN (:warehouseId))) and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (oh.ref_doc_no IN (:refDocNo))) and \n" +
                    "(COALESCE(:preOutboundNo, null) IS NULL OR (oh.pre_ob_no IN (:preOutboundNo))) and \n" +
                    "(COALESCE(:partnerCode, null) IS NULL OR (oh.partner_code IN (:partnerCode))) and \n" +
                    "(COALESCE(:targetBranchCode, null) IS NULL OR (oh.target_branch_code IN (:targetBranchCode))) and \n" +
                    "(COALESCE(:outboundOrderTypeId, null) IS NULL OR (oh.ob_ord_typ_id IN (:outboundOrderTypeId))) and \n" +
                    "(COALESCE(:statusId, null) IS NULL OR (oh.status_id IN (:statusId))) and \n" +
                    "(COALESCE(:soType, null) IS NULL OR (oh.ref_field_1 IN (:soType))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) IS NULL OR (oh.REQ_DEL_DATE between COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :endRequiredDeliveryDate), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) IS NULL OR (oh.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endDeliveryConfirmedOn), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) IS NULL OR (oh.DLV_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) and COALESCE(CONVERT(VARCHAR(255), :endOrderDate), null))) \n ", nativeQuery = true)
    Stream<OutboundHeaderV2Stream> findAllOutBoundHeader(
            @Param(value = "companyCodeId") List<String> companyCodeId,
            @Param(value = "plantId") List<String> plantId,
            @Param(value = "languageId") List<String> languageId,
            @Param(value = "warehouseId") List<String> warehouseId,
            @Param(value = "refDocNo") List<String> refDocNo,
            @Param(value = "preOutboundNo") List<String> preOutboundNo,
            @Param(value = "partnerCode") List<String> partnerCode,
            @Param(value = "targetBranchCode") List<String> targetBranchCode,
            @Param(value = "outboundOrderTypeId") List<Long> outboundOrderTypeId,
            @Param(value = "statusId") List<Long> statusId,
            @Param(value = "soType") List<String> soType,
            @Param(value = "startRequiredDeliveryDate") Date startRequiredDeliveryDate,
            @Param(value = "endRequiredDeliveryDate") Date endRequiredDeliveryDate,
            @Param(value = "startDeliveryConfirmedOn") Date startDeliveryConfirmedOn,
            @Param(value = "endDeliveryConfirmedOn") Date endDeliveryConfirmedOn,
            @Param(value = "startOrderDate") Date startOrderDate,
            @Param(value = "endOrderDate") Date endOrderDate);

    //This Query for seperate consignment Tab in Delivery
    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
    @Query(value =
            "SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,REF_DOC_NO,PRE_OB_NO,COUNT(PRE_OB_NO) linesCount into #obl FROM tbloutboundline WHERE is_deleted = 0 and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) AND\n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
                    "(COALESCE(:preOutboundNo, null) IS NULL OR (pre_ob_no IN (:preOutboundNo))) \n" +
                    "GROUP BY PRE_OB_NO,REF_DOC_NO,PLANT_ID,WH_ID,C_ID,LANG_ID\n" +

                    "select \n" +
                    "oh.c_id companyCodeId, oh.lang_id languageId, oh.partner_code partnerCode, \n" +
                    "oh.plant_id plantId, oh.pre_ob_no preOutboundNo,oh.ref_doc_no refDocNumber,oh.wh_id warehouseId, \n" +
                    "oh.dlv_ctd_by createdBy, \n" +
                    "oh.dlv_ctd_on createdOn, \n" +
                    "oh.is_deleted deletionIndicator, \n" +
                    "oh.dlv_cnf_by deliveryConfirmedBy, \n" +
                    "oh.dlv_cnf_on deliveryConfirmedOn, \n" +
                    "oh.dlv_ord_no deliveryOrderNo, oh.ob_ord_typ_id outboundOrderTypeId, \n" +
                    "oh.ref_doc_date refDocDate, \n" +
                    "oh.ref_doc_typ referenceDocumentType,oh.remark remarks, \n" +
                    "oh.req_del_date requiredDeliveryDate,\n" +
                    "oh.dlv_rev_by reversedBy, \n" +
                    "oh.dlv_rev_on reversedOn, \n" +
                    "oh.status_id statusId,oh.dlv_utd_by updatedBy, \n" +
                    "oh.dlv_utd_on updatedOn,\n" +
                    "oh.INVOICE_NO invoiceNumber,\n" +
                    "oh.C_TEXT companyDescription,\n" +
                    "oh.PLANT_TEXT plantDescription,\n" +
                    "oh.WH_TEXT warehouseDescription,\n" +
                    "oh.STATUS_TEXT statusDescription,\n" +
                    "oh.MIDDLEWARE_ID middlewareId,\n" +
                    "oh.MIDDLEWARE_TABLE middlewareTable,\n" +
                    "oh.SALES_ORDER_NUMBER salesOrderNumber,\n" +
                    "oh.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
                    "oh.PICK_LIST_NUMBER pickListNumber,\n" +
                    "oh.INVOICE_DATE invoiceDate,\n" +
                    "oh.DELIVERY_TYPE deliveryType,\n" +
                    "oh.CUSTOMER_ID customerId,\n" +
                    "oh.CUSTOMER_NAME customerName,\n" +
                    "oh.TARGET_BRANCH_CODE targetBranchCode,\n" +
                    "oh.ADDRESS address,\n" +
                    "oh.PHONE_NUMBER phoneNumber,\n" +
                    "oh.ALTERNATE_NO alternateNo,\n" +
                    "oh.TOKEN_NUMBER tokenNumber,\n" +
                    "oh.STATUS status,\n" +
                    "oh.CUSTOMER_TYPE customerType,\n" +
                    "oh.ref_field_1 referenceField1,oh.ref_field_2 referenceField2,oh.ref_field_3 referenceField3, \n" +
                    "oh.ref_field_4 referenceField4,oh.ref_field_5 referenceField5,oh.ref_field_6 referenceField6,\n" +
                    "oh.ref_field_7 as referenceField7, \n"+
                    "ol.linesCount as referenceField8, \n"+
                    "oh.ref_field_9 as referenceField9, \n"+
                    "oh.ref_field_10 as referenceField10 \n"+
                    "from tbloutboundheader oh\n" +
                    "Left Join #obl ol on ol.REF_DOC_NO = oh.REF_DOC_NO AND ol.PRE_OB_NO = oh.pre_ob_no AND ol.c_id = oh.c_id AND ol.plant_id = oh.plant_id AND ol.lang_id = oh.lang_id AND ol.wh_id = oh.wh_id\n" +
                    "where oh.is_deleted = 0 and \n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (oh.c_id IN (:companyCodeId))) and \n" +
                    "(COALESCE(:plantId, null) IS NULL OR (oh.plant_id IN (:plantId))) and \n" +
                    "(COALESCE(:languageId, null) IS NULL OR (oh.lang_id IN (:languageId))) and \n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (oh.wh_id IN (:warehouseId))) and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (oh.ref_doc_no IN (:refDocNo))) and \n" +
                    "(COALESCE(:preOutboundNo, null) IS NULL OR (oh.pre_ob_no IN (:preOutboundNo))) and \n" +
                    "(COALESCE(:partnerCode, null) IS NULL OR (oh.partner_code IN (:partnerCode))) and \n" +
                    "(COALESCE(:targetBranchCode, null) IS NULL OR (oh.target_branch_code IN (:targetBranchCode))) and \n" +
                    "(COALESCE(:outboundOrderTypeId, null) IS NULL OR (oh.ob_ord_typ_id IN (:outboundOrderTypeId))) and \n" +
                    "(COALESCE(:statusId, null) IS NULL OR (oh.status_id IN (:statusId))) and \n" +
                    "(COALESCE(:soType, null) IS NULL OR (oh.ref_field_1 IN (:soType))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) IS NULL OR (oh.REQ_DEL_DATE between COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :endRequiredDeliveryDate), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) IS NULL OR (oh.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endDeliveryConfirmedOn), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) IS NULL OR (oh.DLV_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) and COALESCE(CONVERT(VARCHAR(255), :endOrderDate), null))) \n ", nativeQuery = true)
    Stream<OutboundHeaderV2Stream> findAllOutBoundHeaderV2(
            @Param(value = "companyCodeId") List<String> companyCodeId,
            @Param(value = "plantId") List<String> plantId,
            @Param(value = "languageId") List<String> languageId,
            @Param(value = "warehouseId") List<String> warehouseId,
            @Param(value = "refDocNo") List<String> refDocNo,
            @Param(value = "preOutboundNo") List<String> preOutboundNo,
            @Param(value = "partnerCode") List<String> partnerCode,
            @Param(value = "targetBranchCode") List<String> targetBranchCode,
            @Param(value = "outboundOrderTypeId") List<Long> outboundOrderTypeId,
            @Param(value = "statusId") List<Long> statusId,
            @Param(value = "soType") List<String> soType,
            @Param(value = "startRequiredDeliveryDate") Date startRequiredDeliveryDate,
            @Param(value = "endRequiredDeliveryDate") Date endRequiredDeliveryDate,
            @Param(value = "startDeliveryConfirmedOn") Date startDeliveryConfirmedOn,
            @Param(value = "endDeliveryConfirmedOn") Date endDeliveryConfirmedOn,
            @Param(value = "startOrderDate") Date startOrderDate,
            @Param(value = "endOrderDate") Date endOrderDate);

    List<OutboundHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeliveryConfirmedOnBetween(
            String companyCodeId, String plantId, String languageId, String warehouseIds, Long statusId, Date fromDeliveryDateD, Date toDeliveryDateD);

    public List<OutboundHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndPartnerCodeAndDeliveryConfirmedOnBetween(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long statusId, String partnerCode, Date fromDeliveryDate_d, Date toDeliveryDate_d);

    List<OutboundHeaderV2> findBySalesOrderNumber(String salesOrderNumber);

    OutboundHeaderV2 findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdAndDeletionIndicator(
            String companyCodeId, String languageId, String plantId, String warehouseId, String oldPickListNumber, Long statusId, Long deletionIndicator);
    OutboundHeaderV2 findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String languageId, String plantId, String warehouseId, String oldPickListNumber, Long deletionIndicator);

    OutboundHeaderV2 findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String languageId, String plantId, String warehouseId, String oldPickListNumber, String preOutboundNo, Long deletionIndicator);

    OutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);
    OutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

    List<OutboundHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndOutboundOrderTypeIdAndDeliveryConfirmedOnBetween(
            String companyCode, String plantId, String languageId, String warehouseId, Long statusId, Long partnerCode, Date fromDeliveryDateD, Date toDeliveryDateD);

    @Transactional
    @Procedure(procedureName = "obheader_preobheader_update_proc")
    public void updateObheaderPreobheaderUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("updatedOn") Date updatedOn,
            @Param("updatedBy") String updatedBy,
            @Param("statusId47") Long statusId47,
            @Param("statusId50") Long statusId50,
            @Param("statusId51") Long statusId51,
            @Param("statusDescription50") String statusDescription50,
            @Param("statusDescription51") String statusDescription51
    );

    List<OutboundHeaderV2> findBySalesOrderNumberAndDeletionIndicator(String salesOrderNumber, Long deletionIndicator);

    List<OutboundHeaderV2> findBySalesOrderNumberAndOutboundOrderTypeIdAndDeletionIndicator(String salesOrderNumber, Long outboundOrderTypeId, Long deletionIndicator);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "Update tbloutboundheader SET SALES_ORDER_NUMBER = :salesOrderNumber, SALES_INVOICE_NUMBER = :salesInvoiceNumber, INVOICE_DATE = :invoiceDate, \r\n "
            + " DELIVERY_TYPE = :deliveryType, CUSTOMER_ID = :customerId, CUSTOMER_NAME = :customerName, ADDRESS = :address, PHONE_NUMBER = :phoneNumber, \r\n"
            + " ALTERNATE_NO = :alternateNo, STATUS = :status, DLV_UTD_ON = :updatedOn \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND \r\n"
            + "LANG_ID = :languageId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber", nativeQuery = true)
    void updateSalesInvoiceOutboundHeaderV2(@Param("companyCodeId") String companyCodeId,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("warehouseId") String warehouseId,
                                            @Param("refDocNumber") String refDocNumber,
                                            @Param("salesOrderNumber") String salesOrderNumber,
                                            @Param("salesInvoiceNumber") String salesInvoiceNumber,
                                            @Param("invoiceDate") Date invoiceDate,
                                            @Param("deliveryType") String deliveryType,
                                            @Param("customerId") String customerId,
                                            @Param("customerName") String customerName,
                                            @Param("address") String address,
                                            @Param("phoneNumber") String phoneNumber,
                                            @Param("alternateNo") String alternateNo,
                                            @Param("status") String status,
                                            @Param("updatedOn") Date updatedOn);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "Update tbloutboundline SET SALES_ORDER_NUMBER = :salesOrderNumber, SALES_INVOICE_NUMBER = :salesInvoiceNumber, INVOICE_DATE = :invoiceDate, \r\n "
            + " DELIVERY_TYPE = :deliveryType, CUSTOMER_ID = :customerId, CUSTOMER_NAME = :customerName, ADDRESS = :address, PHONE_NUMBER = :phoneNumber, \r\n"
            + " ALTERNATE_NO = :alternateNo, STATUS = :status, DLV_UTD_ON = :updatedOn \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND \r\n"
            + "LANG_ID = :languageId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber", nativeQuery = true)
    void updateSalesInvoiceOutboundLineV2(@Param("companyCodeId") String companyCodeId,
                                          @Param("plantId") String plantId,
                                          @Param("languageId") String languageId,
                                          @Param("warehouseId") String warehouseId,
                                          @Param("refDocNumber") String refDocNumber,
                                          @Param("salesOrderNumber") String salesOrderNumber,
                                          @Param("salesInvoiceNumber") String salesInvoiceNumber,
                                          @Param("invoiceDate") Date invoiceDate,
                                          @Param("deliveryType") String deliveryType,
                                          @Param("customerId") String customerId,
                                          @Param("customerName") String customerName,
                                          @Param("address") String address,
                                          @Param("phoneNumber") String phoneNumber,
                                          @Param("alternateNo") String alternateNo,
                                          @Param("status") String status,
                                          @Param("updatedOn") Date updatedOn);

    //======================================Walkaroo-V3==================================================================

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OutboundHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.preOutboundNo = :preOutboundNo")
    public void updateOutboundHeaderStatusV3(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("languageId") String languageId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("preOutboundNo") String preOutboundNo,
                                             @Param("statusId") Long statusId,
                                             @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OutboundHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND \n"
            + "ob.warehouseId = :warehouseId AND ob.refDocNumber in :refDocNumbers")
    public void updateOutboundHeaderStatusV3(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("languageId") String languageId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("refDocNumbers") List<String> refDocNumbers,
                                             @Param("statusId") Long statusId,
                                             @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND \n"
            + "ob.warehouseId = :warehouseId AND ob.refDocNumber in :refDocNumbers")
    public void updateOutboundLineStatusV3(@Param("companyCodeId") String companyCodeId,
                                           @Param("plantId") String plantId,
                                           @Param("languageId") String languageId,
                                           @Param("warehouseId") String warehouseId,
                                           @Param("refDocNumbers") List<String> refDocNumbers,
                                           @Param("statusId") Long statusId,
                                           @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update PickupHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND \n"
            + "ob.warehouseId = :warehouseId AND ob.refDocNumber in :refDocNumbers")
    public void updatePickupHeaderStatusV3(@Param("companyCodeId") String companyCodeId,
                                           @Param("plantId") String plantId,
                                           @Param("languageId") String languageId,
                                           @Param("warehouseId") String warehouseId,
                                           @Param("refDocNumbers") List<String> refDocNumbers,
                                           @Param("statusId") Long statusId,
                                           @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update PreOutboundHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND \n"
            + "ob.warehouseId = :warehouseId AND ob.refDocNumber in :refDocNumbers")
    public void updatePreOutboundHeaderStatusV3(@Param("companyCodeId") String companyCodeId,
                                                @Param("plantId") String plantId,
                                                @Param("languageId") String languageId,
                                                @Param("warehouseId") String warehouseId,
                                                @Param("refDocNumbers") List<String> refDocNumbers,
                                                @Param("statusId") Long statusId,
                                                @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update PreOutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND \n"
            + "ob.warehouseId = :warehouseId AND ob.refDocNumber in :refDocNumbers")
    public void updatePreoutboundLineStatusV3(@Param("companyCodeId") String companyCodeId,
                                              @Param("plantId") String plantId,
                                              @Param("languageId") String languageId,
                                              @Param("warehouseId") String warehouseId,
                                              @Param("refDocNumbers") List<String> refDocNumbers,
                                              @Param("statusId") Long statusId,
                                              @Param("statusDescription") String statusDescription);

    //======================================================Impex-V4==========================================================================
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OutboundHeaderV2 ob SET ob.deliveryOrderNo = :deliveryOrderNo \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND \n"
            + "ob.warehouseId = :warehouseId AND ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo ")
    public void updateOutboundHeaderV4(@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("preOutboundNo") String preOutboundNo,
                                       @Param("refDocNumber") String refDocNumber,
                                       @Param("deliveryOrderNo") String deliveryOrderNo);
}