package com.mnrclara.spark.core.service;


import com.mnrclara.spark.core.model.FindInventoryMovement;
import com.mnrclara.spark.core.model.InventoryMovement;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;


@Service
@Slf4j
public class InventoryMovementService {

    Properties connProp = new Properties();

    SparkSession sparkSession = null;

    public InventoryMovementService() {

        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tblinventorymovement", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblinventorymovement");
    }

    public List<InventoryMovement> findInventoryMovement(FindInventoryMovement findInventoryMovement) throws ParseException {


        String fromDate = "2022-01-01 00:00:00";
        String toDate = "2023-09-19 23:59:59";
        Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromDate);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toDate);
        log.info("dateConv[0]-------> : " + date1);
        log.info("dateConv[1]-------> : " + date2);

        StringBuilder inventoryQuery = new StringBuilder("SELECT WH_ID as warehouseId, \r\n"
                + "ITM_CODE as itemCode, TEXT as description, PACK_BARCODE as packBarcodes, ST_BIN as storageBin, " +
                "MVT_TYP_ID as movementType, SUB_MVT_TYP_ID as submovementType, REF_DOC_NO as refDocNumber, MVT_QTY as movementQty, " +
                "MVT_UOM as inventoryUom, IM_CTD_ON as createdOn \r\n" +
                "FROM tblinventorymovement " +
                "WHERE IM_CTD_ON BETWEEN '" + fromDate + "' AND '" + toDate + "'");

        if (findInventoryMovement.getWarehouseId() != null) {
            inventoryQuery.append(" AND WH_ID = '").append(findInventoryMovement.getWarehouseId()).append("'");
        }

        String dbInventoryQuery = inventoryQuery.toString();
        Dataset<Row> queryDF = sparkSession.sql(dbInventoryQuery);
        queryDF.cache();


        Encoder<InventoryMovement> inventoryMovement = Encoders.bean(InventoryMovement.class);
        Dataset<InventoryMovement> dataSetControlGroup = queryDF.as(inventoryMovement).cache();
        return dataSetControlGroup.collectAsList();

    }

}
