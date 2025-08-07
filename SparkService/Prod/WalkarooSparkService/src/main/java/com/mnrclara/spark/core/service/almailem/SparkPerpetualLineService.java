//package com.mnrclara.spark.core.service.almailem;
//
//
//import com.mnrclara.spark.core.model.Almailem.*;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
//import static org.apache.spark.sql.functions.col;
//
//@Service
//@Slf4j
//public class SparkPerpetualLineService {
//
//    Properties conProp = new Properties();
//    SparkSession sparkSession = null;
//
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public SparkPerpetualLineService() throws ParseException {
//        // Connection properties
//        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        conProp.put("user", "sa");
//        conProp.put("password", "AL.5p6^VjhzZm0t|A25n^m9D1H");
//
//        // Initialize Spark session and read data from SQL table
//        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://10.0.2.8;databaseName=WMS_ALMPRD", "tblperpetualline", conProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tblperpetuallinev2");
//    }
//
//    /**
//     * @param searchPerpetualLineV2
//     * @return
//     * @throws ParseException
//     */
//    public List<PerpetualLineV2> findPerpetualLine(SearchPerpetualLineV2 searchPerpetualLineV2) throws ParseException {
//
//
//        Dataset<Row> perpetualLineQuery = sparkSession.sql("SELECT "
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCodeId, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "CC_NO as cycleCountNo, "
//                + "ST_BIN as storageBin, "
//                + "ITM_CODE as itemCode, "
//                + "PACK_BARCODE as packBarcodes, "
//                + "ITM_DESC as itemDesc, "
//                + "MFR_PART as manufacturerPartNo, "
//                + "VAR_ID as variantCode, "
//                + "VAR_SUB_ID as variantSubCode, "
//                + "STR_NO as batchSerialNumber, "
//                + "STCK_TYP_ID as stockTypeId, "
//                + "SP_ST_IND_ID as specialStockIndicator, "
//                + "ST_SEC_ID as storageSectionId, "
//                + "INV_QTY as inventoryQuantity, "
//                + "INV_UOM as inventoryUom, "
//                + "CTD_QTY as countedQty, "
//                + "VAR_QTY as varianceQty, "
//                + "COUNTER_ID as cycleCounterId, "
//                + "COUNTER_NM as cycleCounterName, "
//                + "STATUS_ID as statusId, "
//                + "ACTION as cycleCountAction, "
//                + "REF_NO as referenceNo, "
//                + "APP_PROCESS_ID as approvalProcessId, "
//                + "APP_LVL as approvalLevel, "
//                + "APP_CODE as approverCode, "
//                + "APP_STATUS as approvalStatus, "
//                + "REMARK as remarks, "
//                + "REF_FIELD_1 as referenceField1, "
//                + "REF_FIELD_2 as referenceField2, "
//                + "REF_FIELD_3 as referenceField3, "
//                + "REF_FIELD_4 as referenceField4, "
//                + "REF_FIELD_5 as referenceField5, "
//                + "REF_FIELD_6 as referenceField6, "
//                + "REF_FIELD_7 as referenceField7, "
//                + "REF_FIELD_8 as referenceField8, "
//                + "REF_FIELD_9 as referenceField9, "
//                + "REF_FIELD_10 as referenceField10, "
//                + "IS_DELETED as deletionIndicator, "
//                + "CC_CTD_BY as createdBy, "
//                + "CC_CTD_ON as createdOn, "
//                + "CC_CNF_BY as confirmedBy, "
//                + "CC_CNF_ON as confirmedOn, "
//                + "CC_CNT_BY as countedBy, "
//                + "CC_CNT_ON as countedOn, "
//                + "C_TEXT as companyDescription, "
//                + "PLANT_TEXT as plantDescription, "
//                + "WH_TEXT as warehouseDescription, "
//                + "STATUS_TEXT as statusDescription, "
//                + "MFR_NAME as manufacturerName, "
//                + "MFR_CODE as manufacturerCode, "
//                + "PARTNER_ITEM_BARCODE as barcodeId, "
//                + "MIDDLEWARE_ID as middlewareId, "
//                + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
//                + "MIDDLEWARE_TABLE as middlewareTable, "
//                + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
//                + "REF_DOC_TYPE as referenceDocumentType, "
//                + "FROZEN_QTY as frozenQty, "
//                + "IB_QTY as inboundQuantity, "
//                + "OB_QTY as outboundQuantity, "
//                + "FIRST_CTD_QTY as firstCountedQty, "
//                + "SECOND_CTD_QTY as secondCountedQty "
//                + "From tblperpetuallinev2 where IS_DELETED = 0 ");
//
////        perpetualLineQuery.cache();
//
//        if (searchPerpetualLineV2.getLanguageId() != null && !searchPerpetualLineV2.getLanguageId().isEmpty()) {
//            perpetualLineQuery = perpetualLineQuery.filter(col("LANG_ID").isin(searchPerpetualLineV2.getLanguageId().toArray()));
//        }
//        if (searchPerpetualLineV2.getCompanyCodeId() != null && !searchPerpetualLineV2.getCompanyCodeId().isEmpty()) {
//            perpetualLineQuery = perpetualLineQuery.filter(col("C_ID").isin(searchPerpetualLineV2.getCompanyCodeId().toArray()));
//        }
//        if (searchPerpetualLineV2.getPlantId() != null && !searchPerpetualLineV2.getPlantId().isEmpty()) {
//            perpetualLineQuery = perpetualLineQuery.filter(col("PLANT_ID").isin(searchPerpetualLineV2.getPlantId().toArray()));
//        }
//        if (searchPerpetualLineV2.getWarehouseId() != null && !searchPerpetualLineV2.getWarehouseId().isEmpty()) {
//            perpetualLineQuery = perpetualLineQuery.filter(col("WH_ID").isin(searchPerpetualLineV2.getWarehouseId().toArray()));
//        }
//        if (searchPerpetualLineV2.getCycleCountNo() != null && !searchPerpetualLineV2.getCycleCountNo().isEmpty()) {
//            perpetualLineQuery = perpetualLineQuery.filter(col("CC_NO").isin(searchPerpetualLineV2.getCycleCountNo().toArray()));
//        }
//        if (searchPerpetualLineV2.getLineStatusId() != null && !searchPerpetualLineV2.getLineStatusId().isEmpty()) {
//            List<String> statusString = searchPerpetualLineV2.getLineStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            perpetualLineQuery = perpetualLineQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
//        }
//        if (searchPerpetualLineV2.getCycleCounterId() != null && !searchPerpetualLineV2.getCycleCounterId().isEmpty()) {
//            perpetualLineQuery = perpetualLineQuery.filter(col("COUNTER_ID").isin(searchPerpetualLineV2.getCycleCounterId().toArray()));
//        }
//        if (searchPerpetualLineV2.getItemCode() != null && !searchPerpetualLineV2.getItemCode().isEmpty()) {
//            perpetualLineQuery = perpetualLineQuery.filter(col("ITM_CODE").isin(searchPerpetualLineV2.getItemCode().toArray()));
//        }
//        if (searchPerpetualLineV2.getStorageBin() != null && !searchPerpetualLineV2.getStorageBin().isEmpty()) {
//            perpetualLineQuery = perpetualLineQuery.filter(col("ST_BIN").isin(searchPerpetualLineV2.getStorageBin().toArray()));
//        }
//        if (searchPerpetualLineV2.getPackBarcodes() != null && !searchPerpetualLineV2.getPackBarcodes().isEmpty()) {
//            perpetualLineQuery = perpetualLineQuery.filter(col("PACK_BARCODE").isin(searchPerpetualLineV2.getPackBarcodes().toArray()));
//        }
//
//        if(searchPerpetualLineV2.getStartCreatedOn() != null){
//            Date fromCreatedOn = searchPerpetualLineV2.getStartCreatedOn();
//            fromCreatedOn = org.apache.commons.lang3.time.DateUtils.truncate(fromCreatedOn, Calendar.DAY_OF_MONTH);
//            perpetualLineQuery = perpetualLineQuery.filter(col("CC_CTD_ON").$greater$eq(dateFormat.format(fromCreatedOn)));
//        }
//        if(searchPerpetualLineV2.getEndCreatedOn() != null){
//            Date toCreatedOn = searchPerpetualLineV2.getEndCreatedOn();
//            toCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(toCreatedOn, Calendar.DAY_OF_MONTH);
//            perpetualLineQuery = perpetualLineQuery.filter(col("CC_CTD_ON").$less$eq(dateFormat.format(toCreatedOn)));
//        }
//
//        Encoder<PerpetualLineV2> perpetualLineV2Encoder = Encoders.bean(PerpetualLineV2.class);
//        Dataset<PerpetualLineV2> dataSetPerpetualLineV2 = perpetualLineQuery.as(perpetualLineV2Encoder);
//        List<PerpetualLineV2> results = dataSetPerpetualLineV2.collectAsList();
//        return results;
//    }
//}
