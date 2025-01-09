//package com.mnrclara.spark.core.service.wmsindus;
//
//import com.mnrclara.spark.core.model.wmscorev2.FindInventoryV2;
//import com.mnrclara.spark.core.model.wmscorev2.InventoryV2;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.util.List;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
//import static org.apache.spark.sql.functions.col;
//
//@Slf4j
//@Service
//public class SparkInventoryIndusServiceV2 {
//
//    Properties connProp = new Properties();
//    SparkSession sparkSession = null;
//
//    public SparkInventoryIndusServiceV2() {
//        //connection properties
//        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        connProp.put("user", "sa");
//        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
//        sparkSession = SparkSession.builder().master("local[*]").appName("Inventory.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//        //Read from Sql Table
//        val df2 = sparkSession.read()
//                .option("fetchSize", "10000")
//                .jdbc("jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMF", "tblinventory", connProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tblinventoryv5");
//    }
//
//
//    public List<InventoryV2> findInventoryV2(FindInventoryV2 findInventoryV2) throws ParseException {
//
//        Dataset<Row> inventoryV2Query = sparkSession.sql("Select "
//                + "MAX(INV_ID) as inventoryId, "
//                + "LANG_ID as languageId, "
//                + "C_ID as companyCodeId, "
//                + "PLANT_ID as plantId, "
//                + "WH_ID as warehouseId, "
//                + "PAL_CODE as palletCode, "
//                + "CASE_CODE as caseCode, "
//                + "PACK_BARCODE as packBarcodes, "
//                + "ITM_CODE as itemCode, "
//                + "VAR_ID as variantCode, "
//                + "VAR_SUB_ID as variantSubCode, "
//                + "STR_NO as batchSerialNumber, "
//                + "ST_BIN as storageBin, "
//                + "STCK_TYP_ID as stockTypeId, "
//                + "SP_ST_IND_ID as specialStockIndicatorId, "
//                + "REF_ORD_NO as referenceOrderNo, "
//                + "STR_MTD as storageMethod, "
//                + "BIN_CL_ID as binClassId, "
//                + "TEXT as description, "
//                + "SUM(INV_QTY) as inventoryQuantity, "
//                + "SUM(ALLOC_QTY) as allocatedQuantity, "
//                + "INV_QTY as inventoryQuantity, "
//                + "ALLOC_QTY as allocatedQuantity, "
//                + "INV_UOM as inventoryUom, "
//                + "SUM(INV_QTY) + SUM(ALLOC_QTY) as referenceField4, "
//                + "MFR_DATE as manufacturerDate, "
//                + "EXP_DATE as expiryDate, "
//                + "IS_DELETED as deletionIndicator, "
//                + "REF_FIELD_1 as referenceField1, "
//                + "REF_FIELD_2 as referenceField2, "
//                + "REF_FIELD_3 as referenceField3, "
////                + "REF_FIELD_4 as referenceField4, "
//                + "REF_FIELD_5 as referenceField5, "
//                + "REF_FIELD_6 as referenceField6, "
//                + "REF_FIELD_7 as referenceField7, "
//                + "REF_FIELD_8 as referenceField8, "
//                + "REF_FIELD_9 as referenceField9, "
//                + "REF_FIELD_10 as referenceField10, "
//                + "IU_CTD_BY as createdBy, "
//                + "IU_CTD_ON as createdOn, "
//                + "UTD_BY as updatedBy, "
//                + "UTD_ON as updatedOn, "
//                + "MFR_CODE as manufacturerCode, "
//
//                // V2 fields
//                + "BARCODE_ID as barcodeId, "
//                + "CBM as cbm, "
//                + "CBM_UNIT as cbmUnit, "
//                + "CBM_PER_QTY as cbmPerQuantity, "
//                + "MFR_NAME as manufacturerName, "
//                + "LEVEL_ID as levelId, "
//                + "ORIGIN as origin, "
//                + "BRAND as brand, "
//                + "REF_DOC_NO as referenceDocumentNo, "
//                + "C_TEXT as companyDescription, "
//                + "PLANT_TEXT as plantDescription, "
//                + "WH_TEXT as warehouseDescription, "
//                + "STATUS_TEXT as statusDescription, "
//                + "STCK_TYP_TEXT as stockTypeDescription "
//                + "FROM tblinventoryv5 WHERE IS_DELETED = 0 "
//                + "GROUP BY itm_code, mfr_name, st_bin, "
//                + "LANG_ID, C_ID, PLANT_ID, WH_ID, PAL_CODE, CASE_CODE, PACK_BARCODE, "
//                + "VAR_ID, VAR_SUB_ID, STR_NO, STCK_TYP_ID, SP_ST_IND_ID, REF_ORD_NO, "
//                + "STR_MTD, BIN_CL_ID, TEXT, INV_UOM, MFR_DATE, EXP_DATE, IS_DELETED, "
//                + "REF_FIELD_1, REF_FIELD_2, REF_FIELD_3, REF_FIELD_5, REF_FIELD_6, "
//                + "REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, IU_CTD_BY, "
//                + "IU_CTD_ON, UTD_BY, UTD_ON, MFR_CODE, BARCODE_ID, CBM, CBM_UNIT, "
//                + "CBM_PER_QTY, MFR_NAME, LEVEL_ID, ORIGIN, BRAND, REF_DOC_NO, "
//                + "C_TEXT, PLANT_TEXT, WH_TEXT, STATUS_TEXT, STCK_TYP_TEXT");
//
//
//
////        inventoryV2Query.cache();
//
//        if (findInventoryV2.getWarehouseId() != null && !findInventoryV2.getWarehouseId().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("WH_ID").isin(findInventoryV2.getWarehouseId().toArray()));
//        }
//        if (findInventoryV2.getPackBarcodes() != null && !findInventoryV2.getPackBarcodes().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("PACK_BARCODE").isin(findInventoryV2.getPackBarcodes().toArray()));
//        }
//        if (findInventoryV2.getItemCode() != null && !findInventoryV2.getItemCode().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("ITM_CODE").isin(findInventoryV2.getItemCode().toArray()));
//        }
//        if (findInventoryV2.getStorageBin() != null && !findInventoryV2.getStorageBin().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("ST_BIN").isin(findInventoryV2.getStorageBin().toArray()));
//        }
//        if (findInventoryV2.getStorageSectionId() != null && !findInventoryV2.getStorageSectionId().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("ST_SEC_ID").isin(findInventoryV2.getStorageSectionId().toArray()));
//        }
//        if (findInventoryV2.getStockTypeId() != null && !findInventoryV2.getStockTypeId().isEmpty()) {
//            List<String> stockTypeIdString = findInventoryV2.getStockTypeId().stream().map(String::valueOf).collect(Collectors.toList());
//            inventoryV2Query = inventoryV2Query.filter(col("STCK_TYP_ID").isin(stockTypeIdString.toArray()));
//        }
//        if (findInventoryV2.getSpecialStockIndicatorId() != null && !findInventoryV2.getSpecialStockIndicatorId().isEmpty()) {
//           List<String>specialStockString = findInventoryV2.getSpecialStockIndicatorId().stream().map(String::valueOf).collect(Collectors.toList());
//            inventoryV2Query = inventoryV2Query.filter(col("SP_ST_IND_ID").isin(specialStockString.toArray()));
//        }
//        if (findInventoryV2.getBinClassId() != null && !findInventoryV2.getBinClassId().isEmpty()) {
//            List<String> binClassIdString = findInventoryV2.getBinClassId().stream().map(String::valueOf).collect(Collectors.toList());
//            inventoryV2Query = inventoryV2Query.filter(col("BIN_CL_ID").isin(binClassIdString.toArray()));
//        }
//        if (findInventoryV2.getDescription() != null && !findInventoryV2.getDescription().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("TEXT").isin(findInventoryV2.getDescription().toArray()));
//        }
//
//        // V2 fields
//        if (findInventoryV2.getLanguageId() != null && !findInventoryV2.getLanguageId().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("LANG_ID").isin(findInventoryV2.getLanguageId().toArray()));
//        }
//        if (findInventoryV2.getCompanyCodeId() != null && !findInventoryV2.getCompanyCodeId().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("C_ID").isin(findInventoryV2.getCompanyCodeId().toArray()));
//        }
//        if (findInventoryV2.getPlantId() != null && !findInventoryV2.getPlantId().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("PLANT_ID").isin(findInventoryV2.getPlantId().toArray()));
//        }
//        if (findInventoryV2.getBarcodeId() != null && !findInventoryV2.getBarcodeId().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("BARCODE_ID").isin(findInventoryV2.getBarcodeId().toArray()));
//        }
//        if (findInventoryV2.getManufacturerCode() != null && !findInventoryV2.getManufacturerCode().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("MFR_CODE").isin(findInventoryV2.getManufacturerCode().toArray()));
//        }
//        if (findInventoryV2.getManufacturerName() != null && !findInventoryV2.getManufacturerName().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("MFR_NAME").isin(findInventoryV2.getManufacturerName().toArray()));
//        }
//        if (findInventoryV2.getReferenceDocumentNo() != null && !findInventoryV2.getReferenceDocumentNo().isEmpty()) {
//            inventoryV2Query = inventoryV2Query.filter(col("REF_DOC_NO").isin(findInventoryV2.getReferenceDocumentNo().toArray()));
//        }
//
//        Encoder<InventoryV2> inventoryV2Encoder = Encoders.bean(InventoryV2.class);
//        Dataset<InventoryV2> datasetControlGroup = inventoryV2Query.as(inventoryV2Encoder);
//        List<InventoryV2> results = datasetControlGroup.collectAsList();
//        return results;
//    }
//
//}