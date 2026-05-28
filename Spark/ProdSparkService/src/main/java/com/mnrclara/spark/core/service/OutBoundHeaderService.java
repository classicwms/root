//package com.mnrclara.spark.core.service;
//
//import com.mnrclara.spark.core.model.FindOutBoundHeader;
//import com.mnrclara.spark.core.model.FindPutAwayHeader;
//import com.mnrclara.spark.core.model.OutBoundHeader;
//import com.mnrclara.spark.core.model.PutAwayHeader;
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
//@Slf4j
//@Service
//public class OutBoundHeaderService {
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public OutBoundHeaderService() throws ParseException {
//
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "z4U5P@u$LWJiVkuuOR");
//        sparkSession = SparkSession.builder().master("local[*]").appName("OutBoundHeader.com") .config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://13.234.179.29;databaseName=WMS", "tbloutboundheader", connProp)
//                .repartition(16)
//                .cache();
//        df2.createOrReplaceTempView("tbloutboundheader");
//    }
//
//    public List<OutBoundHeader> findOutBoundHeader(FindOutBoundHeader findOutBoundHeader) throws ParseException {
//
//        Dataset<Row> imOutBoundHeaderQuery = sparkSession.sql("SELECT "
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCodeId, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "PRE_OB_NO as preOutboundNo, "
//                + "REF_DOC_NO as refDocNumber, "
//                + "PARTNER_CODE as partnerCode, "
//                + "DLV_ORD_NO as deliveryOrderNo, "
//                + "REF_DOC_TYP as referenceDocumentType, "
//                + "OB_ORD_TYP_ID as outboundOrderTypeId, "
//                + "STATUS_ID as statusId, "
//                + "REF_DOC_DATE as refDocDate, "
//                + "REQ_DEL_DATE as requiredDeliveryDate, "
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
//                + "DLV_CTD_BY as createdBy, "
//                + "DLV_CTD_ON as createdOn, "
//                + "DLV_CNF_BY as deliveryConfirmedBy, "
//                + "DLV_CNF_ON as deliveryConfirmedOn, "
//                + "DLV_UTD_BY as updatedBy, "
//                + "DLV_UTD_ON as updatedOn, "
//                + "DLV_REV_BY as reversedBy, "
//                + "DLV_REV_ON as reversedOn "
//
//                + "FROM tbloutboundheader WHERE IS_DELETED = 0");
//                imOutBoundHeaderQuery.cache();
//
//
//        if (findOutBoundHeader.getWarehouseId() != null && !findOutBoundHeader.getWarehouseId().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("WH_ID").isin(findOutBoundHeader.getWarehouseId().toArray()));
//        }
//        if (findOutBoundHeader.getRefDocNumber() != null && !findOutBoundHeader.getRefDocNumber().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("REF_DOC_NO").isin(findOutBoundHeader.getRefDocNumber().toArray()));
//        }
//        if (findOutBoundHeader.getPartnerCode() != null && !findOutBoundHeader.getPartnerCode().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("PARTNER_CODE").isin(findOutBoundHeader.getPartnerCode().toArray()));
//        }
//        if (findOutBoundHeader.getSoType() != null && !findOutBoundHeader.getSoType().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("REF_FIELD_1").isin(findOutBoundHeader.getSoType().toArray()));
//        }
//        if (findOutBoundHeader.getStatusId() != null && !findOutBoundHeader.getStatusId().isEmpty()) {
//            List<String> statusIdStrings = findOutBoundHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
//        }
//        if (findOutBoundHeader.getOutboundOrderTypeId() != null && !findOutBoundHeader.getOutboundOrderTypeId().isEmpty()) {
//            List<String> outboundOrderTypeIdStrings = findOutBoundHeader.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("OB_ORD_TYP_ID").isin(outboundOrderTypeIdStrings.toArray()));
//        }
//        if (findOutBoundHeader.getStartRequiredDeliveryDate() != null) {
//            Date startDate = findOutBoundHeader.getStartRequiredDeliveryDate();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("REQ_DEL_DATE").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findOutBoundHeader.getEndRequiredDeliveryDate() != null) {
//            Date endDate = findOutBoundHeader.getEndRequiredDeliveryDate();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("REQ_DEL_DATE").$less$eq(dateFormat.format(endDate)));
//        }
//        if (findOutBoundHeader.getStartDeliveryConfirmedOn() != null) {
//            Date startDate = findOutBoundHeader.getStartDeliveryConfirmedOn();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("DLV_CNF_ON").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findOutBoundHeader.getEndDeliveryConfirmedOn() != null) {
//            Date endDate = findOutBoundHeader.getEndDeliveryConfirmedOn();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("DLV_CNF_ON").$less$eq(dateFormat.format(endDate)));
//        }
//
//
//        Encoder<OutBoundHeader> outBoundHeaderEncoder = Encoders.bean(OutBoundHeader.class);
//        Dataset<OutBoundHeader> dataSetControlGroup = imOutBoundHeaderQuery.as(outBoundHeaderEncoder);
//        List<OutBoundHeader> result = dataSetControlGroup.collectAsList();
//        imOutBoundHeaderQuery.unpersist();
//
//        return result;
//
//    }
//}