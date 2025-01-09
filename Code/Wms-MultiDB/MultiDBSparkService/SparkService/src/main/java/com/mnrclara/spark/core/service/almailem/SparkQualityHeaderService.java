package com.mnrclara.spark.core.service.almailem;

import com.mnrclara.spark.core.model.Almailem.FindQualityHeaderV2;
import com.mnrclara.spark.core.model.Almailem.QualityHeaderV2;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;

@Service
@Slf4j
public class SparkQualityHeaderService {
    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SparkQualityHeaderService() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        sparkSession = SparkSession.builder().master("local[*]").appName("QualityHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://3.109.20.248;databaseName=WMS_ALMDEV", "tblqualityheader", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblqualityheaderv2");
    }

    /**
     * @param findQualityHeaderV2
     * @return
     * @throws ParseException
     */
    public List<QualityHeaderV2> findQualityHeaderV2(FindQualityHeaderV2 findQualityHeaderV2) throws ParseException {

        Dataset<Row> imQualityHeaderQuery = sparkSession.sql("SELECT "
                                                                     + "LANG_ID as languageId, "
                                                                     + "C_ID as companyCodeId, "
                                                                     + "PLANT_ID as plantId, "
                                                                     + "WH_ID as warehouseId, "
                                                                     + "PRE_OB_NO as preOutboundNo, "
                                                                     + "REF_DOC_NO as refDocNumber, "
                                                                     + "PARTNER_CODE as partnerCode, "
                                                                     + "PU_NO as pickupNumber, "
                                                                     + "QC_NO as qualityInspectionNo, "
                                                                     + "PICK_HE_NO as actualHeNo, "
                                                                     + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                                                                     + "STATUS_ID as statusId, "
                                                                     + "QC_TO_QTY as qcToQty, "
                                                                     + "QC_UOM as qcUom, "
                                                                     + "MFR_PART as manufacturerPartNo, "
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
                                                                     + "REMARK as remarks, "
                                                                     + "QC_CTD_BY as qualityCreatedBy, "
                                                                     + "QC_CTD_ON as qualityCreatedOn, "
                                                                     + "QC_CNF_BY as qualityConfirmedBy, "
                                                                     + "QC_CNF_ON as qualityConfirmedOn, "
                                                                     + "QC_UTD_BY as qualityUpdatedBy, "
                                                                     + "QC_UTD_ON as qualityUpdatedOn, "
                                                                     + "QC_REV_BY as qualityReversedBy, "
                                                                     + "QC_REV_ON as qualityReversedOn, "

                                                                     // V2 fields
                                                                     + "C_TEXT as companyDescription, "
                                                                     + "PLANT_TEXT as plantDescription, "
                                                                     + "WH_TEXT as warehouseDescription, "
                                                                     + "STATUS_TEXT as statusDescription, "
                                                                     + "MIDDLEWARE_ID as middlewareId, "
                                                                     + "MIDDLEWARE_TABLE as middlewareTable, "
                                                                     + "REF_DOC_TYPE as referenceDocumentType, "
                                                                     + "SALES_ORDER_NUMBER as salesOrderNumber, "
                                                                     + "SALES_INVOICE_NUMBER as salesInvoiceNumber, "
                                                                     + "SUPPLIER_INVOICE_NO as supplierInvoiceNo, "
                                                                     + "PICK_LIST_NUMBER as pickListNumber, "
                                                                     + "TOKEN_NUMBER as tokenNumber, "
                                                                     + "MFR_NAME as manufacturerName, "
                                                                     + "TARGET_BRANCH_CODE as targetBranchCode, "
                                                                     + "CSTR_COD as customerCode, "
                                                                     + "TFR_REQ_TYP as TransferRequestType, "
                                                                     + "IMS_SALE_TYP_CODE as imsSaleTypeCode "
                                                                     + "FROM tblqualityheaderv2 WHERE IS_DELETED = 0 ");

        if (findQualityHeaderV2.getRefDocNumber() != null && !findQualityHeaderV2.getRefDocNumber().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("REF_DOC_NO").isin(findQualityHeaderV2.getRefDocNumber().toArray()));
        }
        if (findQualityHeaderV2.getPartnerCode() != null && !findQualityHeaderV2.getPartnerCode().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("PARTNER_CODE").isin(findQualityHeaderV2.getPartnerCode().toArray()));
        }
        if (findQualityHeaderV2.getQualityInspectionNo() != null && !findQualityHeaderV2.getQualityInspectionNo().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("QC_NO").isin(findQualityHeaderV2.getQualityInspectionNo().toArray()));
        }
        if (findQualityHeaderV2.getActualHeNo() != null && !findQualityHeaderV2.getActualHeNo().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("PICK_HE_NO").isin(findQualityHeaderV2.getActualHeNo().toArray()));
        }
        if (findQualityHeaderV2.getOutboundOrderTypeId() != null && !findQualityHeaderV2.getOutboundOrderTypeId().isEmpty()) {
            List<String> outboundOrderString = findQualityHeaderV2.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("OB_ORD_TYP_ID").isin(outboundOrderString.toArray()));
        }
        if (findQualityHeaderV2.getStatusId() != null && !findQualityHeaderV2.getStatusId().isEmpty()) {
            List<String> statusString = findQualityHeaderV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findQualityHeaderV2.getSoType() != null && !findQualityHeaderV2.getSoType().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("REF_FIELD_1").isin(findQualityHeaderV2.getSoType().toArray()));
        }
        if (findQualityHeaderV2.getWarehouseId() != null && !findQualityHeaderV2.getWarehouseId().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("WH_ID").isin(findQualityHeaderV2.getWarehouseId().toArray()));
        }
        if (findQualityHeaderV2.getPreOutboundNo() != null && !findQualityHeaderV2.getPreOutboundNo().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("PRE_OB_NO").isin(findQualityHeaderV2.getPreOutboundNo().toArray()));
        }
        if (findQualityHeaderV2.getStartQualityCreatedOn() != null) {
            Date startQuality = findQualityHeaderV2.getStartQualityCreatedOn();
            startQuality = org.apache.commons.lang3.time.DateUtils.truncate(startQuality, Calendar.DAY_OF_MONTH);
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("QC_CTD_ON").$greater$eq(dateFormat.format(startQuality)));
        }
        if (findQualityHeaderV2.getEndQualityCreatedOn() != null) {
            Date endQualityDate = findQualityHeaderV2.getEndQualityCreatedOn();
            endQualityDate = org.apache.commons.lang3.time.DateUtils.ceiling(endQualityDate, Calendar.DAY_OF_MONTH);
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("QC_CTD_ON").$less$eq(dateFormat.format(endQualityDate)));
        }

        // V2 fields
        if (findQualityHeaderV2.getLanguageId() != null && !findQualityHeaderV2.getLanguageId().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("LANG_ID").isin(findQualityHeaderV2.getLanguageId().toArray()));
        }
        if (findQualityHeaderV2.getCompanyCodeId() != null && !findQualityHeaderV2.getCompanyCodeId().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("C_ID").isin(findQualityHeaderV2.getCompanyCodeId().toArray()));
        }
        if (findQualityHeaderV2.getPlantId() != null && !findQualityHeaderV2.getPlantId().isEmpty()) {
            imQualityHeaderQuery = imQualityHeaderQuery.filter(col("PLANT_ID").isin(findQualityHeaderV2.getPlantId().toArray()));
        }

        Encoder<QualityHeaderV2> qualityHeaderEncoder = Encoders.bean(QualityHeaderV2.class);
        Dataset<QualityHeaderV2> dataSetControlGroup = imQualityHeaderQuery.as(qualityHeaderEncoder);
        List<QualityHeaderV2> result = dataSetControlGroup.collectAsList();
        return result;
    }

}