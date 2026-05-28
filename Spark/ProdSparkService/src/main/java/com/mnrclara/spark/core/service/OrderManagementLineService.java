//package com.mnrclara.spark.core.service;
//
//import com.mnrclara.spark.core.model.FindOrderManagementLine;
//import com.mnrclara.spark.core.model.OrderManagementLine;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.apache.spark.sql.functions.col;
//
//@Slf4j
//@Service
//public class OrderManagementLineService {
//
//    Properties conProp = new Properties();
//
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    public OrderManagementLineService() throws ParseException {
//        //connection properties
//        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        conProp.put("user", "sa");
//        conProp.put("password", "z4U5P@u$LWJiVkuuOR");
//        sparkSession = SparkSession.builder().master("local[*]").appName("OrderManagementLine.com") .config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://13.234.179.29;databaseName=WMS", "tblordermangementline", conProp)
//                .repartition(12)
//                .cache();
//        df2.createOrReplaceTempView("tblordermangementline");
//    }
//
//
//    /**
//     *
//     * @param findOrderManagementLine
//     * @return
//     * @throws ParseException
//     */
//    public List<OrderManagementLine> findOrderManagementLine(FindOrderManagementLine findOrderManagementLine)
//            throws ParseException {
//
//        Dataset<Row> imOrderManagementLineQuery = sparkSession.sql("SELECT "
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCodeId, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "PRE_OB_NO as preOutboundNo, "
//                + "REF_DOC_NO as refDocNumber, "
//                + "PARTNER_CODE as partnerCode, "
//                + "OB_LINE_NO as lineNumber, "
//                + "ITM_CODE as itemCode, "
//                + "PROP_ST_BIN as proposedStorageBin, "
//                + "PROP_PACK_BARCODE as proposedPackBarCode, "
//                + "PU_NO as pickupNumber, "
//                + "VAR_ID as variantCode, "
//                + "VAR_SUB_ID as variantSubCode, "
//                + "OB_ORD_TYP_ID as outboundOrderTypeId, "
//                + "STATUS_ID as statusId, "
//                + "STCK_TYP_ID as stockTypeId, "
//                + "SP_ST_IND_ID as specialStockIndicatorId, "
//                + "ITEM_TEXT as description, "
//                + "MFR_PART as manufacturerPartNo, "
//                + "HSN_CODE as hsnCode, "
//                + "ITM_BARCODE as itemBarcode, "
//                + "ORD_QTY as orderQty, "
//                + "ORD_UOM as orderUom, "
//                + "INV_QTY as inventoryQty, "
//                + "ALLOC_QTY as allocatedQty, "
//                + "RE_ALLOC_QTY as reAllocatedQty, "
//                + "STR_TYP_ID as strategyTypeId, "
//                + "ST_NO as strategyNo, "
//                + "REQ_DEL_DATE as requiredDeliveryDate, "
//                + "PROP_STR_NO as proposedBatchSerialNumber, "
//                + "PROP_PAL_CODE as proposedPalletCode, "
//                + "PROP_CASE_CODE as proposedCaseCode, "
//                + "PROP_HE_NO as proposedHeNo, "
//                + "PROP_PICKER_ID as proposedPicker, "
//                + "ASS_PICKER_ID as assignedPickerId, "
//                + "REASS_PICKER_ID as reassignedPickerId, "
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
//                + "RE_ALLOC_BY as reAllocatedBy, "
//                + "RE_ALLOC_ON as reAllocatedOn, "
//                + "PICK_UP_CTD_BY as pickupCreatedBy, "
//                + "PICK_UP_CTD_ON as pickupCreatedOn, "
//                + "PICK_UP_UTD_BY as pickupUpdatedBy, "
//                + "PICK_UP_UTD_ON as pickupUpdatedOn, "
//                + "PICKER_ASSIGN_BY as pickerAssignedBy, "
//                + "PICKER_ASSIGN_ON as pickerAssignedOn, "
//                + "PICKER_REASSIGN_BY as pickerReassignedBy, "
//                + "PICKER_REASSIGN_ON as pickerReassignedOn "
//                + "FROM tblordermangementline WHERE IS_DELETED = 0");
////        imOrderManagementLineQuery.cache();
//
//        if (findOrderManagementLine.getWarehouseId() != null && !findOrderManagementLine.getWarehouseId().isEmpty()) {
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("WH_ID").isin(findOrderManagementLine.getWarehouseId().toArray()));
//        }
//        if (findOrderManagementLine.getPreOutboundNo() != null && !findOrderManagementLine.getPreOutboundNo().isEmpty()) {
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("PRE_OB_NO").isin(findOrderManagementLine.getPreOutboundNo().toArray()));
//        }
//        if (findOrderManagementLine.getRefDocNumber() != null && !findOrderManagementLine.getRefDocNumber().isEmpty()) {
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("REF_DOC_NO").isin(findOrderManagementLine.getRefDocNumber().toArray()));
//        }
//        if (findOrderManagementLine.getPartnerCode() != null && !findOrderManagementLine.getPartnerCode().isEmpty()) {
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("PARTNER_CODE").isin(findOrderManagementLine.getPartnerCode().toArray()));
//        }
//        if (findOrderManagementLine.getItemCode() != null && !findOrderManagementLine.getItemCode().isEmpty()) {
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("ITM_CODE").isin(findOrderManagementLine.getItemCode().toArray()));
//        }
//        if (findOrderManagementLine.getStatusId() != null && !findOrderManagementLine.getStatusId().isEmpty()) {
//            List<String> statusIdStrings = findOrderManagementLine.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
//        }
//        if (findOrderManagementLine.getOutboundOrderTypeId() != null && !findOrderManagementLine.getOutboundOrderTypeId().isEmpty()) {
//            List<String> outboundOrderTypeIdStrings = findOrderManagementLine.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("OB_ORD_TYP_ID").isin(outboundOrderTypeIdStrings.toArray()));
//        }
//        if (findOrderManagementLine.getDescription() != null && !findOrderManagementLine.getDescription().isEmpty()) {
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("ITEM_TEXT").isin(findOrderManagementLine.getDescription().toArray()));
//        }
//        if (findOrderManagementLine.getSoType() != null && !findOrderManagementLine.getSoType().isEmpty()) {
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("REF_FIELD_1").isin(findOrderManagementLine.getSoType().toArray()));
//        }
//        if (findOrderManagementLine.getStartRequiredDeliveryDate() != null) {
//            Date startDate = findOrderManagementLine.getStartRequiredDeliveryDate();
//            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("REQ_DEL_DATE").$greater$eq(dateFormat.format(startDate)));
//        }
//        if (findOrderManagementLine.getEndRequiredDeliveryDate() != null) {
//            Date endDate = findOrderManagementLine.getEndRequiredDeliveryDate();
//            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
//            imOrderManagementLineQuery = imOrderManagementLineQuery.filter(col("REQ_DEL_DATE").$less$eq(dateFormat.format(endDate)));
//        }
//
//
//        Encoder<OrderManagementLine> OrderManagementLineEncoder = Encoders.bean(OrderManagementLine.class);
//        Dataset<OrderManagementLine> dataSetControlGroup = imOrderManagementLineQuery.as(OrderManagementLineEncoder);
//        List<OrderManagementLine> result = dataSetControlGroup.collectAsList();
//
//        return result;
//    }
//}