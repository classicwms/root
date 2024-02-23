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
    @Query("Update OutboundHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.deliveryConfirmedOn = :deliveryConfirmedOn \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.refDocNumber = :refDocNumber")
    public void updateOutboundHeaderStatusV2(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("languageId") String languageId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("refDocNumber") String refDocNumber,
                                             @Param("statusId") Long statusId,
                                             @Param("statusDescription") String statusDescription,
                                             @Param("deliveryConfirmedOn") Date deliveryConfirmedOn);

    OutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPickListNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String pickListNumber, Long deletionIndicator);


    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
    @Query(value =
                    "SELECT SUM(ORD_QTY) sOrdQty,REF_DOC_NO into #pobl FROM tblpreoutboundline WHERE is_deleted = 0 and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) \n" +
                    "GROUP BY REF_DOC_NO\n" +

                    "SELECT COUNT(ORD_QTY) cOrdQty,REF_DOC_NO into #cntpobl FROM tblpreoutboundline WHERE is_deleted = 0 and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) \n" +
                    "GROUP BY REF_DOC_NO\n" +

                    "select \n" +
                    "oh.c_id companyCodeId, oh.lang_id languageId, oh.partner_code partnerCode, \n" +
                    "oh.plant_id plantId, oh.pre_ob_no preOutboundNo,oh.ref_doc_no refDocNumber,oh.wh_id warehouseId, \n" +
                    "oh.dlv_ctd_by createdBy, \n" +
                    "DATEADD(HOUR,3,oh.dlv_ctd_on) createdOn, \n" +
                    "oh.is_deleted deletionIndicator, \n" +
                    "oh.dlv_cnf_by deliveryConfirmedBy, \n" +
                    "DATEADD(HOUR,3,oh.dlv_cnf_on) deliveryConfirmedOn, \n" +
                    "oh.dlv_ord_no deliveryOrderNo, oh.ob_ord_typ_id outboundOrderTypeId, \n" +
                    "DATEADD(HOUR,3,oh.ref_doc_date) refDocDate, \n" +
                    "oh.ref_doc_typ referenceDocumentType,oh.remark remarks, \n" +
                    "DATEADD(HOUR,3,oh.req_del_date) requiredDeliveryDate,\n" +
                    "oh.dlv_rev_by reversedBy, \n" +
                    "DATEADD(HOUR,3,oh.dlv_rev_on) reversedOn, \n" +
                    "oh.status_id statusId,oh.dlv_utd_by updatedBy, \n" +
                    "DATEADD(HOUR,3,oh.dlv_utd_on) updatedOn,\n" +
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
                    "DATEADD(HOUR,3,oh.INVOICE_DATE) invoiceDate,\n" +
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
                    "(CASE WHEN sum(dlv_qty) is not null THEN sum(dlv_qty) ELSE 0 END) as referenceField7,\n" +
                    "COUNT(CASE WHEN dlv_qty is not null and dlv_qty > 0 THEN dlv_qty ELSE NULL END) as referenceField8,\n" +
                    "(select sOrdQty from #pobl pobl1 where pobl1.REF_DOC_NO = oh.REF_DOC_NO) as referenceField9,\n" +
                    "(select cOrdQty from #cntpobl pobl2 where pobl2.REF_DOC_NO = oh.REF_DOC_NO) as referenceField10 \n" +
                    "from tbloutboundheader oh\n" +
                    "join tbloutboundline ol on ol.ref_doc_no = oh.ref_doc_no\n" +
                    "where ol.ref_field_2 is null and oh.is_deleted = 0 and \n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (oh.c_id IN (:companyCodeId))) and \n" +
                    "(COALESCE(:plantId, null) IS NULL OR (oh.plant_id IN (:plantId))) and \n" +
                    "(COALESCE(:languageId, null) IS NULL OR (oh.lang_id IN (:languageId))) and \n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (oh.wh_id IN (:warehouseId))) and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (oh.ref_doc_no IN (:refDocNo))) and \n" +
                    "(COALESCE(:partnerCode, null) IS NULL OR (oh.partner_code IN (:partnerCode))) and \n" +
                    "(COALESCE(:outboundOrderTypeId, null) IS NULL OR (oh.ob_ord_typ_id IN (:outboundOrderTypeId))) and \n" +
                    "(COALESCE(:statusId, null) IS NULL OR (oh.status_id IN (:statusId))) and \n" +
                    "(COALESCE(:soType, null) IS NULL OR (oh.ref_field_1 IN (:soType))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) IS NULL OR (oh.REQ_DEL_DATE between COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :endRequiredDeliveryDate), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) IS NULL OR (oh.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endDeliveryConfirmedOn), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) IS NULL OR (oh.DLV_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) and COALESCE(CONVERT(VARCHAR(255), :endOrderDate), null)))\n" +
                    "group by oh.c_id , oh.lang_id, oh.partner_code, oh.plant_id, oh.pre_ob_no,oh.ref_doc_no ,oh.wh_id,oh.dlv_ctd_by,oh.dlv_ctd_on,oh.is_deleted,oh.dlv_cnf_by,oh.dlv_cnf_on,\n" +
                    "oh.dlv_ord_no, oh.ob_ord_typ_id,oh.ref_doc_date,oh.ref_doc_typ,oh.remark,oh.req_del_date,oh.dlv_rev_by,oh.dlv_rev_on,oh.status_id,oh.dlv_utd_by,oh.dlv_utd_on,\n" +
                    "oh.ref_field_1,oh.ref_field_2,oh.ref_field_3,oh.ref_field_4,oh.ref_field_5,oh.ref_field_6,\n" +
                    "oh.INVOICE_NO,oh.C_TEXT,oh.PLANT_TEXT,oh.WH_TEXT,oh.STATUS_TEXT,\n" +
                    "oh.MIDDLEWARE_ID,oh.MIDDLEWARE_TABLE,oh.REF_DOC_TYP,oh.SALES_ORDER_NUMBER,\n" +
                    "oh.SALES_INVOICE_NUMBER,oh.PICK_LIST_NUMBER,oh.INVOICE_DATE,oh.DELIVERY_TYPE,\n" +
                    "oh.CUSTOMER_ID,oh.CUSTOMER_NAME,oh.ADDRESS,oh.PHONE_NUMBER,oh.ALTERNATE_NO,oh.STATUS,oh.TOKEN_NUMBER,oh.TARGET_BRANCH_CODE,oh.CUSTOMER_TYPE, \n" +
                    "ol.ref_doc_no , ol.c_id , ol.lang_id, ol.plant_id, ol.wh_id, ol.pre_ob_no, ol.partner_code ", nativeQuery = true)
    Stream<OutboundHeaderV2Stream> findAllOutBoundHeader(
            @Param(value = "companyCodeId") List<String> companyCodeId,
            @Param(value = "plantId") List<String> plantId,
            @Param(value = "languageId") List<String> languageId,
            @Param(value = "warehouseId") List<String> warehouseId,
            @Param(value = "refDocNo") List<String> refDocNo,
            @Param(value = "partnerCode") List<String> partnerCode,
            @Param(value = "outboundOrderTypeId") List<Long> outboundOrderTypeId,
            @Param(value = "statusId") List<Long> statusId,
            @Param(value = "soType") List<String> soType,
            @Param(value = "startRequiredDeliveryDate") Date startRequiredDeliveryDate,
            @Param(value = "endRequiredDeliveryDate") Date endRequiredDeliveryDate,
            @Param(value = "startDeliveryConfirmedOn") Date startDeliveryConfirmedOn,
            @Param(value = "endDeliveryConfirmedOn") Date endDeliveryConfirmedOn,
            @Param(value = "startOrderDate") Date startOrderDate,
            @Param(value = "endOrderDate") Date endOrderDate);

    @Query(value =
            "SELECT SUM(ORD_QTY) sOrdQty,REF_DOC_NO into #pobl FROM tblpreoutboundline WHERE is_deleted = 0 and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) \n" +
                    "GROUP BY REF_DOC_NO\n" +

                    "SELECT COUNT(ORD_QTY) cOrdQty,REF_DOC_NO into #cntpobl FROM tblpreoutboundline WHERE is_deleted = 0 and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (ref_doc_no IN (:refDocNo))) \n" +
                    "GROUP BY REF_DOC_NO\n" +

                    "select \n" +
                    "oh.c_id companyCodeId, oh.lang_id languageId, oh.partner_code partnerCode, \n" +
                    "oh.plant_id plantId, oh.pre_ob_no preOutboundNo,oh.ref_doc_no refDocNumber,oh.wh_id warehouseId, \n" +
                    "oh.dlv_ctd_by createdBy, \n" +
                    "DATEADD(HOUR,3,oh.dlv_ctd_on) createdOn, \n" +
                    "oh.is_deleted deletionIndicator, \n" +
                    "oh.dlv_cnf_by deliveryConfirmedBy, \n" +
                    "DATEADD(HOUR,3,oh.dlv_cnf_on) deliveryConfirmedOn, \n" +
                    "oh.dlv_ord_no deliveryOrderNo, oh.ob_ord_typ_id outboundOrderTypeId, \n" +
                    "DATEADD(HOUR,3,oh.ref_doc_date) refDocDate, \n" +
                    "oh.ref_doc_typ referenceDocumentType,oh.remark remarks, \n" +
                    "DATEADD(HOUR,3,oh.req_del_date) requiredDeliveryDate,\n" +
                    "oh.dlv_rev_by reversedBy, \n" +
                    "DATEADD(HOUR,3,oh.dlv_rev_on) reversedOn, \n" +
                    "oh.status_id statusId,oh.dlv_utd_by updatedBy, \n" +
                    "DATEADD(HOUR,3,oh.dlv_utd_on) updatedOn,\n" +
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
                    "(CASE WHEN sum(dlv_qty) is not null THEN sum(dlv_qty) ELSE 0 END) as referenceField7,\n" +
                    "COUNT(CASE WHEN dlv_qty is not null and dlv_qty > 0 THEN dlv_qty ELSE NULL END) as referenceField8,\n" +
                    "(select sOrdQty from #pobl pobl1 where pobl1.REF_DOC_NO = oh.REF_DOC_NO) as referenceField9,\n" +
                    "(SELECT COUNT(*) FROM tblpickupline WHERE REF_DOC_NO = oh.ref_doc_no AND STATUS_ID = 50 AND IS_DELETED = 0) AS countOfPickedLine, \n " +
                    "(SELECT SUM(PICK_CNF_QTY) FROM tblpickupline WHERE REF_DOC_NO = oh.ref_doc_no AND IS_DELETED = 0 ) AS sumOfPickedQty, \n " +
//                    "(select cPckCnfQty from #cntpil pil1 where pil1.REF_DOC_NO = oh.REF_DOC_NO AND STATUS_ID = 50 AND IS_DELETED = 0) as countOfPickedLine,\n" +
//                    "(select sPckCnfQty from #sumpil pil2 where pil2.REF_DOC_NO = oh.REF_DOC_NO AND IS_DELETED = 0 ) as sumOfPickedQty,\n" +
                    "(select cOrdQty from #cntpobl pobl2 where pobl2.REF_DOC_NO = oh.REF_DOC_NO) as referenceField10 \n" +
//                    "SUM(ORD_QTY) as referenceField9,\n" +
//                    "count(ord_qty) as referenceField10 \n" +
                    "from tbloutboundheader oh\n" +
                    "join tbloutboundline ol on ol.ref_doc_no = oh.ref_doc_no\n" +
                    "where ol.ref_field_2 is null and oh.is_deleted = 0 and \n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (oh.c_id IN (:companyCodeId))) and \n" +
                    "(COALESCE(:plantId, null) IS NULL OR (oh.plant_id IN (:plantId))) and \n" +
                    "(COALESCE(:languageId, null) IS NULL OR (oh.lang_id IN (:languageId))) and \n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (oh.wh_id IN (:warehouseId))) and \n" +
                    "(COALESCE(:refDocNo, null) IS NULL OR (oh.ref_doc_no IN (:refDocNo))) and \n" +
                    "(COALESCE(:partnerCode, null) IS NULL OR (oh.partner_code IN (:partnerCode))) and \n" +
                    "(COALESCE(:outboundOrderTypeId, null) IS NULL OR (oh.ob_ord_typ_id IN (:outboundOrderTypeId))) and \n" +
                    "(COALESCE(:statusId, null) IS NULL OR (oh.status_id IN (:statusId))) and \n" +
                    "(COALESCE(:soType, null) IS NULL OR (oh.ref_field_1 IN (:soType))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) IS NULL OR (oh.REQ_DEL_DATE between COALESCE(CONVERT(VARCHAR(255), :startRequiredDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :endRequiredDeliveryDate), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) IS NULL OR (oh.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :startDeliveryConfirmedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endDeliveryConfirmedOn), null))) and\n" +
                    "(COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) IS NULL OR (oh.DLV_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startOrderDate), null) and COALESCE(CONVERT(VARCHAR(255), :endOrderDate), null)))\n" +
                    "group by oh.c_id , oh.lang_id, oh.partner_code, oh.plant_id, oh.pre_ob_no,oh.ref_doc_no ,oh.wh_id,oh.dlv_ctd_by,oh.dlv_ctd_on,oh.is_deleted,oh.dlv_cnf_by,oh.dlv_cnf_on,\n" +
                    "oh.dlv_ord_no, oh.ob_ord_typ_id,oh.ref_doc_date,oh.ref_doc_typ,oh.remark,oh.req_del_date,oh.dlv_rev_by,oh.dlv_rev_on,oh.status_id,oh.dlv_utd_by,oh.dlv_utd_on,\n" +
                    "oh.ref_field_1,oh.ref_field_2,oh.ref_field_3,oh.ref_field_4,oh.ref_field_5,oh.ref_field_6,\n" +
                    "oh.INVOICE_NO,oh.C_TEXT,oh.PLANT_TEXT,oh.WH_TEXT,oh.STATUS_TEXT,\n" +
                    "oh.MIDDLEWARE_ID,oh.MIDDLEWARE_TABLE,oh.REF_DOC_TYP,oh.SALES_ORDER_NUMBER,\n" +
                    "oh.SALES_INVOICE_NUMBER,oh.PICK_LIST_NUMBER,oh.INVOICE_DATE,oh.DELIVERY_TYPE,\n" +
                    "oh.CUSTOMER_ID,oh.CUSTOMER_NAME,oh.ADDRESS,oh.PHONE_NUMBER,oh.ALTERNATE_NO,oh.STATUS,oh.TOKEN_NUMBER,oh.TARGET_BRANCH_CODE,oh.CUSTOMER_TYPE,\n" +
                    "ol.ref_doc_no , ol.c_id , ol.lang_id, ol.plant_id, ol.wh_id, ol.pre_ob_no, ol.partner_code", nativeQuery = true)
    List<OutboundHeaderV2Stream> findAllOutBoundHeaderForRFD(
            @Param(value = "companyCodeId") List<String> companyCodeId,
            @Param(value = "plantId") List<String> plantId,
            @Param(value = "languageId") List<String> languageId,
            @Param(value = "warehouseId") List<String> warehouseId,
            @Param(value = "refDocNo") List<String> refDocNo,
            @Param(value = "partnerCode") List<String> partnerCode,
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

    OutboundHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

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
}