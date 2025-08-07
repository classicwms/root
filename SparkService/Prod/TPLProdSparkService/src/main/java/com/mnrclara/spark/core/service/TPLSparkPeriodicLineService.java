package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.Almailem.FindPeriodicLineV2;
import com.mnrclara.spark.core.model.Almailem.PeriodicLineV2;
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
public class TPLSparkPeriodicLineService {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TPLSparkPeriodicLineService() {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "gQ3PmSL5D7u32Vtw5F74UwWH");
        sparkSession = SparkSession.builder().master("local[*]").appName("PeriodicLine.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://10.10.18.14;databaseName=WMS_3PL", "tblperiodicline", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblperiodiclinev6");
    }

    /**
     *
     * @param findPeriodicLineV2
     * @return
     * @throws ParseException
     */
    public List<PeriodicLineV2> findPeriodicLine(FindPeriodicLineV2 findPeriodicLineV2) throws ParseException {

        Dataset<Row> periodicLineV2Query = sparkSession.sql("Select "
                + "LANG_ID as languageId, "
                + "C_ID as companyCode, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "CC_NO as cycleCountNo, "
                + "ST_BIN as storageBin, "
                + "ITM_CODE as itemCode, "
                + "PACK_BARCODE as packBarcodes, "
                + "VAR_ID as variantCode, "
                + "VAR_SUB_ID as variantSubCode, "
                + "STR_NO as batchSerialNumber, "
                + "STCK_TYP_ID as stockTypeId, "
                + "SP_ST_IND_ID as specialStockIndicator, "
                + "INV_QTY as inventoryQuantity, "
                + "INV_UOM as inventoryUom, "
                + "CTD_QTY as countedQty, "
                + "VAR_QTY as varianceQty, "
                + "COUNTER_ID as cycleCounterId, "
                + "COUNTER_NM as cycleCounterName, "
                + "STATUS_ID as statusId, "
                + "ACTION as cycleCountAction, "
                + "REF_NO as referenceNo, "
                + "APP_PROCESS_ID as approvalProcessId, "
                + "APP_LVL as approvalLevel, "
                + "APP_CODE as approverCode, "
                + "APP_STATUS as approvalStatus, "
                + "REMARK as remarks, "
                + "REF_FIELD_1 as referenceField1, "
                + "REF_FIELD_2 as referenceField2, "
                + "REF_FIELD_3 as referenceField3, "
                + "REF_FIELD_4 as referenceField4, "
                + "REF_FIELD_5 as referenceField5, "
                + "REF_FIELD_6 as referenceField6, "
                + "REF_FIELD_7 as referenceField7, "
                + "REF_FIELD_8 as referenceField8, "
                + "REF_FIELD_9 as referenceField9, "
                + "REF_FIELD_10 as referenceField10, "
                + "IS_DELETED as deletionIndicator, "
                + "CC_CTD_BY as createdBy, "
                + "CC_CTD_ON as createdOn, "
                + "CC_CNF_BY as confirmedBy, "
                + "CC_CNF_ON as confirmedOn, "
                + "CC_CNT_BY as countedBy, "
                + "CC_CNT_ON as countedOn, "

                // V2 fields
                + "C_TEXT as companyDescription, "
                + "PLANT_TEXT as plantDescription, "
                + "WH_TEXT as warehouseDescription, "
                + "STATUS_TEXT as statusDescription, "
                + "MFR_NAME as manufacturerName, "
                + "PARTNER_ITEM_BARCODE as barcodeId, "
                + "ITM_TEXT as itemDesc, "
                + "ST_SEC_ID as storageSectionId, "
                + "MFR_PART as manufacturerPartNo, "
                + "MIDDLEWARE_ID as middlewareId, "
                + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                + "MIDDLEWARE_TABLE as middlewareTable, "
                + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                + "MFR_CODE as manufacturerCode, "
                + "REF_DOC_TYPE as referenceDocumentType, "
                + "FROZEN_QTY as frozenQty, "
                + "IB_QTY as inboundQuantity, "
                + "OB_QTY as outboundQuantity, "
                + "FIRST_CTD_QTY as firstCountedQty, "
                + "SECOND_CTD_QTY as secondCountedQty "
                + "From tblperiodiclinev6 Where IS_DELETED = 0 "
        );
//        periodicLineV2Query.cache();

        if (findPeriodicLineV2.getLanguageId() != null && !findPeriodicLineV2.getLanguageId().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("LANG_ID").isin(findPeriodicLineV2.getLanguageId().toArray()));
        }
        if (findPeriodicLineV2.getCompanyCode() != null && !findPeriodicLineV2.getCompanyCode().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("C_ID").isin(findPeriodicLineV2.getCompanyCode().toArray()));
        }
        if (findPeriodicLineV2.getPlantId() != null && !findPeriodicLineV2.getPlantId().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("PLANT_ID").isin(findPeriodicLineV2.getPlantId().toArray()));
        }
        if (findPeriodicLineV2.getWarehouseId() != null && !findPeriodicLineV2.getWarehouseId().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("WH_ID").isin(findPeriodicLineV2.getWarehouseId().toArray()));
        }
        if (findPeriodicLineV2.getCycleCountNo() != null && !findPeriodicLineV2.getCycleCountNo().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("CC_NO").isin(findPeriodicLineV2.getCycleCountNo().toArray()));
        }
        if (findPeriodicLineV2.getLineStatusId() != null && !findPeriodicLineV2.getLineStatusId().isEmpty()) {
            List<String> statusString = findPeriodicLineV2.getLineStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            periodicLineV2Query = periodicLineV2Query.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findPeriodicLineV2.getCycleCounterId() != null && !findPeriodicLineV2.getCycleCounterId().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("COUNTER_ID").isin(findPeriodicLineV2.getCycleCounterId().toArray()));
        }
        if (findPeriodicLineV2.getItemCode() != null && !findPeriodicLineV2.getItemCode().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("ITM_CODE").isin(findPeriodicLineV2.getItemCode().toArray()));
        }
        if (findPeriodicLineV2.getStorageBin() != null && !findPeriodicLineV2.getStorageBin().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("ST_BIN").isin(findPeriodicLineV2.getStorageBin().toArray()));
        }
        if (findPeriodicLineV2.getPackBarcodes() != null && !findPeriodicLineV2.getPackBarcodes().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("PACK_BARCODE").isin(findPeriodicLineV2.getPackBarcodes().toArray()));
        }
        if (findPeriodicLineV2.getStockTypeId() != null && !findPeriodicLineV2.getStockTypeId().isEmpty()) {
            List<String> stockTypeString = findPeriodicLineV2.getStockTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            periodicLineV2Query = periodicLineV2Query.filter(col("STCK_TYP_ID").isin(stockTypeString.toArray()));
        }
        if (findPeriodicLineV2.getReferenceField9() != null && !findPeriodicLineV2.getReferenceField9().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("REF_FIELD_9").isin(findPeriodicLineV2.getReferenceField9().toArray()));
        }
        if (findPeriodicLineV2.getReferenceField10() != null && !findPeriodicLineV2.getReferenceField10().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("REF_FIELD_10").isin(findPeriodicLineV2.getReferenceField10().toArray()));
        }
        if (findPeriodicLineV2.getManufactureName() != null && !findPeriodicLineV2.getManufactureName().isEmpty()) {
            periodicLineV2Query = periodicLineV2Query.filter(col("MFR_NAME").isin(findPeriodicLineV2.getManufactureName().toArray()));
        }
        if (findPeriodicLineV2.getStartCreatedOn() != null) {
            Date startDate = findPeriodicLineV2.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            periodicLineV2Query = periodicLineV2Query.filter(col("CC_CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findPeriodicLineV2.getEndCreatedOn() != null) {
            Date endDate = findPeriodicLineV2.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            periodicLineV2Query = periodicLineV2Query.filter(col("CC_CTD_ON").$greater$eq(dateFormat.format(endDate)));
        }


        Encoder<PeriodicLineV2> periodicLineV2Encoder = Encoders.bean(PeriodicLineV2.class);
        Dataset<PeriodicLineV2> datasetControlGroup = periodicLineV2Query.as(periodicLineV2Encoder);
        List<PeriodicLineV2> results = datasetControlGroup.collectAsList();
        return results;
    }

}
