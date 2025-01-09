//package com.mnrclara.spark.core.service.wmsindus;
//
//import com.mnrclara.spark.core.model.wmscorev2.PutAwayHeaderV2;
//import com.mnrclara.spark.core.model.wmscorev2.SearchPutAwayHeaderV2;
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
//public class SparkPutAwayHeaderIndusServiceV2 {
//
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public SparkPutAwayHeaderIndusServiceV2() throws ParseException {
//        //connection properties
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
//        sparkSession = SparkSession.builder().master("local[*]").appName("PutAwayHeader.com") .config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMF", "tblputawayheader", connProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tblputawayheaderV5");
//
//    }
//
//
//    /**
//     *
//     * @param searchPutAwayHeaderV2
//     * @return
//     * @throws ParseException
//     */
//    public List<PutAwayHeaderV2> findPutAwayHeaderV2(SearchPutAwayHeaderV2 searchPutAwayHeaderV2) throws ParseException {
//
//        Dataset<Row> imPutawayHeaderQueryV2 = sparkSession.sql("SELECT "
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCodeId, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "PRE_IB_NO as preInboundNo, "
//                + "REF_DOC_NO as refDocNumber, "
//                + "GR_NO as goodsReceiptNo, "
//                + "IB_ORD_TYP_ID as inboundOrderTypeId, "
//                + "PAL_CODE as palletCode, "
//                + "CASE_CODE as caseCode, "
//                + "PACK_BARCODE as packBarcodes, "
//                + "PA_NO as putAwayNumber, "
//                + "PROP_ST_BIN as proposedStorageBin, "
//                + "PA_QTY as putAwayQuantity, "
//                + "PA_UOM as putAwayUom, "
//                + "STR_TYP_ID as strategyTypeId, "
//                + "ST_NO as strategyNo, "
//                + "PROP_HE_NO as proposedHandlingEquipment, "
//                + "ASS_USER_ID as assignedUserId, "
//                + "STATUS_ID as statusId, "
//                + "QTY_TYPE as quantityType, "
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
//                + "PA_CTD_BY as createdBy, "
//                + "PA_CTD_ON as createdOn, "
//                + "PA_UTD_BY as updatedBy, "
//                + "PA_UTD_ON as updatedOn, "
//                + "PA_CNF_BY as confirmedBy, "
//                + "PA_CNF_ON as confirmedOn, "
//                + "INV_QTY as inventoryQuantity, "
//                + "BARCODE_ID as barcodeId, "
//                + "MFR_DATE as manufacturerDate, "
//                + "EXP_DATE as expiryDate, "
//                + "MFR_CODE as manufacturerCode, "
//                + "MFR_NAME as manufacturerName, "
//                + "ORIGIN as origin, "
//                + "BRAND as brand, "
//                + "ORD_QTY as orderQty, "
//                + "CBM as cbm, "
//                + "CBM_UNIT as cbmUnit, "
//                + "CBM_QTY as cbmQuantity, "
//                + "APP_STATUS as approvalStatus, "
//                + "REMARK as remark, "
//                + "C_TEXT as companyDescription, "
//                + "PLANT_TEXT as plantDescription, "
//                + "WH_TEXT as warehouseDescription, "
//                + "STATUS_TEXT as statusDescription, "
//                + "ACTUAL_PACK_BARCODE as actualPackBarcodes, "
//                + "MIDDLEWARE_ID as middlewareId, "
//                + "MIDDLEWARE_TABLE as middlewareTable, "
//                + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
//                + "REF_DOC_TYPE as referenceDocumentType, "
//                + "TRANSFER_ORDER_DATE as transferOrderDate, "
//                + "IS_COMPLETED as isCompleted, "
//                + "IS_CANCELLED as isCancelled, "
//                + "M_UPDATED_ON as mUpdatedOn, "
//                + "SOURCE_BRANCH_CODE as sourceBranchCode, "
//                + "SOURCE_COMPANY_CODE as sourceCompanyCode, "
//                + "LEVEL_ID as levelId, "
//                + "STR_NO as batchSerialNumber "
//                + "FROM tblputawayheaderV5 WHERE IS_DELETED = 0");
//
//        if (searchPutAwayHeaderV2.getWarehouseId() != null && !searchPutAwayHeaderV2.getWarehouseId().isEmpty()) {
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("WH_ID").isin(searchPutAwayHeaderV2.getWarehouseId().toArray()));
//        }
//        if (searchPutAwayHeaderV2.getRefDocNumber() != null && !searchPutAwayHeaderV2.getRefDocNumber().isEmpty()) {
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("REF_DOC_NO").isin(searchPutAwayHeaderV2.getRefDocNumber().toArray()));
//        }
//        if (searchPutAwayHeaderV2.getPackBarcodes() != null && !searchPutAwayHeaderV2.getPackBarcodes().isEmpty()) {
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("PACK_BARCODE").isin(searchPutAwayHeaderV2.getPackBarcodes().toArray()));
//        }
//        if (searchPutAwayHeaderV2.getPutAwayNumber() != null && !searchPutAwayHeaderV2.getPutAwayNumber().isEmpty()) {
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("PA_NO").isin(searchPutAwayHeaderV2.getPutAwayNumber().toArray()));
//        }
//        if (searchPutAwayHeaderV2.getProposedStorageBin() != null && !searchPutAwayHeaderV2.getProposedStorageBin().isEmpty()) {
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("PROP_ST_BIN").isin(searchPutAwayHeaderV2.getProposedStorageBin().toArray()));
//        }
//        if (searchPutAwayHeaderV2.getProposedHandlingEquipment() != null && !searchPutAwayHeaderV2.getProposedHandlingEquipment().isEmpty()) {
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("PROP_HE_NO").isin(searchPutAwayHeaderV2.getProposedHandlingEquipment().toArray()));
//        }
//        if (searchPutAwayHeaderV2.getBarcodeId() != null && !searchPutAwayHeaderV2.getBarcodeId().isEmpty()) {
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("BARCODE_ID").isin(searchPutAwayHeaderV2.getBarcodeId().toArray()));
//        }
//        if (searchPutAwayHeaderV2.getStatusId() != null && !searchPutAwayHeaderV2.getStatusId().isEmpty()) {
//            List<String> statusIdStrings = searchPutAwayHeaderV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
//        }
//        if (searchPutAwayHeaderV2.getCreatedBy() != null) {
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("PA_CTD_BY").isin(searchPutAwayHeaderV2.getCreatedBy().toArray()));
//        }
//        if (searchPutAwayHeaderV2.getStartCreatedOn() != null) {
//            Date startDate = searchPutAwayHeaderV2.getStartCreatedOn();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("PA_CTD_ON").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (searchPutAwayHeaderV2.getEndCreatedOn() != null) {
//            Date endDate = searchPutAwayHeaderV2.getEndCreatedOn();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("PA_CTD_ON").$less$eq(dateFormat.format(endDate)));
//        }
//
//        //v2 fields
//        if(searchPutAwayHeaderV2.getCompanyCodeId() != null && !searchPutAwayHeaderV2.getCompanyCodeId().isEmpty()){
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("C_ID").isin(searchPutAwayHeaderV2.getCompanyCodeId().toArray()));
//        }
//        if(searchPutAwayHeaderV2.getPlantId() != null && !searchPutAwayHeaderV2.getPlantId().isEmpty()){
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("PLANT_ID").isin(searchPutAwayHeaderV2.getPlantId().toArray()));
//        }
//        if(searchPutAwayHeaderV2.getLanguageId() != null && !searchPutAwayHeaderV2.getLanguageId().isEmpty()){
//            imPutawayHeaderQueryV2 = imPutawayHeaderQueryV2.filter(col("LANG_ID").isin(searchPutAwayHeaderV2.getLanguageId().toArray()));
//        }
//        Encoder<PutAwayHeaderV2> putAwayHeaderEncoderV2 = Encoders.bean(PutAwayHeaderV2.class);
//        Dataset<PutAwayHeaderV2> dataSetControlGroup = imPutawayHeaderQueryV2.as(putAwayHeaderEncoderV2);
//        List<PutAwayHeaderV2> result = dataSetControlGroup.collectAsList();
//
//        return result;
//    }
//}