package com.mnrclara.spark.core.service.almailem;

import com.mnrclara.spark.core.model.Almailem.FindOutboundLineV2;
import com.mnrclara.spark.core.model.Almailem.OutboundLineV2;
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
public class SparkOutboundLineV3Service {

    // SparkSession
    SparkSession spark = SparkSessionUtil.createSparkSession();

    /**
     * FindOutboundLine
     *
     * @param findOutboundLineV2
     * @return
     */
    public List<OutboundLineV2> getOutBoundLineV3(FindOutboundLineV2 findOutboundLineV2) {
        String joinQueryData = "SELECT ob.PLANT_TEXT, ob.WH_TEXT, ob.REF_DOC_NO, ob.DLV_QTY, ob.REF_DOC_TYPE, ob.SALES_ORDER_NUMBER, ob.TARGET_BRANCH_CODE, \n " +
                "ob.MFR_NAME, ob.ITM_CODE, ob.ITEM_TEXT, ob.SALES_INVOICE_NUMBER, ob.ORD_QTY, ob.STATUS_TEXT, ob.DLV_CTD_ON, COALESCE(qc.QC_QTY, 0) AS QC_QTY, \n " +
                " COALESCE(pick.PICK_CNF_QTY, 0) AS PICK_CNF_QTY, IMS_SALE_TYP_CODE FROM tbloutboundline ob LEFT JOIN (SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, \n " +
                " REF_DOC_NO, PRE_OB_NO, OB_LINE_NO, ITM_CODE, SUM(QC_QTY) AS QC_QTY FROM tblqualityline GROUP BY C_ID, PLANT_ID, LANG_ID, WH_ID, \n " +
                " REF_DOC_NO, PRE_OB_NO, OB_LINE_NO, ITM_CODE) qc ON ob.C_ID = qc.C_ID AND ob.PLANT_ID = qc.PLANT_ID AND ob.LANG_ID = qc.LANG_ID AND \n " +
                " ob.WH_ID = qc.WH_ID AND ob.REF_DOC_NO = qc.REF_DOC_NO AND ob.OB_LINE_NO = qc.OB_LINE_NO AND ob.ITM_CODE = qc.ITM_CODE \n " +
                " LEFT JOIN ( SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_OB_NO, OB_LINE_NO, ITM_CODE, SUM(PICK_CNF_QTY) AS PICK_CNF_QTY \n " +
                " FROM tblpickupline \n " +
                " WHERE IS_DELETED = 0 \n " +
                " GROUP BY C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_OB_NO, OB_LINE_NO, ITM_CODE ) pick ON \n " +
                " ob.C_ID = pick.C_ID AND ob.PLANT_ID = pick.PLANT_ID AND ob.LANG_ID = pick.LANG_ID AND \n " +
                " ob.WH_ID = pick.WH_ID AND ob.REF_DOC_NO = pick.REF_DOC_NO AND ob.OB_LINE_NO = pick.OB_LINE_NO AND ob.ITM_CODE = pick.ITM_CODE " +
                " WHERE ob.IS_DELETED = 0 ";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "ob.WH_ID", findOutboundLineV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "ob.PRE_OB_NO", findOutboundLineV2.getPreOutboundNo());
        ConditionUtils.addCondition(conditions, "ob.REF_DOC_NO", findOutboundLineV2.getRefDocNumber());
        ConditionUtils.addCondition(conditions, "ob.PARTNER_CODE", findOutboundLineV2.getPartnerCode());
        ConditionUtils.addCondition(conditions, "ob.ITM_CODE", findOutboundLineV2.getItemCode());
        ConditionUtils.addCondition(conditions, "ob.REF_DOC_TYPE", findOutboundLineV2.getOrderType());
        ConditionUtils.addCondition(conditions, "ob.LANG_ID", findOutboundLineV2.getLanguageId());
        ConditionUtils.addCondition(conditions, "ob.C_ID", findOutboundLineV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "ob.PLANT_ID", findOutboundLineV2.getPlantId());
        ConditionUtils.addCondition(conditions, "ob.MFR_NAME", findOutboundLineV2.getManufacturerName());
        ConditionUtils.addCondition(conditions, "ob.TARGET_BRANCH_CODE", findOutboundLineV2.getTargetBranchCode());
        ConditionUtils.addCondition(conditions, "ob.SALES_ORDER_NUMBER", findOutboundLineV2.getSalesOrderNumber());
        ConditionUtils.numericConditions(conditions, "ob.STATUS_ID", findOutboundLineV2.getStatusId());
        ConditionUtils.numericConditions(conditions, "ob.OB_LINE_NO", findOutboundLineV2.getLineNumber());
        ConditionUtils.addDateCondition(conditions, "ob.DLV_CTD_ON", findOutboundLineV2.getFromDeliveryDate(), findOutboundLineV2.getToDeliveryDate());

        if (!conditions.isEmpty()) {
            joinQueryData += " AND " + String.join(" AND ", conditions);
        }

        Properties connProp = DatabaseConnectionUtil.getDatabaseConnectionProperties();
        String jdbcUrl = DatabaseConnectionUtil.getJdbcUrl();

        Dataset<Row> outboundLine = spark.read()
                .option("fetchSize", "5000")
                .option("pushDownPredicate", true)
                .jdbc(jdbcUrl, "(" + joinQueryData + ") as pl", connProp);


        outboundLine.show();
        Encoder<OutboundLineV2> outboundLineV2Encoder = Encoders.bean(OutboundLineV2.class);
        Dataset<OutboundLineV2> resultData = outboundLine.as(outboundLineV2Encoder);
//        resultData.show();

        return resultData.collectAsList();
    }
}