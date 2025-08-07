package com.mnrclara.spark.core.service.amgra;

import com.mnrclara.spark.core.model.Almailem.GrHeaderV2;
import com.mnrclara.spark.core.model.Almailem.SearchGrHeaderV2;
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
public class GrHeaderService {

    Properties conProp = new Properties();

    SparkSession sparkSession = null;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GrHeaderService() throws ParseException {
        //connection properties
        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conProp.put("user", "sa");
        conProp.put("password", "4V7lOXaxgAi3i6mgJL7qBUSPM");
        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://10.10.10.61;databaseName=WMS_ALMPRD", "tblgrheader", conProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblgrheaderV3");
    }

    public List<GrHeaderV2> findGrHeaderV2(SearchGrHeaderV2 searchGrHeaderV2) throws ParseException {

        Dataset<Row> imgrHeaderQueryV2 = sparkSession.sql("SELECT "
                + "LANG_ID as languageId, "
                + "C_ID as companyCode, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "PRE_IB_NO as preInboundNo, "
                + "REF_DOC_NO as refDocNumber, "
                + "STG_NO as stagingNo, "
                + "GR_NO as goodsReceiptNo, "
                + "PAL_CODE as palletCode, "
                + "CASE_CODE as caseCode, "
                + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                + "STATUS_ID as statusId, "
                + "GR_MTD as grMethod, "
                + "CONT_REC_NO as containerReceiptNo, "
                + "DOCK_ALL_NO as dockAllocationNo, "
                + "CONT_NO as containerNo, "
                + "VEH_NO as vehicleNo, "
                + "EA_DATE as expectedArrivalDate, "
                + "GR_DATE as goodsReceiptDate, "
                + "IS_DELETED as deletionIndicator, "
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
                + "GR_CTD_BY as createdBy, "
                + "GR_CTD_ON as createdOn, "
                + "GR_UTD_BY as updatedBy, "
                + "GR_UTD_ON as updatedOn, "
                + "GR_CNF_BY as confirmedBy, "
                + "GR_CNF_ON as confirmedOn, "

                + "ACCEPT_QTY as acceptedQuantity, "
                + "DAMAGE_QTY as damagedQuantity, "
                + "C_TEXT as companyDescription, "
                + "PLANT_TEXT as plantDescription, "
                + "WH_TEXT as warehouseDescription, "
                + "STATUS_TEXT as statusDescription, "
                + "MIDDLEWARE_ID as middlewareId, "
                + "MIDDLEWARE_TABLE as middlewareTable, "
                + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                + "CSTR_COD as customerCode, "
                + "TFR_REQ_TYP as TransferRequestType, "
                + "AMS_SUP_INV as AMSSupplierInvoiceNo, "
                + "REF_DOC_TYPE as referenceDocumentType "
                + "FROM tblgrheaderV3 WHERE IS_DELETED = 0");

//        imgrHeaderQueryV2.cache();


        if (searchGrHeaderV2.getInboundOrderTypeId() != null && !searchGrHeaderV2.getInboundOrderTypeId().isEmpty()) {
            List<String> inboundOrderIdStrings = searchGrHeaderV2.getInboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("IB_ORD_TYP_ID").isin(inboundOrderIdStrings.toArray()));
        }
        if (searchGrHeaderV2.getGoodsReceiptNo() != null && !searchGrHeaderV2.getGoodsReceiptNo().isEmpty()) {
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("GR_NO").isin(searchGrHeaderV2.getGoodsReceiptNo().toArray()));
        }
        if (searchGrHeaderV2.getPreInboundNo() != null && !searchGrHeaderV2.getPreInboundNo().isEmpty()) {
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("PRE_IB_NO").isin(searchGrHeaderV2.getPreInboundNo().toArray()));
        }
        if (searchGrHeaderV2.getRefDocNumber() != null && !searchGrHeaderV2.getRefDocNumber().isEmpty()) {
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("REF_DOC_NO").isin(searchGrHeaderV2.getRefDocNumber().toArray()));
        }
        if (searchGrHeaderV2.getStatusId() != null && !searchGrHeaderV2.getStatusId().isEmpty()) {
            List<String> statusIdStrings = searchGrHeaderV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
        }
        if (searchGrHeaderV2.getWarehouseId() != null && !searchGrHeaderV2.getWarehouseId().isEmpty()) {
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("WH_ID").isin(searchGrHeaderV2.getWarehouseId().toArray()));
        }
        if (searchGrHeaderV2.getLanguageId() != null && !searchGrHeaderV2.getLanguageId().isEmpty()) {
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("LANG_ID").isin(searchGrHeaderV2.getLanguageId().toArray()));
        }
        if (searchGrHeaderV2.getCompanyCodeId() != null && !searchGrHeaderV2.getCompanyCodeId().isEmpty()) {
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("C_ID").isin(searchGrHeaderV2.getCompanyCodeId().toArray()));
        }
        if (searchGrHeaderV2.getPlantId() != null && !searchGrHeaderV2.getPlantId().isEmpty()) {
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("PLANT_ID").isin(searchGrHeaderV2.getPlantId().toArray()));
        }
        if (searchGrHeaderV2.getCaseCode() != null && !searchGrHeaderV2.getCaseCode().isEmpty()) {
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("CASE_CODE").isin(searchGrHeaderV2.getCaseCode().toArray()));
        }
        if (searchGrHeaderV2.getCreatedBy() != null && !searchGrHeaderV2.getCreatedBy().isEmpty()) {
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("GR_CTD_BY").isin(searchGrHeaderV2.getCreatedBy().toArray()));
        }
        if (searchGrHeaderV2.getStartCreatedOn() != null) {
            Date startDate = searchGrHeaderV2.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("GR_CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (searchGrHeaderV2.getEndCreatedOn() != null) {
            Date endDate = searchGrHeaderV2.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            imgrHeaderQueryV2 = imgrHeaderQueryV2.filter(col("GR_CTD_ON").$less$eq(dateFormat.format(endDate)));
        }

        Encoder<GrHeaderV2> GrHeaderEncoderV2 = Encoders.bean(GrHeaderV2.class);
        Dataset<GrHeaderV2> dataSetControlGroup = imgrHeaderQueryV2.as(GrHeaderEncoderV2);
        List<GrHeaderV2> result = dataSetControlGroup.collectAsList();

        return result;
    }
}
