//package com.mnrclara.spark.core.service.b2b;
//
//
//import com.mnrclara.spark.core.model.b2b.FindQPWebhook;
//import com.mnrclara.spark.core.model.b2b.QPWebhook;
//import com.mnrclara.spark.core.util.DateUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//
//import static org.apache.spark.sql.functions.col;
//
//@Slf4j
//@Service
//public class QatarPostService {
//    private static Properties CONN_PROP = new Properties();
//    private static SparkSession sparkSession;
//    private static Dataset<Row> conTbl;
//    private static Dataset<Row> webHkTbl;
//    private static Dataset<Row> conVal;
//    private static Dataset<Row> conFilVal;
//    private static Dataset<Row> joinVal;
//    private static final int REPARTITION_COUNT = 16;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//
//    static {
//        // Connection Properties
//        CONN_PROP.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        CONN_PROP.put("user", "sa");
//        CONN_PROP.put("password", "z4U5P@u$LWJiVkuuOR");
//        sparkSession = SparkSession.builder().master("local[*]").appName("QPWebhook.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        conTbl = readJdbcTable("tblconsignment3");
//        webHkTbl = readJdbcTable("tblqpwebhook");
//        conTbl.createOrReplaceTempView("tblconsignment3");
//        webHkTbl.createOrReplaceTempView("tblqpwebhook");
//
//        conTbl = conTbl.filter(col("HUBCODE_3RD_PARTY").isin("QATARPOST")).toDF();
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
//    public static List<QPWebhook> findQPWebhook(FindQPWebhook findQPWebhook)throws Exception {
//
//        // Consignment table value assign
//        conVal = conTbl;
////        conVal.show();
////        if (findQPWebhook.getReference_number() != null && !findQPWebhook.getReference_number().isEmpty()) {
////            conVal = conVal.filter(col("REFERENCE_NUMBER").isin(findQPWebhook.getReference_number().toArray(new String[0]))).toDF();
////        }
//
//        if (findQPWebhook.getCustomer_code() != null && !findQPWebhook.getCustomer_code().isEmpty()) {
//            conVal = conVal.filter(col("CUSTOMER_CODE").isin(findQPWebhook.getCustomer_code().toArray(new String[0]))).toDF();
//        }
//        if (findQPWebhook.getCustomer_reference_number() != null && !findQPWebhook.getCustomer_reference_number().isEmpty()) {
//            conVal = conVal.filter(col("CUSTOMER_REFERENCE_NUMBER").isin(findQPWebhook.getCustomer_reference_number().toArray(new String[0]))).toDF();
//        }
//        if (findQPWebhook.getAwb_3rd_Party() != null && !findQPWebhook.getAwb_3rd_Party().isEmpty()) {
//            conVal = conVal.filter(col("AWB_3RD_PARTY").isin(findQPWebhook.getAwb_3rd_Party().toArray(new String[0]))).toDF();
//        }
//        if (findQPWebhook.getHubCode_3rd_Party() != null && !findQPWebhook.getHubCode_3rd_Party().isEmpty()) {
//            conVal = conVal.filter(col("HUBCODE_3RD_PARTY").isin(findQPWebhook.getHubCode_3rd_Party().toArray(new String[0]))).toDF();
//        }
//        if (findQPWebhook.getOrderType() != null && !findQPWebhook.getOrderType().isEmpty()) {
//            conVal = conVal.filter(col("ORDER_TYPE").isin(findQPWebhook.getOrderType().toArray(new String[0]))).toDF();
//        }
//
//        if (findQPWebhook.getStartDate() != null && findQPWebhook.getEndDate() != null) {
//            Date[] dates = DateUtils.addTimeToDatesForSearch(findQPWebhook.getStartDate(), findQPWebhook.getEndDate());
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
////        if (findQPWebhook.getStartDate() != null) {
////            Date startDate = findQPWebhook.getStartDate();
////            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
////            conVal = conVal.filter(col("created_at").$greater$eq(dateFormat.format(startDate)));
////        }
////        if (findQPWebhook.getStartDate() != null) {
////            Date endDate = findQPWebhook.getEndDate();
////            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
////            conVal = conVal.filter(col("created_at").$less$eq(dateFormat.format(endDate)));
////        }
//
//
//        // Consignment table filter values assign
//        conFilVal = conVal;
////        conFilVal.show();
//
//        // Join tblconsignment/tblqpwebhook table
//        joinVal = conFilVal.join(webHkTbl, conFilVal.col("REFERENCE_NUMBER").equalTo(webHkTbl.col("TRACKING_NO")), "left");
//
//        // Filter Join Values
////        if (findQPWebhook.getScanType() != null && !findQPWebhook.getScanType().isEmpty()) {
////            joinVal = joinVal.filter(col("SCAN_TYPE").isin(findQPWebhook.getScanType().toArray(new String[0]))).toDF();
////        }
//
//
//
//
//        //Encode
//        Encoder<QPWebhook> qpWebhookEncoder = Encoders.bean(QPWebhook.class);
////        joinVal.show();
//        Dataset<QPWebhook> qpWebhookDataset = joinVal.as(qpWebhookEncoder);
//        return qpWebhookDataset.collectAsList();
//    }
//}
