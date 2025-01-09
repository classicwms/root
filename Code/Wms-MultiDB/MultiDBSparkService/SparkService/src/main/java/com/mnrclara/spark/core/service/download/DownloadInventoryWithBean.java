package com.mnrclara.spark.core.service.download;


import com.mnrclara.spark.core.model.Almailem.InventoryV2;
import com.mnrclara.spark.core.model.Inventory;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


@Service
@Slf4j
public class DownloadInventoryWithBean {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    Dataset<Row> df2;

    public DownloadInventoryWithBean() {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        sparkSession = SparkSession.builder().master("local[*]")
                .appName("Inventory.com")
                .config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4")
                .getOrCreate();

        //Read from Sql Table
        df2 = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://3.109.20.248;databaseName=WMS_ALMDEV", "tblinventory", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblinventoryv5");
    }

    public void downloadInventoryInCSV() throws ParseException {

        // Map the DataFrame to a Dataset of Inventory
        Dataset<InventoryV2> inventoryDataset = df2.map((MapFunction<Row, InventoryV2>) row -> {
            InventoryV2 inventory = new InventoryV2();
            inventory.setInventoryId(row.getAs("INV_ID"));
            inventory.setLanguageId(row.getAs("LANG_ID"));
            inventory.setCompanyCodeId(row.getAs("C_ID"));
            return inventory;
        }, Encoders.bean(InventoryV2.class));

        // Get the current date in a specific format (e.g., "yyyyMMdd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(new Date());

        // Append the current date to the file name
        String fileName = "inventory_" + currentDate + ".csv";
        // Construct the output path with the current date and the desired file name
        String outputPath = "/home/ubuntu/project/root/Classic\\ WMS/Code/Project/inventory/" + fileName;

        // Save the Dataset as a CSV file
        inventoryDataset.write()
                .mode("overwrite")
                .option("header", "true")
                .csv(outputPath);

    }
}

