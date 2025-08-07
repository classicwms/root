//package com.mnrclara.spark.core.service;
//
//import com.mnrclara.spark.core.model.FindImBasicData1;
//import com.mnrclara.spark.core.model.FindQualityHeader;
//import com.mnrclara.spark.core.model.ImBasicData1;
//import com.mnrclara.spark.core.model.QualityHeader;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.spark.sql.*;
//import lombok.val;
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
//public class QualityHeaderService {
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public QualityHeaderService() throws ParseException {
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "z4U5P@u$LWJiVkuuOR");
//        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read().option("fetchSize", "20000").jdbc("jdbc:sqlserver://13.234.179.29;databaseName=WMS", "tblqualityheader", connProp)
//                .repartition(20);
////                .cache();
//        df2.createOrReplaceTempView("tblqualityheader");
//    }
//
//    public List<QualityHeader> findQualityHeader(FindQualityHeader findQualityHeader) throws ParseException {
//
//        Dataset<Row> imQualityHeaderQuery = sparkSession.sql("SELECT "
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCodeId, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "PRE_OB_NO as preOutboundNo, "
//                + "REF_DOC_NO as refDocNumber, "
//                + "PARTNER_CODE as partnerCode, "
//                + "PU_NO as pickupNumber, "
//                + "QC_NO as qualityInspectionNo, "
//                + "PICK_HE_NO as actualHeNo, "
//                + "OB_ORD_TYP_ID as outboundOrderTypeId, "
//                + "STATUS_ID as statusId, "
//                + "QC_TO_QTY as qcToQty, "
//                + "QC_UOM as qcUom, "
//                + "MFR_PART as manufacturerPartNo, "
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
//                + "REMARK as remarks, "
//                + "QC_CTD_BY as qualityCreatedBy, "
//                + "QC_CTD_ON as qualityCreatedOn, "
//                + "QC_CNF_BY as qualityConfirmedBy, "
//                + "QC_CNF_ON as qualityConfirmedOn, "
//                + "QC_UTD_BY as qualityUpdatedBy, "
//                + "QC_UTD_ON as qualityUpdatedOn, "
//                + "QC_REV_BY as qualityReversedBy, "
//                + "QC_REV_ON as qualityReversedOn "
//                + "FROM tblqualityheader WHERE IS_DELETED = 0 ");
//
//        imQualityHeaderQuery.cache();
////        String dbQualityQueryString = imQualityHeaderQuery.toString();
////        Dataset<Row> imQualityHeaderQuery = sparkSession.sql(dbQualityQueryString).cache();
////
//        if (findQualityHeader.getRefDocNumber() != null && !findQualityHeader.getRefDocNumber().isEmpty()) {
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("REF_DOC_NO").isin(findQualityHeader.getRefDocNumber().toArray()));
//        }
//        if (findQualityHeader.getPartnerCode() != null && !findQualityHeader.getPartnerCode().isEmpty()) {
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("PARTNER_CODE").isin(findQualityHeader.getPartnerCode().toArray()));
//        }
//        if (findQualityHeader.getQualityInspectionNo() != null && !findQualityHeader.getQualityInspectionNo().isEmpty()) {
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("QC_NO").isin(findQualityHeader.getQualityInspectionNo().toArray()));
//        }
//        if (findQualityHeader.getActualHeNo() != null && !findQualityHeader.getActualHeNo().isEmpty()) {
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("PICK_HE_NO").isin(findQualityHeader.getActualHeNo().toArray()));
//        }
//        if (findQualityHeader.getOutboundOrderTypeId() != null && !findQualityHeader.getOutboundOrderTypeId().isEmpty()) {
//            List<String> outboundOrderString = findQualityHeader.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("OB_ORD_TYP_ID").isin(outboundOrderString.toArray()));
//        }
//        if (findQualityHeader.getStatusId() != null && !findQualityHeader.getStatusId().isEmpty()) {
//            List<String> statusString = findQualityHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
//        }
//        if (findQualityHeader.getSoType() != null && !findQualityHeader.getSoType().isEmpty()) {
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("REF_FIELD_1").isin(findQualityHeader.getSoType().toArray()));
//        }
//        if (findQualityHeader.getWarehouseId() != null && !findQualityHeader.getWarehouseId().isEmpty()) {
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("WH_ID").isin(findQualityHeader.getWarehouseId().toArray()));
//        }
//        if (findQualityHeader.getStartQualityCreatedOn() != null) {
//            Date startQuality = findQualityHeader.getStartQualityCreatedOn();
//            startQuality = org.apache.commons.lang3.time.DateUtils.truncate(startQuality, Calendar.DAY_OF_MONTH);
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("QC_CTD_ON").$greater$eq(dateFormat.format(startQuality)));
//        }
//        if (findQualityHeader.getEndQualityCreatedOn() != null) {
//            Date endQualityDate = findQualityHeader.getEndQualityCreatedOn();
//            endQualityDate = org.apache.commons.lang3.time.DateUtils.truncate(endQualityDate, Calendar.DAY_OF_MONTH);
//            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("QC_CTD_ON").$less$eq(dateFormat.format(endQualityDate)));
//        }
//
//        Encoder<QualityHeader> qualityHeaderEncoder = Encoders.bean(QualityHeader.class);
//        Dataset<QualityHeader> dataSetControlGroup = imQualityHeaderQuery.as(qualityHeaderEncoder);
//        List<QualityHeader> result = dataSetControlGroup.collectAsList();
////        imQualityHeaderQuery.unpersist();
//        return result;
//    }
//
//
//}