//package com.mnrclara.spark.core.service;
//
//import com.mnrclara.spark.core.model.FindPutAwayHeader;
//import com.mnrclara.spark.core.model.ImBasicData1;
//import com.mnrclara.spark.core.model.PutAwayHeader;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.apache.spark.sql.*;
//import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
//import org.apache.spark.sql.catalyst.encoders.RowEncoder;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.apache.spark.sql.functions.col;
//
//@Service
//@Slf4j
//public class PutAwayHeaderService {
//
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public PutAwayHeaderService() throws ParseException {
//        //connection properties
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "z4U5P@u$LWJiVkuuOR");
//        sparkSession = SparkSession.builder().master("local[*]").appName("PutAwayHeader.com") .config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://13.234.179.29;databaseName=WMS", "tblputawayheader", connProp)
//                .repartition(16);
////                .cache();
//        df2.createOrReplaceTempView("tblputawayheader");
//
//    }
//
//    /**
//     * @param findPutAwayHeader
//     * @return
//     * @throws ParseException
//     */
//    public List<PutAwayHeader> findPutAwayHeader(FindPutAwayHeader findPutAwayHeader) throws ParseException {
//
//        Dataset<Row> imPutawayHeaderQuery = sparkSession.sql("SELECT "
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
//                + "PA_CNF_ON as confirmedOn "
//                + "FROM tblputawayheader WHERE IS_DELETED = 0");
//        imPutawayHeaderQuery.cache();
//
//        if (findPutAwayHeader.getWarehouseId() != null && !findPutAwayHeader.getWarehouseId().isEmpty()) {
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("WH_ID").isin(findPutAwayHeader.getWarehouseId().toArray()));
//        }
//        if (findPutAwayHeader.getRefDocNumber() != null && !findPutAwayHeader.getRefDocNumber().isEmpty()) {
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("REF_DOC_NO").isin(findPutAwayHeader.getRefDocNumber().toArray()));
//        }
//        if (findPutAwayHeader.getPackBarcodes() != null && !findPutAwayHeader.getPackBarcodes().isEmpty()) {
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("PACK_BARCODE").isin(findPutAwayHeader.getPackBarcodes().toArray()));
//        }
//        if (findPutAwayHeader.getPutAwayNumber() != null && !findPutAwayHeader.getPutAwayNumber().isEmpty()) {
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("PA_NO").isin(findPutAwayHeader.getPutAwayNumber().toArray()));
//        }
//        if (findPutAwayHeader.getProposedStorageBin() != null && !findPutAwayHeader.getProposedStorageBin().isEmpty()) {
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("PROP_ST_BIN").isin(findPutAwayHeader.getProposedStorageBin().toArray()));
//        }
//        if (findPutAwayHeader.getProposedHandlingEquipment() != null && !findPutAwayHeader.getProposedHandlingEquipment().isEmpty()) {
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("PROP_HE_NO").isin(findPutAwayHeader.getProposedHandlingEquipment().toArray()));
//        }
//        if (findPutAwayHeader.getStatusId() != null && !findPutAwayHeader.getStatusId().isEmpty()) {
//            List<String> statusIdStrings = findPutAwayHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
//        }
//        if (findPutAwayHeader.getCreatedBy() != null) {
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("PA_CTD_BY").isin(findPutAwayHeader.getCreatedBy().toArray()));
//        }
//        if (findPutAwayHeader.getStartCreatedOn() != null) {
//            Date startDate = findPutAwayHeader.getStartCreatedOn();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("PA_CTD_ON").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findPutAwayHeader.getEndCreatedOn() != null) {
//            Date endDate = findPutAwayHeader.getEndCreatedOn();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            imPutawayHeaderQuery = imPutawayHeaderQuery.filter(col("PA_CTD_ON").$less$eq(dateFormat.format(endDate)));
//        }
//        Encoder<PutAwayHeader> putAwayHeaderEncoder = Encoders.bean(PutAwayHeader.class);
//        Dataset<PutAwayHeader> dataSetControlGroup = imPutawayHeaderQuery.as(putAwayHeaderEncoder);
//        List<PutAwayHeader> result = dataSetControlGroup.collectAsList();
////        imPutawayHeaderQuery.unpersist();
//
//        return result;
//    }
//
//}