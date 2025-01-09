package com.mnrclara.spark.core.service;//package com.mnrclara.spark.core.service;
//
//import com.mnrclara.spark.core.model.FindInventoryMovement;
//import com.mnrclara.spark.core.model.InventoryMovement;
//import com.mnrclara.spark.core.model.InventoryMovementV2;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.function.FilterFunction;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Encoders;
//import org.apache.spark.sql.SparkSession;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//
//
//@Slf4j
//@Service
//public class InventoryMovementServiceV2 {
//
//        private SparkSession sparkSession;
//
//        public InventoryMovementServiceV2() {
//            SparkConf sparkConf = new SparkConf()
//                    .setAppName("SparkByExample")
//                    .set("spark.sql.shuffle.partitions", "16")
//                    .set("spark.memory.fraction", "0.6");
//
//            sparkSession = SparkSession.builder()
//                    .config(sparkConf)
//                    .master("local[*]")
//                    .getOrCreate();
//        }
//
//        private Properties getJdbcProperties() {
//            Properties connProp = new Properties();
//            connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            connProp.setProperty("user", "sa");
//            connProp.setProperty("password", "30NcyBuK");
//            return connProp;
//        }
//
//        public List<InventoryMovementV2> findInventoryMovement(FindInventoryMovement findInventoryMovement) throws ParseException {
//            String fromDate = "2022-01-01 00:00:00";
//            String toDate = "2023-09-19 23:59:59";
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date1 = dateFormat.parse(fromDate);
//            Date date2 = dateFormat.parse(toDate);
//
//            log.info("dateConv[0]-------> : " + date1);
//            log.info("dateConv[1]-------> : " + date2);
//
//            Dataset<InventoryMovementV2> queryDF = sparkSession.read()
//                    .jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tblinventorymovement", getJdbcProperties())
//                    .filter(row ->
//                            row.getTimestamp(Integer.parseInt("createdOn")).after(date1) && row.getTimestamp(Integer.parseInt("createdOn")).before(date2))
//                    .repartition(16)
//                    .as(Encoders.bean(InventoryMovementV2.class));
//
//            queryDF.cache();
//            return queryDF.collectAsList();
//        }
//    }
//
//
//
