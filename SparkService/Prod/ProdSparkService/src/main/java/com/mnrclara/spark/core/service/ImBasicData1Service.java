//package com.mnrclara.spark.core.service;
//
//import com.mnrclara.spark.core.model.FindImBasicData1;
//import com.mnrclara.spark.core.model.ImBasicData1;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.apache.commons.lang3.time.DateUtils;
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
//
//@Service
//@Slf4j
//public class ImBasicData1Service {
//
//    Properties connProp = new Properties();
//
//    SparkSession sparkSession = null;
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//
//    public ImBasicData1Service() {
//
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "AL.8B/XoM>D3vR]Q4s41?.8hP>|");
//        sparkSession = SparkSession.builder().master("local[*]").appName("ImBasicData1.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        // Read from Sql Table
//        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://10.10.6.30;databaseName=WMS_ALMPRD", "tblimbasicdata1", connProp)
//                .repartition(16);
////                .cache();
//        df2.createOrReplaceTempView("tblimbasicdata1");
//    }
//
//    /**
//     * @param findImBasicData1
//     * @return
//     * @throws ParseException
//     */
//    public List<ImBasicData1> findImBasicData1(FindImBasicData1 findImBasicData1) throws ParseException {
//
//        Dataset<Row> imBasicData1Query = sparkSession.sql("SELECT "
//                + "UOM_ID as uomId, "
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCodeId, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "ITM_CODE as itemCode, "
//                + "MFR_PART as manufacturerPartNo, "
//                + "TEXT as description, "
//                + "MODEL as model, "
//                + "SPEC_01 as specifications1, "
//                + "SPEC_02 as specifications2, "
//                + "EAN_UPC_NO as eanUpcNo, "
//                + "HSN_CODE as hsnCode, "
//                + "ITM_TYP_ID as itemType, "
//                + "ITM_GRP_ID as itemGroup, "
//                + "SUB_ITM_GRP_ID as subItemGroup, "
//                + "ST_SEC_ID as storageSectionId, "
//                + "TOT_STK as totalStock, "
//                + "MIN_STK as minimumStock, "
//                + "MAX_STK as maximumStock, "
//                + "RE_ORD_LVL as reorderLevel, "
//                + "CAP_CHK as capacityCheck, "
//                + "REP_QTY as replenishmentQty, "
//                + "SAFTY_STCK as safetyStock, "
//                + "STATUS_ID as statusId, "
//                + "SHELF_LIFE_IND as shelfLifeIndicator, "
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
//                + "CTD_BY as createdBy, "
//                + "CTD_ON as createdOn, "
//                + "UTD_BY as updatedBy, "
//                + "UTD_ON as updatedOn "
//                + "FROM tblimbasicdata1 WHERE IS_DELETED = 0 ");
//
//
//        if (findImBasicData1.getWarehouseId() != null && !findImBasicData1.getWarehouseId().isEmpty()) {
//            imBasicData1Query = imBasicData1Query.filter(col("WH_ID").isin(findImBasicData1.getWarehouseId().toArray()));
//        }
//        if (findImBasicData1.getItemCode() != null && !findImBasicData1.getItemCode().isEmpty()) {
//            imBasicData1Query = imBasicData1Query.filter(col("ITM_CODE").isin(findImBasicData1.getItemCode().toArray()));
//        }
//        if (findImBasicData1.getDescription() != null && !findImBasicData1.getDescription().isEmpty()) {
//            imBasicData1Query = imBasicData1Query.filter(col("TEXT").isin(findImBasicData1.getDescription().toArray()));
//        }
//        if (findImBasicData1.getItemType() != null && !findImBasicData1.getItemType().isEmpty()) {
//            List<String> itemTypeStrings = findImBasicData1.getItemType().stream().map(String::valueOf).collect(Collectors.toList());
//            imBasicData1Query = imBasicData1Query.filter(col("ITM_TYP_ID").isin(itemTypeStrings.toArray()));
//        }
//        if (findImBasicData1.getItemGroup() != null && !findImBasicData1.getItemGroup().isEmpty()) {
//            List<String> itemGroups = findImBasicData1.getItemGroup().stream().map(String::valueOf).collect(Collectors.toList());
//            imBasicData1Query = imBasicData1Query.filter(col("ITM_GRP_ID").isin(itemGroups.toArray()));
//        }
//        if (findImBasicData1.getSubItemGroup() != null && !findImBasicData1.getSubItemGroup().isEmpty()) {
//            List<String> subItemGroups = findImBasicData1.getSubItemGroup().stream().map(String::valueOf).collect(Collectors.toList());
//            imBasicData1Query = imBasicData1Query.filter(col("SUB_ITM_GRP_ID").isin(subItemGroups.toArray()));
//        }
//        if (findImBasicData1.getStartCreatedOn() != null) {
//            Date startCreatedOn = findImBasicData1.getStartCreatedOn();
//            startCreatedOn = DateUtils.ceiling(startCreatedOn, Calendar.DAY_OF_MONTH);
//            imBasicData1Query = imBasicData1Query.filter(col("CTD_ON").$greater$eq(dateFormat.format(startCreatedOn)));
//        }
//        if (findImBasicData1.getEndCreatedOn() != null) {
//            Date endCreatedOn = findImBasicData1.getEndCreatedOn();
//            endCreatedOn = DateUtils.ceiling(endCreatedOn, Calendar.DAY_OF_MONTH);
//            imBasicData1Query = imBasicData1Query.filter(col("CTD_ON").$less$eq(dateFormat.format(endCreatedOn)));
//        }
//        if (findImBasicData1.getStartUpdatedOn() != null) {
//            Date startUpdatedOn = findImBasicData1.getStartUpdatedOn();
//            startUpdatedOn = DateUtils.ceiling(startUpdatedOn, Calendar.DAY_OF_MONTH);
//            imBasicData1Query = imBasicData1Query.filter(col("UTD_ON").$greater$eq(dateFormat.format(startUpdatedOn)));
//        }
//        if (findImBasicData1.getEndUpdatedOn() != null) {
//            Date endUpdatedOn = findImBasicData1.getEndUpdatedOn();
//            endUpdatedOn = DateUtils.ceiling(endUpdatedOn, Calendar.DAY_OF_MONTH);
//            imBasicData1Query = imBasicData1Query.filter(col("UTD_ON").$greater$eq(dateFormat.format(endUpdatedOn)));
//        }
//
//
//        Encoder<ImBasicData1> imBasicData1Encoder = Encoders.bean(ImBasicData1.class);
//        Dataset<ImBasicData1> dataSetControlGroup = imBasicData1Query.as(imBasicData1Encoder);
//        List<ImBasicData1> result = dataSetControlGroup.collectAsList();
////        imBasicData1Query.unpersist();
//
//        return result;
//
//    }
//}