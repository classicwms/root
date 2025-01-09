package com.mnrclara.spark.core.service.jaincord;


import com.mnrclara.spark.core.model.wmscorev2.FindPreOutBoundLineV2;
import com.mnrclara.spark.core.model.wmscorev2.PreOutBoundLineV2;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;

@Service
@Slf4j
public class SparkPreOutBoundLineServiceV2JainCord {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SparkPreOutBoundLineServiceV2JainCord() {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
        sparkSession = SparkSession.builder().master("local[*]").appName("PeriodicHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblpreoutboundline", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblpreoutboundlinev5jain");
    }

    /**
     *
     * @param findPreOutBoundLineV2
     * @return
     * @throws ParseException
     */
    public List<PreOutBoundLineV2> findPreOutBoundLine(FindPreOutBoundLineV2 findPreOutBoundLineV2) throws ParseException {

        Dataset<Row> periodicLineQuery = sparkSession.sql("Select "
                + "LANG_ID as languageId, "
                + "C_ID as companyCodeId, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "REF_DOC_NO as refDocNumber, "
                + "PRE_OB_NO as preOutboundNo, "
                + "PARTNER_CODE as partnerCode, "
                + "OB_LINE_NO as lineNumber, "
                + "ITM_CODE as itemCode, "
                + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                + "VAR_ID as variantCode, "
                + "VAR_SUB_ID as variantSubCode, "
                + "STATUS_ID as statusId, "
                + "STCK_TYP_ID as stockTypeId, "
                + "SP_ST_IND_ID as specialStockIndicatorId, "
                + "TEXT as description, "
                + "MFR_PART as manufacturerPartNo, "
                + "HSN_CODE as hsnCode, "
                + "ITM_BARCODE as itemBarcode, "
                + "ORD_QTY as orderQty, "
                + "ORD_UOM as orderUom, "
                + "REQ_DEL_DATE as requiredDeliveryDate, "
                + "REF_FIELD_1 as referenceField1, "
                + "REF_FIELD_2 as referenceField2, "
                + "REF_FIELD_3 as referenceField3, "
                + "REF_FIELD_4 as referenceField4, "
                + "REF_FIELD_5 as referenceField5, "
                + "REF_FIELD_6 as referenceField6, "
                + "REF_FIELD_7 as referenceField7, "
                + "REF_FIELD_8 as referenceField8, "
                + "REF_FIELD_9 as referenceField9, "
                + "REF_FIELD_10 as referenceField10, "
                + "IS_DELETED as deletionIndicator, "
                + "PRE_OB_CTD_BY as createdBy, "
                + "PRE_OB_CTD_ON as createdOn, "
                + "PRE_OB_UTD_BY as countedBy, "
                + "PRE_OB_UTD_ON as countedOn, "
                + "PRE_OB_UTD_BY as updatedBy, "
                + "PRE_OB_UTD_ON as updatedOn, "
                + "MFR_CODE as manufacturerCode, "
                + "MFR_NAME as manufacturerName, "
                + "ORIGIN as origin, "
                + "BRAND as brand, "
                + "C_TEXT as companyDescription, "
                + "PLANT_TEXT as plantDescription, "
                + "WH_TEXT as warehouseDescription, "
                + "STATUS_TEXT as statusDescription, "
                + "TOKEN_NUMBER as tokenNumber, "
                + "MIDDLEWARE_ID as middlewareId, "
                + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                + "MIDDLEWARE_TABLE as middlewareTable, "
                + "REF_DOC_TYPE as referenceDocumentType, "
                + "SALES_INVOICE_NUMBER as salesInvoiceNumber, "
                + "SUPPLIER_INVOICE_NO as supplierInvoiceNo, "
                + "SALES_ORDER_NUMBER as salesOrderNumber, "
                + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                + "PICK_LIST_NUMBER as pickListNumber, "
                + "TRANSFER_ORDER_NO as transferOrderNo, "
                + "RET_ORDER_NO as returnOrderNo, "
                + "IS_COMPLETED as isCompleted, "
                + "IS_CANCELLED as isCancelled, "
                + "TARGET_BRANCH_CODE as targetBranchCode, "
                + "CUSTOMER_ID as customerId, "
                + "CUSTOMER_NAME as customerName "
                + "From tblpreoutboundlinev5jain Where IS_DELETED = 0 "
        );
//        periodicLineQuery.cache();

        if (findPreOutBoundLineV2.getWarehouseId() != null && !findPreOutBoundLineV2.getWarehouseId().isEmpty()) {
            periodicLineQuery = periodicLineQuery.filter(col("WH_ID").isin(findPreOutBoundLineV2.getWarehouseId().toArray()));
        }
        if (findPreOutBoundLineV2.getLanguageId() != null && !findPreOutBoundLineV2.getLanguageId().isEmpty()) {
            periodicLineQuery = periodicLineQuery.filter(col("LANG_ID").isin(findPreOutBoundLineV2.getLanguageId().toArray()));
        }
        if (findPreOutBoundLineV2.getCompanyCodeId() != null && !findPreOutBoundLineV2.getCompanyCodeId().isEmpty()) {
            periodicLineQuery = periodicLineQuery.filter(col("C_ID").isin(findPreOutBoundLineV2.getCompanyCodeId().toArray()));
        }
        if (findPreOutBoundLineV2.getPlantId() != null && !findPreOutBoundLineV2.getPlantId().isEmpty()) {
            periodicLineQuery = periodicLineQuery.filter(col("PLANT_ID").isin(findPreOutBoundLineV2.getPlantId().toArray()));
        }
        if (findPreOutBoundLineV2.getRefDocNumber() != null && !findPreOutBoundLineV2.getRefDocNumber().isEmpty()) {
            periodicLineQuery = periodicLineQuery.filter(col("REF_DOC_NO").isin(findPreOutBoundLineV2.getRefDocNumber().toArray()));
        }
        if (findPreOutBoundLineV2.getPreOutboundNo() != null && !findPreOutBoundLineV2.getPreOutboundNo().isEmpty()) {
            periodicLineQuery = periodicLineQuery.filter(col("PRE_OB_NO").isin(findPreOutBoundLineV2.getPreOutboundNo().toArray()));
        }
        if (findPreOutBoundLineV2.getLineNumber() != null && !findPreOutBoundLineV2.getLineNumber().isEmpty()) {
            List<String> lineNumberString = findPreOutBoundLineV2.getLineNumber().stream().map(String::valueOf).collect(Collectors.toList());
            periodicLineQuery = periodicLineQuery.filter(col("STATUS_ID").isin(lineNumberString.toArray()));
        }

        if (findPreOutBoundLineV2.getItemCode() != null && !findPreOutBoundLineV2.getItemCode().isEmpty()) {
            periodicLineQuery = periodicLineQuery.filter(col("ITM_CODE").isin(findPreOutBoundLineV2.getItemCode().toArray()));
        }


        Encoder<PreOutBoundLineV2> preOutBoundLineV2Encoder = Encoders.bean(PreOutBoundLineV2.class);
        Dataset<PreOutBoundLineV2> datasetControlGroup = periodicLineQuery.as(preOutBoundLineV2Encoder);
        List<PreOutBoundLineV2> results = datasetControlGroup.collectAsList();
        return results;
    }
}