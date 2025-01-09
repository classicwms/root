package com.mnrclara.spark.core.service.almailem;


import com.mnrclara.spark.core.model.Almailem.PickUpLineV3;
import com.mnrclara.spark.core.model.Almailem.SearchPickupLineV2;
import com.mnrclara.spark.core.util.ConditionUtils;
import com.mnrclara.spark.core.util.DatabaseConnectionUtil;
import com.mnrclara.spark.core.util.SparkSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class SparkPickUpLineV3Service {

    // SparkSession
    SparkSession spark = SparkSessionUtil.createSparkSession();

    /**
     * FindPickUpLineV2
     *
     * @param searchPickupLineV2
     * @return
     */
    public List<PickUpLineV3> getPickupLine(SearchPickupLineV2 searchPickupLineV2) {
        String sqlQuery = "SELECT C_TEXT, PLANT_TEXT, WH_TEXT, STATUS_TEXT, REF_DOC_TYPE, REF_DOC_NO, PU_NO, " +
                " OB_LINE_NO, MFR_NAME, ITM_CODE, ITEM_TEXT, PARTNER_ITEM_BARCODE, PICK_ST_BIN, LEVEL_ID, ASS_PICKER_ID, " +
                " PARTNER_CODE, ALLOC_QTY,PICK_CNF_QTY, VAR_QTY, PICK_CTD_ON, PICK_UTD_ON, IMS_SALE_TYP_CODE FROM tblpickupline ";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "LANG_ID", searchPickupLineV2.getLanguageId());

        ConditionUtils.addCondition(conditions, "C_ID", searchPickupLineV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "PLANT_ID", searchPickupLineV2.getPlantId());
        ConditionUtils.addCondition(conditions, "WH_ID", searchPickupLineV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "ITM_CODE", searchPickupLineV2.getItemCode());
        ConditionUtils.addCondition(conditions, "PU_NO", searchPickupLineV2.getPickupNumber());
        ConditionUtils.addCondition(conditions, "ASS_PICKER_ID", searchPickupLineV2.getAssignedPickerId());
        ConditionUtils.addCondition(conditions, "PICK_HE_NO", searchPickupLineV2.getActualHeNo());
        ConditionUtils.addCondition(conditions, "LEVEL_ID", searchPickupLineV2.getLevelId());
        ConditionUtils.addCondition(conditions, "PARTNER_CODE", searchPickupLineV2.getPartnerCode());
        ConditionUtils.addCondition(conditions, "PICK_ST_BIN", searchPickupLineV2.getPickedStorageBin());
        ConditionUtils.addCondition(conditions, "PICK_PACK_BARCODE", searchPickupLineV2.getPickedPackCode());

        ConditionUtils.addCondition(conditions, "PRE_OB_NO", searchPickupLineV2.getPreOutboundNo());
        ConditionUtils.addCondition(conditions, "REF_DOC_NO", searchPickupLineV2.getRefDocNumber());
        ConditionUtils.numericConditions(conditions, "STATUS_ID", searchPickupLineV2.getStatusId());
        ConditionUtils.numericConditions(conditions, "OB_LINE_NO", searchPickupLineV2.getLineNumber());
        ConditionUtils.addDateCondition(conditions, "PICK_CNF_ON", searchPickupLineV2.getFromPickConfirmedOn(), searchPickupLineV2.getToPickConfirmedOn());

        if((!conditions.isEmpty())) {
            sqlQuery += " WHERE IS_DELETED = 0 AND " + String.join(" AND ", conditions);
        } else {
            sqlQuery += " WHERE IS_DELETED = 0 ";
        }

        Properties connProp = DatabaseConnectionUtil.getDatabaseConnectionProperties();
        String jdbcUrl = DatabaseConnectionUtil.getJdbcUrl();

        Dataset<Row> data = spark.read()
                .option("fetchSize", "10000")
                .option("pushDownloadPredicate", true)
                .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);


        Encoder<PickUpLineV3> pickUpLineV3Encoder = Encoders.bean(PickUpLineV3.class);
        Dataset<PickUpLineV3> dataSet = data.as(pickUpLineV3Encoder);
        dataSet.show();
        return dataSet.collectAsList();

    }
}