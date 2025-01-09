package com.mnrclara.spark.core.service.almailem.joinspark;

import com.mnrclara.spark.core.model.Almailem.FindOutBoundHeaderV2;
import com.mnrclara.spark.core.model.Almailem.joinspark.OutboundHeaderV3;
import com.mnrclara.spark.core.util.ConditionUtils;
import com.mnrclara.spark.core.util.DatabaseConnectionUtil;
import com.mnrclara.spark.core.util.SparkSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class ObHeaderJoinService {

    SparkSession sparkSession = SparkSessionUtil.createSparkSession();

    public List<OutboundHeaderV3> findOutboundOrderSummaryReport(FindOutBoundHeaderV2 findOutBoundHeaderV2) {

        String query = "Select oh.c_text, oh.plant_text, oh.wh_text, oh.status_text, oh.target_branch_code, oh.ref_doc_typ,"
                + " oh.sales_order_number, oh.pre_ob_no, oh.ref_doc_no, oh.ref_doc_date, oh.dlv_cnf_on"
                + " From tbloutboundheader oh Where is_deleted = 0";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "oh.lang_id", findOutBoundHeaderV2.getLanguageId());
        ConditionUtils.addCondition(conditions, "oh.c_id", findOutBoundHeaderV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "oh.plant_id", findOutBoundHeaderV2.getPlantId());
        ConditionUtils.addCondition(conditions, "oh.wh_id", findOutBoundHeaderV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "oh.ref_doc_no", findOutBoundHeaderV2.getRefDocNumber());
        ConditionUtils.addCondition(conditions, "oh.pre_ob_no", findOutBoundHeaderV2.getPreOutboundNo());
        ConditionUtils.addCondition(conditions, "oh.target_branch_code", findOutBoundHeaderV2.getTargetBranchCode());
        ConditionUtils.addCondition(conditions, "oh.ref_field_1", findOutBoundHeaderV2.getSoType());
        ConditionUtils.addCondition(conditions, "oh.partner_code", findOutBoundHeaderV2.getPartnerCode());
        ConditionUtils.numericConditions(conditions, "oh.ob_ord_typ_id", findOutBoundHeaderV2.getOutboundOrderTypeId());
        ConditionUtils.numericConditions(conditions, "oh.status_id", findOutBoundHeaderV2.getStatusId());
        ConditionUtils.addDateCondition(conditions, "oh.req_del_date", findOutBoundHeaderV2.getStartRequiredDeliveryDate(), findOutBoundHeaderV2.getEndRequiredDeliveryDate());
        ConditionUtils.addDateCondition(conditions, "oh.dlv_cnf_on", findOutBoundHeaderV2.getStartDeliveryConfirmedOn(), findOutBoundHeaderV2.getEndDeliveryConfirmedOn());
        ConditionUtils.addDateCondition(conditions, "oh.dlv_ctd_on", findOutBoundHeaderV2.getStartOrderDate(), findOutBoundHeaderV2.getEndOrderDate());

        if (!conditions.isEmpty()) {
            query += " AND " + String.join(" AND ", conditions);
        }

        Properties conn = DatabaseConnectionUtil.getDatabaseConnectionProperties();
        String jdbcUrl = DatabaseConnectionUtil.getJdbcUrl();

        Dataset<Row> obHeader = sparkSession.read()
                .option("fetchSize", "5000")
                .option("pushDownPredicate", true)
                .jdbc(jdbcUrl, "(" + query + ") as obh", conn);

        obHeader.show();
        Encoder<OutboundHeaderV3> obHeaderEncoder = Encoders.bean(OutboundHeaderV3.class);
        Dataset<OutboundHeaderV3> resultData = obHeader.as(obHeaderEncoder);

        return resultData.collectAsList();

    }

}
