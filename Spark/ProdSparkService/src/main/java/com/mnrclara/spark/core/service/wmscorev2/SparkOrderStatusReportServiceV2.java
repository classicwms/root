//package com.mnrclara.spark.core.service.wmscorev2;
//
//import com.mnrclara.spark.core.model.wmscorev2.OrderStatusReport;
//import com.mnrclara.spark.core.model.wmscorev2.SearchOrderStatusReport;
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
//public class SparkOrderStatusReportServiceV2 {
//
//    Properties conProp = new Properties();
//
//    SparkSession sparkSession = null;
//
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public SparkOrderStatusReportServiceV2() throws ParseException {
//        //connection properties
//        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        conProp.put("user", "sa");
//        conProp.put("password", "AL.8B/XoM>D3vR]Q4s41?.8hP>|");
//        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS_CORE", "tbloutboundline", conProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tbloutboundlinev2");
//    }
//
//    public List<OrderStatusReport> findOrderStatusReport(SearchOrderStatusReport searchOrderStatusReport) throws ParseException {
//
//        Dataset<Row> orderStatusReportQuery = sparkSession.sql("SELECT"
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCodeId, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "DLV_CNF_ON as deliveryConfirmedOn, "
//                + "REF_DOC_NO as soNumber, "
//                + "DLV_ORD_NO as doNumber, "
//                + "PARTNER_CODE as customerCode, "
//                + "PARTNER_NM as customerName, "
//                + "ITM_CODE as sku, "
//                + "ITEM_TEXT as skuDescription, "
//                + "ORD_QTY as orderedQty, "
//                + "DLV_QTY as deliveredQty, "
//                + "REF_DOC_DATE as orderReceivedDate, "
//                + "REQ_DEL_DATE as expectedDeliveryDate, "
//               // + " as percentageOfDelivered, "// <-------------------
//                + "STATUS_ID as statusId, "
//                + "ORDER_TYPE as orderType, "
//                + "C_TEXT as companyDescription, "
//                + "PLANT_TEXT as plantDescription, "
//                + "WH_TEXT as warehouseDescription, "
//                + "ITM_CODE as itemCode, "
//                + "REF_DOC_NO as refDocNumber"
//                + "FROM tbloutboundlinev2 WHERE IS_DELETED = 0");
//
//        orderStatusReportQuery = orderStatusReportQuery.withColumn("percentageOfDelivered",
//                functions.expr("(DLV_QTY / ORD_QTY) * 100"));
//
////        orderStatusReportQuery.cache();
//
//        if (searchOrderStatusReport.getStatusId() != null && !searchOrderStatusReport.getStatusId().isEmpty()) {
//            List<String> inboundOrderIdStrings = searchOrderStatusReport.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("STATUS_ID").isin(inboundOrderIdStrings.toArray()));
//        }
//        if (searchOrderStatusReport.getLanguageId() != null && !searchOrderStatusReport.getLanguageId().isEmpty()) {
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("LANG_ID").isin(searchOrderStatusReport.getLanguageId().toArray()));
//        }
//
//        if (searchOrderStatusReport.getCompanyCodeId() != null && !searchOrderStatusReport.getCompanyCodeId().isEmpty()) {
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("C_ID").isin(searchOrderStatusReport.getCompanyCodeId().toArray()));
//        }
//
//        if (searchOrderStatusReport.getPlantId() != null && !searchOrderStatusReport.getPlantId().isEmpty()) {
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("PLANT_ID").isin(searchOrderStatusReport.getPlantId().toArray()));
//        }
//
//        if (searchOrderStatusReport.getWarehouseId() != null && !searchOrderStatusReport.getWarehouseId().isEmpty()) {
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("WH_ID").isin(searchOrderStatusReport.getWarehouseId().toArray()));
//        }
//
//        if (searchOrderStatusReport.getPartnerCode() != null && !searchOrderStatusReport.getPartnerCode().isEmpty()) {
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("PARTNER_CODE").isin(searchOrderStatusReport.getPartnerCode().toArray()));
//        }
//        if (searchOrderStatusReport.getRefDocNumber() != null && !searchOrderStatusReport.getRefDocNumber().isEmpty()) {
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("REF_DOC_NO").isin(searchOrderStatusReport.getRefDocNumber().toArray()));
//        }
//
//        if (searchOrderStatusReport.getOrderType() != null && !searchOrderStatusReport.getOrderType().isEmpty()) {
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("REF_FIELD_1").isin(searchOrderStatusReport.getOrderType().toArray()));
//        }
//
////        if (searchOrderStatusReport.getCustomerCode() != null && !searchOrderStatusReport.getCustomerCode().isEmpty()) {
////            orderStatusReportQuery = orderStatusReportQuery.filter(col("").isin(searchOrderStatusReport.getCustomerCode().toArray()));
////        }
//
////        if (searchOrderStatusReport.getOrderNumber() != null && !searchOrderStatusReport.getOrderNumber().isEmpty()) {
////            orderStatusReportQuery = orderStatusReportQuery.filter(col("").isin(searchOrderStatusReport.getOrderNumber().toArray()));
////        }
//
//        if (searchOrderStatusReport.getItemCode() != null && !searchOrderStatusReport.getItemCode().isEmpty()) {
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("ITM_CODE").isin(searchOrderStatusReport.getItemCode().toArray()));
//        }
//
//        if (searchOrderStatusReport.getFromDeliveryDate() != null) {
//            Date startDate = searchOrderStatusReport.getFromDeliveryDate();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("DLV_CNF_ON").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (searchOrderStatusReport.getToDeliveryDate() != null) {
//            Date endDate = searchOrderStatusReport.getToDeliveryDate();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            orderStatusReportQuery = orderStatusReportQuery.filter(col("DLV_CNF_ON").$less$eq(dateFormat.format(endDate)));
//        }
//
//        Encoder<OrderStatusReport> OrderStatusReportEncoder = Encoders.bean(OrderStatusReport.class);
//        Dataset<OrderStatusReport> dataSetControlGroup = orderStatusReportQuery.as(OrderStatusReportEncoder);
//        List<OrderStatusReport> result = dataSetControlGroup.collectAsList();
//
//        return result;
//
//    }
//
//}