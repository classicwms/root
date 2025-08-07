//package com.mnrclara.spark.core.service.almailem;
//
//import com.mnrclara.spark.core.model.Almailem.BusinessPartnerV2;
//import com.mnrclara.spark.core.model.Almailem.FindBusinessPartner;
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
//public class SparkBusinessPartnerService {
//
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public SparkBusinessPartnerService() throws ParseException {
//        //connection properties
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "AL.5p6^VjhzZm0t|A25n^m9D1H");
//        sparkSession = SparkSession.builder().master("local[*]").appName("ContainerReceipt.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://10.0.2.8;databaseName=WMS_ALMPRD", "tblbusinesspartner", connProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tblbusinesspartnerv2");
//
//    }
//
//
//    // BusinessPartner
//    public List<BusinessPartnerV2> findBusinessPartner(FindBusinessPartner findBusinessPartner) throws ParseException {
//        Dataset<Row>
//                businessPartnerQuery = sparkSession.sql("SELECT " +
//                " LANG_ID as languageId, " +
//                " C_ID as companyCodeId, " +
//                " PLANT_ID as plantId, " +
//                " WH_ID as warehouseId, " +
//                " PARTNER_TYP as businessPartnerType, " +
//                " PARTNER_CODE as partnerCode, " +
//                " PARTNER_NM as partnerName, " +
//                " STATUS_ID as statusId, " +
//                " CTD_BY as createdBy, " +
//                " CTD_ON as createdOn " +
//                " FROM tblbusinesspartnerv2 WHERE IS_DELETED = 0 ");
//
//
//        if (findBusinessPartner.getLanguageId() != null && !findBusinessPartner.getLanguageId().isEmpty()) {
//            businessPartnerQuery = businessPartnerQuery.filter(col("LANG_ID").isin(findBusinessPartner.getLanguageId().toArray()));
//        }
//        if (findBusinessPartner.getCompanyCodeId() != null && !findBusinessPartner.getCompanyCodeId().isEmpty()) {
//            businessPartnerQuery = businessPartnerQuery.filter(col("C_ID").isin(findBusinessPartner.getCompanyCodeId().toArray()));
//        }
//        if (findBusinessPartner.getPlantId() != null && !findBusinessPartner.getPlantId().isEmpty()) {
//            businessPartnerQuery = businessPartnerQuery.filter(col("PLANT_ID").isin(findBusinessPartner.getPlantId().toArray()));
//        }
//        if (findBusinessPartner.getWarehouseId() != null && !findBusinessPartner.getWarehouseId().isEmpty()) {
//            businessPartnerQuery = businessPartnerQuery.filter(col("WH_ID").isin(findBusinessPartner.getWarehouseId().toArray()));
//        }
//        if (findBusinessPartner.getPartnerCode() != null && !findBusinessPartner.getPartnerCode().isEmpty()) {
//            businessPartnerQuery = businessPartnerQuery.filter(col("PARTNER_CODE").isin(findBusinessPartner.getPartnerCode().toArray()));
//        }
//        if (findBusinessPartner.getPartnerName() != null && !findBusinessPartner.getPartnerName().isEmpty()) {
//            businessPartnerQuery = businessPartnerQuery.filter(col("PARTNER_NM").isin(findBusinessPartner.getPartnerName().toArray()));
//        }
//        if (findBusinessPartner.getCreatedBy() != null && !findBusinessPartner.getCreatedBy().isEmpty()) {
//            businessPartnerQuery = businessPartnerQuery.filter(col("CTD_BY").isin(findBusinessPartner.getCreatedBy().toArray()));
//        }
//        if (findBusinessPartner.getBusinessPartnerType() != null && !findBusinessPartner.getBusinessPartnerType().isEmpty()) {
//            List<String> businessString = findBusinessPartner.getBusinessPartnerType().stream().map(String::valueOf).collect(Collectors.toList());
//            businessPartnerQuery = businessPartnerQuery.filter(col("PARTNER_TYP").isin(businessString.toArray()));
//        }
//        if (findBusinessPartner.getStatusId() != null && !findBusinessPartner.getStatusId().isEmpty()) {
//            List<String> statusString = findBusinessPartner.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            businessPartnerQuery = businessPartnerQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
//        }
//        if (findBusinessPartner.getStartCreatedOn() != null) {
//            Date startCreatedOn = findBusinessPartner.getStartCreatedOn();
//            startCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(startCreatedOn, Calendar.DAY_OF_MONTH);
//            businessPartnerQuery = businessPartnerQuery.filter(col("CTD_ON").$greater$eq(dateFormat.format(startCreatedOn)));
//        }
//        if (findBusinessPartner.getEndCreatedOn() != null) {
//            Date endCreatedOn = findBusinessPartner.getEndCreatedOn();
//            endCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(endCreatedOn, Calendar.DAY_OF_MONTH);
//            businessPartnerQuery = businessPartnerQuery.filter(col("CTD_ON").$less$eq(dateFormat.format(endCreatedOn)));
//        }
//        if (findBusinessPartner.getStartUpdatedOn() != null) {
//            Date startUpdatedOn = findBusinessPartner.getStartUpdatedOn();
//            startUpdatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(startUpdatedOn, Calendar.DAY_OF_MONTH);
//            businessPartnerQuery = businessPartnerQuery.filter(col("UTD_ON").$greater$eq(dateFormat.format(startUpdatedOn)));
//        }
//        if (findBusinessPartner.getEndUpdatedOn() != null) {
//            Date endUpdatedOn = findBusinessPartner.getEndUpdatedOn();
//            endUpdatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(endUpdatedOn, Calendar.DAY_OF_MONTH);
//            businessPartnerQuery = businessPartnerQuery.filter(col("UTD_ON").$less$eq(dateFormat.format(endUpdatedOn)));
//        }
//        Encoder<BusinessPartnerV2> businessPartnerV2Encoder = Encoders.bean(BusinessPartnerV2.class);
//        Dataset<BusinessPartnerV2> businessPartnerV2Dataset = businessPartnerQuery.as(businessPartnerV2Encoder);
//
//        List<BusinessPartnerV2> result = businessPartnerV2Dataset.collectAsList();
//
//        return result;
//    }
//
//}
