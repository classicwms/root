package com.mnrclara.spark.core.service;


import com.mnrclara.spark.core.model.Almailem.FindPreInboundHeaderV2;
import com.mnrclara.spark.core.model.Almailem.PreInboundHeaderV2;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
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
public class TPLSparkPreInboundHeaderService {
    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TPLSparkPreInboundHeaderService() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "gQ3PmSL5D7u32Vtw5F74UwWH");
        sparkSession = SparkSession.builder().master("local[*]").appName("PreInboundHeader.com") .config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://10.10.18.14;databaseName=WMS_3PL", "tblpreinboundheader", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblpreinboundheaderv6");

    }

    public List<PreInboundHeaderV2> findPreInboundHeaderv2(FindPreInboundHeaderV2 findPreInboundHeaderV2) throws ParseException {
        Dataset<Row> imPreInboundHeaderQueryv2 = sparkSession.sql("SELECT "
                + "LANG_ID as languageId, "
                + "C_ID as companyCode, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "PRE_IB_NO as preInboundNo, "
                + "REF_DOC_NO as refDocNumber, "
                + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                + "REF_DOC_TYP as referenceDocumentType, "
                + "STATUS_ID as statusId, "
                + "CONT_NO as containerNo, "
                + "NO_CONTAINERS as noOfContainers, "
                + "CONT_TYP as containerType, "
                + "REF_DOC_DATE as refDocDate, "
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
                + "CTD_BY as createdBy, "
                + "CTD_ON as createdOn, "
                + "UTD_BY as updatedBy, "
                + "UTD_ON as updatedOn, "
                + "C_TEXT as companyDescription, "
                + "PLANT_TEXT as plantDescription, "
                + "WH_TEXT as warehouseDescription, "
                + "STATUS_TEXT as statusDescription, "
                + "MIDDLEWARE_ID as middlewareId, "
                + "MIDDLEWARE_TABLE as middlewareTable, "
                + "CSTR_COD as customerCode, "
                + "TFR_REQ_TYP as TransferRequestType, "
                + "AMS_SUP_INV as AMSSupplierInvoiceNo, "
                + "MANUFACTURER_FULL_NAME as manufacturerFullName "

                + "FROM tblpreinboundheaderv6 WHERE IS_DELETED = 0");
//        imPreInboundHeaderQueryv2.cache();


        if (findPreInboundHeaderV2.getLanguageId() != null && !findPreInboundHeaderV2.getLanguageId().isEmpty()) {
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("LANG_ID").isin(findPreInboundHeaderV2.getLanguageId().toArray()));
        }
        if (findPreInboundHeaderV2.getCompanyCodeId() != null && !findPreInboundHeaderV2.getCompanyCodeId().isEmpty()) {
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("C_ID").isin(findPreInboundHeaderV2.getCompanyCodeId().toArray()));
        }
        if (findPreInboundHeaderV2.getPlantId() != null && !findPreInboundHeaderV2.getPlantId().isEmpty()) {
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("PLANT_ID").isin(findPreInboundHeaderV2.getPlantId().toArray()));
        }
        if (findPreInboundHeaderV2.getWarehouseId() != null && !findPreInboundHeaderV2.getWarehouseId().isEmpty()) {
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("WH_ID").isin(findPreInboundHeaderV2.getWarehouseId().toArray()));
        }
        if (findPreInboundHeaderV2.getPreInboundNo() != null && !findPreInboundHeaderV2.getPreInboundNo().isEmpty()) {
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("PRE_IB_NO").isin(findPreInboundHeaderV2.getPreInboundNo().toArray()));
        }
        if (findPreInboundHeaderV2.getInboundOrderTypeId() != null && !findPreInboundHeaderV2.getInboundOrderTypeId().isEmpty()) {
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("IB_ORD_TYP_ID").isin(findPreInboundHeaderV2.getInboundOrderTypeId().toArray()));
        }
        if (findPreInboundHeaderV2.getRefDocNumber() != null && !findPreInboundHeaderV2.getRefDocNumber().isEmpty()) {
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("REF_DOC_NO").isin(findPreInboundHeaderV2.getRefDocNumber().toArray()));
        }
        if (findPreInboundHeaderV2.getPartnerCode() != null && !findPreInboundHeaderV2.getPartnerCode().isEmpty()) {
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("CSTR_COD").isin(findPreInboundHeaderV2.getPartnerCode().toArray()));
        }
        if (findPreInboundHeaderV2.getStatusId() != null && !findPreInboundHeaderV2.getStatusId().isEmpty()) {
            List<String> statusIdStrings = findPreInboundHeaderV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
        }
        if (findPreInboundHeaderV2.getStartCreatedOn() != null) {
            Date startDate = findPreInboundHeaderV2.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findPreInboundHeaderV2.getEndCreatedOn() != null) {
            Date endDate = findPreInboundHeaderV2.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            imPreInboundHeaderQueryv2 = imPreInboundHeaderQueryv2.filter(col("CTD_ON").$less$eq(dateFormat.format(endDate)));
        }

        Encoder<PreInboundHeaderV2> PreInboundHeaderEncoder = Encoders.bean(PreInboundHeaderV2.class);
        Dataset<PreInboundHeaderV2> dataSetControlGroup = imPreInboundHeaderQueryv2.as(PreInboundHeaderEncoder);
        List<PreInboundHeaderV2> result = dataSetControlGroup.collectAsList();

        return result;
    }
}