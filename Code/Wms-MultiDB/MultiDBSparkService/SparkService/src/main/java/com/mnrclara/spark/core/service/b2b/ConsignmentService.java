//package com.mnrclara.spark.core.service.b2b;
//
//
//import com.mnrclara.spark.core.model.b2b.Consignment;
//import com.mnrclara.spark.core.model.b2b.FindConsignment;
//import com.mnrclara.spark.core.util.DateUtils;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.sql.Timestamp;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
//import org.apache.spark.sql.functions;
//
//import static org.apache.spark.sql.functions.col;
//
//
//@Service
//@Slf4j
//public class ConsignmentService {
//
//
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//
//    Dataset<Row> consignment= null;
//
//    Dataset<Row> filter =null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public ConsignmentService() throws ParseException {
//        //connection properties
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "z4U5P@u$LWJiVkuuOR");
//        sparkSession = SparkSession.builder().master("local[*]").appName("Consignment.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//         consignment = sparkSession.read().jdbc("jdbc:sqlserver://13.234.179.29;databaseName=IWE", "tblconsignment3", connProp)
//                .repartition(16);
////        consignment.createOrReplaceTempView("tblconsignment3");
//    }
//
//    /**
//     *
//     * @param findConsignment
//     * @return
//     * @throws ParseException
//     */
//    public List<Consignment> findConsignment(FindConsignment findConsignment) throws ParseException {
//
//
////        Dataset<Row> consignmentQuery = sparkSession.sql("SELECT " +
////                "CONSIGNMENT_ID AS consignmentId, " +
////                "REFERENCE_NUMBER AS referenceNumber, " +
////                "COD_AMOUNT AS codAmount," +
////                "COD_COLLECTION_MODE AS codCollectionMode," +
////                "CUSTOMER_CODE AS customerCode," +
////                "SERVICE_TYPE_ID AS serviceTypeId," +
////                "CONSIGNMENT_TYPE AS consignmentType," +
////                "LOAD_TYPE AS loadType," +
////                "DESCRIPTION AS description," +
////                "COD_FAVOR_OF AS codFavorOf," +
////                "DIMENSION_UNIT AS dimensionUnit," +
////                "LENGTH AS length," +
////                "WIDTH AS width," +
////                "HEIGHT AS height," +
////                "WEIGHT_UNIT AS weightUnit," +
////                "WEIGHT AS weight, " +
////                "DECLARED_VALUE AS declaredValue," +
////                "NUM_PIECES AS numPieces," +
////                "NOTES AS notes," +
////                "CUSTOMER_REFERENCE_NUMBER AS customerReferenceNumber," +
////                "IS_RISK_SURCHARGE_APPLICABLE AS isRiskSurchargeApplicable," +
////                "CREATED_AT AS createdAt," +
////                "STATUS_DESCRIPTION AS statusDescription," +
////                "CUSTOMER_CIVIL_ID AS customerCivilId," +
////                "RECEIVER_CIVIL_ID AS receiverCivilId," +
////                "CURRENCY AS currency," +
////                "AWB_3RD_PARTY AS awb3rdParty," +
////                "SCANTYPE_3RD_PARTY AS scanType3rdParty," +
////                "HUBCODE_3RD_PARTY AS hubCode3rdParty," +
////                "ORDER_TYPE AS orderType," +
////                "JNT_PUSH_STATUS AS jntPushStatus," +
////                "BOUTIQAAT_PUSH_STATUS AS boutiqaatPushStatus," +
////                "IS_AWB_PRINTED AS isAwbPrinted " +
////                "FROM tblconsignment");
////
////
////        if (findConsignment.getConsignmentId() != null && !findConsignment.getConsignmentId().isEmpty()) {
////            List<String> consignmentIdStrings = findConsignment.getConsignmentId().stream().map(String::valueOf).collect(Collectors.toList());
////            consignmentQuery = consignmentQuery.filter(col("CONSIGNMENT_ID").isin(consignmentIdStrings.toArray()));
////        }
////        if (findConsignment.getReferenceNumber() != null && !findConsignment.getReferenceNumber().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("REFERENCE_NUMBER").isin(findConsignment.getReferenceNumber().toArray()));
////        }
////        if (findConsignment.getCustomerCode() != null && !findConsignment.getCustomerCode().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("CUSTOMER_CODE").isin(findConsignment.getCustomerCode().toArray()));
////        }
////        if (findConsignment.getServiceTypeId() != null && !findConsignment.getServiceTypeId().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("SERVICE_TYPE_ID").isin(findConsignment.getServiceTypeId().toArray()));
////        }
////        if (findConsignment.getConsignmentType() != null && !findConsignment.getConsignmentType().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("CONSIGNMENT_TYPE").isin(findConsignment.getConsignmentType().toArray()));
////        }
////        if (findConsignment.getCustomerReferenceNumber() != null && !findConsignment.getCustomerReferenceNumber().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("CUSTOMER_REFERENCE_NUMBER").isin(findConsignment.getCustomerReferenceNumber().toArray()));
////        }
////        if (findConsignment.getCustomerCivilId() != null && !findConsignment.getCustomerCivilId().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("CUSTOMER_CIVIL_ID").isin(findConsignment.getCustomerCivilId().toArray()));
////        }
////        if (findConsignment.getAwb3rdParty() != null && !findConsignment.getAwb3rdParty().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("AWB_3RD_PARTY").isin(findConsignment.getAwb3rdParty().toArray()));
////        }
////        if (findConsignment.getScanType3rdParty() != null && !findConsignment.getScanType3rdParty().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("SCANTYPE_3RD_PARTY").isin(findConsignment.getScanType3rdParty().toArray()));
////        }
////        if (findConsignment.getHubCode3rdParty() != null && !findConsignment.getHubCode3rdParty().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("HUBCODE_3RD_PARTY").isin(findConsignment.getHubCode3rdParty().toArray()));
////        }
////        if (findConsignment.getOrderType() != null && !findConsignment.getOrderType().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("ORDER_TYPE").isin(findConsignment.getOrderType().toArray()));
////        }
////        if (findConsignment.getBoutiqaatPushStatus() != null && !findConsignment.getBoutiqaatPushStatus().isEmpty()) {
////            consignmentQuery = consignmentQuery.filter(col("BOUTIQAAT_PUSH_STATUS").isin(findConsignment.getBoutiqaatPushStatus().toArray()));
////        }
////        if (findConsignment.getStartDate() != null) {
////            Date startDate = findConsignment.getStartDate();
////            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
////            consignmentQuery = consignmentQuery.filter(col("CREATED_AT").$greater$eq(dateFormat.format(startDate)));
////        }
////
////        if (findConsignment.getEndDate() != null) {
////            Date endDate = findConsignment.getEndDate();
////            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
////            consignmentQuery = consignmentQuery.filter(col("CREATED_AT").$less$eq(dateFormat.format(endDate)));
////        }
//
//        filter = consignment;
//
//        if (findConsignment.getConsignmentId() != null && !findConsignment.getConsignmentId().isEmpty()) {
//            List<String> lineNoString = findConsignment.getConsignmentId().stream().map(String::valueOf).collect(Collectors.toList());
//            filter = filter.filter(col("CONSIGNMENT_ID").isin(lineNoString.toArray()));
//        }
//        if (findConsignment.getReferenceNumber() != null && !findConsignment.getReferenceNumber().isEmpty()) {
//            filter = filter.filter(col("REFERENCE_NUMBER").isin(findConsignment.getReferenceNumber().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getCustomerCode() != null && !findConsignment.getCustomerCode().isEmpty()) {
//            filter = filter.filter(col("CUSTOMER_CODE").isin(findConsignment.getCustomerCode().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getServiceTypeId() != null && !findConsignment.getServiceTypeId().isEmpty()) {
//            filter = filter.filter(col("SERVICE_TYPE_ID").isin(findConsignment.getServiceTypeId().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getConsignmentType() != null && !findConsignment.getConsignmentType().isEmpty()) {
//            filter = filter.filter(col("CONSIGNMENT_TYPE").isin(findConsignment.getConsignmentType().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getCustomerReferenceNumber() != null && !findConsignment.getCustomerReferenceNumber().isEmpty()) {
//            filter = filter.filter(col("CUSTOMER_REFERENCE_NUMBER").isin(findConsignment.getCustomerReferenceNumber().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getCustomerCivilId() != null && !findConsignment.getCustomerCivilId().isEmpty()) {
//            filter = filter.filter(col("CUSTOMER_CIVIL_ID").isin(findConsignment.getCustomerCivilId().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getAwb3rdParty() != null && !findConsignment.getAwb3rdParty().isEmpty()) {
//            filter = filter.filter(col("AWB_3RD_PARTY").isin(findConsignment.getAwb3rdParty().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getScanType3rdParty() != null && !findConsignment.getScanType3rdParty().isEmpty()) {
//            filter = filter.filter(col("SCANTYPE_3RD_PARTY").isin(findConsignment.getScanType3rdParty().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getHubCode3rdParty() != null && !findConsignment.getHubCode3rdParty().isEmpty()) {
//            filter = filter.filter(col("HUBCODE_3RD_PARTY").isin(findConsignment.getHubCode3rdParty().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getOrderType() != null && !findConsignment.getOrderType().isEmpty()) {
//            filter = filter.filter(col("ORDER_TYPE").isin(findConsignment.getOrderType().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getBoutiqaatPushStatus() != null && !findConsignment.getBoutiqaatPushStatus().isEmpty()) {
//            filter = filter.filter(col("BOUTIQAAT_PUSH_STATUS").isin(findConsignment.getBoutiqaatPushStatus().toArray(new String[0]))).toDF();
//        }
//        if (findConsignment.getStartDate() != null && findConsignment.getEndDate() != null) {
//            Date[] dates = DateUtils.addTimeToDatesForSearch(findConsignment.getStartDate(), findConsignment.getEndDate());
//            Date startDate = dates[0];
//            Date endDate = dates[1];
//
//            Timestamp startTimestamp = new Timestamp(startDate.getTime());
//            Timestamp endTimestamp = new Timestamp(endDate.getTime());
//
//            filter = filter.filter(col("CREATED_AT").$greater$eq(startTimestamp));
//            filter = filter.filter(col("CREATED_AT").$less$eq(endTimestamp));
//        }
//
//
//
//        Encoder<Consignment> consignmentEncoder = Encoders.bean(Consignment.class);
//        Dataset<Consignment> consignmentDataset = filter.as(consignmentEncoder);
//        List<Consignment> result = consignmentDataset.collectAsList();
//        return result;
//    }
//}