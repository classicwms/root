//package com.mnrclara.spark.core.service.b2b;
//
//import com.mnrclara.spark.core.model.b2b.ConsignmentV2;
//import com.mnrclara.spark.core.model.b2b.FindConsignmentV2;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Properties;
//
//import static org.apache.spark.sql.functions.col;
//
//
//@Slf4j
//@Service
//public class ConsignmentServiceV2 {
//
//    private static SparkSession sparkSession;
//    private static Properties connProp;
//    private static Dataset<Row> joined1 = null;
//    private static Dataset<Row> joined2 = null;
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
//        Dataset<Row> df2 = readJdbcTable("tblqpwebhook");
//        Dataset<Row> df3 = readJdbcTable("tblconsignment3");
//
//        df2.createOrReplaceTempView("tblqpwebhook");
//        df3.createOrReplaceTempView("tblconsignment");
//
//        joined1 = df2.join(df3, df2.col("tracking_no").equalTo(df3.col("reference_number")), "left");
//
//    }
//
//
//    private static Dataset<Row> readJdbcTable(String tableName) {
//        return sparkSession.read().jdbc("jdbc:sqlserver://13.234.179.29;databaseName=IWE", tableName, connProp);
//    }
//
//
//    public static List<ConsignmentV2> findB2B(FindConsignmentV2 searchConsignment) {
//        log.info("-----> : {}", searchConsignment);
//
//
//        joined2 = joined1;
//
//        if (searchConsignment.getReference_number() != null && !searchConsignment.getReference_number().isEmpty()) {
//            joined2 = joined2.filter(col("REFERENCE_NUMBER").isin(searchConsignment.getReference_number().toArray(new String[0]))).toDF();
//        }
//        if (searchConsignment.getItem_action_name() != null && !searchConsignment.getItem_action_name().isEmpty()) {
//            joined2 = joined2.filter(col("item_action_name").isin(searchConsignment.getItem_action_name().toArray(new String[0]))).toDF();
//        }
//        if (searchConsignment.getCustomer_code() != null && !searchConsignment.getCustomer_code().isEmpty()) {
//            joined2 = joined2.filter(col("CUSTOMER_CODE").isin(searchConsignment.getCustomer_code().toArray(new String[0]))).toDF();
//        }
//        if (searchConsignment.getCustomer_reference_number() != null && !searchConsignment.getCustomer_reference_number().isEmpty()) {
//            joined2 = joined2.filter(col("CUSTOMER_REFERENCE_NUMBER").isin(searchConsignment.getCustomer_reference_number().toArray(new String[0]))).toDF();
//        }
//        if (searchConsignment.getAwb_3rd_Party() != null && !searchConsignment.getAwb_3rd_Party().isEmpty()) {
//            joined2 = joined2.filter(col("AWB_3RD_PARTY").isin(searchConsignment.getAwb_3rd_Party().toArray(new String[0]))).toDF();
//        }
//        if (searchConsignment.getHubCode_3rd_Party() != null && !searchConsignment.getHubCode_3rd_Party().isEmpty()) {
//            joined2 = joined2.filter(col("HUBCODE_3RD_PARTY").isin(searchConsignment.getHubCode_3rd_Party().toArray(new String[0]))).toDF();
//        }
//        if (searchConsignment.getOrderType() != null && !searchConsignment.getOrderType().isEmpty()) {
//            joined2 = joined2.filter(col("ORDER_TYPE").isin(searchConsignment.getOrderType().toArray(new String[0]))).toDF();
//        }
//        if (searchConsignment.getScanType() != null && !searchConsignment.getScanType().isEmpty()) {
//            joined2 = joined2.filter(col("SCAN_TYPE").isin(searchConsignment.getScanType().toArray(new String[0]))).toDF();
//        }
//
//
//
//
//
//
//        Encoder<ConsignmentV2> encConsignment = Encoders.bean(ConsignmentV2.class);
//        Dataset<ConsignmentV2> dsConsignment = joined2.as(encConsignment);
//        return dsConsignment.collectAsList();
//
//    }
//}