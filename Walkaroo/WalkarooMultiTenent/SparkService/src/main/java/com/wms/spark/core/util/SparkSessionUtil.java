package com.wms.spark.core.util;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

public class SparkSessionUtil {

    public static SparkSession createSparkSession() {
        // Spark Configuration
        SparkConf conf = new SparkConf()
                .setAppName("SparkSession")
                .setMaster("local")
                .set("spark.executor.memory", "4g")
                .set("spark.executor.cores", "4")
                .set("spark.driver.memory", "4g")
                .set("spark.sql.shuffle.partitions", "16");

        // SparkSession
        return SparkSession.builder()
                .appName("SparkSession")
                .config(conf)
                .getOrCreate();
    }
}
