package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.Almailem.InventoryMovementV2;
import com.mnrclara.spark.core.model.Almailem.SearchInventoryMovementV2;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;

@Service
@Slf4j
public class TPLSparkInventoryMovementService {

    Properties conProp = new Properties();

    SparkSession sparkSession = null;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TPLSparkInventoryMovementService() throws ParseException {
        //connection properties
        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conProp.put("user", "sa");
        conProp.put("password", "gQ3PmSL5D7u32Vtw5F74UwWH");
        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://10.10.18.14;databaseName=WMS_3PL", "tblinventorymovement", conProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblinventorymovementv6");

//        //Read from Sql Table
//        val df3 = sparkSession.read().jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS_3PL", "tblcompanyid", conProp)
//                .repartition(16);
//        df3.createOrReplaceTempView("tblcompanyid");
//
//        //Read from Sql Table
//        val df4 = sparkSession.read().jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS_3PL", "tblplantid", conProp)
//                .repartition(16);
//        df4.createOrReplaceTempView("tblplantid");
//
//        //Read from Sql Table
//        val df5 = sparkSession.read().jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS_3PL", "tblwarehouseid", conProp)
//                .repartition(16);
//        df5.createOrReplaceTempView("tblwarehouseid");
    }


    /**
     * @param searchInventoryMovementV2
     * @return
     * @throws ParseException
     */
    public List<InventoryMovementV2> findInventoryMovement(SearchInventoryMovementV2 searchInventoryMovementV2) throws ParseException {

        Dataset<Row> imInventoryMovementQuery = sparkSession.sql("SELECT "
                + "imv.LANG_ID as languageId, "
                + "imv.C_ID as companyCodeId, "
                + "imv.PLANT_ID as plantId, "
                + "imv.WH_ID as warehouseId, "
                + "imv.MVT_TYP_ID as movementType, "
                + "imv.SUB_MVT_TYP_ID as submovementType, "
                + "imv.PAL_CODE as palletCode, "
                + "imv.CASE_CODE as caseCode, "
                + "imv.PACK_BARCODE as packBarcodes, "
                + "imv.ITM_CODE as itemCode, "
                + "imv.VAR_ID as variantCode, "
                + "imv.VAR_SUB_ID as variantSubCode, "
                + "imv.STR_NO as batchSerialNumber, "
                + "imv.MVT_DOC_NO as movementDocumentNo, "
                + "imv.MFR_PART as manufacturerName, "
                + "imv.ST_BIN as storageBin, "
                + "imv.STR_MTD as storageMethod, "
                + "imv.TEXT as description, "
                + "imv.STCK_TYP_ID as stockTypeId, "
                + "imv.SP_ST_IND_ID as specialStockIndicator, "
                + "imv.MVT_QTY_VAL as movementQtyValue, "
                + "imv.MVT_QTY as movementQty, "
                + "imv.BAL_OH_QTY as balanceOHQty, "
                + "imv.MVT_UOM as inventoryUom, "
                + "imv.REF_DOC_NO as refDocNumber, "
                + "imv.REF_FIELD_1 as referenceField1, "
                + "imv.REF_FIELD_2 as referenceField2, "
                + "imv.REF_FIELD_3 as referenceField3, "
                + "imv.REF_FIELD_4 as referenceField4, "
                + "imv.REF_FIELD_5 as referenceField5, "
                + "imv.REF_FIELD_6 as referenceField6, "
                + "imv.REF_FIELD_7 as referenceField7, "
                + "imv.REF_FIELD_8 as referenceField8, "
                + "imv.REF_FIELD_9 as referenceField9, "
                + "imv.REF_FIELD_10 as referenceField10, "
                + "imv.IS_DELETED as deletionIndicator, "
                + "imv.IM_CTD_BY as createdBy, "
                + "imv.IM_CTD_ON as createdOn, "
                + "imv.C_TEXT as companyDescription, "
                + "imv.PLANT_TEXT as plantDescription, "
                + "imv.WH_TEXT as warehouseDescription, "
                + "imv.BARCODE_ID as barcodeId "
                + "FROM tblinventorymovementv6 imv " +
//                "JOIN tblcompanyid cDesc ON imv.C_ID = cDesc.C_ID AND imv.LANG_ID = cDesc.LANG_ID " +
//                "JOIN tblplantid pDesc ON imv.C_ID = pDesc.C_ID AND imv.LANG_ID = pDesc.LANG_ID AND imv.PLANT_ID = pDesc.PLANT_ID " +
//                "JOIN tblwarehouseid wDesc ON imv.C_ID = wDesc.C_ID AND imv.LANG_ID = wDesc.LANG_ID AND imv.WH_ID = wDesc.WH_ID " +
                " WHERE imv.IS_DELETED = 0");

//        imInventoryMovementQuery.cache();

        if (searchInventoryMovementV2.getWarehouseId() != null && !searchInventoryMovementV2.getWarehouseId().isEmpty()) {
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.WH_ID").isin(searchInventoryMovementV2.getWarehouseId().toArray()));
        }
        if (searchInventoryMovementV2.getLanguageId() != null && !searchInventoryMovementV2.getLanguageId().isEmpty()) {
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.LANG_ID").isin(searchInventoryMovementV2.getLanguageId().toArray()));
        }
        if (searchInventoryMovementV2.getCompanyCodeId() != null && !searchInventoryMovementV2.getCompanyCodeId().isEmpty()) {
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.C_ID").isin(searchInventoryMovementV2.getCompanyCodeId().toArray()));
        }
        if (searchInventoryMovementV2.getPlantId() != null && !searchInventoryMovementV2.getPlantId().isEmpty()) {
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.PLANT_ID").isin(searchInventoryMovementV2.getPlantId().toArray()));
        }
        if (searchInventoryMovementV2.getItemCode() != null && !searchInventoryMovementV2.getItemCode().isEmpty()) {
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.ITM_CODE").isin(searchInventoryMovementV2.getItemCode().toArray()));
        }
        if (searchInventoryMovementV2.getMovementType() != null && !searchInventoryMovementV2.getMovementType().isEmpty()) {
            List<String> movementTypeString = searchInventoryMovementV2.getMovementType().stream().map(String::valueOf).collect(Collectors.toList());
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.MVT_TYP_ID").isin(movementTypeString.toArray()));
        }
        if (searchInventoryMovementV2.getSubmovementType() != null && !searchInventoryMovementV2.getSubmovementType().isEmpty()) {
            List<String> subMovementTypeString = searchInventoryMovementV2.getSubmovementType().stream().map(String::valueOf).collect(Collectors.toList());
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.SUB_MVT_TYP_ID").isin(subMovementTypeString.toArray()));
        }
        if (searchInventoryMovementV2.getPackBarcodes() != null && !searchInventoryMovementV2.getPackBarcodes().isEmpty()) {
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.PACK_BARCODE").isin(searchInventoryMovementV2.getPackBarcodes().toArray()));
        }
        if (searchInventoryMovementV2.getBatchSerialNumber() != null && !searchInventoryMovementV2.getBatchSerialNumber().isEmpty()) {
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.STR_NO").isin(searchInventoryMovementV2.getBatchSerialNumber().toArray()));
        }
        if (searchInventoryMovementV2.getMovementDocumentNo() != null && !searchInventoryMovementV2.getMovementDocumentNo().isEmpty()) {
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.MVT_DOC_NO").isin(searchInventoryMovementV2.getMovementDocumentNo().toArray()));
        }
        if (searchInventoryMovementV2.getFromCreatedOn() != null) {
            Date startDate = searchInventoryMovementV2.getFromCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.IM_CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (searchInventoryMovementV2.getToCreatedOn() != null) {
            Date endDate = searchInventoryMovementV2.getToCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            imInventoryMovementQuery = imInventoryMovementQuery.filter(col("imv.IM_CTD_ON").$less$eq(dateFormat.format(endDate)));
        }

        Encoder<InventoryMovementV2> inventoryMovementEncoder = Encoders.bean(InventoryMovementV2.class);
        Dataset<InventoryMovementV2> dataSetControlGroup = imInventoryMovementQuery.as(inventoryMovementEncoder);
        List<InventoryMovementV2> result = dataSetControlGroup.collectAsList();
        return result;
    }
}
