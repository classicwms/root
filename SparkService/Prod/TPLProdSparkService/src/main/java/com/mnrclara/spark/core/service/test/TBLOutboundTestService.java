//package com.mnrclara.spark.core.service.test;
//
//import com.mnrclara.spark.core.model.FindOutBoundHeader;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.List;
//import java.util.Properties;
//
//import static org.apache.spark.sql.functions.*;
//
//@Service
//@Slf4j
//public class TBLOutboundTestService {
//
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private Properties connProp;
//    private SparkSession sparkSession;
//    private Dataset<Row> obheadTbl;
//    private Dataset<Row> oblineTbl;
//
//    private Dataset<Row> preoutboundTbl;
//    private Dataset<Row> outboundResult;
//
//    public TBLOutboundTestService() throws ParseException {
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
//        preoutboundTbl = readJdbcTable("tblpreoutboundline");
//        preoutboundTbl.createOrReplaceTempView("tblpreoutboundline");
//
//    }
//
//    private Dataset<Row> readJdbcTable(String tableName) {
//        return sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://3.109.20.248;databaseName=WMS_3PL", tableName, connProp).repartition(16);
//    }
//
//
//    public List<Outbound> findOutBoundHeader(FindOutBoundHeader findOutBoundHeader) throws Exception {
//        Dataset<Row> headerLineVal = obheadTbl.join(oblineTbl, obheadTbl.col("ref_doc_no").equalTo(oblineTbl.col("ref_doc_no")), "left")
//                .groupBy(
//                        obheadTbl.col("c_id").as("header_c_id"),
//                        obheadTbl.col("lang_id").as("header_lang_id"),
//                        obheadTbl.col("partner_code").as("header_partner_code"),
//                        obheadTbl.col("plant_id").as("header_plant_id"),
//                        obheadTbl.col("pre_ob_no").as("header_pre_ob_no"),
//                        obheadTbl.col("ref_doc_no"),
//                        obheadTbl.col("wh_id").as("header_wh_id"),
//                        obheadTbl.col("dlv_ctd_by").as("header_dlv_ctd_by"),
//                        obheadTbl.col("dlv_ctd_on").as("header_dlv_ctd_on"),
//                        obheadTbl.col("dlv_cnf_by").as("header_dlv_cnf_by"),
//                        obheadTbl.col("dlv_cnf_on").as("header_dlv_cnf_on"),
//                        obheadTbl.col("dlv_ord_no").as("header_dlv_ord_no"),
//                        obheadTbl.col("ob_ord_typ_id").as("header_ob_ord_typ_id"),
//                        obheadTbl.col("ref_doc_date").as("header_ref_doc_date"),
//                        obheadTbl.col("ref_doc_typ").as("header_ref_doc_type"),
//                        obheadTbl.col("remark").as("header_remark"),
//                        obheadTbl.col("req_del_date").as("header_req_del_date"),
//                        obheadTbl.col("dlv_rev_by").as("header_dlv_rev_by"),
//                        obheadTbl.col("dlv_rev_on").as("header_dlv_rev_on"),
//                        obheadTbl.col("status_id").as("header_status_id"),
//                        obheadTbl.col("dlv_utd_by").as("header_dlv_utd_by"),
//                        obheadTbl.col("dlv_utd_on").as("header_dlv_utd_on"),
//                        obheadTbl.col("ref_field_1").as("header_ref_field_1"),
//                        obheadTbl.col("ref_field_2").as("header_ref_field_2"),
//                        obheadTbl.col("ref_field_3").as("header_ref_field_3"),
//                        obheadTbl.col("ref_field_4").as("header_ref_field_4"),
//                        obheadTbl.col("ref_field_5").as("header_ref_field_5"),
//                        obheadTbl.col("ref_field_6").as("header_ref_field_6"),
//                        obheadTbl.col("invoice_no").as("header_invoice_no"),
//                        obheadTbl.col("c_text").as("header_c_text"),
//                        obheadTbl.col("plant_text").as("header_plant_text"),
//                        obheadTbl.col("wh_text").as("header_wh_text"),
//                        obheadTbl.col("status_text").as("header_status_text"),
//                        obheadTbl.col("sales_order_number").as("header_sales_order_number"),
//                        obheadTbl.col("sales_invoice_number").as("header_sales_invoice_number"),
//                        obheadTbl.col("pick_list_number").as("header_pick_list_number"),
//                        obheadTbl.col("invoice_date").as("header_invoice_date"),
//                        obheadTbl.col("delivery_type").as("header_delivery_type"),
//                        obheadTbl.col("customer_id").as("header_customer_id"),
//                        obheadTbl.col("customer_name").as("header_customer_name"),
//                        obheadTbl.col("address").as("header_address"),
//                        obheadTbl.col("phone_number").as("header_phone_number"),
//                        obheadTbl.col("alternate_no").as("header_alternate_no"),
//                        obheadTbl.col("status").as("header_status"),
//                        obheadTbl.col("token_number").as("header_token_number"),
//                        obheadTbl.col("target_branch_code").as("header_target_branch_code"),
//                        obheadTbl.col("customer_type").as("header_customer_type"),
//                        //OutboundLine
//                        oblineTbl.col("c_id").as("line_c_id"),
//                        oblineTbl.col("lang_id").as("line_lang_id"),
//                        oblineTbl.col("plant_id").as("line_plant_id"),
//                        oblineTbl.col("wh_id").as("line_wh_id"),
//                        oblineTbl.col("pre_ob_no").as("line_pre_ob_no"),
//                        oblineTbl.col("partner_code").as("line_partner_code")
//                )
//                .agg(
//                        sum(
//                                when(oblineTbl.col("dlv_qty").isNotNull(), oblineTbl.col("dlv_qty")).otherwise(0)
//                        ).alias("header_referenceField7"),
//                        count(
//                                when(oblineTbl.col("dlv_qty").isNotNull(), 1).otherwise(0)
//                        ).alias("header_referenceField8")
//                );
//
//        Dataset<Row> preBLineOutBHeader = headerLineVal.join(preoutboundTbl, headerLineVal.col("ref_doc_no").equalTo(preoutboundTbl.col("ref_doc_no")), "left")
//                .where(preoutboundTbl.col("is_deleted").equalTo(0))
//                .groupBy(preoutboundTbl.col("ref_doc_no"))
//                .agg(
//                        sum(
//                                when(preoutboundTbl.col("ORD_QTY").isNotNull(), preoutboundTbl.col("ORD_QTY")).otherwise(0)
//                        ).alias("referenceField9"),
//                        count(
//                                when(preoutboundTbl.col("ORD_QTY").isNotNull(), 1).otherwise(0)
//                        ).alias("referenceField10")
//                );
//
//        // Joining with headerLineVal and selecting required columns
//        Dataset<Row> finalResult = preBLineOutBHeader
//                .join(headerLineVal, preBLineOutBHeader.col("ref_doc_no").equalTo(headerLineVal.col("ref_doc_no")), "left")
//                .select(
//                        headerLineVal.col("ref_doc_no").as("refDocNumber"),
//                        headerLineVal.col("header_c_id").as("companyCodeId"),
//                        headerLineVal.col("header_lang_id").as("languageId"),
//                        headerLineVal.col("header_plant_id").as("plantId"),
//                        headerLineVal.col("header_wh_id").as("warehouseId"),
//                        headerLineVal.col("header_pre_ob_no").as("preOutboundNo"),
//                        headerLineVal.col("header_partner_code").as("partnerCode"),
//                        headerLineVal.col("header_dlv_ctd_by").as("createdBy"),
//                        headerLineVal.col("header_dlv_ctd_on").as("createdOn"),
//                        headerLineVal.col("header_dlv_cnf_by").as("deliveryConfirmedBy"),
//                        headerLineVal.col("header_dlv_cnf_on").as("deliveryConfirmedOn"),
//                        headerLineVal.col("header_dlv_ord_no").as("deliveryOrderNo"),
//                        headerLineVal.col("header_ob_ord_typ_id").as("outboundOrderTypeId"),
//                        headerLineVal.col("header_ref_doc_date").as("refDocDate"),
//                        headerLineVal.col("header_remark").as("remarks"),
//                        headerLineVal.col("header_ref_doc_type").as("referenceDocumentType"),
//                        headerLineVal.col("header_req_del_date").as("requiredDeliveryDate"),
//                        headerLineVal.col("header_dlv_rev_by").as("reversedBy"),
//                        headerLineVal.col("header_dlv_rev_on").as("reversedOn"),
//                        headerLineVal.col("header_dlv_utd_on").as("updatedOn"),
//                        headerLineVal.col("header_dlv_utd_by").as("updatedBy"),
//                        headerLineVal.col("header_status_id").as("statusId"),
//                        headerLineVal.col("header_invoice_no").as("invoiceNumber"),
//                        headerLineVal.col("header_c_text").as("companyDescription"),
//                        headerLineVal.col("header_plant_text").as("plantDescription"),
//                        headerLineVal.col("header_wh_text").as("warehouseDescription"),
//                        headerLineVal.col("header_status_text").as("statusDescription"),
//                        headerLineVal.col("header_sales_order_number").as("salesOrderNumber"),
//                        headerLineVal.col("header_sales_invoice_number").as("salesInvoiceNumber"),
//                        headerLineVal.col("header_pick_list_number").as("pickListNumber"),
//                        headerLineVal.col("header_invoice_date").as("invoiceDate"),
//                        headerLineVal.col("header_delivery_type").as("deliveryType"),
//                        headerLineVal.col("header_customer_id").as("customerId"),
//                        headerLineVal.col("header_customer_name").as("customerName"),
//                        headerLineVal.col("header_target_branch_code").as("targetBranchCode"),
//                        headerLineVal.col("header_address").as("address"),
//                        headerLineVal.col("header_phone_number").as("phoneNumber"),
//                        headerLineVal.col("header_alternate_no").as("alternateNo"),
//                        headerLineVal.col("header_token_number").as("tokenNumber"),
//                        headerLineVal.col("header_status").as("status"),
//                        headerLineVal.col("header_customer_type").as("customerType"),
//                        headerLineVal.col("header_ref_field_1").as("referenceField1"),
//                        headerLineVal.col("header_ref_field_2").as("referenceField2"),
//                        headerLineVal.col("header_ref_field_3").as("referenceField3"),
//                        headerLineVal.col("header_ref_field_4").as("referenceField4"),
//                        headerLineVal.col("header_ref_field_5").as("referenceField5"),
//                        headerLineVal.col("header_ref_field_6").as("referenceField6"),
//                        col("header_referenceField7").as("referenceField7"),
//                        col("header_referenceField8").as("referenceField8"),
//                        col("referenceField9"),
//                        col("referenceField10")
//                );
//
//        outboundResult = finalResult;
//        if (findOutBoundHeader.getRefDocNo() != null && !findOutBoundHeader.getRefDocNo().isEmpty()) {
//            outboundResult = outboundResult.filter(col("refDocNumber").isin(findOutBoundHeader.getRefDocNo().toArray(new String[0]))).toDF();
//        }
//
//        // Mapping DataFrame rows to Outbound model class
////        List<Outbound> outBoundList = finalResult
////                .map(row -> new Outbound(
////                        row.getString(0),
////                        row.getString(1),
////                        row.getString(2),
////                        row.getString(3),
////                        row.getString(4),
////                        row.getString(5),
////                        row.getString(6),
////                        row.getDouble(7),
////                        row.getLong(8),
////                        row.getDouble(9),
////                        row.getLong(10)
////                ), Encoders.bean(Outbound.class))
////                .collectAsList();
//
//        Encoder<Outbound> outBoundHeaderEncoder = Encoders.bean(Outbound.class);
//        Dataset<Outbound> outBoundHeaderDataset = outboundResult.as(outBoundHeaderEncoder);
//        outBoundHeaderDataset.show();
//        return outBoundHeaderDataset.collectAsList();
//    }
//
//}
//
//
////package com.mnrclara.spark.core.service.almailem.test;
////
////import com.mnrclara.spark.core.model.FindOutBoundHeader;
////import lombok.extern.slf4j.Slf4j;
////import org.apache.spark.sql.*;
////import org.springframework.stereotype.Service;
////
////import java.text.ParseException;
////import java.text.SimpleDateFormat;
////import java.util.List;
////import java.util.Properties;
////
////import static org.apache.spark.sql.functions.*;
////
////@Service
////@Slf4j
////public class OutboundTestService {
////
////    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////    private Properties connProp;
////    private SparkSession sparkSession;
////    private Dataset<Row> obheadTbl;
////    private Dataset<Row> oblineTbl;
////
////    private Dataset<Row> preoutboundTbl;
////
////    public OutboundTestService() throws ParseException {
////
////        connProp = new Properties();
////        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
////        connProp.put("user", "sa");
////        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
////        sparkSession = SparkSession.builder().master("local[*]").appName("OutBoundHeader.com").config("spark.executor.memory", "4g")
////                .config("spark.executor.cores", "4").getOrCreate();
////
////        obheadTbl = readJdbcTable("tbloutboundheader");
////        obheadTbl.createOrReplaceTempView("tbloutboundheader");
////
////        oblineTbl = readJdbcTable("tbloutboundline");
////        oblineTbl.createOrReplaceTempView("tbloutboundline");
////
////        preoutboundTbl = readJdbcTable("tblpreoutboundline");
////        preoutboundTbl.createOrReplaceTempView("tblpreoutboundline");
////
////    }
////
////    private Dataset<Row> readJdbcTable(String tableName) {
////        return sparkSession.read().jdbc("jdbc:sqlserver://3.109.20.248;databaseName=WMS_3PL", tableName, connProp);
////    }
////
////
////    public List<Outbound> findOutBoundHeader(FindOutBoundHeader findOutBoundHeader) throws Exception {
////        Dataset<Row> headerLineVal = obheadTbl.join(oblineTbl, obheadTbl.col("ref_doc_no").equalTo(oblineTbl.col("ref_doc_no")), "left")
////                .groupBy(
////                        obheadTbl.col("ref_doc_no"),
////                        obheadTbl.col("ref_doc_typ"),
////                        obheadTbl.col("status_id"),
////                        obheadTbl.col("invoice_no"),
////                        obheadTbl.col("c_text"),
////                        obheadTbl.col("plant_text"),
////                        obheadTbl.col("wh_text"),
////                        obheadTbl.col("status_text"),
////                        obheadTbl.col("sales_order_number"),
////                        obheadTbl.col("sales_invoice_number"),
////                        obheadTbl.col("pick_list_number"),
////                        obheadTbl.col("invoice_date"),
////                        oblineTbl.col("c_id").as("header_c_id"),
////                        oblineTbl.col("lang_id").as("header_lang_id"),
////                        oblineTbl.col("plant_id").as("header_plant_id"),
////                        oblineTbl.col("wh_id").as("header_wh_id"),
////                        oblineTbl.col("pre_ob_no").as("header_pre_ob_no"),
////                        oblineTbl.col("partner_code").as("header_partner_code")
////                )
////                .agg(
////                        sum(
////                                when(oblineTbl.col("dlv_qty").isNotNull(), oblineTbl.col("dlv_qty")).otherwise(0)
////                        ).alias("header_referenceField7"),
////                        count(
////                                when(oblineTbl.col("dlv_qty").isNotNull(), 1).otherwise(0)
////                        ).alias("header_referenceField8")
////                );
////
////        Dataset<Row> preBLineOutBHeader = headerLineVal.join(preoutboundTbl, headerLineVal.col("ref_doc_no").equalTo(preoutboundTbl.col("REF_DOC_NO")), "left")
////                .where(preoutboundTbl.col("is_deleted").equalTo(0))
////                .groupBy(headerLineVal.col("ref_doc_no"))
////                .agg(
////                        sum(
////                                when(preoutboundTbl.col("ORD_QTY").isNotNull(), preoutboundTbl.col("ORD_QTY")).otherwise(0)
////                        ).alias("referenceField9"),
////                        count(
////                                when(preoutboundTbl.col("ORD_QTY").isNotNull(), 1).otherwise(0)
////                        ).alias("referenceField10")
////                );
////
////        // Joining with headerLineVal and selecting required columns
////        Dataset<Row> finalResult = preBLineOutBHeader
////                .join(headerLineVal, preBLineOutBHeader.col("ref_doc_no").equalTo(headerLineVal.col("ref_doc_no")), "left")
////                .selectExpr(
////                        "headerLineVal.ref_doc_no AS refDocNumber",
////                        "headerLineVal.header_c_id AS companyCodeId",
////                        "headerLineVal.header_lang_id AS languageId",
////                        "headerLineVal.header_plant_id AS plantId",
////                        "headerLineVal.header_wh_id AS warehouseId",
////                        "headerLineVal.header_pre_ob_no AS preOutboundNo",
////                        "headerLineVal.header_partner_code AS partnerCode",
////                        "headerLineVal.ref_doc_typ AS referenceDocumentType",
////                        "headerLineVal.status_id AS statusId",
////                        "headerLineVal.invoice_no AS invoiceNumber",
////                        "headerLineVal.c_text AS companyDescription",
////                        "headerLineVal.plant_text AS plantDescription",
////                        "headerLineVal.wh_text AS warehouseDescription",
////                        "headerLineVal.status_text AS statusDescription",
////                        "headerLineVal.sales_order_number AS salesOrderNumber",
////                        "headerLineVal.sales_invoice_number AS salesInvoiceNumber",
////                        "headerLineVal.pick_list_number AS pickListNumber",
////                        "headerLineVal.invoice_date AS invoiceDate",
////                        "preBLineOutBHeader.referenceField7 AS referenceField7",
////                        "preBLineOutBHeader.referenceField8 AS referenceField8",
////                        "preBLineOutBHeader.referenceField9 AS referenceField9",
////                        "preBLineOutBHeader.referenceField10 AS referenceField10"
////                );
////
////        Encoder<Outbound> outBoundHeaderEncoder = Encoders.bean(Outbound.class);
////        Dataset<Outbound> outBoundHeaderDataset = finalResult.as(outBoundHeaderEncoder);
////        return outBoundHeaderDataset.collectAsList();
////
////    }
////
////}
