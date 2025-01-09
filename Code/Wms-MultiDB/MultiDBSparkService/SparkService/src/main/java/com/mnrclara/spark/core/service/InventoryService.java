package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.FindInventory;
import com.mnrclara.spark.core.model.Inventory;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;


@Slf4j
@Service
public class InventoryService {

    Properties connProp = new Properties();

    SparkSession sparkSession = null;

    public InventoryService() {

        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tblinventory", connProp)
                .repartition(8);
        df2.createOrReplaceTempView("tblinventory");
    }

    public List<Inventory> findInventory(FindInventory findInventory) throws ParseException {

//        /*
//         * @Column(name = "") private Date ;
//         */
        Dataset<Row> queryDF = sparkSession.sql("SELECT LANG_ID as languageId, C_ID as companyCodeId, \r\n"
                + "	PLANT_ID as plantId, WH_ID as warehouseId, \r\n"
                + "	PAL_CODE as palletCode, CASE_CODE as caseCode,PACK_BARCODE as packBarcodes,ITM_CODE as itemCode, \r\n"
                + " VAR_ID as variantCode, VAR_SUB_ID as variantSubCode, STR_NO as batchSerialNumber,ST_BIN as storageBin, \r\n"
                + " STCK_TYP_ID as stockTypeId, SP_ST_IND_ID as specialStockIndicatorId,REF_ORD_NO as referenceOrderNo,\r\n"
                + " STR_MTD as storageMethod, BIN_CL_ID as binClassId, TEXT as description, INV_QTY as inventoryQuantity, \r\n"
                + " ALLOC_QTY as allocatedQuantity, INV_UOM as inventoryUom, MFR_DATE as manufacturerDate, EXP_DATE as expiryDate,\r\n "
                + " IS_DELETED as deletionIndicator, REF_FIELD_1 as referenceField1, REF_FIELD_2 as referenceField2,REF_FIELD_3 as referenceField3, \r\n"
                + " REF_FIELD_4 as referenceField4, REF_FIELD_5 as referenceField5, REF_FIELD_6 as referenceField6, REF_FIELD_7 as referenceField7, \r\n"
                + " REF_FIELD_8 as referenceField8, REF_FIELD_9 as referenceField9, REF_FIELD_10 as referenceField10,IU_CTD_BY as createdBy, \r\n"
                + " IU_CTD_ON as createdOn, UTD_BY as updatedBy, \r\n "
                + " UTD_ON as updatedOn \r\n" +  " FROM tblinventory");

        String fromDate = "2022-01-01 00:00:00";
        String toDate = "2023-09-19 23:59:59";
        Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromDate);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toDate);
//        String fromDate = "2022-01-01 00:00:00";
//        String toDate = "2023-09-19 23:59:59";


        log.info("dateConv[0]-------> : " + date1);
        log.info("dateConv[1]-------> : " + date2);

        Encoder<Inventory> inventory = Encoders.bean(Inventory.class);
        Dataset<Inventory> dataSetControlGroup = queryDF.filter(a -> a.getTimestamp(35).after(date1) && a.getTimestamp(35).before(date2))
                .as(inventory);
        return dataSetControlGroup.collectAsList();
    }

}
