package com.mnrclara.spark.core.service.jaincord;

import com.mnrclara.spark.core.model.wmscorev2.FindStockReport;
import com.mnrclara.spark.core.model.wmscorev2.StockReport;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Properties;

import static org.apache.spark.sql.functions.col;

@Slf4j
@Service
public class SparkStockReportServiceV2JainCord {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;

    public SparkStockReportServiceV2JainCord() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
        sparkSession = SparkSession.builder().master("local[*]").appName("InhouseTransferHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblinventory", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblinventoryv2jain");
    }

    public List<StockReport> getStockReport(FindStockReport findStockReport) throws ParseException {

        Dataset<Row> stockReportQuery = sparkSession.sql("SELECT "
                        + "LANG_ID as languageId, "
                        + "C_ID as companyCodeId, "
                        + "PLANT_ID as plantId, "
                        + "WH_ID as warehouseId, "
                        + "ITM_CODE as itemCode, "
                        + "MFR_CODE as manufacturerSKU, "
//                + " as itemText, "
                        + "INV_QTY as onHandQty, "
//                + " as damageQty, "           //------> no column names in tblinventory
//                + " as holdQty, "
//                + " as availableQty, "
                        + "C_TEXT as companyDescription, "
                        + "PLANT_TEXT as plantDescription, "
                        + "WH_TEXT as warehouseDescription, "
                        + "BARCODE_ID as barcodeId, "
                        + "MFR_NAME as manufacturerName "
                        + "FROM tblinventoryv2jain WHERE IS_DELETED = 0 "
        );
//        stockReportQuery.cache();

        if (findStockReport.getLanguageId() != null && !findStockReport.getLanguageId().isEmpty()) {
            stockReportQuery = stockReportQuery.filter(col("LANG_ID").isin(findStockReport.getLanguageId().toArray()));
        }
        if (findStockReport.getCompanyCodeId() != null && !findStockReport.getCompanyCodeId().isEmpty()) {
            stockReportQuery = stockReportQuery.filter(col("C_ID").isin(findStockReport.getCompanyCodeId().toArray()));
        }
        if (findStockReport.getPlantId() != null && !findStockReport.getPlantId().isEmpty()) {
            stockReportQuery = stockReportQuery.filter(col("PLANT_ID").isin(findStockReport.getPlantId().toArray()));
        }
        if (findStockReport.getWarehouseId() != null && !findStockReport.getWarehouseId().isEmpty()) {
            stockReportQuery = stockReportQuery.filter(col("WH_ID").isin(findStockReport.getWarehouseId().toArray()));
        }
        if (findStockReport.getItemCode() != null && !findStockReport.getItemCode().isEmpty()) {
            stockReportQuery = stockReportQuery.filter(col("ITM_CODE").isin(findStockReport.getItemCode().toArray()));
        }
//        if (findStockReport.getItemText() != null && !findStockReport.getItemText().isEmpty()) {
//            stockReportQuery = stockReportQuery.filter(col("itemText").isin(findStockReport.getWarehouseId().toArray()));
//        }
//        if (findStockReport.getStockTypeText() != null && !findStockReport.getStockTypeText().isEmpty()) {
//            stockReportQuery = stockReportQuery.filter(col("stockTypeText").isin(findStockReport.getStockTypeText().toArray()));
//        }


        Encoder<StockReport> stockReportEncoder = Encoders.bean(StockReport.class);
        Dataset<StockReport> datasetControlGroup = stockReportQuery.as(stockReportEncoder);
        List<StockReport> results = datasetControlGroup.collectAsList();
        return results;
    }
}