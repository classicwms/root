package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.model.dto.BusinessPartner;
import com.tekclover.wms.api.outbound.transaction.model.impl.OutBoundLineImpl;
import com.tekclover.wms.api.outbound.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.OutboundLineOutput;
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
    @Query(value = "select sum(PICK_CNF_QTY) pcQty, sum(no_bags) noBags, ref_field_1, ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id \n" +
            "into #tpl from tblpickupline where is_deleted=0 \n" +
            "group by ref_doc_no,itm_code,ob_line_no,pre_ob_no,c_id,plant_id,lang_id,wh_id,ref_field_1 \n" +

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
}