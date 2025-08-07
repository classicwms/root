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
////public class TPLOutBoundHeaderJoinServices1 {
//
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private Properties connProp;
//    private SparkSession sparkSession;
//    private Dataset<Row> obheadTbl;
//    private Dataset<Row> oblineTbl;
//    private Dataset<Row> headerLineVal;
//    private Dataset<Row> preoutboundTbl;
//    private Dataset<Row> preBLineOutBHeader;
//    private Dataset<Row> filerVal;
//
//    public TPLOutBoundHeaderJoinServices1() throws ParseException {
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
//    }
//
//    private Dataset<Row> readJdbcTable(String tableName) {
//        return sparkSession.read().jdbc("jdbc:sqlserver://3.109.20.248;databaseName=WMS_3PL", tableName, connProp);
//    }
//
//    public List<TestOutboundHeader> findOutBoundHeader(FindOutBoundHeader findOutBoundHeader) throws Exception {
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
//                                when(oblineTbl.col("dlv_qty").isNotNull(), 1).otherwise(0)).alias("referenceField8")
//                );
//
//        preBLineOutBHeader = headerLineVal.join(preoutboundTbl, headerLineVal.col("ref_doc_no").equalTo(preoutboundTbl.col("REF_DOC_NO")), "left")
//                .where(preoutboundTbl.col("is_deleted").equalTo(0))
//                .groupBy(headerLineVal.col("ref_doc_no")) // Group by refDocNumber from headerLineVal
//                .agg(
//                        sum(when(preoutboundTbl.col("ORD_QTY").isNotNull(), preoutboundTbl.col("ORD_QTY")).otherwise(0)).alias("referenceField9"),
//                        count(when(preoutboundTbl.col("ORD_QTY").isNotNull(), 1)).alias("referenceField10")
//                ).select(
//                        headerLineVal.col("c_id").alias("companyCode"),
//                        headerLineVal.col("lang_id").alias("languageId"),
//                        headerLineVal.col("partner_code").alias("partnerCode"),
//                        headerLineVal.col("plant_id").alias("plantId"),
//                        headerLineVal.col("pre_ob_no").alias("preOutboundNo"),
//                        headerLineVal.col("ref_doc_no").alias("refDocNumber"),
//                        headerLineVal.col("wh_id").alias("warehouseId"),
//                        headerLineVal.col("dlv_ctd_by").alias("createdBy"),
//                        headerLineVal.col("dlv_ctd_on").alias("createdOn"),
//                        headerLineVal.col("is_deleted").alias("deletionIndicator"),
//                        headerLineVal.col("dlv_cnf_by").alias("deliveryConfirmedBy"),
//                        headerLineVal.col("dlv_cnf_on").alias("deliveryConfirmedOn"),
//                        headerLineVal.col("dlv_ord_no").alias("deliveryOrderNo"),
//                        headerLineVal.col("ob_ord_typ_id").alias("outboundOrderTypeId"),
//                        headerLineVal.col("ref_doc_date").alias("refDocDate"),
//                        headerLineVal.col("ref_doc_typ").alias("referenceDocumentType"),
//                        headerLineVal.col("remark").alias("remarks"),
//                        headerLineVal.col("req_del_date").alias("requiredDeliveryDate"),
//                        headerLineVal.col("dlv_rev_by").alias("reversedBy"),
//                        headerLineVal.col("dlv_rev_on").alias("reversedOn"),
//                        headerLineVal.col("status_id").alias("statusId"),
//                        headerLineVal.col("dlv_utd_by").alias("updatedBy"),
//                        headerLineVal.col("dlv_utd_on").alias("updatedOn"),
//                        headerLineVal.col("INVOICE_NO").alias("invoiceNumber"),
//                        headerLineVal.col("C_TEXT").alias("companyDescription"),
//                        headerLineVal.col("PLANT_TEXT").alias("plantDescription"),
//                        headerLineVal.col("WH_TEXT").alias("warehouseDescription"),
//                        headerLineVal.col("STATUS_TEXT").alias("statusDescription"),
//                        headerLineVal.col("MIDDLEWARE_ID").alias("middlewareId"),
//                        headerLineVal.col("MIDDLEWARE_TABLE").alias("middlewareTable"),
//                        headerLineVal.col("INVOICE_DATE").alias("invoiceDate"),
//                        headerLineVal.col("MIDDLEWARE_ID").alias("middlewareId"),
//                        headerLineVal.col("SALES_ORDER_NUMBER").alias("salesOrderNumber"),
//                        headerLineVal.col("SALES_INVOICE_NUMBER").alias("salesInvoiceNumber"),
//                        headerLineVal.col("PICK_LIST_NUMBER").alias("pickListNumber"),
//                        headerLineVal.col("CUSTOMER_ID").alias("customerId"),
//                        headerLineVal.col("CUSTOMER_TYPE").alias("customerType"),
//                        headerLineVal.col("CUSTOMER_NAME").alias("customerName"),
//                        headerLineVal.col("TARGET_BRANCH_CODE").alias("targetBranchCode"),
//                        headerLineVal.col("ADDRESS").alias("address"),
//                        headerLineVal.col("PHONE_NUMBER").alias("phoneNumber"),
//                        headerLineVal.col("ALTERNATE_NO").alias("alternateNo"),
//                        headerLineVal.col("TOKEN_NUMBER").alias("tokenNumber"),
//                        headerLineVal.col("STATUS").alias("status"),
//                        headerLineVal.col("CUSTOMER_TYPE").alias("customerType"),
//                        headerLineVal.col("ref_field_1").alias("referenceField1"),
//                        headerLineVal.col("ref_field_2").alias("referenceField2"),
//                        headerLineVal.col("ref_field_3").alias("referenceField3"),
//                        headerLineVal.col("ref_field_4").alias("referenceField4"),
//                        headerLineVal.col("ref_field_5").alias("referenceField5"),
//                        headerLineVal.col("ref_field_6").alias("referenceField6"),
//                        headerLineVal.col("referenceField7"),
//                        headerLineVal.col("referenceField8"),
//                        col("referenceField9").alias("referenceField9"),
//                        col("referenceField10").alias("referenceField10")
//                );
//
//
////            preBLineOutBHeader = headerLineVal.join(preoutboundTbl, headerLineVal.col("refDocNumber").equalTo(preoutboundTbl.col("REF_DOC_NO")), "left")
////                    .where(preoutboundTbl.col("is_deleted").equalTo(0))
////                    .groupBy(preoutboundTbl.col("REF_DOC_NO"))
////                    .agg(
////                            sum(when(preoutboundTbl.col("ORD_QTY").isNotNull(), preoutboundTbl.col("ORD_QTY")).otherwise(0)).alias("referenceField9"),
////                            count(when(preoutboundTbl.col("ORD_QTY").isNotNull(), 1)).alias("referenceField10")
////                    ).select(col("referenceField9"), col("referenceField10"));
////
////            preBLineOutBHeader.show();
//
//            filerVal = preBLineOutBHeader;
////
////            if (findOutBoundHeader.getCompanyCodeId() != null && !findOutBoundHeader.getCompanyCodeId().isEmpty()) {
////                filerVal = filerVal.filter(col("companyCode").isin(findOutBoundHeader.getCompanyCodeId().toArray(new String[0])));
////            }
//        // Repeat similar optimizations for other filter conditions
//
////            headerLineVal.show();
//        Encoder<TestOutboundHeader> outBoundHeaderEncoder = Encoders.bean(TestOutboundHeader.class);
//        Dataset<TestOutboundHeader> outBoundHeaderDataset = filerVal.as(outBoundHeaderEncoder);
//        return outBoundHeaderDataset.collectAsList();
//
//
//    }
//}