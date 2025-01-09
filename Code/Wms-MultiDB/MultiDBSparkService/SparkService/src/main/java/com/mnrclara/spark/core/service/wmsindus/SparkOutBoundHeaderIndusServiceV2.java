//package com.mnrclara.spark.core.service.wmsindus;
//
//import com.mnrclara.spark.core.model.wmscorev2.FindOutBoundHeaderV2;
//import com.mnrclara.spark.core.model.wmscorev2.OutBoundHeaderV2;
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
//public class SparkOutBoundHeaderIndusServiceV2 {
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public SparkOutBoundHeaderIndusServiceV2() throws ParseException {
//        // connection properties
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
//        sparkSession = SparkSession.builder().master("local[*]").appName("OutBoundHeader.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read()
//                .option("fetchSize", "10000").jdbc("jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMF", "tbloutboundheader", connProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tbloutboundheaderv5");
//    }
//
//    /**
//     *
//     * @param findOutBoundHeaderV2
//     * @return
//     * @throws ParseException
//     */
//    public List<OutBoundHeaderV2> findOutBoundHeaderV2(FindOutBoundHeaderV2 findOutBoundHeaderV2) throws ParseException {
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
//                + "DLV_REV_ON as reversedOn, "
//
//                // V2 fields
//                + "INVOICE_NO as invoiceNumber, "
//                + "C_TEXT as companyDescription, "
//                + "PLANT_TEXT as plantDescription, "
//                + "WH_TEXT as warehouseDescription, "
//                + "STATUS_TEXT as statusDescription, "
//                + "MIDDLEWARE_ID as middlewareId, "
//                + "MIDDLEWARE_TABLE as middlewareTable, "
//                + "SALES_ORDER_NUMBER as salesOrderNumber, "
//                + "SALES_INVOICE_NUMBER as salesInvoiceNumber, "
//                + "PICK_LIST_NUMBER as pickListNumber, "
//                + "INVOICE_DATE as invoiceDate, "
//                + "DELIVERY_TYPE as deliveryType, "
//                + "CUSTOMER_ID as customerId, "
//                + "CUSTOMER_NAME as customerName, "
//                + "ADDRESS as address, "
//                + "PHONE_NUMBER as phoneNumber, "
//                + "ALTERNATE_NO as alternateNo, "
//                + "STATUS as status, "
//                + "SUPPLIER_INVOICE_NO as supplierInvoiceNo, "
//                + "TOKEN_NUMBER as tokenNumber, "
//                + "FROM_BRANCH_CODE as fromBranchCode, "
//                + "IS_COMPLETED as isCompleted, "
//                + "IS_CANCELLED as isCancelled, "
//                + "M_UPDATED_ON as mUpdatedOn, "
//                + "PICK_LINE_COUNT as countOfPickedLine, "
//                + "SUM_PICK_QTY as sumOfPickedQty "
//                + "FROM tbloutboundheaderv5 WHERE IS_DELETED = 0 ");
//
////        imOutBoundHeaderQuery.cache();
//
//
//        if (findOutBoundHeaderV2.getWarehouseId() != null && !findOutBoundHeaderV2.getWarehouseId().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("WH_ID").isin(findOutBoundHeaderV2.getWarehouseId().toArray()));
//        }
//        if (findOutBoundHeaderV2.getRefDocNumber() != null && !findOutBoundHeaderV2.getRefDocNumber().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("REF_DOC_NO").isin(findOutBoundHeaderV2.getRefDocNumber().toArray()));
//        }
//        if (findOutBoundHeaderV2.getPartnerCode() != null && !findOutBoundHeaderV2.getPartnerCode().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("PARTNER_CODE").isin(findOutBoundHeaderV2.getPartnerCode().toArray()));
//        }
//        if (findOutBoundHeaderV2.getSoType() != null && !findOutBoundHeaderV2.getSoType().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("REF_FIELD_1").isin(findOutBoundHeaderV2.getSoType().toArray()));
//        }
//        if (findOutBoundHeaderV2.getStatusId() != null && !findOutBoundHeaderV2.getStatusId().isEmpty()) {
//            List<String> statusIdStrings = findOutBoundHeaderV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
//        }
//        if (findOutBoundHeaderV2.getOutboundOrderTypeId() != null && !findOutBoundHeaderV2.getOutboundOrderTypeId().isEmpty()) {
//            List<String> outboundOrderTypeIdStrings = findOutBoundHeaderV2.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("OB_ORD_TYP_ID").isin(outboundOrderTypeIdStrings.toArray()));
//        }
//        if (findOutBoundHeaderV2.getStartRequiredDeliveryDate() != null) {
//            Date startDate = findOutBoundHeaderV2.getStartRequiredDeliveryDate();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("REQ_DEL_DATE").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findOutBoundHeaderV2.getEndRequiredDeliveryDate() != null) {
//            Date endDate = findOutBoundHeaderV2.getEndRequiredDeliveryDate();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("REQ_DEL_DATE").$less$eq(dateFormat.format(endDate)));
//        }
//        if (findOutBoundHeaderV2.getStartDeliveryConfirmedOn() != null) {
//            Date startDate = findOutBoundHeaderV2.getStartDeliveryConfirmedOn();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("DLV_CNF_ON").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findOutBoundHeaderV2.getEndDeliveryConfirmedOn() != null) {
//            Date endDate = findOutBoundHeaderV2.getEndDeliveryConfirmedOn();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("DLV_CNF_ON").$less$eq(dateFormat.format(endDate)));
//        }
//
//        // V2 fields
//        if (findOutBoundHeaderV2.getLanguageId() != null && !findOutBoundHeaderV2.getLanguageId().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("LANG_ID").isin(findOutBoundHeaderV2.getLanguageId().toArray()));
//        }
//        if (findOutBoundHeaderV2.getCompanyCodeId() != null && !findOutBoundHeaderV2.getCompanyCodeId().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("C_ID").isin(findOutBoundHeaderV2.getCompanyCodeId().toArray()));
//        }
//        if (findOutBoundHeaderV2.getPlantId() != null && !findOutBoundHeaderV2.getPlantId().isEmpty()) {
//            imOutBoundHeaderQuery = imOutBoundHeaderQuery.filter(col("PLANT_ID").isin(findOutBoundHeaderV2.getPlantId().toArray()));
//        }
//
//        Encoder<OutBoundHeaderV2> outBoundHeaderEncoder = Encoders.bean(OutBoundHeaderV2.class);
//        Dataset<OutBoundHeaderV2> dataSetControlGroup = imOutBoundHeaderQuery.as(outBoundHeaderEncoder);
//        List<OutBoundHeaderV2> result = dataSetControlGroup.collectAsList();
//
//        return result;
//
//    }
//}