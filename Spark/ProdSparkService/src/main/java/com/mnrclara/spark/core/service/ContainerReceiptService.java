//package com.mnrclara.spark.core.service;
//
//
//
//import com.mnrclara.spark.core.model.ContainerReceipt;
//import com.mnrclara.spark.core.model.FindContainerReceipt;
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
//import static org.apache.spark.sql.functions.col;
//
//@Service
//@Slf4j
//public class ContainerReceiptService {
//
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public ContainerReceiptService() throws ParseException {
//        //connection properties
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "z4U5P@u$LWJiVkuuOR");
//        sparkSession = SparkSession.builder().master("local[*]").appName("PutAwayHeader.com") .config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://13.234.179.29;databaseName=WMS", "tblcontainerreceipt", connProp)
//                .repartition(16)
//                .cache();
//        df2.createOrReplaceTempView("tblcontainerreceipt");
//
//    }
//
//    public List<ContainerReceipt> findContainerReceipt(FindContainerReceipt findContainerReceipt) throws ParseException{
//        Dataset<Row>
//        containerReceiptQuery =sparkSession.sql( "SELECT " +
//                " LANG_ID as languageId, " +
//                " C_ID as companyCodeId, " +
//                " PLANT_ID as plantId, " +
//                " WH_ID as warehouseId, " +
//                " PRE_IB_NO as preInboundNo, " +
//                " REF_DOC_NO as refDocNumber, " +
//                " CONT_REC_NO as containerReceiptNo, " +
//                " CONT_REC_DATE as containerReceivedDate, " +
//                " CONT_NO as containerNo, " +
//                " STATUS_ID as statusId, " +
//                " CONT_TYP as containerType, " +
//                " PARTNER_CODE as partnerCode, " +
//                " INV_NO as invoiceNo, " +
//                " CONS_TYPE as consignmentType, " +
//                " ORIGIN as origin, " +
//                " PAL_QTY as numberOfPallets, " +
//                " CASE_NO as numberOfCases, " +
//                " DOCK_ALL_NO as dockAllocationNo, " +
//                " REMARK as remarks, " +
//                " IS_DELETED as deletionIndicator, " +
//                " REF_FIELD_1 as referenceField1, " +
//                " REF_FIELD_2 as referenceField2, " +
//                " REF_FIELD_3 as referenceField3, " +
//                " REF_FIELD_4 as referenceField4, " +
//                " REF_FIELD_5 as referenceField5, " +
//                " REF_FIELD_6 as referenceField6, " +
//                " REF_FIELD_7 as referenceField7, " +
//                " REF_FIELD_8 as referenceField8, " +
//                " REF_FIELD_9 as referenceField9, " +
//                " REF_FIELD_10 as referenceField10, " +
//                " CTD_BY as createdBy, " +
//                " CTD_ON as createdOn, " +
//                " UTD_BY as updatedBy, " +
//                " UTD_ON as updatedOn " +
//                " FROM tblcontainerreceipt WHERE IS_DELETED = 0 ");
//
//        containerReceiptQuery.cache();
//
//        if (findContainerReceipt.getWarehouseId() != null && !findContainerReceipt.getWarehouseId().isEmpty()) {
//            containerReceiptQuery = containerReceiptQuery.filter(col("WH_ID").isin(findContainerReceipt.getWarehouseId().toArray()));
//        }
//        if (findContainerReceipt.getContainerReceiptNo() != null && !findContainerReceipt.getContainerReceiptNo().isEmpty()) {
//            containerReceiptQuery = containerReceiptQuery.filter(col("CONT_REC_NO").isin(findContainerReceipt.getContainerReceiptNo().toArray()));
//        }
//        if (findContainerReceipt.getContainerNo() != null && !findContainerReceipt.getContainerNo().isEmpty()) {
//            containerReceiptQuery = containerReceiptQuery.filter(col("CONT_NO").isin(findContainerReceipt.getContainerNo().toArray()));
//        }
//        if (findContainerReceipt.getPartnerCode() != null && !findContainerReceipt.getPartnerCode().isEmpty()) {
//            containerReceiptQuery = containerReceiptQuery.filter(col("PARTNER_CODE").isin(findContainerReceipt.getPartnerCode().toArray()));
//        }
//        if (findContainerReceipt.getUnloadedBy() != null && !findContainerReceipt.getUnloadedBy().isEmpty()) {
//            containerReceiptQuery = containerReceiptQuery.filter(col("REF_FIELD_1").isin(findContainerReceipt.getUnloadedBy().toArray()));
//        }
//        if (findContainerReceipt.getStatusId() != null && !findContainerReceipt.getStatusId().isEmpty()) {
//           List<String> statusString = findContainerReceipt.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            containerReceiptQuery = containerReceiptQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
//        }
//        if (findContainerReceipt.getStartContainerReceivedDate() != null) {
//            Date startContainerReceiveDate = findContainerReceipt.getStartContainerReceivedDate();
//            startContainerReceiveDate = org.apache.commons.lang3.time.DateUtils.truncate(startContainerReceiveDate, Calendar.DAY_OF_MONTH);
//            containerReceiptQuery = containerReceiptQuery.filter(col("CONT_REC_DATE").$greater$eq(dateFormat.format(startContainerReceiveDate)));
//        }
//        if (findContainerReceipt.getEndContainerReceivedDate() != null) {
//            Date endContainerReceiveDate = findContainerReceipt.getEndContainerReceivedDate();
//            endContainerReceiveDate = org.apache.commons.lang3.time.DateUtils.ceiling(endContainerReceiveDate, Calendar.DAY_OF_MONTH);
//            containerReceiptQuery = containerReceiptQuery.filter(col("CONT_REC_DATE").$less$eq(dateFormat.format(endContainerReceiveDate)));
//        }
//        if(findContainerReceipt.getFromCreatedOn() != null){
//            Date fromCreatedOn = findContainerReceipt.getFromCreatedOn();
//            fromCreatedOn = org.apache.commons.lang3.time.DateUtils.truncate(fromCreatedOn, Calendar.DAY_OF_MONTH);
//            containerReceiptQuery = containerReceiptQuery.filter(col("CTD_ON").$greater$eq(dateFormat.format(fromCreatedOn)));
//        }
//        if(findContainerReceipt.getToCreatedOn() != null){
//            Date toCreatedOn = findContainerReceipt.getToCreatedOn();
//            toCreatedOn = org.apache.commons.lang3.time.DateUtils.truncate(toCreatedOn, Calendar.DAY_OF_MONTH);
//            containerReceiptQuery = containerReceiptQuery.filter(col("CTD_ON").$less$eq(dateFormat.format(toCreatedOn)));
//        }
//        Encoder<ContainerReceipt> containerReceiptEncoder = Encoders.bean(ContainerReceipt.class);
//        Dataset<ContainerReceipt> dataSetControlGroup = containerReceiptQuery.as(containerReceiptEncoder);
//        List<ContainerReceipt> result = dataSetControlGroup.collectAsList();
//        containerReceiptQuery.unpersist();
//
//        return result;
//    }
//
//}