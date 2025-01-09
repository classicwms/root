//package com.mnrclara.spark.core.service.b2b;
//
//
//import com.mnrclara.spark.core.model.b2b.FindJntwebhook;
//import com.mnrclara.spark.core.model.b2b.Jntwebhook;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//
//import static org.apache.spark.sql.functions.col;
//import static org.apache.spark.sql.functions.to_timestamp;
//
//@Slf4j
//@Service
//public class JntwebhookService {
//
//    private static SparkSession sparkSession;
//    private static Properties connProp;
//    private static Dataset<Row> joined1 = null;
//    private static Dataset<Row> joined2 = null;
//
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    static {
//        sparkSession = SparkSession.builder().master("local[*]")
//                .appName("SparkByExample.com")
//                .getOrCreate();
//
//        // Connection Properties
//
//        connProp = new Properties();
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "z4U5P@u$LWJiVkuuOR");
//
//        // Read from SQL Server Table
//        Dataset<Row> df2 = readJdbcTable("tblconsignment3");
//        Dataset<Row> df3 = readJdbcTable("tbljntwebhook");
//
//        df2.createOrReplaceTempView("tblconsignment");
//        df3.createOrReplaceTempView("tbljntwebhook");
//
//        Dataset<Row> dfConsignmentAlias = df2
//                .withColumnRenamed("description", "con_description");
//
//
//
//        Dataset<Row> dfjntwebhookAlias = df3
//                .withColumnRenamed("description", "description");
//
//
//
//        joined1 = dfConsignmentAlias.join(
//                dfjntwebhookAlias,
//                dfConsignmentAlias.col("AWB_3RD_PARTY").equalTo(dfjntwebhookAlias.col("BILL_CODE")),"left");
////                joined1 = df2.join(df3, df2.col("AWB_3RD_PARTY").equalTo(df3.col("BILL_CODE")),"left");
//
//    }
//
//
//    private static Dataset<Row> readJdbcTable(String tableName) {
//        return sparkSession.read().jdbc("jdbc:sqlserver://13.234.179.29;databaseName=IWE", tableName, connProp);
//    }
//
//
//    public  static List<Jntwebhook> findB2BJntwebhook(FindJntwebhook findJntwebhook) {
//        log.info("-----> : {}", findJntwebhook);
//
//
//
//        joined2 = joined1;
//        joined2.show();
//
//        if (findJntwebhook.getReference_number() != null && !findJntwebhook.getReference_number().isEmpty()) {
//            joined2 = joined2.filter(col("reference_number").isin(findJntwebhook.getReference_number().toArray(new String[0]))).toDF();
//            System.out.println(findJntwebhook.getReference_number());
//        }
//        if (findJntwebhook.getCustomer_code() != null && !findJntwebhook.getCustomer_code().isEmpty()) {
//            joined2 = joined2.filter(col("CUSTOMER_CODE").isin(findJntwebhook.getCustomer_code().toArray(new String[0]))).toDF();
//        }
//        if (findJntwebhook.getCustomer_reference_number() != null && !findJntwebhook.getCustomer_reference_number().isEmpty()) {
//            joined2 = joined2.filter(col("CUSTOMER_REFERENCE_NUMBER").isin(findJntwebhook.getCustomer_reference_number().toArray(new String[0]))).toDF();
//        }
//        if (findJntwebhook.getAwb_3rd_Party() != null && !findJntwebhook.getAwb_3rd_Party().isEmpty()) {
//            joined2 = joined2.filter(col("AWB_3RD_PARTY").isin(findJntwebhook.getAwb_3rd_Party().toArray(new String[0]))).toDF();
//        }
//        if (findJntwebhook.getHubCode_3rd_Party() != null && !findJntwebhook.getHubCode_3rd_Party().isEmpty()) {
//            joined2 = joined2.filter(col("HUBCODE_3RD_PARTY").isin(findJntwebhook.getHubCode_3rd_Party().toArray(new String[0]))).toDF();
//        }
//        if (findJntwebhook.getOrderType() != null && !findJntwebhook.getOrderType().isEmpty()) {
//            joined2 = joined2.filter(col("ORDER_TYPE").isin(findJntwebhook.getOrderType().toArray(new String[0]))).toDF();
//        }
//        if (findJntwebhook.getScanType() != null && !findJntwebhook.getScanType().isEmpty()) {
//            joined2 = joined2.filter(col("SCAN_TYPE").isin(findJntwebhook.getScanType().toArray(new String[0]))).toDF();
//        }
//
////        if (findJntwebhook.getStartDate() != null) {
////            Date startDate = findJntwebhook.getStartDate();
////            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
////            joined2 = joined2.filter(col("created_at").$greater$eq(dateFormat.format(startDate)));
////        }
////        if (findJntwebhook.getStartDate() != null) {
////            Date endDate = findJntwebhook.getEndDate();
////            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
////            joined2 = joined2.filter(col("created_at").$less$eq(dateFormat.format(endDate)));
////        }
//
//        if (findJntwebhook.getStartDate() != null) {
//            Date startDate = findJntwebhook.getStartDate();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            String startDateStr = dateFormat.format(startDate);
//            joined2 = joined2.filter(to_timestamp(col("created_at"), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX").$greater$eq(startDateStr));
//        }
//
//        if (findJntwebhook.getEndDate() != null) {
//            Date endDate = findJntwebhook.getEndDate();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            String endDateStr = dateFormat.format(endDate);
//            joined2 = joined2.filter(to_timestamp(col("created_at"), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX").$less$eq(endDateStr));
//        }
//
//
//        Encoder<Jntwebhook> encJntwebhook = Encoders.bean(Jntwebhook.class);
//        Dataset<Jntwebhook> dsJntwebhook = joined2.as(encJntwebhook);
//        return dsJntwebhook.collectAsList();
//
//    }
//
//}