package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.model.dto.BusinessPartner;
import com.tekclover.wms.api.outbound.transaction.model.impl.OutBoundLineImpl;
import com.tekclover.wms.api.outbound.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.OutboundLineOutput;
import com.tekclover.wms.api.outbound.transaction.model.report.ContainerReceiptOutboundlineImpl;
import com.tekclover.wms.api.outbound.transaction.model.report.OutwardReportResponse;
import com.tekclover.wms.api.outbound.transaction.model.report.StockMovementLedgerReport;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.OutboundLineV2;
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

@Repository
@Transactional
public interface OutboundLineV2Repository extends JpaRepository<OutboundLineV2, Long>,
        JpaSpecificationExecutor<OutboundLineV2>,
        StreamableJpaSpecificationRepository<OutboundLineV2> {


    List<OutboundLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String preOutboundNo, String refDocNumber, String partnerCode, Long deletionIndicator);


    OutboundLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String refDocNumber, String partnerCode, Long deletionIndicator);


    List<OutboundLineV2> findByCompanyCodeIdInAndPlantIdInAndLanguageIdInAndWarehouseIdInAndPreOutboundNoInAndRefDocNumberInAndDeletionIndicator(
            List<String> companyCodeId, List<String> plantId, List<String> languageId, List<String> warehouseId,
            List<String> preOutboundNo, List<String> refDocNumber, Long deletionIndicator);

    @Query("Select count(ob) from OutboundLine ob where ob.companyCodeId=:companyCodeId and ob.plantId=:plantId and ob.languageId=:languageId and \r\n"
            + "ob.warehouseId=:warehouseId and ob.preOutboundNo=:preOutboundNo and \r\n"
            + " ob.refDocNumber=:refDocNumber and ob.partnerCode=:partnerCode and ob.statusId in :statusId and ob.deletionIndicator=:deletionIndicator")
    public long getOutboudLineByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicatorV2(
            @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId, @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId, @Param("preOutboundNo") String preOutboundNo,
            @Param("refDocNumber") String refDocNumber, @Param("partnerCode") String partnerCode, @Param("statusId") List<Long> statusId,
            @Param("deletionIndicator") long deletionIndicator);

    List<OutboundLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, List<Long> statusIds, Long deletionIndicator);

    List<OutboundLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String preOutboundNo, String refDocNumber, String referenceField2, Long deletionIndicator);

    /*
     * Delivery Queries
     */
    @Query(value = "SELECT COUNT(OB_LINE_NO) AS countOfOrdLines \r\n"
            + "FROM tbloutboundline \r\n"
            + "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo "
            + "AND REF_DOC_NO = :refDocNumber AND REF_FIELD_2 IS NULL \r\n"
            + "GROUP BY OB_LINE_NO;", nativeQuery = true)
    public List<Long> getCountofOrderedLinesV2(@Param("companyCodeId") String companyCodeId,
                                               @Param("plantId") String plantId,
                                               @Param("languageId") String languageId,
                                               @Param("warehouseId") String warehouseId,
                                               @Param("preOutboundNo") String preOutboundNo,
                                               @Param("refDocNumber") String refDocNumber);

    @Query(value = "SELECT SUM(ORD_QTY) AS ordQtyTotal \r\n"
            + "FROM tbloutboundline \r\n"
            + "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo "
            + "AND REF_DOC_NO = :refDocNumber AND REF_FIELD_2 IS NULL \r\n"
            + "GROUP BY REF_DOC_NO;", nativeQuery = true)
    public List<Long> getSumOfOrderedQtyV2(@Param("companyCodeId") String companyCodeId,
                                           @Param("plantId") String plantId,
                                           @Param("languageId") String languageId,
                                           @Param("warehouseId") String warehouseId,
                                           @Param("preOutboundNo") String preOutboundNo,
                                           @Param("refDocNumber") String refDocNumber);

    @Query(value = "SELECT SUM(ORD_QTY) AS ordQtyTotal \r\n"
            + "FROM tbloutboundline \r\n"
            + "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND PRE_OB_NO IN :preOutboundNo "
            + "AND REF_DOC_NO IN :refDocNumber AND REF_FIELD_2 IS NULL AND OB_ORD_TYP_ID = :outboundOrderTypeId \r\n"
            + "GROUP BY OB_ORD_TYP_ID;", nativeQuery = true)
    public Long getSumOfOrderedQtyByPartnerCodeV2(@Param("companyCodeId") String companyCodeId,
                                                  @Param("plantId") String plantId,
                                                  @Param("languageId") String languageId,
                                                  @Param("warehouseId") String warehouseId,
                                                  @Param("preOutboundNo") List<String> preOutboundNo,
                                                  @Param("refDocNumber") List<String> refDocNumber,
                                                  @Param("outboundOrderTypeId") Long outboundOrderTypeId);

    @Query(value = "SELECT COUNT(OB_LINE_NO) AS deliveryLines \r\n"
            + "FROM tbloutboundline \r\n"
            + "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo "
            + "AND REF_DOC_NO = :refDocNumber AND REF_FIELD_2 IS NULL AND DLV_QTY > 0\r\n"
            + "GROUP BY REF_DOC_NO;", nativeQuery = true)
    public List<Long> getDeliveryLinesV2(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("preOutboundNo") String preOutboundNo,
                                         @Param("refDocNumber") String refDocNumber);

    @Query(value = "SELECT COUNT(OB_LINE_NO) FROM tbloutboundline \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND REF_DOC_NO IN :refDocNo AND REF_FIELD_2 IS NULL \r\n"
            + " GROUP BY OB_LINE_NO", nativeQuery = true)
    List<Long> findLineItem_NByRefDocNoAndRefField2IsNull(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param(value = "refDocNo") List<String> refDocNo);

    @Query(value = "SELECT COUNT(OB_LINE_NO) FROM tbloutboundline \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND REF_DOC_NO IN :refDocNo AND DLV_QTY > 0 AND REF_FIELD_2 IS NULL \r\n"
            + " AND DLV_CNF_ON BETWEEN :startDate AND :endDate \r\n"
            + " GROUP BY OB_LINE_NO", nativeQuery = true)
    List<Long> findShippedLines(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param(value = "refDocNo") List<String> refDocNo,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT SUM(DLV_QTY) AS deliveryQty \r\n"
            + "FROM tbloutboundline \r\n"
            + "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND PRE_OB_NO = :preOutboundNo "
            + "AND REF_DOC_NO = :refDocNumber AND REF_FIELD_2 IS NULL AND DLV_QTY > 0\r\n"
            + "GROUP BY REF_DOC_NO;", nativeQuery = true)
    public List<Long> getDeliveryQtyV2(@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("languageId") String languageId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("preOutboundNo") String preOutboundNo,
                                       @Param("refDocNumber") String refDocNumber);

    @Query(value = "SELECT SUM(DLV_QTY) AS deliveryQty \r\n"
            + "FROM tbloutboundline \r\n"
            + "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND PRE_OB_NO IN :preOutboundNo "
            + "AND REF_DOC_NO IN :refDocNumber AND REF_FIELD_2 IS NULL AND DLV_QTY > 0 \r\n"
            + "AND OB_ORD_TYP_ID = :outboundOrderTypeId GROUP BY OB_ORD_TYP_ID;", nativeQuery = true)
    public Long getDeliveryQtyByPartnerCodeV2(@Param("companyCodeId") String companyCodeId,
                                              @Param("plantId") String plantId,
                                              @Param("languageId") String languageId,
                                              @Param("warehouseId") String warehouseId,
                                              @Param("preOutboundNo") List<String> preOutboundNo,
                                              @Param("refDocNumber") List<String> refDocNumber,
                                              @Param("outboundOrderTypeId") Long outboundOrderTypeId);

    /*
     * Line Shipped
     * ---------------------
     * Pass PRE_OB_NO/OB_LINE_NO/ITM_CODE in OUTBOUNDLINE table and fetch Count of OB_LINE_NO values
     * where REF_FIELD_2 = Null and DLV_QTY>0
     */
    @Query(value = "SELECT COUNT(OB_LINE_NO) FROM tbloutboundline \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND PRE_OB_NO = :preOBNo AND OB_LINE_NO = :obLineNo AND ITM_CODE = :itemCode \r\n"
            + " AND DLV_QTY > 0 AND REF_FIELD_2 IS NULL \r\n"
            + " GROUP BY REF_DOC_NO", nativeQuery = true)
    public List<Long> findLineShippedV2(@Param("companyCodeId") String companyCodeId,
                                        @Param("plantId") String plantId,
                                        @Param("languageId") String languageId,
                                        @Param(value = "preOBNo") String preOBNo,
                                        @Param(value = "obLineNo") Long obLineNo,
                                        @Param(value = "itemCode") String itemCode);

    @Query(value = "select \n" +
            "ref_doc_no as refDocNo,\n" +
            "count(ord_qty) as linesOrdered,\n" +
            "SUM(ORD_QTY) as orderedQty,\n" +
            "COUNT(CASE WHEN dlv_qty is not null and dlv_qty > 0 THEN  dlv_qty ELSE  NULL END) as linesShipped,\n" +
            "(CASE WHEN sum(dlv_qty) is not null THEN sum(dlv_qty) ELSE 0 END) as shippedQty\n" +
            "from tbloutboundline \n" +
            "where C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND ref_doc_no in (:refDocNo) and ref_field_2 is null\n" +
            "group by ref_doc_no , c_id , lang_id, plant_id, wh_id, pre_ob_no, partner_code", nativeQuery = true)
    public List<OutBoundLineImpl> getOutBoundLineDataForOutBoundHeaderV2(@Param("companyCodeId") String companyCodeId,
                                                                         @Param("plantId") String plantId,
                                                                         @Param("languageId") String languageId,
                                                                         @Param("refDocNo") List<String> refDocNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.deliveryConfirmedOn = :deliveryConfirmedOn \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber in :lineNumber")
    public void updateOutboundLineStatusV2(@Param("companyCodeId") String companyCodeId,
                                           @Param("plantId") String plantId,
                                           @Param("languageId") String languageId,
                                           @Param("warehouseId") String warehouseId,
                                           @Param("refDocNumber") String refDocNumber,
                                           @Param("preOutboundNo") String preOutboundNo,
                                           @Param("statusId") Long statusId,
                                           @Param("statusDescription") String statusDescription,
                                           @Param("lineNumber") List<Long> lineNumber,
                                           @Param("deliveryConfirmedOn") Date deliveryConfirmedOn);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.deliveredPercentage = :deliveredPercentage, ob.deliveryConfirmedOn = :deliveryConfirmedOn \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber in :lineNumber")
    public void updateOutboundLineStatusV5(@Param("companyCodeId") String companyCodeId,
                                           @Param("plantId") String plantId,
                                           @Param("languageId") String languageId,
                                           @Param("warehouseId") String warehouseId,
                                           @Param("refDocNumber") String refDocNumber,
                                           @Param("preOutboundNo") String preOutboundNo,
                                           @Param("statusId") Long statusId,
                                           @Param("statusDescription") String statusDescription,
                                           @Param("lineNumber") List<Long> lineNumber,
                                           @Param("deliveredPercentage") String deliveredPercentage,
                                           @Param("deliveryConfirmedOn") Date deliveryConfirmedOn);


    OutboundLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, Long deletionIndicator);

    List<OutboundLineV2> findByRefDocNumberAndItemCodeAndDeletionIndicator(String refDocNumber, String itemCode, Long deletionIndicator);

    List<OutboundLineV2> findByRefDocNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(String refDocNumber, String itemCode, String manufacturerName, Long deletionIndicator);

//    @Query(value = "select ob.c_id,ob.itm_code,ob.lang_id,ob.ob_line_no,ob.partner_code,ob.plant_id,ob.pre_ob_no,ob.ref_doc_no,ob.wh_id,ob.str_no,ob.dlv_ctd_by,\n" +
//            "ob.dlv_ctd_on,ob.is_deleted,ob.dlv_cnf_by,ob.dlv_cnf_on,ob.dlv_ord_no,ob.dlv_qty,ob.dlv_uom,ob.item_text,ob.ord_qty,ob.ord_uom,ob.ob_ord_typ_id,\n" +
//            "ob.ref_field_1,ob.ref_field_2,ob.ref_field_3,ob.ref_field_4,ob.ref_field_5,ob.ref_field_6,ob.ref_field_7,ob.ref_field_8,\n" +
//            "ob.dlv_rev_by,ob.dlv_rev_on,ob.sp_st_ind_id,ob.status_id,ob.stck_typ_id,ob.dlv_utd_by,ob.dlv_utd_on,ob.var_id,ob.var_sub_id,\n" +
//            "ob.c_text,ob.plant_text,ob.wh_text,ob.status_text,ob.middleware_id,ob.middleware_header_id,ob.middleware_table,ob.supplier_invoice_no,ob.sales_order_number,ob.manufacturer_full_name,\n" +
//            "(select SUM(p.PICK_CNF_QTY) from tblpickupline p \n" +
//            "where \n" +
//            "p.wh_id = ob.wh_id and p.PRE_OB_NO = ob.PRE_OB_NO and p.OB_LINE_NO = ob.OB_LINE_NO and p.itm_code = ob.itm_code and p.ref_doc_no = ob.ref_doc_no and p.is_deleted = 0 \n" +
//            "group by p.ref_doc_no) as ref_field_9,\n" +
//            "(select SUM(q.QC_QTY) from tblqualityline q\n" +
//            "where \n" +
//            "q.wh_id = ob.wh_id and q.PRE_OB_NO = ob.PRE_OB_NO and q.OB_LINE_NO = ob.OB_LINE_NO and q.itm_code = ob.itm_code and q.ref_doc_no = ob.ref_doc_no and q.is_deleted = 0 \n" +
//            "group by q.ref_doc_no) as ref_field_10 \n" +
//            "from tbloutboundline ob\n" +
//            "where \n" +
//            "(COALESCE(:companyCodeId, null) IS NULL OR (ob.c_id IN (:companyCodeId))) and \n" +
//            "(COALESCE(:languageId, null) IS NULL OR (ob.lang_id IN (:languageId))) and \n" +
//            "(COALESCE(:plantId, null) IS NULL OR (ob.wh_id IN (:plantId))) and \n" +
//            "(COALESCE(:warehouseId, null) IS NULL OR (ob.wh_id IN (:warehouseId))) and \n" +
//            "(COALESCE(:refDocNo, null) IS NULL OR (ob.ref_doc_no IN (:refDocNo))) and \n" +
//            "(COALESCE(:partnerCode, null) IS NULL OR (ob.partner_code IN (:partnerCode))) and \n" +
//            "(COALESCE(:preObNumber, null) IS NULL OR (ob.pre_ob_no IN (:preObNumber))) and \n" +
//            "(COALESCE(:statusId, null) IS NULL OR (ob.status_id IN (:statusId))) and \n" +
//            "(COALESCE(:lineNo, null) IS NULL OR (ob.ob_line_no IN (:lineNo))) and \n" +
//            "(COALESCE(:itemCode, null) IS NULL OR (ob.itm_code IN (:itemCode))) and\n" +
//            "(COALESCE(:orderType, null) IS NULL OR (ob.ob_ord_typ_id IN (:orderType))) and \n" +
//            "(COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) IS NULL OR (ob.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDeliveryDate), null))) \n" +
//            "group by ob.c_id,ob.itm_code,ob.lang_id,ob.ob_line_no,ob.partner_code,ob.plant_id,ob.pre_ob_no,ob.ref_doc_no,ob.wh_id,ob.str_no,ob.dlv_ctd_by,\n" +
//            "ob.dlv_ctd_on,ob.is_deleted,ob.dlv_cnf_by,ob.dlv_cnf_on,ob.dlv_ord_no,ob.dlv_qty,ob.dlv_uom,ob.item_text,ob.ord_qty,ob.ord_uom,ob.ob_ord_typ_id,\n" +
//            "ob.ref_field_1,ob.ref_field_2,ob.ref_field_3,ob.ref_field_4,ob.ref_field_5,ob.ref_field_6,ob.ref_field_7,ob.ref_field_8,ob.ref_field_9,ob.ref_field_10,\n" +
//            "ob.dlv_rev_by,ob.dlv_rev_on,ob.sp_st_ind_id,ob.status_id,ob.stck_typ_id,ob.dlv_utd_by,ob.dlv_utd_on,ob.var_id,ob.var_sub_id,\n" +
//            "ob.c_text,ob.plant_text,ob.wh_text,ob.status_text,ob.middleware_id,ob.middleware_header_id,ob.middleware_table,\n" +
//            "ob.supplier_invoice_no,ob.sales_order_number,ob.manufacturer_full_name", nativeQuery = true)
//    public List<FindQualityLineOutput> findOutboundLineNew(@Param("companyCodeId") List<String> companyCodeId,
//                                                           @Param("languageId") List<String> languageId,
//                                                           @Param("plantId") List<String> plantId,
//                                                           @Param("warehouseId") List<String> warehouseId,
//                                                           @Param("fromDeliveryDate") Date fromDeliveryDate,
//                                                           @Param("toDeliveryDate") Date toDeliveryDate,
//                                                           @Param("preObNumber") List<String> preObNumber,
//                                                           @Param("refDocNo") List<String> refDocNo,
//                                                           @Param("lineNo") List<Long> lineNo,
//                                                           @Param("itemCode") List<String> itemCode,
//                                                           @Param("statusId") List<Long> statusId,
//                                                           @Param("orderType") List<String> orderType,
//                                                           @Param("partnerCode") List<String> partnerCode);

    @Query(value = "select sum(PICK_CNF_QTY) pcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tpl from tblpickupline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select sum(QC_QTY) qcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tql from tblqualityline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select \n" +
            "ob.c_id companyCodeId,\n" +
            "ob.itm_code itemCode,\n" +
            "ob.lang_id languageId,\n" +
            "ob.ob_line_no lineNumber,\n" +
            "ob.partner_code partnerCode,\n" +
            "ob.plant_id plantId,\n" +
            "ob.pre_ob_no preOutboundNo,\n" +
            "ob.ref_doc_no refDocNumber,\n" +
            "ob.wh_id warehouseId,\n" +
            "ob.str_no batchSerialNumber,\n" +
            "ob.dlv_ctd_by createdBy,\n" +
//            "DATEADD(HOUR,3,ob.dlv_ctd_on) createdOn,\n" +
            "ob.dlv_ctd_on createdOn,\n" +
            "ob.is_deleted deletionIndicator,\n" +
            "ob.dlv_cnf_by deliveryConfirmedBy,\n" +
//            "DATEADD(HOUR,3,ob.dlv_cnf_on) deliveryConfirmedOn,\n" +
            "ob.dlv_cnf_on deliveryConfirmedOn,\n" +
            "ob.dlv_ord_no deliveryOrderNo,\n" +
            "ob.dlv_qty deliveryQty,\n" +
            "ob.dlv_uom deliveryUom,\n" +
            "ob.item_text description,\n" +
            "ob.ord_qty orderQty,\n" +
            "ob.ord_uom orderUom,\n" +
            "ob.ob_ord_typ_id outboundOrderTypeId,\n" +
            "ob.ref_field_1 referenceField1,\n" +
            "ob.ref_field_2 referenceField2,\n" +
            "ob.ref_field_3 referenceField3,\n" +
            "ob.ref_field_4 referenceField4,\n" +
            "ob.ref_field_5 referenceField5,\n" +
            "ob.ref_field_6 referenceField6,\n" +
            "ob.ref_field_7 referenceField7,\n" +
            "ob.ref_field_8 referenceField8,\n" +
            "ob.dlv_rev_by reversedBy,\n" +
//            "DATEADD(HOUR,3,ob.dlv_rev_on) reversedOn,\n" +
            "ob.dlv_rev_on reversedOn,\n" +
            "ob.sp_st_ind_id specialStockIndicatorId,\n" +
            "ob.status_id statusId,\n" +
            "ob.stck_typ_id stockTypeId,\n" +
            "ob.dlv_utd_by updatedBy,\n" +
//            "DATEADD(HOUR,3,ob.dlv_utd_on) updatedOn,\n" +
            "ob.dlv_utd_on updatedOn,\n" +
            "ob.var_id variantCode,\n" +
            "ob.var_sub_id variantSubCode,\n" +
            "ob.mfr_name manufacturerName,\n" +
            "ob.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
            "ob.PICK_LIST_NUMBER pickListNumber,\n" +
//            "DATEADD(HOUR,3,ob.INVOICE_DATE) invoiceDate,\n" +
            "ob.INVOICE_DATE invoiceDate,\n" +
            "ob.DELIVERY_TYPE deliveryType,\n" +
            "ob.CUSTOMER_ID customerId,\n" +
            "ob.CUSTOMER_NAME customerName,\n" +
            "ob.ADDRESS address,\n" +
            "ob.PHONE_NUMBER phoneNumber,\n" +
            "ob.ALTERNATE_NO alternateNo,\n" +
            "ob.STATUS status,\n" +
            "ob.TARGET_BRANCH_CODE targetBranchCode,\n" +
            "ob.c_text companyDescription,\n" +
            "ob.plant_text plantDescription,\n" +
            "ob.wh_text warehouseDescription,\n" +
            "ob.status_text statusDescription,\n" +
            "ob.middleware_id middlewareId,\n" +
            "ob.middleware_header_id middlewareHeaderId,\n" +
            "ob.middleware_table middlewareTable,\n" +
            "ob.ref_doc_type referenceDocumentType,\n" +
            "ob.supplier_invoice_no supplierInvoiceNo,\n" +
            "ob.sales_order_number salesOrderNumber,\n" +
            "ob.manufacturer_full_name manufacturerFullName,\n" +
            "ob.PARTNER_ITEM_BARCODE barcodeId,\n" +
            "ob.HE_NO handlingEquipment,\n" +
            "ob.ASS_PICKER_ID assignedPickerId,\n" +
            "ob.CUSTOMER_TYPE customerType,\n" +
//            "(select count(ref_doc_no) from tbloutboundline ob2 where \n" +
//            "ob2.wh_id = ob.wh_id and ob2.c_id = ob2.c_id and ob2.plant_id=ob.plant_id and ob2.lang_id = ob.lang_id and \n" +
//            "ob2.status_id in (48,50,57) and ob2.is_deleted = 0) tracking, \n" +
            "(select pcQty from #tpl p \n" +
            "where \n" +
            "p.wh_id = ob.wh_id and p.c_id = ob.c_id and p.plant_id=ob.plant_id and p.lang_id = ob.lang_id and \n" +
            "p.PRE_OB_NO = ob.PRE_OB_NO and p.OB_LINE_NO = ob.OB_LINE_NO and p.itm_code = ob.itm_code and p.ref_doc_no = ob.ref_doc_no) as referenceField9,\n" +
            "(select qcQty from #tql q\n" +
            "where \n" +
            "q.wh_id = ob.wh_id and q.c_id = ob.c_id and q.plant_id=ob.plant_id and q.lang_id = ob.lang_id and \n" +
            "q.PRE_OB_NO = ob.PRE_OB_NO and q.OB_LINE_NO = ob.OB_LINE_NO and q.itm_code = ob.itm_code and q.ref_doc_no = ob.ref_doc_no) as referenceField10 \n" +
            "from tbloutboundline ob\n" +
            "where \n" +
            "ob.is_deleted = 0 and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (ob.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (ob.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (ob.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (ob.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:refDocNo, null) IS NULL OR (ob.ref_doc_no IN (:refDocNo))) and \n" +
            "(COALESCE(:partnerCode, null) IS NULL OR (ob.partner_code IN (:partnerCode))) and \n" +
            "(COALESCE(:preObNumber, null) IS NULL OR (ob.pre_ob_no IN (:preObNumber))) and \n" +
            "(COALESCE(:statusId, null) IS NULL OR (ob.status_id IN (:statusId))) and \n" +
            "(COALESCE(:lineNo, null) IS NULL OR (ob.ob_line_no IN (:lineNo))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ob.itm_code IN (:itemCode))) and\n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (ob.MFR_NAME IN (:manufacturerName))) and\n" +
            "(COALESCE(:salesOrderNumber, null) IS NULL OR (ob.SALES_ORDER_NUMBER IN (:salesOrderNumber))) and\n" +
            "(COALESCE(:targetBranchCode, null) IS NULL OR (ob.TARGET_BRANCH_CODE IN (:targetBranchCode))) and\n" +
            "(COALESCE(:orderType, null) IS NULL OR (ob.ob_ord_typ_id IN (:orderType))) and \n" +
            "(COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) IS NULL OR (ob.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDeliveryDate), null))) \n"
            , nativeQuery = true)
    public List<OutboundLineOutput> findOutboundLineNew(@Param("companyCodeId") List<String> companyCodeId,
                                                        @Param("languageId") List<String> languageId,
                                                        @Param("plantId") List<String> plantId,
                                                        @Param("warehouseId") List<String> warehouseId,
                                                        @Param("fromDeliveryDate") Date fromDeliveryDate,
                                                        @Param("toDeliveryDate") Date toDeliveryDate,
                                                        @Param("preObNumber") List<String> preObNumber,
                                                        @Param("refDocNo") List<String> refDocNo,
                                                        @Param("lineNo") List<Long> lineNo,
                                                        @Param("itemCode") List<String> itemCode,
                                                        @Param("salesOrderNumber") List<String> salesOrderNumber,
                                                        @Param("targetBranchCode") List<String> targetBranchCode,
                                                        @Param("manufacturerName") List<String> manufacturerName,
                                                        @Param("statusId") List<Long> statusId,
                                                        @Param("orderType") List<String> orderType,
                                                        @Param("partnerCode") List<String> partnerCode);

    //this method to avoid time out while calling findoutboundline
    @Query(value = "select sum(PICK_CNF_QTY) pcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tpl from tblpickupline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select sum(QC_QTY) qcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tql from tblqualityline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select \n" +
            "ob.c_id companyCodeId,\n" +
            "ob.itm_code itemCode,\n" +
            "ob.lang_id languageId,\n" +
            "ob.ob_line_no lineNumber,\n" +
            "ob.partner_code partnerCode,\n" +
            "ob.plant_id plantId,\n" +
            "ob.pre_ob_no preOutboundNo,\n" +
            "ob.ref_doc_no refDocNumber,\n" +
            "ob.wh_id warehouseId,\n" +
            "ob.str_no batchSerialNumber,\n" +
            "ob.dlv_ctd_by createdBy,\n" +
            "ob.dlv_ctd_on createdOn,\n" +
            "ob.is_deleted deletionIndicator,\n" +
            "ob.dlv_cnf_by deliveryConfirmedBy,\n" +
            "ob.dlv_cnf_on deliveryConfirmedOn,\n" +
            "ob.dlv_ord_no deliveryOrderNo,\n" +
            "ob.dlv_qty deliveryQty,\n" +
            "ob.dlv_uom deliveryUom,\n" +
            "ob.item_text description,\n" +
            "ob.ord_qty orderQty,\n" +
            "ob.ord_uom orderUom,\n" +
            "ob.ob_ord_typ_id outboundOrderTypeId,\n" +
            "ob.ref_field_1 referenceField1,\n" +
            "ob.ref_field_2 referenceField2,\n" +
            "ob.ref_field_3 referenceField3,\n" +
            "ob.ref_field_4 referenceField4,\n" +
            "ob.ref_field_5 referenceField5,\n" +
            "ob.ref_field_6 referenceField6,\n" +
            "ob.ref_field_7 referenceField7,\n" +
            "ob.ref_field_8 referenceField8,\n" +
            "ob.dlv_rev_by reversedBy,\n" +
            "ob.dlv_rev_on reversedOn,\n" +
            "ob.sp_st_ind_id specialStockIndicatorId,\n" +
            "ob.status_id statusId,\n" +
            "ob.stck_typ_id stockTypeId,\n" +
            "ob.dlv_utd_by updatedBy,\n" +
            "ob.dlv_utd_on updatedOn,\n" +
            "ob.var_id variantCode,\n" +
            "ob.var_sub_id variantSubCode,\n" +
            "ob.mfr_name manufacturerName,\n" +
            "ob.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
            "ob.PICK_LIST_NUMBER pickListNumber,\n" +
            "ob.INVOICE_DATE invoiceDate,\n" +
            "ob.DELIVERY_TYPE deliveryType,\n" +
            "ob.CUSTOMER_ID customerId,\n" +
            "ob.CUSTOMER_NAME customerName,\n" +
            "ob.ADDRESS address,\n" +
            "ob.PHONE_NUMBER phoneNumber,\n" +
            "ob.ALTERNATE_NO alternateNo,\n" +
            "ob.STATUS status,\n" +
            "ob.TARGET_BRANCH_CODE targetBranchCode,\n" +
            "ob.c_text companyDescription,\n" +
            "ob.plant_text plantDescription,\n" +
            "ob.wh_text warehouseDescription,\n" +
            "ob.status_text statusDescription,\n" +
            "ob.middleware_id middlewareId,\n" +
            "ob.middleware_header_id middlewareHeaderId,\n" +
            "ob.middleware_table middlewareTable,\n" +
            "ob.ref_doc_type referenceDocumentType,\n" +
            "ob.supplier_invoice_no supplierInvoiceNo,\n" +
            "ob.sales_order_number salesOrderNumber,\n" +
            "ob.manufacturer_full_name manufacturerFullName,\n" +
            "ob.PARTNER_ITEM_BARCODE barcodeId,\n" +
            "ob.HE_NO handlingEquipment,\n" +
            "ob.ASS_PICKER_ID assignedPickerId, \n" +
            "ob.VEHICLE_NO vehicleNO, ob.DRIVER_NAME driverName, ob.REMARKS remarks, \n" +
            "ob.CUSTOMER_TYPE customerType,\n" +
            "p.pcQty referenceField9,\n" +
            "q.qcQty referenceField10\n" +
            "from tbloutboundline ob \n" +
            "left join #tpl p on p.wh_id = ob.wh_id and p.c_id = ob.c_id and p.plant_id=ob.plant_id and p.lang_id = ob.lang_id and \n" +
            "p.PRE_OB_NO = ob.PRE_OB_NO and p.OB_LINE_NO = ob.OB_LINE_NO and p.itm_code = ob.itm_code and p.ref_doc_no = ob.ref_doc_no \n" +
            "left join #tql q on q.wh_id = ob.wh_id and q.c_id = ob.c_id and q.plant_id=ob.plant_id and q.lang_id = ob.lang_id and \n" +
            "q.PRE_OB_NO = ob.PRE_OB_NO and q.OB_LINE_NO = ob.OB_LINE_NO and q.itm_code = ob.itm_code and q.ref_doc_no = ob.ref_doc_no \n" +
            "where \n" +
            "ob.is_deleted = 0 and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (ob.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (ob.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (ob.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (ob.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:refDocNo, null) IS NULL OR (ob.ref_doc_no IN (:refDocNo))) and \n" +
            "(COALESCE(:partnerCode, null) IS NULL OR (ob.partner_code IN (:partnerCode))) and \n" +
            "(COALESCE(:preObNumber, null) IS NULL OR (ob.pre_ob_no IN (:preObNumber))) and \n" +
            "(COALESCE(:statusId, null) IS NULL OR (ob.status_id IN (:statusId))) and \n" +
            "(COALESCE(:lineNo, null) IS NULL OR (ob.ob_line_no IN (:lineNo))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ob.itm_code IN (:itemCode))) and\n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (ob.MFR_NAME IN (:manufacturerName))) and\n" +
            "(COALESCE(:salesOrderNumber, null) IS NULL OR (ob.SALES_ORDER_NUMBER IN (:salesOrderNumber))) and\n" +
            "(COALESCE(:targetBranchCode, null) IS NULL OR (ob.TARGET_BRANCH_CODE IN (:targetBranchCode))) and\n" +
            "(COALESCE(:orderType, null) IS NULL OR (ob.ob_ord_typ_id IN (:orderType))) and \n" +
            "(COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) IS NULL OR (ob.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDeliveryDate), null))) \n"
            , nativeQuery = true)
    public List<OutboundLineOutput> findOutboundLine(@Param("companyCodeId") List<String> companyCodeId,
                                                     @Param("languageId") List<String> languageId,
                                                     @Param("plantId") List<String> plantId,
                                                     @Param("warehouseId") List<String> warehouseId,
                                                     @Param("fromDeliveryDate") Date fromDeliveryDate,
                                                     @Param("toDeliveryDate") Date toDeliveryDate,
                                                     @Param("preObNumber") List<String> preObNumber,
                                                     @Param("refDocNo") List<String> refDocNo,
                                                     @Param("lineNo") List<Long> lineNo,
                                                     @Param("itemCode") List<String> itemCode,
                                                     @Param("salesOrderNumber") List<String> salesOrderNumber,
                                                     @Param("targetBranchCode") List<String> targetBranchCode,
                                                     @Param("manufacturerName") List<String> manufacturerName,
                                                     @Param("statusId") List<Long> statusId,
                                                     @Param("orderType") List<String> orderType,
                                                     @Param("partnerCode") List<String> partnerCode);

    //this method to avoid time out while calling findoutboundline
    @Query(value = "select sum(PICK_CNF_QTY) pcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tpl from tblpickupline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select sum(QC_QTY) qcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tql from tblqualityline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select \n" +
            "ob.c_id companyCodeId,\n" +
            "ob.itm_code itemCode,\n" +
            "ob.lang_id languageId,\n" +
            "ob.ob_line_no lineNumber,\n" +
            "ob.partner_code partnerCode,\n" +
            "ob.plant_id plantId,\n" +
            "ob.pre_ob_no preOutboundNo,\n" +
            "ob.ref_doc_no refDocNumber,\n" +
            "ob.wh_id warehouseId,\n" +
            "ob.str_no batchSerialNumber,\n" +
            "ob.dlv_ctd_by createdBy,\n" +
            "ob.dlv_ctd_on createdOn,\n" +
            "ob.is_deleted deletionIndicator,\n" +
            "ob.dlv_cnf_by deliveryConfirmedBy,\n" +
            "ob.dlv_cnf_on deliveryConfirmedOn,\n" +
            "ob.dlv_ord_no deliveryOrderNo,\n" +
            //"ob.dlv_qty deliveryQty,\n" +
            "pcQty deliveryQty,\n" +
            "ob.dlv_uom deliveryUom,\n" +
            "ob.item_text description,\n" +
            "ob.ord_qty orderQty,\n" +
            "ob.ord_uom orderUom,\n" +
            "ob.ob_ord_typ_id outboundOrderTypeId,\n" +
            "ob.ref_field_1 referenceField1,\n" +
            "ob.ref_field_2 referenceField2,\n" +
            "ob.ref_field_3 referenceField3,\n" +
            "ob.ref_field_4 referenceField4,\n" +
            "ob.ref_field_5 referenceField5,\n" +
            "ob.ref_field_6 referenceField6,\n" +
            "ob.ref_field_7 referenceField7,\n" +
            "ob.ref_field_8 referenceField8,\n" +
            "ob.dlv_rev_by reversedBy,\n" +
            "ob.dlv_rev_on reversedOn,\n" +
            "ob.sp_st_ind_id specialStockIndicatorId,\n" +
            "ob.status_id statusId,\n" +
            "ob.stck_typ_id stockTypeId,\n" +
            "ob.dlv_utd_by updatedBy,\n" +
            "ob.dlv_utd_on updatedOn,\n" +
            "ob.var_id variantCode,\n" +
            "ob.var_sub_id variantSubCode,\n" +
            "ob.mfr_name manufacturerName,\n" +
            "ob.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
            "ob.PICK_LIST_NUMBER pickListNumber,\n" +
            "ob.INVOICE_DATE invoiceDate,\n" +
            "ob.DELIVERY_TYPE deliveryType,\n" +
            "ob.CUSTOMER_ID customerId,\n" +
            "ob.CUSTOMER_NAME customerName,\n" +
            "ob.ADDRESS address,\n" +
            "ob.PHONE_NUMBER phoneNumber,\n" +
            "ob.ALTERNATE_NO alternateNo,\n" +
            "ob.STATUS status,\n" +
            "ob.TARGET_BRANCH_CODE targetBranchCode,\n" +
            "ob.c_text companyDescription,\n" +
            "ob.plant_text plantDescription,\n" +
            "ob.wh_text warehouseDescription,\n" +
            "ob.status_text statusDescription,\n" +
            "ob.middleware_id middlewareId,\n" +
            "ob.middleware_header_id middlewareHeaderId,\n" +
            "ob.middleware_table middlewareTable,\n" +
            "ob.ref_doc_type referenceDocumentType,\n" +
            "ob.supplier_invoice_no supplierInvoiceNo,\n" +
            "ob.sales_order_number salesOrderNumber,\n" +
            "ob.manufacturer_full_name manufacturerFullName,\n" +
            "ob.PARTNER_ITEM_BARCODE barcodeId,\n" +
            "ob.HE_NO handlingEquipment,\n" +
            "ob.ASS_PICKER_ID assignedPickerId, \n" +
            "ob.VEHICLE_NO vehicleNO, ob.DRIVER_NAME driverName, ob.REMARKS remarks, \n" +
            "ob.CUSTOMER_TYPE customerType,\n" +
            "p.pcQty referenceField9,\n" +
            "q.qcQty referenceField10\n" +
            "from tbloutboundline ob \n" +
            "left join #tpl p on p.wh_id = ob.wh_id and p.c_id = ob.c_id and p.plant_id=ob.plant_id and p.lang_id = ob.lang_id and \n" +
            "p.PRE_OB_NO = ob.PRE_OB_NO and p.OB_LINE_NO = ob.OB_LINE_NO and p.itm_code = ob.itm_code and p.ref_doc_no = ob.ref_doc_no \n" +
            "left join #tql q on q.wh_id = ob.wh_id and q.c_id = ob.c_id and q.plant_id=ob.plant_id and q.lang_id = ob.lang_id and \n" +
            "q.PRE_OB_NO = ob.PRE_OB_NO and q.OB_LINE_NO = ob.OB_LINE_NO and q.itm_code = ob.itm_code and q.ref_doc_no = ob.ref_doc_no \n" +
            "where \n" +
            "ob.is_deleted = 0 and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (ob.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (ob.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (ob.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (ob.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:refDocNo, null) IS NULL OR (ob.ref_doc_no IN (:refDocNo))) and \n" +
            "(COALESCE(:partnerCode, null) IS NULL OR (ob.partner_code IN (:partnerCode))) and \n" +
            "(COALESCE(:preObNumber, null) IS NULL OR (ob.pre_ob_no IN (:preObNumber))) and \n" +
            "(COALESCE(:statusId, null) IS NULL OR (ob.status_id IN (:statusId))) and \n" +
            "(COALESCE(:lineNo, null) IS NULL OR (ob.ob_line_no IN (:lineNo))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ob.itm_code IN (:itemCode))) and\n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (ob.MFR_NAME IN (:manufacturerName))) and\n" +
            "(COALESCE(:salesOrderNumber, null) IS NULL OR (ob.SALES_ORDER_NUMBER IN (:salesOrderNumber))) and\n" +
            "(COALESCE(:targetBranchCode, null) IS NULL OR (ob.TARGET_BRANCH_CODE IN (:targetBranchCode))) and\n" +
            "(COALESCE(:orderType, null) IS NULL OR (ob.ob_ord_typ_id IN (:orderType))) and \n" +
            "(COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) IS NULL OR (ob.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDeliveryDate), null))) \n"
            , nativeQuery = true)
    public List<OutboundLineOutput> findOutboundLineV7(@Param("companyCodeId") List<String> companyCodeId,
                                                     @Param("languageId") List<String> languageId,
                                                     @Param("plantId") List<String> plantId,
                                                     @Param("warehouseId") List<String> warehouseId,
                                                     @Param("fromDeliveryDate") Date fromDeliveryDate,
                                                     @Param("toDeliveryDate") Date toDeliveryDate,
                                                     @Param("preObNumber") List<String> preObNumber,
                                                     @Param("refDocNo") List<String> refDocNo,
                                                     @Param("lineNo") List<Long> lineNo,
                                                     @Param("itemCode") List<String> itemCode,
                                                     @Param("salesOrderNumber") List<String> salesOrderNumber,
                                                     @Param("targetBranchCode") List<String> targetBranchCode,
                                                     @Param("manufacturerName") List<String> manufacturerName,
                                                     @Param("statusId") List<Long> statusId,
                                                     @Param("orderType") List<String> orderType,
                                                     @Param("partnerCode") List<String> partnerCode);

    //this method to avoid time out while calling findoutboundline
    @Query(value =
//            "select sum(PICK_CNF_QTY) pcQty, sum(no_bags) noBags, ref_field_1, ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
//            "into #tpl from tblpickupline where is_deleted=0 \n" +
//            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id,ref_field_1 \n" +

            "select sum(p.PICK_CNF_QTY) pcQty, sum(p.no_bags) noBags, \n" +
            "    p.ref_field_1, p.ref_doc_no,p.itm_code,\n" +
            "    p.ob_line_no, p.pre_ob_no,p.c_id,p.plant_id,p.lang_id, p.wh_id,\n" +
            "    inv.mfr_date as manufacturerDate,inv.exp_date as expiryDate into #tpl from tblpickupline p\n" +
            "    LEFT JOIN tblinventory inv \n" +
            "    ON inv.ref_doc_no = p.ref_doc_no \n" +
            "    AND inv.itm_code = p.itm_code\n" +
            "    where p.is_deleted = 0 \n" +
            "    group by p.ref_doc_no, p.itm_code, p.ob_line_no,p.pre_ob_no, p.c_id, \n" +
            "    p.plant_id, p.lang_id, p.wh_id, p.ref_field_1,inv.mfr_date,inv.exp_date \n"+

            "select sum(QC_QTY) qcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tql from tblqualityline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select \n" +
            "ob.c_id companyCodeId,\n" +
            "ob.itm_code itemCode,\n" +
            "ob.lang_id languageId,\n" +
            "ob.ob_line_no lineNumber,\n" +
            "ob.partner_code partnerCode,\n" +
            "ob.plant_id plantId,\n" +
            "ob.pre_ob_no preOutboundNo,\n" +
            "ob.ref_doc_no refDocNumber,\n" +
            "ob.wh_id warehouseId,\n" +
            "ob.str_no batchSerialNumber,\n" +
            "ob.dlv_ctd_by createdBy,\n" +
            "ob.dlv_ctd_on createdOn,\n" +
            "ob.is_deleted deletionIndicator,\n" +
            "ob.dlv_cnf_by deliveryConfirmedBy,\n" +
            "ob.dlv_cnf_on deliveryConfirmedOn,\n" +
            "ob.dlv_ord_no deliveryOrderNo,\n" +
            "ob.dlv_qty deliveryQty,\n" +
            "ob.dlv_uom deliveryUom,\n" +
            "ob.item_text description,\n" +
            "ob.ord_qty orderQty,\n" +
            "ob.ord_uom orderUom,\n" +
            "ob.ob_ord_typ_id outboundOrderTypeId,\n" +
            "ob.ref_field_1 referenceField1,\n" +
            "ob.ref_field_2 referenceField2,\n" +
            "ob.ref_field_3 referenceField3,\n" +
            "ob.ref_field_4 referenceField4,\n" +
            "ob.ref_field_5 referenceField5,\n" +
            "ob.ref_field_6 referenceField6,\n" +
            "ob.ref_field_7 referenceField7,\n" +
            "ob.ref_field_8 referenceField8,\n" +
            "ob.dlv_rev_by reversedBy,\n" +
            "ob.dlv_rev_on reversedOn,\n" +
            "ob.sp_st_ind_id specialStockIndicatorId,\n" +
            "ob.status_id statusId,\n" +
            "ob.stck_typ_id stockTypeId,\n" +
            "ob.dlv_utd_by updatedBy,\n" +
            "ob.dlv_utd_on updatedOn,\n" +
            "ob.var_id variantCode,\n" +
            "ob.var_sub_id variantSubCode,\n" +
            "ob.mfr_name manufacturerName,\n" +
            "ob.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
            "ob.PICK_LIST_NUMBER pickListNumber,\n" +
            "ob.INVOICE_DATE invoiceDate,\n" +
            "ob.DELIVERY_TYPE deliveryType,\n" +
            "ob.CUSTOMER_ID customerId,\n" +
            "ob.CUSTOMER_NAME customerName,\n" +
            "ob.ADDRESS address,\n" +
            "ob.PHONE_NUMBER phoneNumber,\n" +
            "ob.ALTERNATE_NO alternateNo,\n" +
            "ob.STATUS status,\n" +
            "ob.TARGET_BRANCH_CODE targetBranchCode,\n" +
            "ob.c_text companyDescription,\n" +
            "ob.plant_text plantDescription,\n" +
            "ob.wh_text warehouseDescription,\n" +
            "ob.status_text statusDescription,\n" +
            "ob.middleware_id middlewareId,\n" +
            "ob.middleware_header_id middlewareHeaderId,\n" +
            "ob.middleware_table middlewareTable,\n" +
            "ob.ref_doc_type referenceDocumentType,\n" +
            "ob.supplier_invoice_no supplierInvoiceNo,\n" +
            "ob.sales_order_number salesOrderNumber,\n" +
            "ob.manufacturer_full_name manufacturerFullName,\n" +
            "ob.PARTNER_ITEM_BARCODE barcodeId,\n" +
            "ob.HE_NO handlingEquipment,\n" +
            "ob.ASS_PICKER_ID assignedPickerId, \n" +
            "ob.VEHICLE_NO vehicleNO, ob.DRIVER_NAME driverName, ob.REMARKS remarks, \n" +
            "ob.CUSTOMER_TYPE customerType,\n" +
            "p.pcQty referenceField9,\n" +
            "p.noBags noBags, \n" +
            "p.ref_field_1 reasons, \n" +
            "ROUND(q.qcQty, 2) AS referenceField10\n" +  // Rounding off 2 decimal places - Aakash Vinayak, 04/07/2025
            "from tbloutboundline ob \n" +
            "left join #tpl p on p.wh_id = ob.wh_id and p.c_id = ob.c_id and p.plant_id=ob.plant_id and p.lang_id = ob.lang_id and \n" +
            "p.PRE_OB_NO = ob.PRE_OB_NO and p.OB_LINE_NO = ob.OB_LINE_NO and p.itm_code = ob.itm_code and p.ref_doc_no = ob.ref_doc_no \n" +
            "left join #tql q on q.wh_id = ob.wh_id and q.c_id = ob.c_id and q.plant_id=ob.plant_id and q.lang_id = ob.lang_id and \n" +
            "q.PRE_OB_NO = ob.PRE_OB_NO and q.OB_LINE_NO = ob.OB_LINE_NO and q.itm_code = ob.itm_code and q.ref_doc_no = ob.ref_doc_no \n" +
            "where \n" +
            "ob.is_deleted = 0 and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (ob.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (ob.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (ob.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (ob.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:refDocNo, null) IS NULL OR (ob.ref_doc_no IN (:refDocNo))) and \n" +
            "(COALESCE(:partnerCode, null) IS NULL OR (ob.partner_code IN (:partnerCode))) and \n" +
            "(COALESCE(:preObNumber, null) IS NULL OR (ob.pre_ob_no IN (:preObNumber))) and \n" +
            "(COALESCE(:statusId, null) IS NULL OR (ob.status_id IN (:statusId))) and \n" +
            "(COALESCE(:lineNo, null) IS NULL OR (ob.ob_line_no IN (:lineNo))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ob.itm_code IN (:itemCode))) and\n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (ob.MFR_NAME IN (:manufacturerName))) and\n" +
            "(COALESCE(:salesOrderNumber, null) IS NULL OR (ob.SALES_ORDER_NUMBER IN (:salesOrderNumber))) and\n" +
            "(COALESCE(:targetBranchCode, null) IS NULL OR (ob.TARGET_BRANCH_CODE IN (:targetBranchCode))) and\n" +
            "(COALESCE(:orderType, null) IS NULL OR (ob.ob_ord_typ_id IN (:orderType))) and \n" +
            "(COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) IS NULL OR (ob.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDeliveryDate), null))) \n"
            , nativeQuery = true)
    public List<OutboundLineOutput> findOutboundLineV6(@Param("companyCodeId") List<String> companyCodeId,
                                                     @Param("languageId") List<String> languageId,
                                                     @Param("plantId") List<String> plantId,
                                                     @Param("warehouseId") List<String> warehouseId,
                                                     @Param("fromDeliveryDate") Date fromDeliveryDate,
                                                     @Param("toDeliveryDate") Date toDeliveryDate,
                                                     @Param("preObNumber") List<String> preObNumber,
                                                     @Param("refDocNo") List<String> refDocNo,
                                                     @Param("lineNo") List<Long> lineNo,
                                                     @Param("itemCode") List<String> itemCode,
                                                     @Param("salesOrderNumber") List<String> salesOrderNumber,
                                                     @Param("targetBranchCode") List<String> targetBranchCode,
                                                     @Param("manufacturerName") List<String> manufacturerName,
                                                     @Param("statusId") List<Long> statusId,
                                                     @Param("orderType") List<String> orderType,
                                                     @Param("partnerCode") List<String> partnerCode);


    //this method to avoid time out while calling findoutboundline
    @Query(value = "select sum(PICK_CNF_QTY) pcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tpl from tblpickupline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select sum(QC_QTY) qcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tql from tblqualityline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select \n" +
            "ob.c_id companyCodeId,\n" +
            "ob.itm_code itemCode,\n" +
            "ob.lang_id languageId,\n" +
            "ob.ob_line_no lineNumber,\n" +
            "ob.partner_code partnerCode,\n" +
            "ob.plant_id plantId,\n" +
            "ob.pre_ob_no preOutboundNo,\n" +
            "ob.ref_doc_no refDocNumber,\n" +
            "ob.wh_id warehouseId,\n" +
            "ob.str_no batchSerialNumber,\n" +
            "ob.dlv_ctd_by createdBy,\n" +
            "ob.dlv_ctd_on createdOn,\n" +
            "ob.is_deleted deletionIndicator,\n" +
            "ob.dlv_cnf_by deliveryConfirmedBy,\n" +
            "ob.dlv_cnf_on deliveryConfirmedOn,\n" +
            "ob.dlv_ord_no deliveryOrderNo,\n" +
            "ob.dlv_qty deliveryQty,\n" +
            "ob.dlv_uom deliveryUom,\n" +
            "ob.item_text description,\n" +
            "ob.ord_qty orderQty,\n" +
            "ob.ord_uom orderUom,\n" +
            "ob.ob_ord_typ_id outboundOrderTypeId,\n" +
            "ob.ref_field_1 referenceField1,\n" +
            "ob.ref_field_2 referenceField2,\n" +
            "ob.ref_field_3 referenceField3,\n" +
            "ob.ref_field_4 referenceField4,\n" +
            "ob.ref_field_5 referenceField5,\n" +
            "ob.ref_field_6 referenceField6,\n" +
            "ob.ref_field_7 referenceField7,\n" +
            "ob.ref_field_8 referenceField8,\n" +
            "ob.dlv_rev_by reversedBy,\n" +
            "ob.dlv_rev_on reversedOn,\n" +
            "ob.sp_st_ind_id specialStockIndicatorId,\n" +
            "ob.status_id statusId,\n" +
            "ob.stck_typ_id stockTypeId,\n" +
            "ob.dlv_utd_by updatedBy,\n" +
            "ob.dlv_utd_on updatedOn,\n" +
            "ob.var_id variantCode,\n" +
            "ob.var_sub_id variantSubCode,\n" +
            "ob.mfr_name manufacturerName,\n" +
            "ob.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
            "ob.PICK_LIST_NUMBER pickListNumber,\n" +
            "ob.INVOICE_DATE invoiceDate,\n" +
            "ob.DELIVERY_TYPE deliveryType,\n" +
            "ob.CUSTOMER_ID customerId,\n" +
            "ob.CUSTOMER_NAME customerName,\n" +
            "ob.ADDRESS address,\n" +
            "ob.PHONE_NUMBER phoneNumber,\n" +
            "ob.ALTERNATE_NO alternateNo,\n" +
            "ob.STATUS status,\n" +
            "ob.TARGET_BRANCH_CODE targetBranchCode,\n" +
            "ob.c_text companyDescription,\n" +
            "ob.plant_text plantDescription,\n" +
            "ob.wh_text warehouseDescription,\n" +
            "ob.status_text statusDescription,\n" +
            "ob.middleware_id middlewareId,\n" +
            "ob.middleware_header_id middlewareHeaderId,\n" +
            "ob.middleware_table middlewareTable,\n" +
            "ob.ref_doc_type referenceDocumentType,\n" +
            "ob.supplier_invoice_no supplierInvoiceNo,\n" +
            "ob.sales_order_number salesOrderNumber,\n" +
            "ob.manufacturer_full_name manufacturerFullName,\n" +
            "ob.PARTNER_ITEM_BARCODE barcodeId,\n" +
            "ob.HE_NO handlingEquipment,\n" +
            "ob.ASS_PICKER_ID assignedPickerId,\n" +
            "ob.CUSTOMER_TYPE customerType,\n" +

            "ob.MATERIAL_NO materialNo, \n" +
            "ob.PRICE_SEGMENT priceSegment, \n" +
            "ob.ARTICLE_NO articleNo, \n" +
            "ob.GENDER gender, \n" +
            "ob.COLOR color, \n" +
            "ob.SIZE size, \n" +
            "ob.NO_PAIRS noPairs, \n" +
            "ob.QTY_IN_CASE qtyInCase, \n" +
            "ob.QTY_IN_PIECE qtyInPiece, \n" +
            "ob.QTY_IN_CRATE qtyInCrate, \n" +
            "ob.DELIVERED_PERCENTAGE deliveredPercentage, \n" +
            "p.pcQty referenceField9,\n" +
            "q.qcQty referenceField10\n" +
            "from tbloutboundline ob \n" +
            "left join #tpl p on p.wh_id = ob.wh_id and p.c_id = ob.c_id and p.plant_id=ob.plant_id and p.lang_id = ob.lang_id and \n" +
            "p.PRE_OB_NO = ob.PRE_OB_NO and p.OB_LINE_NO = ob.OB_LINE_NO and p.itm_code = ob.itm_code and p.ref_doc_no = ob.ref_doc_no \n" +
            "left join #tql q on q.wh_id = ob.wh_id and q.c_id = ob.c_id and q.plant_id=ob.plant_id and q.lang_id = ob.lang_id and \n" +
            "q.PRE_OB_NO = ob.PRE_OB_NO and q.OB_LINE_NO = ob.OB_LINE_NO and q.itm_code = ob.itm_code and q.ref_doc_no = ob.ref_doc_no \n" +
            "where \n" +
            "ob.is_deleted = 0 and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (ob.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (ob.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (ob.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (ob.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:refDocNo, null) IS NULL OR (ob.ref_doc_no IN (:refDocNo))) and \n" +
            "(COALESCE(:partnerCode, null) IS NULL OR (ob.partner_code IN (:partnerCode))) and \n" +
            "(COALESCE(:preObNumber, null) IS NULL OR (ob.pre_ob_no IN (:preObNumber))) and \n" +
            "(COALESCE(:statusId, null) IS NULL OR (ob.status_id IN (:statusId))) and \n" +
            "(COALESCE(:lineNo, null) IS NULL OR (ob.ob_line_no IN (:lineNo))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ob.itm_code IN (:itemCode))) and\n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (ob.MFR_NAME IN (:manufacturerName))) and\n" +
            "(COALESCE(:salesOrderNumber, null) IS NULL OR (ob.SALES_ORDER_NUMBER IN (:salesOrderNumber))) and\n" +
            "(COALESCE(:targetBranchCode, null) IS NULL OR (ob.TARGET_BRANCH_CODE IN (:targetBranchCode))) and\n" +
            "(COALESCE(:orderType, null) IS NULL OR (ob.ob_ord_typ_id IN (:orderType))) and \n" +

            "(COALESCE(:materialNo, null) IS NULL OR (ob.MATERIAL_NO IN (:materialNo))) and\n" +
            "(COALESCE(:priceSegment, null) IS NULL OR (ob.PRICE_SEGMENT IN (:priceSegment))) and\n" +
            "(COALESCE(:articleNo, null) IS NULL OR (ob.ARTICLE_NO IN (:articleNo))) and\n" +
            "(COALESCE(:gender, null) IS NULL OR (ob.GENDER IN (:gender))) and\n" +
            "(COALESCE(:color, null) IS NULL OR (ob.COLOR IN (:color))) and\n" +
            "(COALESCE(:size, null) IS NULL OR (ob.SIZE IN (:size))) and\n" +
            "(COALESCE(:noPairs, null) IS NULL OR (ob.NO_PAIRS IN (:noPairs))) and \n" +

            "(COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) IS NULL OR (ob.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDeliveryDate), null))) \n"
            , nativeQuery = true)
    public List<OutboundLineOutput> findOutboundLineV5(@Param("companyCodeId") List<String> companyCodeId,
                                                       @Param("languageId") List<String> languageId,
                                                       @Param("plantId") List<String> plantId,
                                                       @Param("warehouseId") List<String> warehouseId,
                                                       @Param("fromDeliveryDate") Date fromDeliveryDate,
                                                       @Param("toDeliveryDate") Date toDeliveryDate,
                                                       @Param("preObNumber") List<String> preObNumber,
                                                       @Param("refDocNo") List<String> refDocNo,
                                                       @Param("lineNo") List<Long> lineNo,
                                                       @Param("itemCode") List<String> itemCode,
                                                       @Param("salesOrderNumber") List<String> salesOrderNumber,
                                                       @Param("targetBranchCode") List<String> targetBranchCode,
                                                       @Param("manufacturerName") List<String> manufacturerName,
                                                       @Param("statusId") List<Long> statusId,
                                                       @Param("orderType") List<String> orderType,
                                                       @Param("materialNo") List<String> materialNo,
                                                       @Param("priceSegment") List<String> priceSegment,
                                                       @Param("articleNo") List<String> articleNo,
                                                       @Param("gender") List<String> gender,
                                                       @Param("color") List<String> color,
                                                       @Param("size") List<String> size,
                                                       @Param("noPairs") List<String> noPairs,
                                                       @Param("partnerCode") List<String> partnerCode);


    @Transactional
    @Procedure(procedureName = "obline_update_qlcreate_proc")
    public void updateOBlineByQLCreateProcedure(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("refDocNumber") String refDocNumber,
            @Param("partnerCode") String partnerCode,
            @Param("lineNumber") Long lineNumber,
            @Param("itmCode") String itmCode,
            @Param("deliveryQty") Double deliveryQty,
            @Param("deliveryOrderNo") String deliveryOrderNo,
            @Param("statusDescription") String statusDescription,
            @Param("statusId") Long statusId
    );

    @Query(value = "select ol.wh_id as warehouseId,ol.c_id as companyCodeId,ol.plant_id as plantId,ol.lang_id as languageId, ol.itm_code as itemCode , \n" +
            " ol.wh_text as warehouseDescription,ol.c_text as companyDescription,ol.plant_text as plantDescription,ol.status_text as statusDescription,\n" +
            " 'OutBound' as documentType , ol.ref_doc_no as documentNumber, ol.partner_code as customerCode,\n" +
            " ol.DLV_CNF_ON as confirmedOn, ol.dlv_qty as movementQty, ol.item_text as itemText,ol.mfr_name as manufacturerSKU \n" +
            " from tbloutboundline ol\n" +
//            " join tblimbasicdata1 im on ol.itm_code = im.itm_code \n" +
            " WHERE ol.ITM_CODE in (:itemCode) " +
//            "AND im.WH_ID in (:warehouseId) " +
            " AND ol.C_ID in (:companyCodeId) AND ol.PLANT_ID in (:plantId) AND ol.LANG_ID in (:languageId) AND ol.WH_ID in (:warehouseId) AND ol.status_id = :statusId " +
            " AND ol.DLV_CNF_ON between :fromDate and :toDate ", nativeQuery = true)
    public List<StockMovementReportImpl> findOutboundLineForStockMovement(@Param("itemCode") List<String> itemCode,
                                                                          @Param("warehouseId") List<String> warehouseId,
                                                                          @Param("companyCodeId") List<String> companyCodeId,
                                                                          @Param("plantId") List<String> plantId,
                                                                          @Param("languageId") List<String> languageId,
                                                                          @Param("statusId") Long statusId,
                                                                          @Param("fromDate") Date fromDate,
                                                                          @Param("toDate") Date toDate);

    List<OutboundLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long DeletionIndicator);

    List<OutboundLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long DeletionIndicator);

    List<OutboundLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String refDocNumber, String itemCode, String manufacturerName, Long DeletionIndicator);

    @Transactional
    @Procedure(procedureName = "outboundline_status_update_proc")
    public void updateOutboundlineStatusUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("itmCode") String itmCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("partnerCode") String partnerCode,
            @Param("handlingEquipment") String handlingEquipment,
            @Param("assignedPickerId") String assignedPickerId,
            @Param("lineNumber") Long lineNumber,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedOn") Date updatedOn
    );

    @Transactional
    @Procedure(procedureName = "outboundline_status_update_proc")
    public void updateOutboundlineStatusUpdateProcV5(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("itmCode") String itmCode,
            @Param("barcodeId") String barcodeId,
            @Param("manufacturerName") String manufacturerName,
            @Param("partnerCode") String partnerCode,
            @Param("handlingEquipment") String handlingEquipment,
            @Param("assignedPickerId") String assignedPickerId,
            @Param("lineNumber") Long lineNumber,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedOn") Date updatedOn
    );

    @Query(value = "SELECT COUNT(ref_doc_no) as count FROM \n"
            + "tbloutboundline qh WHERE \n"
            + "(:companyCode IS NULL OR qh.c_id IN (:companyCode)) AND \n"
            + "(:plantId IS NULL OR qh.plant_id IN (:plantId)) AND \n"
            + "(:languageId IS NULL OR qh.lang_id IN (:languageId)) AND \n"
            + "(:warehouseId IS NULL OR qh.wh_id IN (:warehouseId)) AND \n"
            + "(qh.status_id IN (:statusId)) AND \n"
            + "qh.is_deleted = 0 ", nativeQuery = true)
    public Long gettrackingCount(
            @Param("companyCode") List<String> companyCode,
            @Param("plantId") List<String> plantId,
            @Param("languageId") List<String> languageId,
            @Param("warehouseId") List<String> warehouseId,
            @Param("statusId") List<Long> statusId);

    OutboundLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String manufacturerName, Long deletionIndicator);

    @Query(value = "SELECT COUNT(OB_LINE_NO) \r\n"
            + "FROM tbloutboundline \r\n"
            + "WHERE C_ID IN (:companyCodeId) AND PLANT_ID IN (:plantId) AND LANG_ID IN (:languageId) AND WH_ID IN (:warehouseId) AND PRE_OB_NO IN (:preOutboundNo) \r\n"
            + "AND REF_DOC_NO IN (:refDocNumber) AND IS_DELETED = 0 ", nativeQuery = true)
    public Long getOutboundLinesCount(@Param("companyCodeId") List<String> companyCodeId,
                                      @Param("plantId") List<String> plantId,
                                      @Param("languageId") List<String> languageId,
                                      @Param("warehouseId") List<String> warehouseId,
                                      @Param("preOutboundNo") List<String> preOutboundNo,
                                      @Param("refDocNumber") List<String> refDocNumber);

    @Query(value = "SELECT COUNT(OB_LINE_NO) \r\n"
            + "FROM tbloutboundline \r\n"
            + "WHERE C_ID IN (:companyCodeId) AND PLANT_ID IN (:plantId) AND LANG_ID IN (:languageId) AND WH_ID IN (:warehouseId) AND PRE_OB_NO IN (:preOutboundNo) \r\n"
            + "AND REF_DOC_NO IN (:refDocNumber) AND STATUS_ID IN (:statusId) AND IS_DELETED = 0 ", nativeQuery = true)
    public Long getOutboundLinesStatusIdCount(@Param("companyCodeId") List<String> companyCodeId,
                                              @Param("plantId") List<String> plantId,
                                              @Param("languageId") List<String> languageId,
                                              @Param("warehouseId") List<String> warehouseId,
                                              @Param("preOutboundNo") List<String> preOutboundNo,
                                              @Param("refDocNumber") List<String> refDocNumber,
                                              @Param("statusId") List<Long> statusId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.updatedOn = :updatedOn, \r\n"
            + " ob.assignedPickerId = :assignedPickerId, ob.manufacturerName = :manufacturerName, ob.updatedBy = :loginUserId \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.partnerCode = :partnerCode AND ob.itemCode = :itemCode AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber")
    void updateOutboundLineV2(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param("preOutboundNo") String preOutboundNo,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("partnerCode") String partnerCode,
                              @Param("lineNumber") Long lineNumber,
                              @Param("itemCode") String itemCode,
                              @Param("statusId") Long statusId,
                              @Param("statusDescription") String statusDescription,
                              @Param("assignedPickerId") String assignedPickerId,
                              @Param("manufacturerName") String manufacturerName,
                              @Param("loginUserId") String loginUserId,
                              @Param("updatedOn") Date updatedOn);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, \r\n"
            + " ob.assignedPickerId = :assignedPickerId \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.partnerCode = :partnerCode AND ob.itemCode = :itemCode AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber")
    void updateOutboundLineV2(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param("preOutboundNo") String preOutboundNo,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("partnerCode") String partnerCode,
                              @Param("lineNumber") Long lineNumber,
                              @Param("itemCode") String itemCode,
                              @Param("statusId") Long statusId,
                              @Param("statusDescription") String statusDescription,
                              @Param("assignedPickerId") String assignedPickerId);

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

    @Transactional
    @Procedure(procedureName = "outboundline_status_update_bags_proc")
    public void updateOutboundlineStatusUpdateBagsProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("itmCode") String itmCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("partnerCode") String partnerCode,
            @Param("handlingEquipment") String handlingEquipment,
            @Param("assignedPickerId") String assignedPickerId,
            @Param("lineNumber") Long lineNumber,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedOn") Date updatedOn,
            @Param("bagSize") Double bagSize,
            @Param("noBags") Double noBags);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.updatedOn = :updatedOn, \r\n"
            + " ob.handlingEquipment = :handlingEquipment, ob.deliveryQty = :deliveryQty, ob.deliveryOrderNo = :deliveryOrderNo, ob.updatedBy = :loginUserId \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.partnerCode = :partnerCode AND ob.itemCode = :itemCode AND ob.manufacturerName = :manufacturerName AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber")
    void updateOutboundLineV2(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param("preOutboundNo") String preOutboundNo,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("partnerCode") String partnerCode,
                              @Param("lineNumber") Long lineNumber,
                              @Param("itemCode") String itemCode,
                              @Param("manufacturerName") String manufacturerName,
                              @Param("handlingEquipment") String handlingEquipment,
                              @Param("deliveryQty") Double deliveryQty,
                              @Param("deliveryOrderNo") String deliveryOrderNo,
                              @Param("statusId") Long statusId,
                              @Param("statusDescription") String statusDescription,
                              @Param("loginUserId") String loginUserId,
                              @Param("updatedOn") Date updatedOn);


    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE tbloutboundline SET " +
            "STATUS_ID = :statusId, " +
            "STATUS_TEXT = :statusDescription, " +
            "DLV_UTD_ON = :updatedOn, " +
            "HE_NO = :handlingEquipment, " +
            "DLV_QTY = :deliveryQty, " +
            "DLV_ORD_NO = :deliveryOrderNo, " +
            "DLV_UTD_BY = :loginUserId " +
            "WHERE C_ID = :companyCodeId AND " +
            "PLANT_ID = :plantId AND " +
            "LANG_ID = :languageId AND " +
            "WH_ID = :warehouseId AND " +
            "PARTNER_CODE = :partnerCode AND " +
            "ITM_CODE = :itemCode AND " +
            "mfr_name = :manufacturerName AND " +
            "REF_DOC_NO = :refDocNumber AND " +
            "PRE_OB_NO = :preOutboundNo AND " +
            "OB_LINE_NO = :lineNumber", nativeQuery = true)
    void updateOutboundLineNative(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("refDocNumber") String refDocNumber,
            @Param("partnerCode") String partnerCode,
            @Param("lineNumber") Long lineNumber,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("handlingEquipment") String handlingEquipment,
            @Param("deliveryQty") Double deliveryQty,
            @Param("deliveryOrderNo") String deliveryOrderNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("loginUserId") String loginUserId,
            @Param("updatedOn") Date updatedOn
    );


    @Modifying
    @Transactional
    @Query(value =
            "UPDATE tbloutboundline SET " +
                    "STATUS_ID      = :statusId, " +
                    "STATUS_TEXT    = :statusText, " +
                    "ASS_PICKER_ID  = :userId, " +
                    "DLV_UTD_BY     = :updatedBy, " +
                    "DLV_UTD_ON     = :updatedOn WHERE " +
                    "C_ID           = :companyCodeId AND " +
                    "PLANT_ID       = :plantId AND " +
                    "WH_ID          = :warehouseId AND " +
                    "LANG_ID        = :languageId AND " +
                    "PRE_OB_NO      = :preoutboundNo AND " +
                    "REF_DOC_NO     = :refDocNo AND " +
                    "PARTNER_CODE   = :partnerCode AND " +
                    "OB_LINE_NO     = :lineNo AND " +
                    "ITM_CODE       = :itemCode AND " +
                    "IS_DELETED     = 0",
            nativeQuery = true)
    void updateOutboundLineV5(
            @Param("statusId") Long statusId,
            @Param("statusText") String statusText,
            @Param("userId") String userId,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("languageId") String languageId,
            @Param("preoutboundNo") String preoutboundNo,
            @Param("refDocNo") String refDocNo,
            @Param("partnerCode") String partnerCode,
            @Param("lineNo") Long lineNo,
            @Param("itemCode") String itemCode);


    @Modifying
    @Query(value = "UPDATE tbloutboundline SET REF_FIELD_6 = :refField6 WHERE C_ID = :companyCodeId \n " +
            "AND PLANT_ID = :plantId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNo AND PRE_OB_NO = :preOutboundNo \n " +
            "AND OB_LINE_NO = :lineNo AND IS_DELETED = 0 AND ITM_CODE = :itemCode", nativeQuery = true)
    void updateOutboundLineV6(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("warehouseId") String warehouseId,
                              @Param("refDocNo") String refDocNo,
                              @Param("preOutboundNo") String preOutboundNo,
                              @Param("itemCode") String itemCode,
                              @Param("lineNo") Long lineNo,
                              @Param("refField6") String refField6);


    @Modifying
    @Transactional
    @Query(value =
            "UPDATE tbloutboundline SET " +
                    "STATUS_ID      = :statusId, " +
                    "STATUS_TEXT    = :statusText, " +
                    "ASS_PICKER_ID  = :userId, " +
                    "DLV_UTD_BY     = :updatedBy, " +
                    "DLV_UTD_ON     = :updatedOn WHERE " +
                    "C_ID           = :companyCodeId AND " +
                    "PLANT_ID       = :plantId AND " +
                    "WH_ID          = :warehouseId AND " +
                    "LANG_ID        = :languageId AND " +
                    "PRE_OB_NO      = :preoutboundNo AND " +
                    "REF_DOC_NO     = :refDocNo AND " +
                    "PARTNER_CODE   = :partnerCode AND " +
                    "OB_LINE_NO     = :lineNo AND " +
                    "ITM_CODE       = :itemCode AND " +
                    "IS_DELETED     = 0",
            nativeQuery = true)
    void updateOutboundLineV7(
            @Param("statusId") Long statusId,
            @Param("statusText") String statusText,
            @Param("userId") String userId,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("languageId") String languageId,
            @Param("preoutboundNo") String preoutboundNo,
            @Param("refDocNo") String refDocNo,
            @Param("partnerCode") String partnerCode,
            @Param("lineNo") Long lineNo,
            @Param("itemCode") String itemCode);

    @Query(value ="select  tl.partner_code AS partnerCode,tl.partner_nm AS description \n"+
            " from tblbusinesspartner tl \n" +
            "WHERE \n"+
            "tl.partner_code IN (:partnerCode) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n"+
            "tl.is_deleted=0 ",nativeQuery = true)

    public IKeyValuePair getPartnerCodeAndDescription(@Param(value="partnerCode") String partnerCode,
                                                        @Param(value = "languageId") String languageId,
                                                        @Param(value = "companyCodeId")String companyCodeId,
                                                        @Param(value = "plantId")String plantId,
                                                        @Param(value = "warehouseId")String warehouseId);

    void deleteByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);


    @Query(value = "select itm_code itemCode, max(ord_qty) orderQty, (select sum(pick_cnf_qty) from tblpickupline where ref_doc_no = :refDocNo and itm_code = :itemCode group by itm_code) as deliveryQty ,max(driver_name) driverName, max(REMARKS) remarks, max(VEHICLE_NO) vehicleNo, max(item_text) itemText from tbloutboundline " +
            "where ref_doc_no = :refDocNo and itm_code = :itemCode group by itm_code", nativeQuery = true)
    IKeyValuePair getOutboundLineValueV4(@Param("refDocNo") String refDocNo,
                                         @Param("itemCode") String itemCode);

    @Modifying
    @Query(value = "update tbloutboundline set status_id = :statusId , status_text = :statusDescription , dlv_utd_on = :updatedOn ," +
            " he_no = :handlingEquipment , ass_picker_id = :assignedPickerId , bag_size = :bagSize , no_bags = :noBags \n" +
            " where c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and " +
            " ref_doc_no = :refDocNumber and pre_ob_no = :preOutboundNo and itm_code = :itmCode and mfr_name = :manufacturerName " +
            " and ob_line_no = :lineNumber and partner_code = :partnerCode and is_deleted = 0\n",nativeQuery = true)
    void updateOutboundLineStatusV4(@Param("companyCodeId") String companyCodeId,
                                    @Param("plantId") String plantId,
                                    @Param("languageId") String languageId,
                                    @Param("warehouseId") String warehouseId,
                                    @Param("refDocNumber") String refDocNumber,
                                    @Param("preOutboundNo") String preOutboundNo,
                                    @Param("itmCode") String itmCode,
                                    @Param("manufacturerName") String manufacturerName,
                                    @Param("partnerCode") String partnerCode,
                                    @Param("handlingEquipment") String handlingEquipment,
                                    @Param("assignedPickerId") String assignedPickerId,
                                    @Param("lineNumber") Long lineNumber,
                                    @Param("statusId") Long statusId,
                                    @Param("statusDescription") String statusDescription,
                                    @Param("updatedOn") Date updatedOn,
                                    @Param("bagSize") Double bagSize,
                                    @Param("noBags") Double noBags);

    @Query(value = "select sum(PICK_CNF_QTY) pcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tpl from tblpickupline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select sum(QC_QTY) qcQty,ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tql from tblqualityline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +

            "select \n" +
            "ob.c_id companyCodeId,\n" +
            "ob.itm_code itemCode,\n" +
            "ob.lang_id languageId,\n" +
            "ob.ob_line_no lineNumber,\n" +
            "ob.partner_code partnerCode,\n" +
            "ob.plant_id plantId,\n" +
            "ob.pre_ob_no preOutboundNo,\n" +
            "ob.ref_doc_no refDocNumber,\n" +
            "ob.wh_id warehouseId,\n" +
            "ob.str_no batchSerialNumber,\n" +
            "ob.dlv_ctd_by createdBy,\n" +
            "ob.dlv_ctd_on createdOn,\n" +
            "ob.is_deleted deletionIndicator,\n" +
            "ob.dlv_cnf_by deliveryConfirmedBy,\n" +
            "ob.dlv_cnf_on deliveryConfirmedOn,\n" +
            "ob.dlv_ord_no deliveryOrderNo,\n" +
            //"ob.dlv_qty deliveryQty,\n" +
            "pcQty deliveryQty,\n" +
            "ob.dlv_uom deliveryUom,\n" +
            "ob.item_text description,\n" +
            "ob.ord_qty orderQty,\n" +
            "ob.ord_uom orderUom,\n" +
            "ob.ob_ord_typ_id outboundOrderTypeId,\n" +
            "ob.ref_field_1 referenceField1,\n" +
            "ob.ref_field_2 referenceField2,\n" +
            "ob.ref_field_3 referenceField3,\n" +
            "ob.ref_field_4 referenceField4,\n" +
            "ob.ref_field_5 referenceField5,\n" +
            "ob.ref_field_6 referenceField6,\n" +
            "ob.ref_field_7 referenceField7,\n" +
            "ob.ref_field_8 referenceField8,\n" +
            "ob.dlv_rev_by reversedBy,\n" +
            "ob.dlv_rev_on reversedOn,\n" +
            "ob.sp_st_ind_id specialStockIndicatorId,\n" +
            "ob.status_id statusId,\n" +
            "ob.stck_typ_id stockTypeId,\n" +
            "ob.dlv_utd_by updatedBy,\n" +
            "ob.dlv_utd_on updatedOn,\n" +
            "ob.var_id variantCode,\n" +
            "ob.var_sub_id variantSubCode,\n" +
            "ob.mfr_name manufacturerName,\n" +
            "ob.SALES_INVOICE_NUMBER salesInvoiceNumber,\n" +
            "ob.PICK_LIST_NUMBER pickListNumber,\n" +
            "ob.INVOICE_DATE invoiceDate,\n" +
            "ob.DELIVERY_TYPE deliveryType,\n" +
            "ob.CUSTOMER_ID customerId,\n" +
            "ob.CUSTOMER_NAME customerName,\n" +
            "ob.ADDRESS address,\n" +
            "ob.PHONE_NUMBER phoneNumber,\n" +
            "ob.ALTERNATE_NO alternateNo,\n" +
            "ob.STATUS status,\n" +
            "ob.TARGET_BRANCH_CODE targetBranchCode,\n" +
            "ob.c_text companyDescription,\n" +
            "ob.plant_text plantDescription,\n" +
            "ob.wh_text warehouseDescription,\n" +
            "ob.status_text statusDescription,\n" +
            "ob.middleware_id middlewareId,\n" +
            "ob.middleware_header_id middlewareHeaderId,\n" +
            "ob.middleware_table middlewareTable,\n" +
            "ob.ref_doc_type referenceDocumentType,\n" +
            "ob.supplier_invoice_no supplierInvoiceNo,\n" +
            "ob.sales_order_number salesOrderNumber,\n" +
            "ob.manufacturer_full_name manufacturerFullName,\n" +
            "ob.PARTNER_ITEM_BARCODE barcodeId,\n" +
            "ob.HE_NO handlingEquipment,\n" +
            "ob.ASS_PICKER_ID assignedPickerId, \n" +
            "ob.VEHICLE_NO vehicleNO, ob.DRIVER_NAME driverName, ob.REMARKS remarks, \n" +
            "ob.CUSTOMER_TYPE customerType,\n" +
            "p.pcQty referenceField9,\n" +
            "q.qcQty referenceField10\n" +
            "from tbloutboundline ob \n" +
            "left join #tpl p on p.wh_id = ob.wh_id and p.c_id = ob.c_id and p.plant_id=ob.plant_id and p.lang_id = ob.lang_id and \n" +
            "p.PRE_OB_NO = ob.PRE_OB_NO and p.OB_LINE_NO = ob.OB_LINE_NO and p.itm_code = ob.itm_code and p.ref_doc_no = ob.ref_doc_no \n" +
            "left join #tql q on q.wh_id = ob.wh_id and q.c_id = ob.c_id and q.plant_id=ob.plant_id and q.lang_id = ob.lang_id and \n" +
            "q.PRE_OB_NO = ob.PRE_OB_NO and q.OB_LINE_NO = ob.OB_LINE_NO and q.itm_code = ob.itm_code and q.ref_doc_no = ob.ref_doc_no \n" +
            "where \n" +
            "ob.is_deleted = 0 and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (ob.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (ob.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (ob.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (ob.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:refDocNo, null) IS NULL OR (ob.ref_doc_no IN (:refDocNo))) and \n" +
            "(COALESCE(:partnerCode, null) IS NULL OR (ob.partner_code IN (:partnerCode))) and \n" +
            "(COALESCE(:preObNumber, null) IS NULL OR (ob.pre_ob_no IN (:preObNumber))) and \n" +
            "(COALESCE(:statusId, null) IS NULL OR (ob.status_id IN (:statusId))) and \n" +
            "(COALESCE(:lineNo, null) IS NULL OR (ob.ob_line_no IN (:lineNo))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ob.itm_code IN (:itemCode))) and\n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (ob.MFR_NAME IN (:manufacturerName))) and\n" +
            "(COALESCE(:salesOrderNumber, null) IS NULL OR (ob.SALES_ORDER_NUMBER IN (:salesOrderNumber))) and\n" +
            "(COALESCE(:targetBranchCode, null) IS NULL OR (ob.TARGET_BRANCH_CODE IN (:targetBranchCode))) and\n" +
            "(COALESCE(:orderType, null) IS NULL OR (ob.ob_ord_typ_id IN (:orderType))) and \n" +
            "(COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) IS NULL OR (ob.DLV_CNF_ON between COALESCE(CONVERT(VARCHAR(255), :fromDeliveryDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDeliveryDate), null))) \n"
            , nativeQuery = true)
    public List<OutboundLineOutput> findOutboundLineV10(@Param("companyCodeId") List<String> companyCodeId,
                                                        @Param("languageId") List<String> languageId,
                                                        @Param("plantId") List<String> plantId,
                                                        @Param("warehouseId") List<String> warehouseId,
                                                        @Param("fromDeliveryDate") Date fromDeliveryDate,
                                                        @Param("toDeliveryDate") Date toDeliveryDate,
                                                        @Param("preObNumber") List<String> preObNumber,
                                                        @Param("refDocNo") List<String> refDocNo,
                                                        @Param("lineNo") List<Long> lineNo,
                                                        @Param("itemCode") List<String> itemCode,
                                                        @Param("salesOrderNumber") List<String> salesOrderNumber,
                                                        @Param("targetBranchCode") List<String> targetBranchCode,
                                                        @Param("manufacturerName") List<String> manufacturerName,
                                                        @Param("statusId") List<Long> statusId,
                                                        @Param("orderType") List<String> orderType,
                                                        @Param("partnerCode") List<String> partnerCode);

    void deleteByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndRefDocNumberAndLineNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String itemCode,String refDocNumber, Long lineNumber, String preOutboundNo, Long deletionIndicator);


//    @Modifying
//    @Query(value = "update tbloutboundline set DLV_QTY = :orderQty, ORD_QTY = :orderQty \n" +
//            "where C_ID = :companyCodeId and PLANT_ID = :plantId and ob_line_no = :lineNumber \n" +
//            "AND WH_ID = :warehouseId and REF_DOC_NO = :refDocNumber and itm_code = :itemCode and is_deleted = 0 ", nativeQuery = true)
//    void updateOrderQtyV10(@Param("companyCodeId") String companyCodeId,
//                           @Param("plantId") String plantId,
//                           @Param("warehouseId") String warehouseId,
//                           @Param("refDocNumber") String refDocNumber,
//                           @Param("itemCode") String itemCode,
//                           @Param("lineNumber") Long lineNumber,
//                           @Param("orderQty") Double orderQty);

    @Modifying
    @Query(value = "update tbloutboundline set DLV_QTY = :orderQty, ORD_QTY = :orderQty \n" +
            "where C_ID = :companyCodeId and PLANT_ID = :plantId and ob_line_no = :lineNumber \n" +
            "AND WH_ID = :warehouseId and REF_DOC_NO = :refDocNumber and itm_code = :itemCode and is_deleted = 0 ", nativeQuery = true)
    int updateOrderQtyV10(@Param("companyCodeId") String companyCodeId,
                          @Param("plantId") String plantId,
                          @Param("warehouseId") String warehouseId,
                          @Param("refDocNumber") String refDocNumber,
                          @Param("itemCode") String itemCode,
                          @Param("lineNumber") Long lineNumber,
                          @Param("orderQty") Double orderQty);

    @Modifying
    @Query(value = "UPDATE tbloutboundline SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, DLV_UTD_ON = :updatedOn, \n" +
            " ASS_PICKER_ID = :assignedPickerId, MFR_NAME = :manufacturerName, DLV_UTD_BY = :loginUserId , REF_FIELD_4 = :qty  \n" +
            " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \n" +
            " ITM_CODE = :itemCode AND \n" +
            " REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo ", nativeQuery = true)
    void updateOutboundWithoutLineV10(@Param("companyCodeId") String companyCodeId,
                                      @Param("plantId") String plantId,
                                      @Param("languageId") String languageId,
                                      @Param("warehouseId") String warehouseId,
                                      @Param("preOutboundNo") String preOutboundNo,
                                      @Param("refDocNumber") String refDocNumber,
                                      @Param("itemCode") String itemCode,
                                      @Param("statusId") Long statusId,
                                      @Param("statusDescription") String statusDescription,
                                      @Param("assignedPickerId") String assignedPickerId,
                                      @Param("manufacturerName") String manufacturerName,
                                      @Param("loginUserId") String loginUserId,
                                      @Param("updatedOn") Date updatedOn,
                                      @Param("qty") String qty);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.updatedOn = :updatedOn, \r\n"
            + " ob.deliveryQty = :deliveryQty, ob.updatedBy = :loginUserId \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.partnerCode = :partnerCode AND ob.itemCode = :itemCode AND ob.manufacturerName = :manufacturerName AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo ")
    void updateOutboundLineForDeliveryConfirmNewV10(@Param("companyCodeId") String companyCodeId,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("warehouseId") String warehouseId,
                                                    @Param("preOutboundNo") String preOutboundNo,
                                                    @Param("refDocNumber") String refDocNumber,
                                                    @Param("partnerCode") String partnerCode,
                                                    @Param("itemCode") String itemCode,
                                                    @Param("manufacturerName") String manufacturerName,
                                                    @Param("deliveryQty") Double deliveryQty,
                                                    @Param("statusId") Long statusId,
                                                    @Param("statusDescription") String statusDescription,
                                                    @Param("loginUserId") String loginUserId,
                                                    @Param("updatedOn") Date updatedOn);

    @Modifying
    @Query(value = "UPDATE tbloutboundline SET REF_FIELD_6 = :refField6 WHERE C_ID = :companyCodeId \n " +
            "AND PLANT_ID = :plantId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNo AND PRE_OB_NO = :preOutboundNo \n " +
            "AND OB_LINE_NO = :lineNo AND IS_DELETED = 0 AND ITM_CODE = :itemCode", nativeQuery = true)
    void updateOutboundLineV10(@Param("companyCodeId") String companyCodeId,
                               @Param("plantId") String plantId,
                               @Param("warehouseId") String warehouseId,
                               @Param("refDocNo") String refDocNo,
                               @Param("preOutboundNo") String preOutboundNo,
                               @Param("itemCode") String itemCode,
                               @Param("lineNo") Long lineNo,
                               @Param("refField6") String refField6);


    @Modifying
    @Query(value = "UPDATE tbloutboundline SET STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, DLV_UTD_ON = :updatedOn, \n" +
            " ASS_PICKER_ID = :assignedPickerId, MFR_NAME = :manufacturerName, DLV_UTD_BY = :loginUserId , REF_FIELD_4 = :qty  \n" +
            " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND \n" +
            " ITM_CODE = :itemCode AND \n" +
            " REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND OB_LINE_NO = :lineNumber ", nativeQuery = true)
    void updateOutboundLineV10(@Param("companyCodeId") String companyCodeId,
                               @Param("plantId") String plantId,
                               @Param("languageId") String languageId,
                               @Param("warehouseId") String warehouseId,
                               @Param("preOutboundNo") String preOutboundNo,
                               @Param("refDocNumber") String refDocNumber,
                               @Param("lineNumber") Long lineNumber,
                               @Param("itemCode") String itemCode,
                               @Param("statusId") Long statusId,
                               @Param("statusDescription") String statusDescription,
                               @Param("assignedPickerId") String assignedPickerId,
                               @Param("manufacturerName") String manufacturerName,
                               @Param("loginUserId") String loginUserId,
                               @Param("updatedOn") Date updatedOn,
                               @Param("qty") String qty);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.updatedOn = :updatedOn, \r\n"
            + " ob.deliveryQty = :deliveryQty, ob.updatedBy = :loginUserId \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.partnerCode = :partnerCode AND ob.itemCode = :itemCode AND ob.manufacturerName = :manufacturerName AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber = :lineNumber")
    void updateOutboundLineForDeliveryConfirmV10(@Param("companyCodeId") String companyCodeId,
                                                 @Param("plantId") String plantId,
                                                 @Param("languageId") String languageId,
                                                 @Param("warehouseId") String warehouseId,
                                                 @Param("preOutboundNo") String preOutboundNo,
                                                 @Param("refDocNumber") String refDocNumber,
                                                 @Param("partnerCode") String partnerCode,
                                                 @Param("lineNumber") Long lineNumber,
                                                 @Param("itemCode") String itemCode,
                                                 @Param("manufacturerName") String manufacturerName,
                                                 @Param("deliveryQty") Double deliveryQty,
                                                 @Param("statusId") Long statusId,
                                                 @Param("statusDescription") String statusDescription,
                                                 @Param("loginUserId") String loginUserId,
                                                 @Param("updatedOn") Date updatedOn);

    // BF
    @Modifying
    @Query(value = "update tbloutboundline set ASS_PICKER_ID = :assignPicker , PARTNER_ITEM_BARCODE = :barcode ," +
            " ORD_QTY = :orderQty , REF_FIELD_1 = :palletCode, brand = :customerPallet, ref_field_2 = :mfrDate, " +
            " REF_FIELD_8 = :expDate where C_ID = :companyCodeId and PLANT_ID = :plantId " +
            " and LANG_ID = :languageId and WH_ID = :warehouseId and REF_DOC_NO = :refDocNumber and " +
            " PRE_OB_NO = :preOutboundNo and ITM_CODE = :itemCode and OB_LINE_NO = :lineNumber " +
            " and PARTNER_CODE = :partnerCode ", nativeQuery = true)
    void updateOutboundLineAssignPickerV9(@Param("companyCodeId") String companyCodeId,
                                          @Param("plantId") String plantId,
                                          @Param("languageId") String languageId,
                                          @Param("warehouseId") String warehouseId,
                                          @Param("refDocNumber") String refDocNumber,
                                          @Param("preOutboundNo") String preOutboundNo,
                                          @Param("itemCode") String itemCode,
                                          @Param("lineNumber") Long lineNumber,
                                          @Param("partnerCode") String partnerCode,
                                          @Param("assignPicker") String assignPicker,
                                          @Param("barcode") String barcode,
                                          @Param("orderQty") Double orderQty,
                                          @Param("palletCode") String palletCode,
                                          @Param("customerPallet") String customerPallet,
                                          @Param("mfrDate") Date mfrDate,
                                          @Param("expDate") Date expDate);

    @Modifying
    @Query(value = "update tblordermangementline set ASS_PICKER_ID = :assignPicker ,PARTNER_ITEM_BARCODE = :barcode , " +
            " PROP_PACK_BARCODE = :packBarcode , ORD_QTY = :orderQty , PALLET_ID = :palletCode, REF_FIELD_2 = :palletCode , PROP_ST_BIN = :storageBin, " +
            " origin = :customerPallet, MFR_DATE = :mfrDate, EXP_DATE = :expDate " +
            " where C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID =:warehouseId " +
            " and REF_DOC_NO = :refDocNumber and PRE_OB_NO = :preOutboundNo and ITM_CODE = :itemCode and " +
            " OB_LINE_NO = :lineNumber and PARTNER_CODE = :partnerCode ", nativeQuery = true)
    void updateOrderManagementAssignPickerV9(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("languageId") String languageId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("refDocNumber") String refDocNumber,
                                             @Param("preOutboundNo") String preOutboundNo,
                                             @Param("itemCode") String itemCode,
                                             @Param("lineNumber") Long lineNumber,
                                             @Param("partnerCode") String partnerCode,
                                             @Param("assignPicker") String assignPicker,
                                             @Param("barcode") String barcode,
                                             @Param("packBarcode") String packBarcode,
                                             @Param("orderQty") Double orderQty,
                                             @Param("palletCode") String palletCode,
                                             @Param("storageBin") String storageBin,
                                             @Param("customerPallet") String customerPallet,
                                             @Param("mfrDate") Date mfrDate,
                                             @Param("expDate") Date expDate);

    @Modifying
    @Query(value = "update tblpickupheader set ASS_PICKER_ID = :assignPicker , PARTNER_ITEM_BARCODE = :barcode , " +
            " PROP_PACK_BARCODE = :packBarcode , PICK_TO_QTY = :orderQty , REF_FIELD_2 = :palletCode , PROP_ST_BIN = :storageBin, " +
            " origin = :customerPallet, MFR_DATE = :mfrDate, EXP_DATE = :expDate " +
            " where C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and " +
            " WH_ID = :warehouseId and REF_DOC_NO = :refDocNumber and PRE_OB_NO = :preOutboundNo and " +
            " ITM_CODE = :itemCode and OB_LINE_NO = :lineNumber and PARTNER_CODE = :partnerCode ", nativeQuery = true)
    void updatePickupHeaderAssignPickerV9(@Param("companyCodeId") String companyCodeId,
                                          @Param("plantId") String plantId,
                                          @Param("languageId") String languageId,
                                          @Param("warehouseId") String warehouseId,
                                          @Param("refDocNumber") String refDocNumber,
                                          @Param("preOutboundNo") String preOutboundNo,
                                          @Param("itemCode") String itemCode,
                                          @Param("lineNumber") Long lineNumber,
                                          @Param("partnerCode") String partnerCode,
                                          @Param("assignPicker") String assignPicker,
                                          @Param("barcode") String barcode,
                                          @Param("packBarcode") String packBarcode,
                                          @Param("orderQty") Double orderQty,
                                          @Param("palletCode") String palletCode,
                                          @Param("storageBin") String storageBin,
                                          @Param("customerPallet") String customerPallet,
                                          @Param("mfrDate") Date mfrDate,
                                          @Param("expDate") Date expDate);

    //BF
    @Modifying
    @Transactional
    @Query(value = "delete tbloutboundline where C_ID = :companyCodeId and PLANT_ID = :plantId \n" +
            "AND WH_ID = :warehouseId and REF_DOC_NO = :refDocNumber and ITM_CODE = :itemCode and REF_FIELD_1 = :palletId and IS_DELETED =0 ", nativeQuery = true)
    int deleteOutboundLineV9(@Param("companyCodeId") String companyCodeId,
                           @Param("plantId") String plantId,
                           @Param("warehouseId") String warehouseId,
                           @Param("refDocNumber") String refDocNumber,
                           @Param("itemCode") String itemCode,
                           @Param("palletId") String palletCode);

    @Modifying
    @Transactional
    @Query(value = "delete tbloutboundline where C_ID = :companyCodeId and PLANT_ID = :plantId \n" +
            "AND WH_ID = :warehouseId and REF_DOC_NO = :refDocNumber and ITM_CODE = :itemCode and ob_line_no = :lineNumber and IS_DELETED =0 ", nativeQuery = true)
    int deleteOutboundLineV9(@Param("companyCodeId") String companyCodeId,
                             @Param("plantId") String plantId,
                             @Param("warehouseId") String warehouseId,
                             @Param("refDocNumber") String refDocNumber,
                             @Param("itemCode") String itemCode,
                             @Param("lineNumber") Long lineNumber);

    // BF
    @Query(value = "Select count(*) from tbloutboundline where C_ID = :companyCodeId and PLANT_ID =:plantId and LANG_ID =:languageId and \r\n"
            + "WH_ID =:warehouseId and PRE_OB_NO =:preOutboundNo and \r\n"
            + " REF_DOC_NO =:refDocNumber and PARTNER_CODE =:partnerCode and status_Id in :statusId and IS_DELETED =:deletionIndicator", nativeQuery = true)
    public long getOutboundLineV9(
            @Param("companyCodeId") String companyCodeId, @Param("plantId") String plantId, @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId, @Param("preOutboundNo") String preOutboundNo,
            @Param("refDocNumber") String refDocNumber, @Param("partnerCode") String partnerCode, @Param("statusId") List<Long> statusId,
            @Param("deletionIndicator") long deletionIndicator);

    // BF
    OutboundLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPartnerCodeAndItemCodeAndLineNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String refDocNumber, String partnerCode, String itemCode, Long lineNumber, Long deletionIndicator);

    // BF
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OutboundLineV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.deliveredPercentage = :deliveredPercentage, ob.deliveryConfirmedOn = :deliveryConfirmedOn \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND ob.warehouseId = :warehouseId AND \r\n "
            + " ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo AND ob.lineNumber in :lineNumber and ob.itemCode =:itemCode")
    public void updateOutboundLineStatusNewV9(@Param("companyCodeId") String companyCodeId,
                                           @Param("plantId") String plantId,
                                           @Param("languageId") String languageId,
                                           @Param("warehouseId") String warehouseId,
                                           @Param("refDocNumber") String refDocNumber,
                                           @Param("preOutboundNo") String preOutboundNo,
                                           @Param("statusId") Long statusId,
                                           @Param("statusDescription") String statusDescription,
                                           @Param("lineNumber") List<Long> lineNumber,
                                           @Param("deliveredPercentage") String deliveredPercentage,
                                           @Param("deliveryConfirmedOn") Date deliveryConfirmedOn,
                                           @Param("itemCode") String itemCode);
    // BF
    @Modifying
    @Transactional
    @Query(value = "update tbloutboundline set DLV_QTY = 0 \n" +
            "where ref_doc_no = :refDocNo AND IS_DELETED = 0 ", nativeQuery = true)
    int updateOutboundLineWithoutPalletV9(@Param("refDocNo") String refDocNo);

    // BF
    @Modifying
    @Transactional
    @Query(value = "update tbloutboundline set DLV_QTY = 0 \n" +
            "where ref_doc_no = :refDocNo and REF_FIELD_1 = :palletCode AND IS_DELETED = 0 ", nativeQuery = true)
    int updateOutboundLineV9(@Param("refDocNo") String refDocNo,
                             @Param("palletCode") String palletCode);

    // BF
    @Query(value = "select cr.CONT_REC_NO as containerReceiptNo, cr.INV_NO as invoiceNo, cr.CONT_NO as containerNo, cr.CONT_TYP as containerType, \n" +
            "cr.CASE_NO as numberOfCases, cr.PAL_QTY as numberOfPallets, cr.ORIGIN as origin, cr.PARTNER_CODE as partnerCode, cr.STATUS_ID as statusId, \n" +
            "cr.STATUS_TEXT as statusDescription, cr.c_id as companyCodeId, cr.plant_id as plantId, cr.lang_id as languageId, cr.wh_id as warehouseId, \n" +
            "cr.REF_FIELD_1 as referenceField1, cr.REF_FIELD_2 as referenceField2, cr.REF_FIELD_3 as referenceField3 , cr.REF_FIELD_4 as referenceField4, \n" +
            "cr.REF_FIELD_5 as referenceField5, cr.REF_FIELD_6 as referenceField6, cr.REF_FIELD_7 as referenceField7, cr.REF_FIELD_8 as referenceField8, \n" +
            "cr.REF_FIELD_9 as referenceField9, cr.REF_FIELD_10 as referenceField10, cr.REF_FIELD_11 as referenceField11, cr.REF_FIELD_12 as referenceField12, \n" +
            "cr.REF_FIELD_13 as referenceField13, cr.REF_FIELD_14 as referenceField14, cr.REF_FIELD_15 as referenceField15, cr.REF_FIELD_16 as referenceField16, \n" +
            "cr.REF_FIELD_17 as referenceField17 , cr.REF_FIELD_18 as referenceField18, cr.REF_FIELD_19 as referenceField19 , cr.REF_FIELD_20 as referenceField20, \n" +
            "ib.REF_FIELD_1 as obReferenceField1, ib.REF_FIELD_2 as obReferenceField2, ib.REF_FIELD_3 as obReferenceField3, ib.REF_FIELD_4 as obReferenceField4, ib.REF_FIELD_5 as obReferenceField5, \n" +
            "ib.REF_FIELD_6 as obReferenceField6, ib.REF_FIELD_7 as obReferenceField7, ib.REF_FIELD_8 as obReferenceField8, ib.REF_FIELD_9 as obReferenceField9, ib.REF_FIELD_10 as obReferenceField10, \n" +
            "ib.PARTNER_ITEM_BARCODE as barcodeId, ib.OB_LINE_NO as lineNumber, ib.ITM_CODE as itemCode, ib.PRE_OB_NO as preOutboundNo, ib.OB_ORD_TYP_ID as outboundOrderTypeId, ib.ITEM_TEXT as description, \n" +
            "ib.ORD_UOM as orderUom, ib.ST_SEC_ID as storageSectionId, ib.pick_st_bin as pickStBin, ib.ALLOC_QTY as allocatedQty, ib.PICK_CNF_QTY as pickConfirmQty, ib.INV_QTY as inventoryQty, \n" +
            "ib.MRP as mrp, ib.QTY_IN_CASE as qtyInCase, ib.QTY_IN_CRATE as qtyInCrate from tblpickupline ib JOIN tblcontainerreceipt cr ON ib.c_id = cr.c_id and ib.lang_id = cr.lang_id and \n" +
            "ib.plant_id = cr.plant_id and ib.wh_id = cr.wh_id and ib.ref_doc_no = cr.inv_no where ib.c_id IN (:companyCode) and ib.plant_id IN (:plantId) and ib.lang_id IN (:languageId) and \n" +
            "ib.wh_id IN (:warehouseId) and (COALESCE(:refDocNumber, null) IS NULL OR (ib.ref_doc_no IN (:refDocNumber))) and ib.is_deleted = 0 and cr.is_deleted = 0 ", nativeQuery = true)
    List<ContainerReceiptOutboundlineImpl> getOutboundLineContainerReceipt(@Param(value = "warehouseId") List<String> warehouseId,
                                                                           @Param(value = "companyCode") List<String> companyCode,
                                                                           @Param(value = "plantId") List<String> plantId,
                                                                           @Param(value = "languageId") List<String> languageId,
                                                                           @Param(value = "refDocNumber") List<String> refDocNumber);
    @Query(value = "SELECT \n" +
            "  ob.ITM_CODE, ob.PARTNER_ITEM_BARCODE, MAX(ob.REF_DOC_NO) as orderNo,\n" +
            "  MAX(au.UOM_QTY) AS uomQty,\n" +
            "  CEILING(\n" +
            "    SUM(ISNULL(ob.PICK_CNF_QTY, 0))\n" +
            "    / NULLIF(MAX(au.UOM_QTY), 0)\n" +
            "  ) AS numberOfPallets,\n" +
            "\n" +
            "    ob.REF_FIELD_2 AS obReferenceField2, \n" +
//            "    CAST(AVG(ob.PICK_CNF_QTY) AS DECIMAL(18,2)) AS deliveryQty, \n" +
//            "    CAST(AVG(ob.PICK_CNF_QTY) AS DECIMAL(18,2)) AS pickConfirmQty, \n" +
//            "    CAST(AVG(ob.INV_QTY)      AS DECIMAL(18,2)) AS inventoryQty,  \n" +
            "    MAX(cr.CONT_REC_NO) AS containerReceiptNo,\n" +
            "    MAX(cr.INV_NO) AS invoiceNo,\n" +
            "    MAX(cr.CONT_NO) AS containerNo,\n" +
            "    MAX(cr.CONT_TYP) AS containerType,\n" +
            "    MAX(cr.CASE_NO) AS numberOfCases,\n" +
            "    MAX(ob.ORIGIN) AS origin,\n" +
            "    MAX(cr.PARTNER_CODE) AS partnerCode,\n" +
            "    MAX(cr.STATUS_ID) AS statusId,\n" +
            "    MAX(cr.STATUS_TEXT) AS statusDescription,\n" +
            "            \n" +
            "    MAX(cr.c_id) AS companyCodeId,\n" +
            "    MAX(cr.plant_id) AS plantId,\n" +
            "    MAX(cr.lang_id) AS languageId,\n" +
            "    MAX(cr.wh_id) AS warehouseId,\n" +
            "            \n" +
            "    MAX(cr.REF_FIELD_1) AS referenceField1,\n" +
            "    MAX(cr.REF_FIELD_2) AS referenceField2,\n" +
            "    MAX(cr.REF_FIELD_3) AS referenceField3,\n" +
            "    MAX(cr.REF_FIELD_4) AS referenceField4,\n" +
            "    MAX(cr.REF_FIELD_5) AS referenceField5,\n" +
            "    MAX(cr.REF_FIELD_6) AS referenceField6,\n" +
            "    MAX(cr.REF_FIELD_7) AS referenceField7,\n" +
            "    MAX(cr.REF_FIELD_8) AS referenceField8,\n" +
            "    MAX(cr.REF_FIELD_9) AS referenceField9,\n" +
            "    MAX(cr.REF_FIELD_10) AS referenceField10,\n" +
            "    MAX(cr.REF_FIELD_11) AS referenceField11,\n" +
            "    MAX(cr.REF_FIELD_12) AS referenceField12,\n" +
            "    MAX(cr.REF_FIELD_13) AS referenceField13,\n" +
            "    MAX(cr.REF_FIELD_14) AS referenceField14,\n" +
            "    MAX(cr.REF_FIELD_15) AS referenceField15,\n" +
            "    MAX(cr.REF_FIELD_16) AS referenceField16,\n" +
            "    MAX(cr.REF_FIELD_17) AS referenceField17,\n" +
            "    MAX(cr.REF_FIELD_18) AS referenceField18,\n" +
            "    MAX(cr.REF_FIELD_19) AS referenceField19,\n" +
            "    MAX(cr.REF_FIELD_20) AS referenceField20,\n" +

            "    MAX(cr.REF_FIELD_21) AS referenceField21,\n" +
            "    MAX(cr.REF_FIELD_22) AS referenceField22,\n" +
            "    MAX(cr.REF_FIELD_23) AS referenceField23,\n" +
            "    MAX(cr.REF_FIELD_24) AS referenceField24,\n" +
            "    MAX(cr.REF_FIELD_25) AS referenceField25,\n" +
            "    MAX(cr.REF_FIELD_26) AS referenceField26,\n" +
            "    MAX(cr.REF_FIELD_27) AS referenceField27,\n" +
            "    MAX(cr.REF_FIELD_28) AS referenceField28,\n" +
            "    MAX(cr.REF_FIELD_29) AS referenceField29,\n" +
            "    MAX(cr.REF_FIELD_30) AS referenceField30,\n" +
            "    MAX(cr.CTD_BY) AS createdBy, \n" +
            "            \n" +
            "    MAX(ob.REF_FIELD_1) AS obReferenceField1,\n" +
            "    MAX(ob.REF_FIELD_3) AS obReferenceField3,\n" +
            "    MAX(ob.REF_FIELD_4) AS obReferenceField4,\n" +
            "    MAX(ob.REF_FIELD_5) AS obReferenceField5,\n" +
            "    MAX(ob.REF_FIELD_6) AS obReferenceField6,\n" +
            "    MAX(ob.REF_FIELD_7) AS obReferenceField7,\n" +
            "    MAX(ob.REF_FIELD_8) AS obReferenceField8,\n" +
            "    MAX(ob.REF_FIELD_9) AS obReferenceField9,\n" +
            "    MAX(ob.REF_FIELD_10) AS obReferenceField10,\n" +
            "    MAX(ob.MATERIAL_NO) AS inventoryOwner,\n" +
            "    MAX(ob.GENDER) AS grossWeight, \n" +
            "    MAX(ob.ARTICLE_NO) AS netWeight, \n" +
            "    MAX(ob.MFR_DATE) AS manufacturerDate,\n" +
            "    MAX(ob.EXP_DATE) AS expiryDate,\n" +
            "            \n" +
            "    ob.ITM_CODE AS itemCode,\n" +
            "    ob.PARTNER_ITEM_BARCODE AS barcodeId,\n" +
            "            \n" +
            "    MAX(ob.ITEM_TEXT) AS description,\n" +
            "    MAX(ob.ORD_UOM) AS orderUom,\n" +
            "    MAX(ob.ST_SEC_ID) AS storageSectionId,\n" +
            "    MAX(ob.pick_st_bin) AS pickStBin,\n" +
            "    MAX(ob.PRE_OB_NO) AS preOutboundNo, \n" +
            "    SUM(ob.PICK_CNF_QTY) AS deliveryQty,\n" +
            "    SUM(ob.PICK_CNF_QTY) AS pickConfirmQty,\n" +
            "    SUM(ob.INV_QTY) AS inventoryQty,\n" +
            "    MAX(ob.MRP) AS mrp,\n" +
            "    MAX(ob.QTY_IN_CASE) AS qtyInCase,\n" +
            " MAX(ob.TOKEN_NUMBER) AS tokenNumber,\n" +
            "    MAX(ob.QTY_IN_CRATE) AS qtyInCrate\n" +
            "            FROM tblpickupline ob\n" +
            "            JOIN tblcontainerreceipt cr ON ob.c_id = cr.c_id\n" +
            "             AND ob.lang_id = cr.lang_id\n" +
            "             AND ob.plant_id = cr.plant_id\n" +
            "             AND ob.wh_id = cr.wh_id\n" +
            "             AND ob.ref_doc_no = cr.inv_no\n" +
            "\n" +
            "      LEFT JOIN (\n" +
            "    SELECT \n" +
            "      ITM_CODE,\n" +
            "      MAX(UOM_QTY) AS UOM_QTY\n" +
            "     FROM tblimalternateuom\n" +
            "     GROUP BY ITM_CODE\n" +
            "    ) au ON au.ITM_CODE = ob.ITM_CODE\n" +
            "\n" +
            "            WHERE (COALESCE(:companyCodeId, NULL) IS NULL OR ob.c_id IN (:companyCodeId))\n" +
            "             AND (COALESCE(:plantId, NULL) IS NULL OR ob.plant_id IN (:plantId)) \n" +
            "             AND (COALESCE(:languageId, NULL) IS NULL OR ob.lang_id IN (:languageId))\n" +
            "             AND (COALESCE(:warehouseId, NULL) IS NULL OR ob.wh_id IN (:warehouseId))\n" +
            "             AND (COALESCE(:refDocNo, NULL) IS NULL OR ob.ref_doc_no IN (:refDocNo))\n" +
            "             AND (COALESCE(:barcodeId, NULL) IS NULL OR ob.PARTNER_ITEM_BARCODE IN (:barcodeId))\n" +
            "             AND (COALESCE(:itemCode, NULL) IS NULL OR ob.ITM_CODE IN (:itemCode))\n" +
            "             AND (COALESCE(:inventoryOwner, NULL) IS NULL OR ob.MATERIAL_NO IN (:inventoryOwner))\n" +
            "     AND (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (ob.PICK_CTD_ON between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null))) \n " +
            "             AND ob.is_deleted = 0\n" +
            "             AND cr.is_deleted = 0\n" +
            "            GROUP BY ob.ref_doc_no, ob.ITM_CODE, ob.PARTNER_ITEM_BARCODE, ob.REF_FIELD_2",
            nativeQuery = true)
    List<ContainerReceiptOutboundlineImpl> getOutboundLineContainerReceiptReport(
            @Param("warehouseId") List<String> warehouseId,
            @Param("companyCodeId") List<String> companyCodeId,
            @Param("plantId") List<String> plantId,
            @Param("languageId") List<String> languageId,
            @Param("refDocNo") List<String> refDocNo,
            @Param("barcodeId") List<String> barcodeId,
            @Param("itemCode") List<String> itemCode,
            @Param("inventoryOwner") List<String> inventoryOwner,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    @Query(value = "SELECT \n" +
            "    ol.ITM_CODE             AS sku,\n" +
            "    ol.PARTNER_ITEM_BARCODE AS batchNo,\n" +
//            "    MAX(ol.DLV_CTD_BY) AS createdBy, \n" +
            "    MAX(cr.CTD_BY) AS createdBy, \n" +
            "\n" +
            "    MAX(ol.REF_DOC_NO)      AS orderNo,\n" +
            "    MAX(ol.ITEM_TEXT)       AS description,\n" +
            "    MAX(ol.TOKEN_NUMBER)       AS tokenNumber,\n" +
            "    MAX(ol.ORD_UOM)         AS uom,\n" +
            "    CAST(SUM(ol.DLV_QTY) AS DECIMAL(18,2)) AS shippedQty, \n" +
            "    MAX(ol.DLV_CNF_ON)      AS shippedDate,\n" +
            "    MAX(ol.CUSTOMER_NAME)   AS customerName,\n" +
            "    MAX(ol.MATERIAL_NO)   AS inventoryOwner, \n" +
            "\n" +
            "    MAX(ph.PICK_CTD_ON)     AS receiptDate,\n" +
            "    MAX(cr.REF_FIELD_1)     AS vehicleNo,\n" +
            "    MAX(cr.REF_FIELD_25)    AS referenceField25, \n" +
            "    MAX(cr.CONT_REC_NO)    AS containerReceiptNo, \n" +
            "    MAX(inv.MFR_DATE)       AS mfg,\n" +
            "    MAX(inv.EXP_DATE)       AS exp,\n" +
            "    MAX(ol.PRE_OB_NO)  AS preOutboundNo, \n" +
            "    MAX(ol.DLV_CTD_ON) AS createdOn \n" +
            "\n" +
            "FROM tbloutboundline ol\n " +
            "\n" +
            "/* Pickup Header (latest) */\n" +
            "OUTER APPLY (\n" +
            "    SELECT TOP 1 PICK_CTD_ON\n" +
            "    FROM tblpickupheader ph\n" +
            "    WHERE ph.C_ID = ol.C_ID\n" +
            "      AND ph.PLANT_ID = ol.PLANT_ID\n" +
            "      AND ph.LANG_ID = ol.LANG_ID\n" +
            "      AND ph.WH_ID = ol.WH_ID\n" +
            "      AND ph.REF_DOC_NO = ol.REF_DOC_NO\n" +
            "      AND ph.ITM_CODE = ol.ITM_CODE\n" +
            "      AND ph.PARTNER_ITEM_BARCODE = ol.PARTNER_ITEM_BARCODE\n" +
            "      AND ph.IS_DELETED = 0\n" +
            "    ORDER BY ph.PICK_CTD_ON DESC\n" +
            ") ph\n" +
            "\n" +
            "/* Vehicle No (latest) */\n" +
            "OUTER APPLY (\n" +
            "    SELECT TOP 1 REF_FIELD_1, REF_FIELD_25, CONT_REC_NO, CTD_BY \n" +
            "    FROM tblcontainerreceipt cr\n" +
            "    WHERE cr.C_ID = ol.C_ID\n" +
            "      AND cr.PLANT_ID = ol.PLANT_ID\n" +
            "      AND cr.LANG_ID = ol.LANG_ID\n" +
            "      AND cr.WH_ID = ol.WH_ID\n" +
            "      AND cr.INV_NO = ol.REF_DOC_NO\n" +
            "      AND cr.IS_DELETED = 0\n" +
            "    ORDER BY cr.CTD_ON DESC\n" +
            ") cr\n" +
            "\n" +
            "/* MFR Date (latest inventory) */\n" +
            "OUTER APPLY (\n" +
            "    SELECT TOP 1 MFR_DATE, EXP_DATE\n" +
            "    FROM tblinventory i\n" +
            "    WHERE i.C_ID = ol.C_ID\n" +
            "      AND i.PLANT_ID = ol.PLANT_ID\n" +
            "      AND i.LANG_ID = ol.LANG_ID\n" +
            "      AND i.WH_ID = ol.WH_ID\n" +
            "      AND i.ITM_CODE = ol.ITM_CODE\n" +
            "      AND i.BARCODE_ID = ol.PARTNER_ITEM_BARCODE\n" +
            "      AND i.IS_DELETED = 0\n" +
            "      AND i.MFR_DATE IS NOT NULL\n" +
            "    ORDER BY i.INV_ID DESC\n" +
            ") inv\n" +
            "\n" +
            "WHERE \n" +
            "    ol.C_ID IN (:companyCodeId) \n" +
            "    AND ol.PLANT_ID IN (:plantId) \n" +
            "    AND ol.LANG_ID IN (:languageId) \n" +
            "    AND ol.WH_ID IN (:warehouseId) \n" +
            "    AND (COALESCE(:refDocNumber, NULL) IS NULL OR ol.REF_DOC_NO IN (:refDocNumber)) \n" +
            "    AND (COALESCE(:itemCode, NULL) IS NULL OR ol.ITM_CODE IN (:itemCode)) \n" +
            "    AND (COALESCE(:barcodeId, NULL) IS NULL OR ol.PARTNER_ITEM_BARCODE IN (:barcodeId)) \n" +
            "    AND (COALESCE(:statusId, NULL) IS NULL OR ol.STATUS_ID IN (:statusId)) \n" +
            "    AND (COALESCE(:customerName, NULL) IS NULL OR ol.CUSTOMER_NAME IN (:customerName)) \n" +
            "    AND (COALESCE(:inventoryOwner, NULL) IS NULL OR ol.MATERIAL_NO IN (:inventoryOwner)) \n" +
            "    AND ol.DLV_CTD_ON BETWEEN :fromDate AND :toDate \n" +
            "    AND ol.IS_DELETED = 0\n" +
            "\n" +
            "GROUP BY\n" +
            "    ol.ITM_CODE,\n" +
            "    ol.PARTNER_ITEM_BARCODE, \n " +
            "    ol.ref_doc_no;", nativeQuery = true)
    public List<OutwardReportResponse> outwardReportGroupByItemBatchV9(@Param("companyCodeId") List<String> companyCodeId,
                                                                       @Param("plantId") List<String> plantId,
                                                                       @Param("languageId") List<String> languageId,
                                                                       @Param("warehouseId") List<String> warehouseId,
                                                                       @Param("refDocNumber") List<String> refDocNumber,
                                                                       @Param("itemCode") List<String> itemCode,
                                                                       @Param("barcodeId") List<String> barcodeId,
                                                                       @Param("statusId") List<Long> statusId,
                                                                       @Param("fromDate") Date fromDate,
                                                                       @Param("toDate") Date toDate,
                                                                       @Param("customerName") List<String> customerName,
                                                                       @Param("inventoryOwner") List<String> inventoryOwner);

    @Query(value = " \n" +
            "\n" +
            "WITH dates AS (\n" +
            "    SELECT DATEADD(DAY, v.number, CAST(:fromDate AS DATE)) date\n" +
            "    FROM master..spt_values v\n" +
            "    WHERE v.type='P'\n" +
            "    AND v.number <= DATEDIFF(DAY,:fromDate, :toDate)\n" +
            "),\n" +
            "\n" +
            "opening_balance AS (\n" +
            "    SELECT \n" +
            "        SUM(stockQty + inboundQty - outboundQty) AS openingQty,\n" +
            "        SUM(stockPallet + inboundPallet - outboundPallet) AS openingPallet,\n" +
            "        SUM(stockWt + inboundWt - outboundWt) AS openingWeight\n" +
            "    FROM (\n" +
            "\n" +
            "        ---------------------------------------------------------\n" +
            "        -- STOCK\n" +
            "        ---------------------------------------------------------\n" +
            "        SELECT \n" +
            "            SUM(qty) AS stockQty,\n" +
            "            0,\n" +
            "            0,\n" +
            "\n" +
            "            SUM(pallet) AS stockPallet,\n" +
            "            0,\n" +
            "            0,\n" +
            "\n" +
            "            SUM(weight),\n" +
            "            0,\n" +
            "            0\n" +
            "        FROM (\n" +
            "            SELECT \n" +
            "                s.ITM_CODE,\n" +
            "                s.BARCODE_ID,\n" +
            "\n" +
            "                SUM(s.INV_QTY) qty,\n" +
            "\n" +
            "                CEILING(SUM(s.INV_QTY) * 1.0 / NULLIF(u.UOM_QTY,0)) pallet,\n" +
            "\n" +
            "                SUM(COALESCE(TRY_CAST(s.PRICE_SEGMENT AS FLOAT),0)) weight\n" +
            "\n" +
            "            FROM tblinventorystockbfs s\n" +
            "            JOIN tblimalternateuom u\n" +
            "                 ON s.ITM_CODE = u.ITM_CODE\n" +
            "\n" +
            "            WHERE s.IU_CTD_ON < :fromDate\n" +
            "            AND s.C_ID = :companyCodeId\n" +
            "            AND s.PLANT_ID = :plantId\n" +
            "            AND s.LANG_ID = :languageId\n" +
            "            AND s.WH_ID = :warehouseId\n" +
            "            AND s.IS_DELETED = 0\n" +
            "            AND (COALESCE(:inventoryOwner, NULL) IS NULL \n" +
            "                 OR s.MATERIAL_NO IN (:inventoryOwner))\n" +
            "\n" +
            "            GROUP BY \n" +
            "                s.ITM_CODE,\n" +
            "                s.BARCODE_ID,\n" +
            "                u.UOM_QTY\n" +
            "        ) stock\n" +
            "\n" +
            "\n" +
            "        UNION ALL\n" +
            "\n" +
            "        ---------------------------------------------------------\n" +
            "        -- INBOUND\n" +
            "        ---------------------------------------------------------\n" +
            "        SELECT \n" +
            "            0,\n" +
            "            SUM(qty),\n" +
            "            0,\n" +
            "\n" +
            "            0,\n" +
            "            SUM(pallet),\n" +
            "            0,\n" +
            "\n" +
            "            0,\n" +
            "            SUM(weight),\n" +
            "            0\n" +
            "        FROM (\n" +
            "            SELECT \n" +
            "                gr.ITM_CODE,\n" +
            "                gr.BARCODE_ID,\n" +
            "\n" +
            "                SUM(gr.accept_qty + gr.damage_qty) qty,\n" +
            "\n" +
            "                CEILING(\n" +
            "                    SUM(gr.accept_qty + gr.damage_qty) * 1.0 / NULLIF(u.UOM_QTY,0)) pallet,\n" +
            "\n" +
            "                SUM(COALESCE(TRY_CAST(gr.PRICE_SEGMENT AS FLOAT),0)) weight\n" +
            "\n" +
            "            FROM tblgrline gr\n" +
            "            JOIN tblimalternateuom u\n" +
            "                 ON gr.ITM_CODE = u.ITM_CODE\n" +
            "\n" +
            "            WHERE gr.GR_CTD_ON < :fromDate\n" +
            "            AND gr.C_ID = :companyCodeId\n" +
            "            AND gr.PLANT_ID = :plantId\n" +
            "            AND gr.LANG_ID = :languageId\n" +
            "            AND gr.WH_ID = :warehouseId\n" +
            "            AND gr.IS_DELETED = 0\n" +
            "            AND (COALESCE(:inventoryOwner, NULL) IS NULL \n" +
            "                 OR gr.MATERIAL_NO IN (:inventoryOwner))\n" +
            "\n" +
            "            GROUP BY \n" +
            "                gr.ITM_CODE,\n" +
            "                gr.BARCODE_ID,\n" +
            "                u.UOM_QTY\n" +
            "        ) inbound\n" +
            "\n" +
            "\n" +
            "        UNION ALL\n" +
            "\n" +
            "        ---------------------------------------------------------\n" +
            "        -- OUTBOUND\n" +
            "        ---------------------------------------------------------\n" +
            "        SELECT \n" +
            "            0,\n" +
            "            0,\n" +
            "            SUM(qty),\n" +
            "\n" +
            "            0,\n" +
            "            0,\n" +
            "            SUM(pallet),\n" +
            "\n" +
            "            0,\n" +
            "            0,\n" +
            "            SUM(weight)\n" +
            "        FROM (\n" +
            "            SELECT \n" +
            "                ob.ITM_CODE,\n" +
            "                ob.PARTNER_ITEM_BARCODE,\n" +
            "\n" +
            "                SUM(ob.dlv_qty) qty,\n" +
            "\n" +
            "                FLOOR(SUM(ob.dlv_qty) * 1.0 / NULLIF(u.UOM_QTY,0)) pallet,\n" +
            "\n" +
            "                SUM(COALESCE(TRY_CAST(ob.PRICE_SEGMENT AS FLOAT),0)) weight\n" +
            "\n" +
            "            FROM tbloutboundline ob\n" +
            "            JOIN tblimalternateuom u\n" +
            "                 ON ob.ITM_CODE = u.ITM_CODE\n" +
            "\n" +
            "            WHERE ob.DLV_CTD_ON < :fromDate\n" +
            "            AND ob.C_ID = :companyCodeId\n" +
            "            AND ob.PLANT_ID = :plantId\n" +
            "            AND ob.LANG_ID = :languageId\n" +
            "            AND ob.WH_ID = :warehouseId\n" +
            "            AND ob.IS_DELETED = 0\n" +
            "            AND (COALESCE(:inventoryOwner, NULL) IS NULL \n" +
            "                 OR ob.MATERIAL_NO IN (:inventoryOwner))\n" +
            "\n" +
            "            GROUP BY \n" +
            "                ob.ITM_CODE,\n" +
            "                ob.PARTNER_ITEM_BARCODE,\n" +
            "                u.UOM_QTY\n" +
            "        ) outbound\n" +
            "\n" +
            "    ) x(\n" +
            "        stockQty,inboundQty,outboundQty,\n" +
            "        stockPallet,inboundPallet,outboundPallet,\n" +
            "        stockWt,inboundWt,outboundWt\n" +
            "    )\n" +
            "),\n" +
            "\n" +
            "daily_sum AS (\n" +
            "    SELECT \n" +
            "        date,\n" +
            "        SUM(inward) inward,\n" +
            "        SUM(outward) outward,\n" +
            "        SUM(inboundPallet) inboundPallet,\n" +
            "        SUM(outboundPallet) outboundPallet,\n" +
            "        SUM(inboundWeight) inboundWeight,\n" +
            "        SUM(outboundWeight) outboundWeight\n" +
            "    FROM (\n" +
            "\n" +
            "        ---------------------------------------------------------\n" +
            "        -- INBOUND\n" +
            "        ---------------------------------------------------------\n" +
            "        SELECT \n" +
            "            dt AS date,\n" +
            "            SUM(qty) inward,\n" +
            "            0 outward,\n" +
            "            SUM(pallet) inboundPallet,\n" +
            "            0 outboundPallet,\n" +
            "            SUM(weight) inboundWeight,\n" +
            "            0 outboundWeight\n" +
            "        FROM (\n" +
            "            SELECT\n" +
            "                CAST(gr.GR_CTD_ON AS DATE) dt,\n" +
            "                gr.ITM_CODE,\n" +
            "                gr.BARCODE_ID,\n" +
            "\n" +
            "                SUM(gr.accept_qty + gr.damage_qty) qty,\n" +
            "\n" +
            "                CEILING(\n" +
            "                    SUM(gr.accept_qty + gr.damage_qty) * 1.0 / NULLIF(u.UOM_QTY,0)) pallet,\n" +
            "\n" +
            "                SUM(COALESCE(TRY_CAST(gr.PRICE_SEGMENT AS FLOAT),0)) weight\n" +
            "\n" +
            "            FROM tblgrline gr\n" +
            "            JOIN tblimalternateuom u\n" +
            "                 ON gr.ITM_CODE = u.ITM_CODE\n" +
            "\n" +
            "            WHERE gr.GR_CTD_ON >= :fromDate\n" +
            "            AND gr.GR_CTD_ON < DATEADD(DAY,1,:toDate)\n" +
            "            AND gr.C_ID = :companyCodeId\n" +
            "            AND gr.PLANT_ID = :plantId\n" +
            "            AND gr.LANG_ID = :languageId\n" +
            "            AND gr.WH_ID = :warehouseId\n" +
            "            AND gr.IS_DELETED = 0\n" +
            "            AND (COALESCE(:inventoryOwner,NULL) IS NULL \n" +
            "                 OR gr.MATERIAL_NO IN (:inventoryOwner))\n" +
            "\n" +
            "            GROUP BY\n" +
            "                CAST(gr.GR_CTD_ON AS DATE),\n" +
            "                gr.ITM_CODE,\n" +
            "                gr.BARCODE_ID,\n" +
            "                u.UOM_QTY\n" +
            "        ) t\n" +
            "        GROUP BY dt\n" +
            "\n" +
            "\n" +
            "        UNION ALL\n" +
            "\n" +
            "\n" +
            "        ---------------------------------------------------------\n" +
            "        -- OUTBOUND\n" +
            "        ---------------------------------------------------------\n" +
            "        SELECT \n" +
            "            dt,\n" +
            "            0,\n" +
            "            SUM(qty),\n" +
            "            0,\n" +
            "            SUM(pallet),\n" +
            "            0,\n" +
            "            SUM(weight)\n" +
            "        FROM (\n" +
            "            SELECT\n" +
            "                CAST(ob.DLV_CTD_ON AS DATE) dt,\n" +
            "                ob.ITM_CODE,\n" +
            "                ob.partner_item_BARCODE,\n" +
            "\n" +
            "                SUM(ob.dlv_qty) qty,\n" +
            "\n" +
            "                FLOOR(SUM(ob.dlv_qty) * 1.0 / NULLIF(u.UOM_QTY,0)) pallet,\n" +
            "\n" +
            "                SUM(COALESCE(TRY_CAST(ob.PRICE_SEGMENT AS FLOAT),0)) weight\n" +
            "\n" +
            "            FROM tbloutboundline ob\n" +
            "            JOIN tblimalternateuom u\n" +
            "                 ON ob.ITM_CODE = u.ITM_CODE\n" +
            "\n" +
            "            WHERE ob.DLV_CTD_ON >= :fromDate\n" +
            "            AND ob.DLV_CTD_ON < DATEADD(DAY,1,:toDate)\n" +
            "            AND ob.C_ID = :companyCodeId\n" +
            "            AND ob.PLANT_ID = :plantId\n" +
            "            AND ob.LANG_ID = :languageId\n" +
            "            AND ob.WH_ID = :warehouseId\n" +
            "            AND ob.IS_DELETED = 0\n" +
            "            AND (COALESCE(:inventoryOwner,NULL) IS NULL \n" +
            "                 OR ob.MATERIAL_NO IN (:inventoryOwner))\n" +
            "\n" +
            "            GROUP BY\n" +
            "                CAST(ob.DLV_CTD_ON AS DATE),\n" +
            "                ob.ITM_CODE,\n" +
            "                ob.partner_item_BARCODE,\n" +
            "                u.UOM_QTY\n" +
            "        ) t\n" +
            "        GROUP BY dt\n" +
            "\n" +
            "    ) x(date,inward,outward,inboundPallet,outboundPallet,inboundWeight,outboundWeight)\n" +
            "    GROUP BY date\n" +
            ")\n" +
            "SELECT\n" +
            "    d.date,\n" +
            "    -- opening case\n" +
            "    o.openingQty\n" +
            "    + COALESCE(SUM(s.inward-s.outward)\n" +
            "      OVER (ORDER BY d.date ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING),0)\n" +
            "    AS openingCases,\n" +
            "\n" +
            "    -- opening pallet\n" +
            "    o.openingPallet\n" +
            "    + COALESCE(SUM(s.inboundPallet-s.outboundPallet)\n" +
            "      OVER (ORDER BY d.date ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING),0)\n" +
            "    AS openingPallets,\n" +
            "\n" +
            "    -- opening weight\n" +
            "    o.openingWeight\n" +
            "    + COALESCE(SUM(s.inboundWeight-s.outboundWeight)\n" +
            "      OVER (ORDER BY d.date ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING),0)\n" +
            "    AS openingWeight,\n" +
            "\n" +
            "    -- daily movement\n" +
            "    COALESCE(s.inward,0) inwardCases,\n" +
            "    COALESCE(s.outward,0) outwardCases,\n" +
            "    COALESCE(s.inboundPallet,0) inwardPallets,\n" +
            "    COALESCE(s.outboundPallet,0) outwardPallets,\n" +
            "    COALESCE(s.inboundWeight,0) inwardWeight,\n" +
            "    COALESCE(s.outboundWeight,0) outwardWeight,\n" +
            "\n" +
            "    -- closing case\n" +
            "    o.openingQty\n" +
            "    + COALESCE(SUM(s.inward-s.outward)\n" +
            "      OVER (ORDER BY d.date ROWS UNBOUNDED PRECEDING),0)\n" +
            "    AS closingCases,\n" +
            "\n" +
            "    -- closing pallet\n" +
            "    o.openingPallet\n" +
            "    + COALESCE(SUM(s.inboundPallet-s.outboundPallet)\n" +
            "      OVER (ORDER BY d.date ROWS UNBOUNDED PRECEDING),0)\n" +
            "    AS closingPallets,\n" +
            "\n" +
            "    -- closing weight\n" +
            "    o.openingWeight\n" +
            "    + COALESCE(SUM(s.inboundWeight-s.outboundWeight)\n" +
            "      OVER (ORDER BY d.date ROWS UNBOUNDED PRECEDING),0)\n" +
            "    AS closingWeight\n" +
            "\n" +
            "FROM dates d\n" +
            "CROSS JOIN opening_balance o\n" +
            "LEFT JOIN daily_sum s ON s.date = d.date\n" +
            "ORDER BY d.date;\n", nativeQuery = true)
    public List<StockMovementLedgerReport> stockLedgerReportV9(@Param("companyCodeId") String companyCodeId,
                                                               @Param("plantId") String plantId,
                                                               @Param("languageId") String languageId,
                                                               @Param("warehouseId") String warehouseId,
                                                               @Param("fromDate") Date fromDate,
                                                               @Param("toDate") Date toDate,
                                                               @Param("inventoryOwner") List<String> inventoryOwner);

    // BF
    @Modifying
    @Transactional
    @Query(value = "delete tbloutboundline where C_ID = :companyCodeId and PLANT_ID = :plantId \n" +
            "AND WH_ID = :warehouseId and REF_DOC_NO = :refDocNumber and ITM_CODE = :itemCode and REF_FIELD_1 = :palletId and IS_DELETED =0 ", nativeQuery = true)
    int deleteOutboundLine(@Param("companyCodeId") String companyCodeId,
                           @Param("plantId") String plantId,
                           @Param("warehouseId") String warehouseId,
                           @Param("refDocNumber") String refDocNumber,
                           @Param("itemCode") String itemCode,
                           @Param("palletId") String palletCode);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbloutboundline " +
            "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND WH_ID = :warehouseId AND " +
            "REF_DOC_NO = :refDocNumber AND PRE_OB_NO = :preOutboundNo AND IS_DELETED = :deletionIndicator",
            nativeQuery = true)
    void deleteOutboundLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("deletionIndicator") Long deletionIndicator
    );


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbloutboundline " +
            "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND WH_ID = :warehouseId AND " +
            "ITM_CODE = :itemCode AND REF_DOC_NO = :refDocNumber AND " +
            "PRE_OB_NO = :preOutboundNo AND IS_DELETED = :deletionIndicator",
            nativeQuery = true)
    void deleteOutboundLine(@Param("companyCodeId") String companyCodeId,
                            @Param("plantId") String plantId,
                            @Param("warehouseId") String warehouseId,
                            @Param("itemCode") String itemCode,
                            @Param("refDocNumber") String refDocNumber,
                            @Param("preOutboundNo") String preOutboundNo,
                            @Param("deletionIndicator") Long deletionIndicator);


}