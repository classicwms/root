//package com.mnrclara.spark.core.service.joinspark;
//
//import com.mnrclara.spark.core.model.FindOutBoundHeader;
//import com.mnrclara.spark.core.model.OutBoundHeader;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
//import static org.apache.spark.sql.functions.*;
//
//@Slf4j
//@Service
//public class TBLOutBoundHeaderJoinService {
//
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private Properties connProp;
//    private SparkSession sparkSession;
//    private Dataset<Row> obheadTbl;
//    private Dataset<Row> oblineTbl;
//
//    private Dataset<Row> preoutboundTbl;
//    private Dataset<Row> headerLineVal;
//    private Dataset<Row> preBLineOutBHeader;
//    private Dataset<Row> filerVal;
//
//
//    public TBLOutBoundHeaderJoinService() throws ParseException {
//
//
//        connProp = new Properties();
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
//        sparkSession = SparkSession.builder().master("local[*]").appName("OutBoundHeader.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        obheadTbl = readJdbcTable("tbloutboundheader");
//        obheadTbl.createOrReplaceTempView("tbloutboundheader");
//
//        oblineTbl = readJdbcTable("tbloutboundline");
//        oblineTbl.createOrReplaceTempView("tbloutboundline");
//
//
//        preoutboundTbl = readJdbcTable("tblpreoutboundline");
//        preoutboundTbl.createOrReplaceTempView("tblpreoutboundline");
////        joinVal = obheadTbl.join(oblineTbl, obheadTbl.col("ref_doc_no").equalTo(oblineTbl.col("ref_doc_no")), "left");
//    }
//
//    private Dataset<Row> readJdbcTable(String tableName) {
//        return sparkSession.read().jdbc("jdbc:sqlserver://3.109.20.248;databaseName=WMS_3PL", tableName, connProp);
//    }
//
//
//    public List<OutBoundHeader> findOutBoundHeader(FindOutBoundHeader findOutBoundHeader) throws Exception {
//
////        oblineTbl.show();
////        obheadTbl.show();
//
////        headerLineVal = obheadTbl.join(oblineTbl, obheadTbl.col("ref_doc_no").equalTo(oblineTbl.col("ref_doc_no")), "left")
////
////                .groupBy(
////                        obheadTbl.col("c_id"),
////                        obheadTbl.col("lang_id"),
////                        obheadTbl.col("partner_code"),
////                        obheadTbl.col("plant_id"),
////                        obheadTbl.col("pre_ob_no"),
////                        obheadTbl.col("ref_doc_no"),
////                        obheadTbl.col("wh_id"),
////                        obheadTbl.col("dlv_ctd_by"),
////                        obheadTbl.col("dlv_ctd_on"),
////                        obheadTbl.col("is_deleted"),
////                        obheadTbl.col("dlv_cnf_by"),
////                        obheadTbl.col("dlv_cnf_on"),
////                        obheadTbl.col("dlv_ord_no"),
////                        obheadTbl.col("ob_ord_typ_id"),
////                        obheadTbl.col("ref_doc_date"),
////                        obheadTbl.col("ref_doc_typ"),
////                        obheadTbl.col("remark"),
////                        obheadTbl.col("req_del_date"),
////                        obheadTbl.col("dlv_rev_by"),
////                        obheadTbl.col("dlv_rev_on"),
////                        obheadTbl.col("status_id"),
////                        obheadTbl.col("dlv_utd_by"),
////                        obheadTbl.col("dlv_utd_on"),
////                        obheadTbl.col("ref_field_1"),
////                        obheadTbl.col("ref_field_2"),
////                        obheadTbl.col("ref_field_3"),
////                        obheadTbl.col("ref_field_4"),
////                        obheadTbl.col("ref_field_5"),
////                        obheadTbl.col("ref_field_6"),
////                        obheadTbl.col("INVOICE_NO"),
////                        obheadTbl.col("C_TEXT"),
////                        obheadTbl.col("PLANT_TEXT"),
////                        obheadTbl.col("WH_TEXT"),
////                        obheadTbl.col("STATUS_TEXT"),
////                        obheadTbl.col("MIDDLEWARE_ID"),
////                        obheadTbl.col("MIDDLEWARE_TABLE"),
////                        obheadTbl.col("REF_DOC_TYP"),
////                        obheadTbl.col("SALES_ORDER_NUMBER"),
////                        obheadTbl.col("SALES_INVOICE_NUMBER"),
////                        obheadTbl.col("PICK_LIST_NUMBER"),
////                        obheadTbl.col("INVOICE_DATE"),
////                        obheadTbl.col("DELIVERY_TYPE"),
////                        obheadTbl.col("CUSTOMER_ID"),
////                        obheadTbl.col("CUSTOMER_NAME"),
////                        obheadTbl.col("ADDRESS"),
////                        obheadTbl.col("PHONE_NUMBER"),
////                        obheadTbl.col("ALTERNATE_NO"),
////                        obheadTbl.col("STATUS"),
////                        obheadTbl.col("TOKEN_NUMBER"),
////                        obheadTbl.col("TARGET_BRANCH_CODE"),
////                        obheadTbl.col("CUSTOMER_TYPE"),
////                        oblineTbl.col("ref_doc_no"),
////                        oblineTbl.col("c_id"),
////                        oblineTbl.col("lang_id"),
////                        oblineTbl.col("plant_id"),
////                        oblineTbl.col("wh_id"),
////                        oblineTbl.col("pre_ob_no"),
////                        oblineTbl.col("partner_code")
////                )
////                .agg(
////                        sum(
////                                when(oblineTbl.col("dlv_qty").isNotNull(), oblineTbl.col("dlv_qty")).otherwise(0)).alias("referenceField7"),
////                        count(
////                                when(oblineTbl.col("dlv_qty").isNotNull(), 1).otherwise(0)).alias("referenceField8")
////                );
////
////
////        preBLineOutBHeader = headerLineVal.join(preoutboundTbl, headerLineVal.col("ref_doc_no").equalTo(preoutboundTbl.col("ref_doc_no")), "left")
////                .where(preoutboundTbl.col("is_deleted").equalTo(0))
////                .groupBy(
////                        preoutboundTbl.col("REF_DOC_NO")
////                )
////                .agg(
////                        sum(
////                                when(preoutboundTbl.col("ORD_QTY").isNotNull(), preoutboundTbl.col("ORD_QTY")).otherwise(0)).alias("referenceField9"),
////                        count(
////                                when(preoutboundTbl.col("ORD_QTY").isNotNull(),1).otherwise(0)).alias("referenceField10")
////                ).select(
////                        obheadTbl.col("c_id").alias("companyCode"),
////                        obheadTbl.col("lang_id").alias("languageId"),
////                        obheadTbl.col("partner_code").alias("partnerCode"),
////                        obheadTbl.col("plant_id").alias("plantId"),
////                        obheadTbl.col("pre_ob_no").alias("preOutboundNo"),
////                        obheadTbl.col("ref_doc_no").alias("refDocNumber"),
////                        obheadTbl.col("wh_id").alias("warehouseId"),
////                        obheadTbl.col("dlv_ctd_by").alias("createdBy"),
////                        obheadTbl.col("dlv_ctd_on").alias("createdOn"),
////                        obheadTbl.col("is_deleted").alias("deletionIndicator"),
////                        obheadTbl.col("dlv_cnf_by").alias("deliveryConfirmedBy"),
////                        obheadTbl.col("dlv_cnf_on").alias("deliveryConfirmedOn"),
////                        obheadTbl.col("dlv_ord_no").alias("deliveryOrderNo"),
////                        obheadTbl.col("ob_ord_typ_id").alias("outboundOrderTypeId"),
////                        obheadTbl.col("ref_doc_date").alias("refDocDate"),
////                        obheadTbl.col("ref_doc_typ").alias("referenceDocumentType"),
////                        obheadTbl.col("remark").alias("remarks"),
////                        obheadTbl.col("req_del_date").alias("requiredDeliveryDate"),
////                        obheadTbl.col("dlv_rev_by").alias("reversedBy"),
////                        obheadTbl.col("dlv_rev_on").alias("reversedOn"),
////                        obheadTbl.col("status_id").alias("statusId"),
////                        obheadTbl.col("dlv_utd_by").alias("updatedBy"),
////                        obheadTbl.col("dlv_utd_on").alias("updatedOn"),
////                        obheadTbl.col("INVOICE_NO").alias("invoiceNumber"),
////                        obheadTbl.col("C_TEXT").alias("companyDescription"),
////                        obheadTbl.col("PLANT_TEXT").alias("plantDescription"),
////                        obheadTbl.col("WH_TEXT").alias("warehouseDescription"),
////                        obheadTbl.col("STATUS_TEXT").alias("statusDescription"),
////                        obheadTbl.col("MIDDLEWARE_ID").alias("middlewareId"),
////                        obheadTbl.col("MIDDLEWARE_TABLE").alias("middlewareTable"),
////                        obheadTbl.col("INVOICE_DATE").alias("invoiceDate"),
////                        obheadTbl.col("MIDDLEWARE_ID").alias("middlewareId"),
////                        obheadTbl.col("SALES_ORDER_NUMBER").alias("salesOrderNumber"),
////                        obheadTbl.col("SALES_INVOICE_NUMBER").alias("salesInvoiceNumber"),
////                        obheadTbl.col("PICK_LIST_NUMBER").alias("pickListNumber"),
//////                        obheadTbl.col("DELIVERY_TYPE").alias("deliveryType"),
////                        obheadTbl.col("CUSTOMER_ID").alias("customerId"),
////                        obheadTbl.col("CUSTOMER_TYPE").alias("customerType"),
////                        obheadTbl.col("CUSTOMER_NAME").alias("customerName"),
////                        obheadTbl.col("TARGET_BRANCH_CODE").alias("targetBranchCode"),
////                        obheadTbl.col("ADDRESS").alias("address"),
////                        obheadTbl.col("PHONE_NUMBER").alias("phoneNumber"),
////                        obheadTbl.col("ALTERNATE_NO").alias("alternateNo"),
////                        obheadTbl.col("TOKEN_NUMBER").alias("tokenNumber"),
////                        obheadTbl.col("STATUS").alias("status"),
////                        obheadTbl.col("CUSTOMER_TYPE").alias("customerType"),
////                        obheadTbl.col("ref_field_1").alias("referenceField1"),
////                        obheadTbl.col("ref_field_2").alias("referenceField2"),
////                        obheadTbl.col("ref_field_3").alias("referenceField3"),
////                        obheadTbl.col("ref_field_4").alias("referenceField4"),
////                        obheadTbl.col("ref_field_5").alias("referenceField5"),
////                        obheadTbl.col("ref_field_6").alias("referenceField6"),
////                        col("referenceField7"),
////                        col("referenceField8"),
////                        col("referenceField9"),
////                        col("referenceField10")
////                );
//
//        headerLineVal = obheadTbl.join(oblineTbl, obheadTbl.col("ref_doc_no").equalTo(oblineTbl.col("ref_doc_no")), "left")
//                .groupBy(
//                        obheadTbl.col("c_id"),
//                        obheadTbl.col("lang_id"),
//                        obheadTbl.col("partner_code"),
//                        obheadTbl.col("plant_id"),
//                        obheadTbl.col("pre_ob_no"),
//                        obheadTbl.col("ref_doc_no"),
//                        obheadTbl.col("wh_id"),
//                        obheadTbl.col("dlv_ctd_by"),
//                        obheadTbl.col("dlv_ctd_on"),
//                        obheadTbl.col("is_deleted"),
//                        obheadTbl.col("dlv_cnf_by"),
//                        obheadTbl.col("dlv_cnf_on"),
//                        obheadTbl.col("dlv_ord_no"),
//                        obheadTbl.col("ob_ord_typ_id"),
//                        obheadTbl.col("ref_doc_date"),
//                        obheadTbl.col("ref_doc_typ"),
//                        obheadTbl.col("remark"),
//                        obheadTbl.col("req_del_date"),
//                        obheadTbl.col("dlv_rev_by"),
//                        obheadTbl.col("dlv_rev_on"),
//                        obheadTbl.col("status_id"),
//                        obheadTbl.col("dlv_utd_by"),
//                        obheadTbl.col("dlv_utd_on"),
//                        obheadTbl.col("ref_field_1"),
//                        obheadTbl.col("ref_field_2"),
//                        obheadTbl.col("ref_field_3"),
//                        obheadTbl.col("ref_field_4"),
//                        obheadTbl.col("ref_field_5"),
//                        obheadTbl.col("ref_field_6"),
//                        obheadTbl.col("INVOICE_NO"),
//                        obheadTbl.col("C_TEXT"),
//                        obheadTbl.col("PLANT_TEXT"),
//                        obheadTbl.col("WH_TEXT"),
//                        obheadTbl.col("STATUS_TEXT"),
//                        obheadTbl.col("MIDDLEWARE_ID"),
//                        obheadTbl.col("MIDDLEWARE_TABLE"),
//                        obheadTbl.col("REF_DOC_TYP"),
//                        obheadTbl.col("SALES_ORDER_NUMBER"),
//                        obheadTbl.col("SALES_INVOICE_NUMBER"),
//                        obheadTbl.col("PICK_LIST_NUMBER"),
//                        obheadTbl.col("INVOICE_DATE"),
//                        obheadTbl.col("DELIVERY_TYPE"),
//                        obheadTbl.col("CUSTOMER_ID"),
//                        obheadTbl.col("CUSTOMER_NAME"),
//                        obheadTbl.col("ADDRESS"),
//                        obheadTbl.col("PHONE_NUMBER"),
//                        obheadTbl.col("ALTERNATE_NO"),
//                        obheadTbl.col("STATUS"),
//                        obheadTbl.col("TOKEN_NUMBER"),
//                        obheadTbl.col("TARGET_BRANCH_CODE"),
//                        obheadTbl.col("CUSTOMER_TYPE"),
//                        oblineTbl.col("ref_doc_no").alias("line_ref_doc_no"),
//                        oblineTbl.col("c_id").alias("line_c_id"),
//                        oblineTbl.col("lang_id").alias("line_lang_id"),
//                        oblineTbl.col("plant_id").alias("line_plant_id"),
//                        oblineTbl.col("wh_id").alias("line_wh_id"),
//                        oblineTbl.col("pre_ob_no").alias("line_pre_ob_no"),
//                        oblineTbl.col("partner_code").alias("line_partner_code")
//                )
//                .agg(
//                        sum(
//                                when(oblineTbl.col("dlv_qty").isNotNull(), oblineTbl.col("dlv_qty")).otherwise(0)).alias("referenceField7"),
//                        count(
//                                when(oblineTbl.col("dlv_qty").isNotNull(), 1).otherwise(0)
//                        ).alias("referenceField8")
//                );
//
//        preBLineOutBHeader = headerLineVal.join(preoutboundTbl, headerLineVal.col("ref_doc_no").equalTo(preoutboundTbl.col("REF_DOC_NO")), "left")
//                .where(preoutboundTbl.col("is_deleted").equalTo(0))
//                .groupBy(preoutboundTbl.col("REF_DOC_NO"))
//                .agg(
//                        sum(
//                                when(preoutboundTbl.col("ORD_QTY").isNotNull(), preoutboundTbl.col("ORD_QTY")).otherwise(0)
//                        ).alias("referenceField9"),
//                        count(
//                                when(preoutboundTbl.col("ORD_QTY").isNotNull(), 1).otherwise(0)
//                        ).alias("referenceField10")
//                ).select(
//                        obheadTbl.col("c_id").alias("companyCode"),
//                        obheadTbl.col("lang_id").alias("languageId"),
//                        obheadTbl.col("partner_code").alias("partnerCode"),
//                        obheadTbl.col("plant_id").alias("plantId"),
//                        obheadTbl.col("pre_ob_no").alias("preOutboundNo"),
//                        obheadTbl.col("ref_doc_no").alias("refDocNumber"),
//                        obheadTbl.col("wh_id").alias("warehouseId"),
//                        obheadTbl.col("dlv_ctd_by").alias("createdBy"),
//                        obheadTbl.col("dlv_ctd_on").alias("createdOn"),
//                        obheadTbl.col("is_deleted").alias("deletionIndicator"),
//                        obheadTbl.col("dlv_cnf_by").alias("deliveryConfirmedBy"),
//                        obheadTbl.col("dlv_cnf_on").alias("deliveryConfirmedOn"),
//                        obheadTbl.col("dlv_ord_no").alias("deliveryOrderNo"),
//                        obheadTbl.col("ob_ord_typ_id").alias("outboundOrderTypeId"),
//                        obheadTbl.col("ref_doc_date").alias("refDocDate"),
//                        obheadTbl.col("ref_doc_typ").alias("referenceDocumentType"),
//                        obheadTbl.col("remark").alias("remarks"),
//                        obheadTbl.col("req_del_date").alias("requiredDeliveryDate"),
//                        obheadTbl.col("dlv_rev_by").alias("reversedBy"),
//                        obheadTbl.col("dlv_rev_on").alias("reversedOn"),
//                        obheadTbl.col("status_id").alias("statusId"),
//                        obheadTbl.col("dlv_utd_by").alias("updatedBy"),
//                        obheadTbl.col("dlv_utd_on").alias("updatedOn"),
//                        obheadTbl.col("INVOICE_NO").alias("invoiceNumber"),
//                        obheadTbl.col("C_TEXT").alias("companyDescription"),
//                        obheadTbl.col("PLANT_TEXT").alias("plantDescription"),
//                        obheadTbl.col("WH_TEXT").alias("warehouseDescription"),
//                        obheadTbl.col("STATUS_TEXT").alias("statusDescription"),
//                        obheadTbl.col("MIDDLEWARE_ID").alias("middlewareId"),
//                        obheadTbl.col("MIDDLEWARE_TABLE").alias("middlewareTable"),
//                        obheadTbl.col("INVOICE_DATE").alias("invoiceDate"),
//                        obheadTbl.col("MIDDLEWARE_ID").alias("middlewareId"),
//                        obheadTbl.col("SALES_ORDER_NUMBER").alias("salesOrderNumber"),
//                        obheadTbl.col("SALES_INVOICE_NUMBER").alias("salesInvoiceNumber"),
//                        obheadTbl.col("PICK_LIST_NUMBER").alias("pickListNumber"),
//                        obheadTbl.col("CUSTOMER_ID").alias("customerId"),
//                        obheadTbl.col("CUSTOMER_TYPE").alias("customerType"),
//                        obheadTbl.col("CUSTOMER_NAME").alias("customerName"),
//                        obheadTbl.col("TARGET_BRANCH_CODE").alias("targetBranchCode"),
//                        obheadTbl.col("ADDRESS").alias("address"),
//                        obheadTbl.col("PHONE_NUMBER").alias("phoneNumber"),
//                        obheadTbl.col("ALTERNATE_NO").alias("alternateNo"),
//                        obheadTbl.col("TOKEN_NUMBER").alias("tokenNumber"),
//                        obheadTbl.col("STATUS").alias("status"),
//                        obheadTbl.col("CUSTOMER_TYPE").alias("customerType"),
//                        obheadTbl.col("ref_field_1").alias("referenceField1"),
//                        obheadTbl.col("ref_field_2").alias("referenceField2"),
//                        obheadTbl.col("ref_field_3").alias("referenceField3"),
//                        obheadTbl.col("ref_field_4").alias("referenceField4"),
//                        obheadTbl.col("ref_field_5").alias("referenceField5"),
//                        obheadTbl.col("ref_field_6").alias("referenceField6"),
//                        col("referenceField7").alias("referenceField7"),
//                        col("referenceField8").alias("referenceField8"),
//                        col("referenceField9").alias("referenceField9"),
//                        col("referenceField10").alias("referenceField10")
//                );
//
//
//
//
//        filerVal = preBLineOutBHeader;
//
////        if (findOutBoundHeader.getCompanyCodeId() != null && !findOutBoundHeader.getCompanyCodeId().isEmpty()) {
////            filerVal = filerVal.filter(col("companyCode").isin(findOutBoundHeader.getCompanyCodeId().toArray(new String[0]))).toDF();
////        }
////        if (findOutBoundHeader.getPlantId() != null && !findOutBoundHeader.getPlantId().isEmpty()) {
////            filerVal = filerVal.filter(col("plantId").isin(findOutBoundHeader.getPlantId().toArray(new String[0]))).toDF();
////        }
////        if (findOutBoundHeader.getLanguageId() != null && !findOutBoundHeader.getLanguageId().isEmpty()) {
////            filerVal = filerVal.filter(col("languageId").isin(findOutBoundHeader.getLanguageId().toArray(new String[0]))).toDF();
////        }
//        if (findOutBoundHeader.getWarehouseId() != null && !findOutBoundHeader.getWarehouseId().isEmpty()) {
//            filerVal = filerVal.filter(col("warehouseId").isin(findOutBoundHeader.getWarehouseId().toArray(new String[0]))).toDF();
//        }
////        if (findOutBoundHeader.getRefDocNo() != null && !findOutBoundHeader.getRefDocNo().isEmpty()) {
////            filerVal = filerVal.filter(col("refDocNumber").isin(findOutBoundHeader.getRefDocNo().toArray(new String[0]))).toDF();
////        }
//        if (findOutBoundHeader.getSoType() != null && !findOutBoundHeader.getSoType().isEmpty()) {
//            filerVal = filerVal.filter(col("referenceField1").isin(findOutBoundHeader.getSoType().toArray(new String[0]))).toDF();
//        }
//        if (findOutBoundHeader.getPartnerCode() != null && !findOutBoundHeader.getPartnerCode().isEmpty()) {
//            List<String> partnerCodeStrings = findOutBoundHeader.getPartnerCode().stream().map(String::valueOf).collect(Collectors.toList());
//            filerVal = filerVal.filter(col("partnerCode").isin(partnerCodeStrings.toArray()));
//        }
//        if (findOutBoundHeader.getOutboundOrderTypeId() != null && !findOutBoundHeader.getOutboundOrderTypeId().isEmpty()) {
//            List<String> outboundOrderTypeIdStrings = findOutBoundHeader.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
//            filerVal = filerVal.filter(col("outboundOrderTypeId").isin(outboundOrderTypeIdStrings.toArray()));
//        }
//        if (findOutBoundHeader.getStatusId() != null && !findOutBoundHeader.getStatusId().isEmpty()) {
//            List<String> statusIdStrings = findOutBoundHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            filerVal = filerVal.filter(col("statusId").isin(statusIdStrings.toArray()));
//        }
//        if (findOutBoundHeader.getStartRequiredDeliveryDate() != null) {
//            Date startDate = findOutBoundHeader.getStartRequiredDeliveryDate();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            filerVal = filerVal.filter(col("requiredDeliveryDate").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findOutBoundHeader.getEndRequiredDeliveryDate() != null) {
//            Date endDate = findOutBoundHeader.getEndRequiredDeliveryDate();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            filerVal = filerVal.filter(col("requiredDeliveryDate").$less$eq(dateFormat.format(endDate)));
//        }
//        if (findOutBoundHeader.getStartDeliveryConfirmedOn() != null) {
//            Date startDate = findOutBoundHeader.getStartDeliveryConfirmedOn();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            filerVal = filerVal.filter(col("deliveryConfirmedOn").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findOutBoundHeader.getEndDeliveryConfirmedOn() != null) {
//            Date endDate = findOutBoundHeader.getEndDeliveryConfirmedOn();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            filerVal = filerVal.filter(col("deliveryConfirmedOn").$less$eq(dateFormat.format(endDate)));
//        }
//        if (findOutBoundHeader.getStartOrderDate() != null) {
//            Date startDate = findOutBoundHeader.getStartOrderDate();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            filerVal = filerVal.filter(col("createdOn").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findOutBoundHeader.getEndOrderDate() != null) {
//            Date endDate = findOutBoundHeader.getEndOrderDate();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            filerVal = filerVal.filter(col("createdOn").$less$eq(dateFormat.format(endDate)));
//        }
//        filerVal.show();
//        //Encode
//        Encoder<OutBoundHeader> outBoundHeaderEncoder = Encoders.bean(OutBoundHeader.class);
//        Dataset<OutBoundHeader> outBoundHeaderDataset = filerVal.as(outBoundHeaderEncoder);
//        return outBoundHeaderDataset.collectAsList();
//    }
//}