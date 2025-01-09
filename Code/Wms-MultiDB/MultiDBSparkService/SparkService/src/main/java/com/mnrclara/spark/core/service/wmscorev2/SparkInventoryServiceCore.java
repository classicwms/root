package com.mnrclara.spark.core.service.wmscorev2;

import com.mnrclara.spark.core.model.wmscorev2.FindInventoryV2;
import com.mnrclara.spark.core.model.wmscorev2.InventoryV2Core;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class SparkInventoryServiceCore {

    SparkConf conf = new SparkConf()
            .setAppName("SparkInventory")
            .setMaster("local")
            .set("spark.executor.memory", "4g")
            .set("spark.executor.cores", "4")
            .set("spark.driver.memory", "4g")
            .set("spark.sql.shuffle.partitions", "16");

    SparkSession spark = SparkSession.builder()
            .appName("SparkSQLOptionalFilter")
            .config(conf)
            .getOrCreate();


    // Filter String values
    private void addCondition(List<String> conditions, String fieldName, List<String> values) {
        if (values != null && !values.isEmpty()) {
            if (values.size() == 1) {
                conditions.add(fieldName + " = '" + values.get(0) + "'");
            } else {
                conditions.add(fieldName + " IN (" + String.join(", ", values.stream().map(val -> "'" + val + "'").toArray(String[]::new)) + ")");
            }
        }
    }

    // Filter Long values
    private void numericCondition(List<String> conditions, String fieldName, List<Long> values) {
        if(values != null && !values.isEmpty()) {
            if(values.size() == 1) {
                conditions.add(fieldName + " = '" + values.get(0) + "'");
            }else {
                conditions.add(fieldName + " IN (" + String.join(",", values.stream().map(val -> "'" + val + "'").toArray(String[]::new)) + ")");
            }
        }
    }

    /**
     * FindInventory
     *
     * @param findInventory
     * @return
     */
    public List<InventoryV2Core> getInventory(FindInventoryV2 findInventory) {
        String sqlQuery = "SELECT INV_ID, LANG_ID, C_ID, PLANT_ID, WH_ID, " +
                "PAL_CODE, CASE_CODE, PACK_BARCODE, ITM_CODE, VAR_ID, VAR_SUB_ID, " +
                "STR_NO, ST_BIN, STCK_TYP_ID, SP_ST_IND_ID, REF_ORD_NO, STR_MTD, " +
                "BIN_CL_ID, TEXT, INV_QTY, ALLOC_QTY, INV_UOM, MFR_DATE, EXP_DATE, IS_DELETED, REF_FIELD_1, REF_FIELD_2, " +
                "REF_FIELD_3, REF_FIELD_4, REF_FIELD_5, REF_FIELD_6, REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, " +
                "IU_CTD_BY, IU_CTD_ON, UTD_BY, UTD_ON, MFR_CODE, BARCODE_ID, CBM, level_id, CBM_UNIT, CBM_PER_QTY, MFR_NAME, " +
                "ORIGIN, BRAND, REF_DOC_NO, C_TEXT, PLANT_TEXT, WH_TEXT, STCK_TYP_TEXT, STATUS_TEXT, PARTNER_CODE, ITM_TYP_ID, ITM_TYP_TXT, BATCH_DATE FROM tblinventory";

        List<String> conditions = new ArrayList<>();
        if (findInventory.getLanguageId() != null) {
            addCondition(conditions, "LANG_ID", findInventory.getLanguageId());
        }
        addCondition(conditions, "C_ID", findInventory.getCompanyCodeId());
        addCondition(conditions, "PLANT_ID", findInventory.getPlantId());
        addCondition(conditions, "WH_ID", findInventory.getWarehouseId());
        addCondition(conditions, "REF_DOC_NO", findInventory.getReferenceDocumentNo());
        addCondition(conditions, "BARCODE_ID", findInventory.getBarcodeId());
        addCondition(conditions, "MFR_CODE", findInventory.getManufacturerCode());
        addCondition(conditions, "MFR_NAME", findInventory.getManufacturerName());
        addCondition(conditions, "PACK_BARCODE", findInventory.getPackBarcodes());
        addCondition(conditions, "ITM_CODE", findInventory.getItemCode());
        addCondition(conditions, "ST_BIN", findInventory.getStorageBin());
        addCondition(conditions, "TEXT", findInventory.getDescription());
        addCondition(conditions, "PARTNER_CODE", findInventory.getPartnerCode());
        addCondition(conditions, "REF_FIELD_10", findInventory.getStorageSectionId());
        addCondition(conditions, "level_id", findInventory.getLevelId());
        if (findInventory.getStockTypeId() != null) {
            numericCondition(conditions, "STCK_TYP_ID", findInventory.getStockTypeId());
        }
        if(findInventory.getSpecialStockIndicatorId() != null) {
            numericCondition(conditions, "SP_ST_IND_ID", findInventory.getSpecialStockIndicatorId());
        }
        if(findInventory.getBinClassId() != null) {
            numericCondition(conditions, "BIN_CL_ID", findInventory.getBinClassId());
        }
        if(findInventory.getItemTypeId() != null) {
            numericCondition(conditions, "ITM_TYP_ID", findInventory.getItemTypeId());
        }
        if (!conditions.isEmpty()) {
            sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
        } else {
            sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id)";
        }

        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");

        Dataset<Row> data = spark.read()
                .option("fetchSize", "5000")
                .option("pushDownPredicate", true)
                .jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS_CORE", "(" + sqlQuery + ") as tmp", connProp);
//                .repartition(16);

        Encoder<InventoryV2Core> inventoryV2CoreEncoder = Encoders.bean(InventoryV2Core.class);
        Dataset<InventoryV2Core> inventoryV2CoreDataset = data.as(inventoryV2CoreEncoder);
        List<InventoryV2Core> result = inventoryV2CoreDataset.collectAsList();

//        spark.close();
        return result;
    }
}