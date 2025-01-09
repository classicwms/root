//package com.mnrclara.spark.core.service.b2b;
//
//import com.mnrclara.spark.core.model.b2b.FindJntHubCode;
//import com.mnrclara.spark.core.model.b2b.JntHubCode;
//import com.mnrclara.spark.core.util.DateUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.time.Instant;
//import java.time.format.DateTimeFormatter;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//
//import static org.apache.spark.sql.functions.broadcast;
//import static org.apache.spark.sql.functions.col;
//
//@Slf4j
//@Service
//public class JntHubCodeService {
//
//    private static Properties CONN_PROP = new Properties();
//    private static SparkSession sparkSession;
//    private static Dataset<Row> conTbl;
//    private static Dataset<Row> webHkTbl;
//    private static Dataset<Row> conVal;
//    private static Dataset<Row> conFilVal;
//    private static Dataset<Row> joinVal;
//    private static final int REPARTITION_COUNT = 16;
//
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//
//
//    static {
//        // Connection Properties
//        CONN_PROP.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        CONN_PROP.put("user", "sa");
//        CONN_PROP.put("password", "z4U5P@u$LWJiVkuuOR");
//        sparkSession = SparkSession.builder().master("local[*]").appName("JntHubCode.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        conTbl = readJdbcTable("tblconsignment3");
//        webHkTbl = readJdbcTable("tbljntwebhook");
//        conTbl.createOrReplaceTempView("tblconsignment3");
//        webHkTbl.createOrReplaceTempView("tbljntwebhook");
//        // HubCode filter
//        conTbl = conTbl.filter(col("HUBCODE_3RD_PARTY").isin("JT")).toDF();
//    }
//
//    // Read Table
//    private static Dataset<Row> readJdbcTable(String tableName) {
//        return sparkSession.read()
//                .option("fetchSize", "10000")
//                .jdbc("jdbc:sqlserver://13.234.179.29;databaseName=IWE", tableName, CONN_PROP)
//                .repartition(REPARTITION_COUNT);
//    }
//
//    // FindJntWebhook
//    public static List<JntHubCode> findJntWebHook(FindJntHubCode findJntHubCode)throws Exception {
//
//        // Consignment table value assign
//        conVal = conTbl;
//
//        if (findJntHubCode.getReference_number() != null && !findJntHubCode.getReference_number().isEmpty()) {
//            conVal = conVal.filter(col("reference_number").isin(findJntHubCode.getReference_number().toArray(new String[0]))).toDF();
//        }
//
//        if (findJntHubCode.getCustomer_code() != null && !findJntHubCode.getCustomer_code().isEmpty()) {
//            conVal = conVal.filter(col("CUSTOMER_CODE").isin(findJntHubCode.getCustomer_code().toArray(new String[0]))).toDF();
//        }
//        if (findJntHubCode.getCustomer_reference_number() != null && !findJntHubCode.getCustomer_reference_number().isEmpty()) {
//            conVal = conVal.filter(col("CUSTOMER_REFERENCE_NUMBER").isin(findJntHubCode.getCustomer_reference_number().toArray(new String[0]))).toDF();
//        }
//        if (findJntHubCode.getAwb_3rd_Party() != null && !findJntHubCode.getAwb_3rd_Party().isEmpty()) {
//            conVal = conVal.filter(col("AWB_3RD_PARTY").isin(findJntHubCode.getAwb_3rd_Party().toArray(new String[0]))).toDF();
//        }
//        if (findJntHubCode.getOrderType() != null && !findJntHubCode.getOrderType().isEmpty()) {
//            conVal = conVal.filter(col("ORDER_TYPE").isin(findJntHubCode.getOrderType().toArray(new String[0]))).toDF();
//        }
//
////        if (findJntHubCode.getStartDate() != null) {
////            Date startDate = findJntHubCode.getStartDate();
////            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
////            conVal = conVal.filter(col("created_at").$greater$eq(dateFormat.format(startDate)));
////        }
////        if (findJntHubCode.getStartDate() != null) {
////            Date endDate = findJntHubCode.getEndDate();
////            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
////            conVal = conVal.filter(col("created_at").$less$eq(dateFormat.format(endDate)));
////        }
////
//        if (findJntHubCode.getStartDate() != null && findJntHubCode.getEndDate() != null) {
//            Date[] dates = DateUtils.addTimeToDatesForSearch(findJntHubCode.getStartDate(), findJntHubCode.getEndDate());
//            Date startDate = dates[0];
//            Date endDate = dates[1];
//
//            Timestamp startTimestamp = new Timestamp(startDate.getTime());
//            Timestamp endTimestamp = new Timestamp(endDate.getTime());
//
//            conVal = conVal.filter(col("CREATED_AT").$greater$eq(startTimestamp));
//            conVal = conVal.filter(col("CREATED_AT").$less$eq(endTimestamp));
//        }
//
//        // Consignment table filter values assign
//        conFilVal = conVal;
////        conFilVal.show();
//
//        // Join tblconsignment/tbljntwebhook table
//        joinVal = conFilVal.join(webHkTbl, conFilVal.col("AWB_3RD_PARTY").equalTo(webHkTbl.col("BILL_CODE")), "left");
//
//        // Filter Join Values
//        if (findJntHubCode.getScanType() != null && !findJntHubCode.getScanType().isEmpty()) {
//            joinVal = joinVal.filter(col("SCAN_TYPE").isin(findJntHubCode.getScanType().toArray(new String[0]))).toDF();
//        }
//
//        joinVal = joinVal.withColumn("printStatus", functions.when(col("is_awb_printed").equalTo(true), "Y")
//                .when(col("is_awb_printed").equalTo(false), "N")
//                .otherwise("N"));
//
//
//        //Encode
//        Encoder<JntHubCode> jntHubCodeEncoder = Encoders.bean(JntHubCode.class);
////        joinVal.show();
//        Dataset<JntHubCode> jntHubCodeDataset = joinVal.as(jntHubCodeEncoder);
//        return jntHubCodeDataset.collectAsList();
//    }
//
//}
