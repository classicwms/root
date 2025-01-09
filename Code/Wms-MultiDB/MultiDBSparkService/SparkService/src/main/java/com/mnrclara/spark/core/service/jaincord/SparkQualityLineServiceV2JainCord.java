package com.mnrclara.spark.core.service.jaincord;

import com.mnrclara.spark.core.model.wmscorev2.FindQualityLineV2;
import com.mnrclara.spark.core.model.wmscorev2.QualityLineV2;
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
public class SparkQualityLineServiceV2JainCord {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");

    public SparkQualityLineServiceV2JainCord() throws ParseException {
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
        sparkSession = SparkSession.builder().master("local[*]").appName("QualityLineExample.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblqualityline", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblqualitylinev5jain");


    }

    /**
     *
     * @param findQualityLineV2
     * @return
     * @throws ParseException
     */
    public List<QualityLineV2> findQualityLineV2(FindQualityLineV2 findQualityLineV2) throws ParseException {
        Dataset<Row>
                qualityLineQueryV2 = sparkSession.sql("SELECT " +
                "LANG_ID as languageId," +
                "C_ID as companyCodeId," +
                "PLANT_ID as plantId," +
                "WH_ID as warehouseId," +
                "PRE_OB_NO as preOutboundNo," +
                "REF_DOC_NO as refDocNumber," +
                "PARTNER_CODE as partnerCode," +
                "OB_LINE_NO as lineNumber ," +
                "QC_NO as qualityInspectionNo," +
                "ITM_CODE as itemCode ," +
                "PICK_HE_NO as actualHeNo ," +
                "PICK_PACK_BARCODE as pickPackBarCode ," +
                "OB_ORD_TYP_ID as outboundOrderTypeId ," +
                "STATUS_ID as statusId ," +
                "STCK_TYP_ID as stockTypeId ," +
                "SP_ST_IND_ID as specialStockIndicatorId ," +
                "ITEM_TEXT as description ," +
                "MFR_PART as manufacturerPartNo ," +
                "PACK_MT_NO as packingMaterialNo ," +
                "VAR_ID as variantCode," +
                "VAR_SUB_ID as variantSubCode ," +
                "STR_NO as batchSerialNumber ," +
                "QC_QTY as qualityQty ," +
                "PICK_CNF_QTY as pickConfirmQty ," +
                "QC_UOM as qualityConfirmUom ," +
                "REJ_QTY as rejectQty ," +
                "REJ_UOM as rejectUom ," +
                "REF_FIELD_1 as referenceField1 ," +
                "REF_FIELD_2 as referenceField2 ," +
                "REF_FIELD_3 as referenceField3 ," +
                "REF_FIELD_4 as referenceField4 ," +
                "REF_FIELD_5 as referenceField5 ," +
                "REF_FIELD_6 as referenceField6 ," +
                "REF_FIELD_7 as referenceField7 ," +
                "REF_FIELD_8 as referenceField8 ," +
                "REF_FIELD_9 as referenceField9 ," +
                "REF_FIELD_10 as referenceField10 ," +
                "IS_DELETED as deletionIndicator ," +
                "QC_CTD_BY as qualityCreatedBy ," +
                "QC_CTD_ON as qualityCreatedOn ," +
                "QC_CNF_BY as qualityConfirmedBy ," +
                "QC_CNF_ON as qualityConfirmedOn ," +
                "QC_UTD_BY as qualityUpdatedBy ," +
                "QC_UTD_ON as qualityUpdatedOn ," +
                "QC_REV_BY as qualityReversedBy ," +
                "QC_REV_ON as qualityReversedOn ," +
                "C_TEXT as companyDescription ," +
                "PLANT_TEXT as plantDescription ," +
                "WH_TEXT as warehouseDescription ," +
                "STATUS_TEXT as statusDescription ," +
                "PARTNER_ITEM_BARCODE as barcodeId ," +
                "MIDDLEWARE_ID as middlewareId ," +
                "MIDDLEWARE_HEADER_ID as middlewareHeaderId ," +
                "MIDDLEWARE_TABLE as middlewareTable ," +
                "REF_DOC_TYPE as referenceDocumentType ," +
                "SALES_INVOICE_NUMBER as salesInvoiceNumber ," +
                "SUPPLIER_INVOICE_NO as supplierInvoiceNo ," +
                "SALES_ORDER_NUMBER as salesOrderNumber ," +
                "MANUFACTURER_FULL_NAME as manufacturerFullName ," +
                "PICK_LIST_NUMBER as pickListNumber ," +
                "TOKEN_NUMBER as tokenNumber ," +
                "MANUFACTURER_NAME as manufacturerName, " +
                "CUSTOMER_ID as customerId, " +
                "CUSTOMER_NAME as customerName " +
                "FROM tblqualitylinev5jain WHERE IS_DELETED = 0 ");

//        qualityLineQueryV2.cache();

        if (findQualityLineV2.getLanguageId() != null && !findQualityLineV2.getLanguageId().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("LANG_ID").isin(findQualityLineV2.getLanguageId().toArray()));
        }
        if (findQualityLineV2.getCompanyCodeId() != null && !findQualityLineV2.getCompanyCodeId().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("C_ID").isin(findQualityLineV2.getCompanyCodeId().toArray()));
        }
        if (findQualityLineV2.getPlantId() != null && !findQualityLineV2.getPlantId().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("PLANT_ID").isin(findQualityLineV2.getPlantId().toArray()));
        }
        if (findQualityLineV2.getWarehouseId() != null && !findQualityLineV2.getWarehouseId().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("WH_ID").isin(findQualityLineV2.getWarehouseId().toArray()));
        }
        if (findQualityLineV2.getRefDocNumber() != null && !findQualityLineV2.getRefDocNumber().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("REF_DOC_NO").isin(findQualityLineV2.getRefDocNumber().toArray()));
        }
        if (findQualityLineV2.getPartnerCode() != null && !findQualityLineV2.getPartnerCode().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("PARTNER_CODE").isin(findQualityLineV2.getPartnerCode().toArray()));
        }
        if (findQualityLineV2.getQualityInspectionNo() != null && !findQualityLineV2.getQualityInspectionNo().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("QC_NO").isin(findQualityLineV2.getQualityInspectionNo().toArray()));
        }
        if (findQualityLineV2.getStatusId() != null && !findQualityLineV2.getStatusId().isEmpty()) {
            List<String> statusString = findQualityLineV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findQualityLineV2.getPreOutboundNo() != null && !findQualityLineV2.getPreOutboundNo().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("PRE_OB_NO").isin(findQualityLineV2.getPreOutboundNo().toArray()));
        }
        if (findQualityLineV2.getLineNumber() != null && !findQualityLineV2.getLineNumber().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("OB_LINE_NO").isin(findQualityLineV2.getLineNumber().toArray()));
        }
        if (findQualityLineV2.getItemCode() != null && !findQualityLineV2.getItemCode().isEmpty()) {
            qualityLineQueryV2 = qualityLineQueryV2.filter(col("ITM_CODE").isin(findQualityLineV2.getItemCode().toArray()));
        }


        Encoder<QualityLineV2> QualityLineEncoder = Encoders.bean(QualityLineV2.class);
        Dataset<QualityLineV2> dataSetControlGroup = qualityLineQueryV2.as(QualityLineEncoder);
        List<QualityLineV2> result = dataSetControlGroup.collectAsList();

        return result;
    }
}