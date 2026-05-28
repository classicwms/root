package com.mnrclara.spark.core.service.almailem;


import com.mnrclara.spark.core.model.Almailem.FindInboundHeaderV2;
import com.mnrclara.spark.core.model.Almailem.InboundHeaderV4;
import com.mnrclara.spark.core.util.ConditionUtils;
import com.mnrclara.spark.core.util.DatabaseConnectionUtil;
import com.mnrclara.spark.core.util.SparkSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class SparkInboundHeaderService {

    // SparkSession
    SparkSession spark = SparkSessionUtil.createSparkSession();

    /**
     * @param findInboundHeader
     * @return
     */
    public List<InboundHeaderV4> findInboundHeader(FindInboundHeaderV2 findInboundHeader) throws ParseException {
        String sqlQuery =
                "SELECT " +
                        "h.LANG_ID as languageId, " +
                        "h.C_ID as companyCode, " +
                        "h.PLANT_ID as plantId, " +
                        "h.WH_ID as warehouseId, " +
                        "h.REF_DOC_NO as refDocNumber, " +
                        "h.PRE_IB_NO as preInboundNo, " +
                        "h.STATUS_ID as statusId, " +
                        "h.IB_ORD_TYP_ID as inboundOrderTypeId, " +
                        "h.CONT_NO as containerNo, " +
                        "h.VEH_NO as vechicleNo, " +
                        "h.IB_TEXT as headerText, " +
                        "h.IS_DELETED as deletionIndicator, " +
                        "h.REF_FIELD_1 as referenceField1, " +
                        "h.REF_FIELD_2 as referenceField2, " +
                        "h.REF_FIELD_3 as referenceField3, " +
                        "h.REF_FIELD_4 as referenceField4, " +
                        "h.REF_FIELD_5 as referenceField5, " +
                        "h.REF_FIELD_6 as referenceField6, " +
                        "h.REF_FIELD_7 as referenceField7, " +
                        "h.REF_FIELD_8 as referenceField8, " +
                        "h.REF_FIELD_9 as referenceField9, " +
                        "h.REF_FIELD_10 as referenceField10, " +
                        "h.CTD_BY as createdBy, " +
                        "h.CTD_ON as createdOn, " +
                        "h.UTD_BY as updatedBy, " +
                        "h.UTD_ON as updatedOn, " +
                        "h.IB_CNF_BY as confirmedBy, " +
                        "h.IB_CNF_ON as confirmedOn, " +
                        "h.C_TEXT as companyDescription, " +
                        "h.PLANT_TEXT as plantDescription, " +
                        "h.WH_TEXT as warehouseDescription, " +
                        "h.STATUS_TEXT as statusDescription, " +
                        "h.PURCHASE_ORDER_NUMBER as purchaseOrderNumber, " +
                        "h.MIDDLEWARE_ID as middlewareId, " +
                        "h.MIDDLEWARE_TABLE as middlewareTable, " +
                        "h.MANUFACTURER_FULL_NAME as manufacturerFullName, " +
                        "h.REF_DOC_TYPE as referenceDocumentType, " +
                        "h.CSTR_COD as customerCode, " +
                        "h.TFR_REQ_TYP as TransferRequestType, " +
                        "h.AMS_SUP_INV as AMSSupplierInvoiceNo, " +
                        "h.count_of_ord_lines as countOfOrderLines, " +
                        "h.received_lines as receivedLines " +
//                        "COUNT(DISTINCT l.ref_doc_no) as countOfOrderLines, " +
//                        "SUM(CASE WHEN p.STATUS_ID IN (20,24) THEN 1 ELSE 0 END) as receivedLines " +
                        "FROM tblinboundheader h " +
//                        "LEFT JOIN tblinboundline l ON h.REF_DOC_NO = l.ref_doc_no " +
//                        "  AND h.PRE_IB_NO = l.PRE_IB_NO " +
//                        "  AND h.C_ID = l.C_ID " +
//                        "  AND h.PLANT_ID = l.PLANT_ID " +
//                        "  AND h.LANG_ID = l.LANG_ID " +
//                        "  AND h.WH_ID = l.WH_ID " +
//                        "  AND l.is_deleted = 0 " +
//                        "LEFT JOIN tblputawayline p ON h.REF_DOC_NO = p.ref_doc_no " +
//                        "  AND h.PRE_IB_NO = p.PRE_IB_NO " +
//                        "  AND h.C_ID = p.C_ID " +
//                        "  AND h.PLANT_ID = p.PLANT_ID " +
//                        "  AND h.LANG_ID = p.LANG_ID " +
//                        "  AND h.WH_ID = p.WH_ID " +
//                        "  AND p.is_deleted = 0 " +
                        "WHERE h.IS_DELETED = 0 ";
//                        "GROUP BY " +
//                        "h.LANG_ID, h.C_ID, h.PLANT_ID, h.WH_ID, h.REF_DOC_NO, h.PRE_IB_NO, h.STATUS_ID, " +
//                        "h.IB_ORD_TYP_ID, h.CONT_NO, h.VEH_NO, h.IB_TEXT, h.IS_DELETED, " +
//                        "h.REF_FIELD_1, h.REF_FIELD_2, h.REF_FIELD_3, h.REF_FIELD_4, h.REF_FIELD_5, " +
//                        "h.REF_FIELD_6, h.REF_FIELD_7, h.REF_FIELD_8, h.REF_FIELD_9, h.REF_FIELD_10, " +
//                        "h.CTD_BY, h.CTD_ON, h.UTD_BY, h.UTD_ON, h.IB_CNF_BY, h.IB_CNF_ON, " +
//                        "h.C_TEXT, h.PLANT_TEXT, h.WH_TEXT, h.STATUS_TEXT, h.PURCHASE_ORDER_NUMBER, " +
//                        "h.MIDDLEWARE_ID, h.MIDDLEWARE_TABLE, h.MANUFACTURER_FULL_NAME, h.REF_DOC_TYPE, " +
//                        "h.CSTR_COD, h.TFR_REQ_TYP, h.AMS_SUP_INV";


        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "h.WH_ID", findInboundHeader.getWarehouseId());
        ConditionUtils.addCondition(conditions, "h.CONT_NO", findInboundHeader.getContainerNo());
        ConditionUtils.addCondition(conditions, "h.C_ID", findInboundHeader.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "h.PLANT_ID", findInboundHeader.getPlantId());
        ConditionUtils.addCondition(conditions, "h.LANG_ID", findInboundHeader.getLanguageId());
        ConditionUtils.addCondition(conditions, "h.REF_DOC_NO", findInboundHeader.getRefDocNumber());

        ConditionUtils.numericConditions(conditions, "h.STATUS_ID", findInboundHeader.getStatusId());
        ConditionUtils.numericConditions(conditions, "h.IB_ORD_TYP_ID", findInboundHeader.getInboundOrderTypeId());
        ConditionUtils.addDateCondition(conditions, "h.CTD_ON", findInboundHeader.getStartCreatedOn(), findInboundHeader.getEndCreatedOn());
        ConditionUtils.addDateCondition(conditions, "h.IB_CNF_ON", findInboundHeader.getStartConfirmedOn(), findInboundHeader.getEndConfirmedOn());

        if (!conditions.isEmpty()) {
            sqlQuery = sqlQuery.replace(
                    "WHERE h.IS_DELETED = 0",
                    "WHERE h.IS_DELETED = 0 AND " + String.join(" AND ", conditions)
            );
        }

        Properties connProp = DatabaseConnectionUtil.getDatabaseConnectionProperties();
        String jdbcUrl = DatabaseConnectionUtil.getJdbcUrl();

        Dataset<Row> data = spark.read()
                .option("fetchSize", "10000")
                .option("pushDownloadPredicate", true)
                .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

        Encoder<InboundHeaderV4> putAwayLineEncoder = Encoders.bean(InboundHeaderV4.class);
        Dataset<InboundHeaderV4> dataset = data.as(putAwayLineEncoder);
        return dataset.collectAsList();
    }

}
