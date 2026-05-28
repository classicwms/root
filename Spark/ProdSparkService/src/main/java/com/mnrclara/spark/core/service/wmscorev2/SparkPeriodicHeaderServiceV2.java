//package com.mnrclara.spark.core.service.wmscorev2;
//
//import com.mnrclara.spark.core.model.wmscorev2.FindPeriodicHeaderV2;
//import com.mnrclara.spark.core.model.wmscorev2.PeriodicHeaderV2;
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
//public class SparkPeriodicHeaderServiceV2 {
//
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public SparkPeriodicHeaderServiceV2() {
//        //connection properties
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "AL.8B/XoM>D3vR]Q4s41?.8hP>|");
//        sparkSession = SparkSession.builder().master("local[*]").appName("PeriodicHeader.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read()
//                .option("fetchSize", "10000")
//                .jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS_CORE", "tblperiodicheader", connProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tblperiodicheaderv5");
//    }
//
//    public List<PeriodicHeaderV2> findPeriodicHeaderV2(FindPeriodicHeaderV2 findPeriodicHeaderV2) throws ParseException {
//
//        Dataset<Row> periodicHeaderV2Query = sparkSession.sql("Select "
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCode, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "CC_TYP_ID as cycleCountTypeId, "
//                + "CC_NO as cycleCountNo, "
//                + "STATUS_ID as statusId, "
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
//                + "CC_CNT_BY as countedBy, "
//                + "CC_CNT_ON as countedOn, "
//                + "CC_CNF_BY as confirmedBy, "
//                + "CC_CNF_ON as confirmedOn, "
//
//                // V2 fields
//                + "C_TEXT as companyDescription, "
//                + "PLANT_TEXT as plantDescription, "
//                + "WH_TEXT as warehouseDescription, "
//                + "STATUS_TEXT as statusDescription, "
//                + "MIDDLEWARE_ID as middlewareId, "
//                + "MIDDLEWARE_TABLE as middlewareTable, "
//                + "REF_DOC_TYPE as referenceDocumentType, "
//                + "REF_CC_NO as referenceCycleCountNo "
//                + "From tblperiodicheaderv5 Where IS_DELETED = 0 "
//        );
////        periodicHeaderV2Query.cache();
//
//        if (findPeriodicHeaderV2.getWarehouseId() != null && !findPeriodicHeaderV2.getWarehouseId().isEmpty()) {
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("WH_ID").isin(findPeriodicHeaderV2.getWarehouseId().toArray()));
//        }
//        if (findPeriodicHeaderV2.getCycleCountTypeId() != null && !findPeriodicHeaderV2.getCycleCountTypeId().isEmpty()) {
//            List<String> cycleCountTypeIdStrings = findPeriodicHeaderV2.getCycleCountTypeId().stream().map(String::valueOf).collect(Collectors.toList());
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("CC_TYP_ID").isin(cycleCountTypeIdStrings.toArray()));
//        }
//        if (findPeriodicHeaderV2.getCycleCountNo() != null && !findPeriodicHeaderV2.getCycleCountNo().isEmpty()) {
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("CC_NO").isin(findPeriodicHeaderV2.getCycleCountNo().toArray()));
//        }
//        if (findPeriodicHeaderV2.getHeaderStatusId() != null && !findPeriodicHeaderV2.getHeaderStatusId().isEmpty()) {
//            List<String> headerStatusIdStrings = findPeriodicHeaderV2.getHeaderStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("STATUS_ID").isin(headerStatusIdStrings.toArray()));
//        }
//        if (findPeriodicHeaderV2.getCreatedBy() != null && !findPeriodicHeaderV2.getCreatedBy().isEmpty()) {
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("CC_CTD_BY").isin(findPeriodicHeaderV2.getCreatedBy().toArray()));
//        }
//        if (findPeriodicHeaderV2.getStartCreatedOn() != null) {
//            Date startDate = findPeriodicHeaderV2.getStartCreatedOn();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("CC_CTD_ON").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findPeriodicHeaderV2.getEndCreatedOn() != null) {
//            Date endDate = findPeriodicHeaderV2.getEndCreatedOn();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("CC_CTD_ON").$less$eq(dateFormat.format(endDate)));
//        }
//
//        // V2 fields
//        if (findPeriodicHeaderV2.getLanguageId() != null && !findPeriodicHeaderV2.getLanguageId().isEmpty()) {
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("LANG_ID").isin(findPeriodicHeaderV2.getLanguageId().toArray()));
//        }
//        if (findPeriodicHeaderV2.getCompanyCodeId() != null && !findPeriodicHeaderV2.getCompanyCodeId().isEmpty()) {
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("C_ID").isin(findPeriodicHeaderV2.getCompanyCodeId().toArray()));
//        }
//        if (findPeriodicHeaderV2.getPlantId() != null && !findPeriodicHeaderV2.getPlantId().isEmpty()) {
//            periodicHeaderV2Query = periodicHeaderV2Query.filter(col("PLANT_ID").isin(findPeriodicHeaderV2.getPlantId().toArray()));
//        }
//
//
//        Encoder<PeriodicHeaderV2> periodicHeaderEncoder = Encoders.bean(PeriodicHeaderV2.class);
//        Dataset<PeriodicHeaderV2> datasetControlGroup = periodicHeaderV2Query.as(periodicHeaderEncoder);
//        List<PeriodicHeaderV2> results = datasetControlGroup.collectAsList();
//        return results;
//    }
//}