package com.mnrclara.spark.core.service.almailem;


import com.mnrclara.spark.core.model.Almailem.FindInboundLineV2;
import com.mnrclara.spark.core.model.Almailem.InboundLineV3;
import com.mnrclara.spark.core.util.ConditionUtils;
import com.mnrclara.spark.core.util.DatabaseConnectionUtil;
import com.mnrclara.spark.core.util.SparkSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class SparkInboundLineServiceV3 {

    // SparkSession
    SparkSession spark = SparkSessionUtil.createSparkSession();


    // Input Filter Condition
    private void addCondition(List<String> conditions, String fieldName, List<String> values) {
        if(values != null && !values.isEmpty()) {
            if(values.size() == 1) {
                conditions.add(fieldName + " = '" + values.get(0) + "'");
            } else {
                conditions.add(fieldName + " IN (" + String.join(", ", values.stream().map(val -> "'" + val + "'").toArray(String[]::new)) + ")");
            }
        }
    }

    // DateFilter
    private void addDateCondition(List<String> conditions, String fieldName, Date startDate, Date endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate != null && endDate != null) {
            conditions.add(fieldName + " BETWEEN '" + dateFormat.format(startDate) + "' AND '" + dateFormat.format(endDate) + "'");
        }
    }


    /**
     * InboundLine
     *
     * @param findInboundLineV2
     * @return
     */
    public List<InboundLineV3> getInboundLine(FindInboundLineV2 findInboundLineV2) {
        String sqlQuery = "SELECT C_TEXT, PLANT_TEXT, WH_TEXT, STATUS_TEXT, SOURCE_BRANCH_CODE, REF_DOC_NO, IB_LINE_NO, " +
                "REF_DOC_TYPE, MFR_NAME, ITM_CODE, TEXT, ORD_QTY, ACCEPT_QTY, DAMAGE_QTY, VAR_QTY, CTD_ON, IB_CNF_ON FROM tblinboundline ";


        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "WH_ID", findInboundLineV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "C_ID", findInboundLineV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "PLANT_ID", findInboundLineV2.getPlantId());
        ConditionUtils.addCondition(conditions, "LANG_ID", findInboundLineV2.getLanguageId());
        ConditionUtils.addCondition(conditions, "ITM_CODE", findInboundLineV2.getItemCode());
        ConditionUtils.addCondition(conditions, "MFR_PART", findInboundLineV2.getManufacturerPartNo());
        ConditionUtils.addCondition(conditions, "MFR_NAME", findInboundLineV2.getManufactureName());
        ConditionUtils.addCondition(conditions, "SOURCE_BRANCH_CODE", findInboundLineV2.getSourceBranchCode());
        ConditionUtils.addCondition(conditions, "SOURCE_COMPANY_CODE", findInboundLineV2.getSourceCompanyCode());
        ConditionUtils.addCondition(conditions,"REF_DOC_NO", findInboundLineV2.getRefDocNumber());
        ConditionUtils.addCondition(conditions, "REF_FIELD_1", findInboundLineV2.getReferenceField1());
        ConditionUtils.addDateCondition(conditions, "IB_CNF_ON", findInboundLineV2.getStartConfirmedOn(), findInboundLineV2.getEndConfirmedOn());
        ConditionUtils.addDateCondition(conditions, "CTD_ON", findInboundLineV2.getStartCreatedOn(), findInboundLineV2.getEndCreatedOn());
        ConditionUtils.numericConditions(conditions, "IB_ORD_TYP_ID", findInboundLineV2.getInboundOrderTypeId());
        ConditionUtils.numericConditions(conditions, "STATUS_ID", findInboundLineV2.getStatusId());

        if(!conditions.isEmpty()) {
            sqlQuery += " WHERE IS_DELETED = 0 AND " + String.join(" AND ", conditions);
        } else {
            sqlQuery += " WHERE IS_DELETED =0 ";
        }

        Properties connProp = DatabaseConnectionUtil.getDatabaseConnectionProperties();
        String jdbcUrl = DatabaseConnectionUtil.getJdbcUrl();

        Dataset<Row> data = spark.read()
                .option("fetchSize", "10000")
                .option("pushDownloadPredicate", true)
                .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

        Encoder<InboundLineV3> inboundLineV3Encoder = Encoders.bean(InboundLineV3.class);
        Dataset<InboundLineV3> dsJntwebhook = data.as(inboundLineV3Encoder);
        return dsJntwebhook.collectAsList();
    }

}
