//package com.mnrclara.spark.core.service.wmsindus;
//
//import com.mnrclara.spark.core.model.wmscorev2.FindInboundHeaderV2;
//import com.mnrclara.spark.core.model.wmscorev2.InboundHeaderV2;
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
//public class SparkInboundHeaderIndusServiceV2 {
//
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public SparkInboundHeaderIndusServiceV2() throws ParseException {
//        //connection properties
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
//        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMF", "tblinboundheader", connProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tblinboundheaderv2");
//
//        val df3 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMF", "tblinboundline", connProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tblinboundlinev2");
//
//    }
//
//    /**
//     * @param findInboundHeader
//     * @return
//     * @throws ParseException
//     */
//    public List<InboundHeaderV2> findInboundHeader(FindInboundHeaderV2 findInboundHeader) throws ParseException {
//
//
//        Dataset<Row> imPreInboundHeaderQuery = sparkSession.sql("SELECT "
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCode, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "REF_DOC_NO as refDocNumber, "
//                + "PRE_IB_NO as preInboundNo, "
//                + "STATUS_ID as statusId, "
//                + "IB_ORD_TYP_ID as inboundOrderTypeId, "
//                + "CONT_NO as containerNo, "
//                + "VEH_NO as vechicleNo, "
//                + "IB_TEXT as headerText, "
//                + "IS_DELETED as deletionIndicator, "
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
//                + "CTD_BY as createdBy, "
//                + "CTD_ON as createdOn, "
//                + "UTD_BY as updatedBy, "
//                + "UTD_ON as updatedOn, "
//                + "IB_CNF_BY as confirmedBy, "
//                + "IB_CNF_ON as confirmedOn, "
//                + "C_TEXT as companyDescription, "
//                + "PLANT_TEXT as plantDescription, "
//                + "WH_TEXT as warehouseDescription, "
//                + "STATUS_TEXT as statusDescription, "
//                + "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, "
//                + "MIDDLEWARE_ID as middlewareId, "
//                + "MIDDLEWARE_TABLE as middlewareTable, "
//                + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
//                + "REF_DOC_TYPE as referenceDocumentType, "
//                + "COUNT_OF_ORD_LINES as countOfOrderLines, "
//                + "RECEIVED_LINES as receivedLines "
//                + "FROM tblinboundheaderv2 WHERE IS_DELETED =0 ");
//
//        //  + "COUNT(tblinboundlinev2.ref_doc_no) as countOfOrderLines, "
//        //                + "RECEIVED_LINES as receivedLines "
//        //                + "FROM tblinboundheaderv2 "
//        //                + "LEFT JOIN tblinboundlinev2 ON tblinboundlinev2.ref_doc_no = tblinboundheaderv2.REF_DOC_NO AND tblinboundlinev2.is_deleted = 0 "
//        //                + "WHERE IS_DELETED = 0 ");
//
//        if (findInboundHeader.getWarehouseId() != null && !findInboundHeader.getWarehouseId().isEmpty()) {
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("WH_ID").isin(findInboundHeader.getWarehouseId().toArray()));
//        }
//        if (findInboundHeader.getContainerNo() != null && !findInboundHeader.getContainerNo().isEmpty()) {
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("CONT_NO").isin(findInboundHeader.getContainerNo().toArray()));
//        }
//        if (findInboundHeader.getStatusId() != null && !findInboundHeader.getStatusId().isEmpty()) {
//            List<String> statusIdStrings = findInboundHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
//        }
//        if (findInboundHeader.getInboundOrderTypeId() != null && !findInboundHeader.getInboundOrderTypeId().isEmpty()) {
//            List<String> inboundIdStrings = findInboundHeader.getInboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("IB_ORD_TYP_ID").isin(inboundIdStrings.toArray()));
//        }
//        if (findInboundHeader.getRefDocNumber() != null && !findInboundHeader.getRefDocNumber().isEmpty()) {
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("REF_DOC_NO").isin(findInboundHeader.getRefDocNumber().toArray()));
//        }
//        //v2 fields
//        if (findInboundHeader.getCompanyCodeId() != null && !findInboundHeader.getCompanyCodeId().isEmpty()) {
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("C_ID").isin(findInboundHeader.getCompanyCodeId().toArray()));
//        }
//        if (findInboundHeader.getPlantId() != null && !findInboundHeader.getPlantId().isEmpty()) {
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("PLANT_ID").isin(findInboundHeader.getPlantId().toArray()));
//        }
//        if (findInboundHeader.getLanguageId() != null && !findInboundHeader.getLanguageId().isEmpty()) {
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("LANG_ID").isin(findInboundHeader.getLanguageId().toArray()));
//        }
//
//        if (findInboundHeader.getStartCreatedOn() != null) {
//            Date startDate = findInboundHeader.getStartCreatedOn();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("CTD_ON").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findInboundHeader.getEndCreatedOn() != null) {
//            Date endDate = findInboundHeader.getEndCreatedOn();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("CTD_ON").$less$eq(dateFormat.format(endDate)));
//        }
//
////        String refDocNumber = findInboundHeader.getRefDocNumber().get(0);
////        Long countOfOrderLines = getCountOfTheOrderLinesByRefDocNumber(refDocNumber);
////
////        Long countOfReceiveLine = getReceivedLinesByRefDocNumber(refDocNumber);
//
//
//        Encoder<InboundHeaderV2> inboundHeaderV2Encoder = Encoders.bean(InboundHeaderV2.class);
//        Dataset<InboundHeaderV2> dataSetControlGroup = imPreInboundHeaderQuery.as(inboundHeaderV2Encoder);
//        List<InboundHeaderV2> result = dataSetControlGroup.collectAsList();
//
////        //Set Count Of OrderLine
////        result.forEach(inboundHeaderV2 -> inboundHeaderV2.setCountOfOrderLines(countOfOrderLines));
////        result.forEach(inboundHeaderV2 -> inboundHeaderV2.setReceivedLines(countOfReceiveLine));
//        return result;
//    }
//
//    //OrderLine
//    public Long getCountOfTheOrderLinesByRefDocNumber(String refDocNumber) {
//        String sqlQuery = "SELECT " +
//                "CASE " +
//                "WHEN OrderLinesCount IS NOT NULL THEN OrderLinesCount " +
//                "ELSE 0 " +
//                "END AS countOfOrderLines " +
//                "FROM " +
//                "(SELECT COUNT(*) AS OrderLinesCount " +
//                "FROM tblinboundlinev2 " +
//                "WHERE ref_doc_no IN ('" + refDocNumber + "') AND is_deleted = 0) AS CountsSubquery";
//
//        Dataset<Row> orderLinesCountDF = sparkSession.sql(sqlQuery);
//        Long countOfOrderLines = orderLinesCountDF.head().getLong(0);
//
//        return countOfOrderLines;
//    }
//
//    //Received Line
//    public Long getReceivedLinesByRefDocNumber(String refDocNumber) {
//
//        // Define the Spark SQL query to calculate receivedLines
//        String sqlQuery = "SELECT " +
//                "CASE WHEN SUM(accept_qty + damage_qty) > 0 " +
//                "THEN (SELECT COUNT(*) FROM inboundline " +
//                "WHERE ref_doc_no IN ('" + refDocNumber + "') AND is_deleted = 0) " +
//                "ELSE 0 END as receivedLines";
//
//        // Execute the Spark SQL query
//        Dataset<Row> resultDF = sparkSession.sql(sqlQuery);
//
//        // Extract the result as Long
//        Long receivedLines = resultDF.head().getLong(0);
//
//        return receivedLines;
//    }
//
//}