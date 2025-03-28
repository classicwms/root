package com.mnrclara.spark.core.service.wmscorev2;


import com.mnrclara.spark.core.model.wmscorev2.FindPutAwayLineV2;
import com.mnrclara.spark.core.model.wmscorev2.PutAwayLineCore;
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
public class SparkCorePutAwayLineNewService {

    // SparkSession
    SparkSession spark = SparkSessionUtil.createSparkSession();

    /**
     *
     * @param findPutAwayLineV2
     * @return
     */
    public List<PutAwayLineCore> getPutAwayLine(FindPutAwayLineV2 findPutAwayLineV2) {
        String sqlQuery = "SELECT C_TEXT, PLANT_TEXT, WH_TEXT, STATUS_TEXT, MFR_NAME, ITM_CODE, REF_DOC_NO, REF_DOC_TYPE, " +
                "PROP_ST_BIN, CNF_ST_BIN, BARCODE_ID, PA_QTY, PA_CNF_QTY, PA_CNF_BY, PA_CTD_ON, PA_UTD_ON as PA_CNF_ON FROM tblputawayline";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "PA_NO", findPutAwayLineV2.getPutAwayNumber());
        ConditionUtils.addCondition(conditions, "BRAND", findPutAwayLineV2.getBrand());
        ConditionUtils.addCondition(conditions, "C_ID", findPutAwayLineV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "PLANT_ID", findPutAwayLineV2.getPlantId());
        ConditionUtils.addCondition(conditions, "WH_ID", findPutAwayLineV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "LANG_ID", findPutAwayLineV2.getLanguageId());
        ConditionUtils.addCondition(conditions, "ITM_CODE", findPutAwayLineV2.getItemCode());
        ConditionUtils.addCondition(conditions, "ORIGIN", findPutAwayLineV2.getOrigin());
        ConditionUtils.addCondition(conditions, "BARCODE_ID", findPutAwayLineV2.getBarcodeId());
        ConditionUtils.addCondition(conditions, "CNF_ST_BIN", findPutAwayLineV2.getConfirmedStorageBin());
        ConditionUtils.addCondition(conditions, "REF_DOC_NO", findPutAwayLineV2.getRefDocNumber());
        ConditionUtils.addCondition(conditions, "GR_NO", findPutAwayLineV2.getGoodsReceiptNo());
        ConditionUtils.addCondition(conditions, "PRE_IB_NO", findPutAwayLineV2.getPreInboundNo());
        ConditionUtils.addCondition(conditions, "PROP_ST_BIN", findPutAwayLineV2.getProposedStorageBin());
        ConditionUtils.addCondition(conditions, "PACK_BARCODE", findPutAwayLineV2.getPackBarCodes());
        ConditionUtils.addCondition(conditions, "MFR_CODE", findPutAwayLineV2.getManufacturerCode());
        ConditionUtils.addCondition(conditions, "MFR_NAME", findPutAwayLineV2.getManufacturerName());
        ConditionUtils.addCondition(conditions, "PA_CNF_BY", findPutAwayLineV2.getConfirmedBy());
        ConditionUtils.numericConditions(conditions, "IB_ORD_TYP_ID", findPutAwayLineV2.getInboundOrderTypeId());
        ConditionUtils.numericConditions(conditions, "STATUS_ID", findPutAwayLineV2.getStatusId());
        ConditionUtils.numericConditions(conditions, "IB_LINE_NO", findPutAwayLineV2.getLineNo());
        ConditionUtils.addDateCondition(conditions, "PA_CTD_ON", findPutAwayLineV2.getFromCreatedDate(), findPutAwayLineV2.getToCreatedDate() );
        ConditionUtils.addDateCondition(conditions, "PA_CNF_ON", findPutAwayLineV2.getFromConfirmedDate(), findPutAwayLineV2.getToConfirmedDate() );

        if(!conditions.isEmpty()) {
            sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
        } else {
            sqlQuery += " WHERE IS_DELETED = 0 ";
        }

        Properties connProp = DatabaseConnectionUtil.getCoreDatabaseConnectionProperties();
        String jdbcUrl = DatabaseConnectionUtil.getCoreJdbcUrl();

        Dataset<Row> data = spark.read()
                .option("fetchSize", "10000")
                .option("pushDownloadPredicate", true)
                .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

        data.show();
        Encoder<PutAwayLineCore> putAwayLineEncoder = Encoders.bean(PutAwayLineCore.class);
        Dataset<PutAwayLineCore> dataset = data.as(putAwayLineEncoder);
        return dataset.collectAsList();
    }
}