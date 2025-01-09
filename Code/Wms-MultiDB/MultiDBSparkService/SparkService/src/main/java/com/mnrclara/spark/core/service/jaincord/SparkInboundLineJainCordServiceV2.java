package com.mnrclara.spark.core.service.jaincord;

import com.mnrclara.spark.core.model.wmscorev2.FindInboundLineV2;
import com.mnrclara.spark.core.model.wmscorev2.InboundLineV2;
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

@Slf4j
@Service
public class SparkInboundLineJainCordServiceV2 {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SparkInboundLineJainCordServiceV2() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
        sparkSession = SparkSession.builder().master("local[*]").appName("InboundLine.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblinboundline", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblinboundlinev5jain");
    }

    public List<InboundLineV2> findInboundLineV2(FindInboundLineV2 findInboundLineV2) throws ParseException {

        Dataset<Row> inboundLineV2Query = sparkSession.sql("SELECT "
                + "LANG_ID as languageId, "
                + "C_ID as companyCode, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "REF_DOC_NO as refDocNumber, "
                + "PRE_IB_NO as preInboundNo, "
                + "IB_LINE_NO as [lineNo], "
                + "ITM_CODE as itemCode, "
                + "ORD_QTY as orderQty, "
                + "ORD_UOM as orderUom, "
                + "ACCEPT_QTY as acceptedQty, "
                + "DAMAGE_QTY as damageQty, "
                + "PA_CNF_QTY as putawayConfirmedQty, "
                + "VAR_QTY as varianceQty, "
                + "VAR_ID as variantCode, "
                + "VAR_SUB_ID as variantSubCode, "
                + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                + "STCK_TYP_ID as stockTypeId, "
                + "SP_ST_IND_ID as specialStockIndicatorId, "
                + "REF_ORD_NO as referenceOrderNo, "
                + "STATUS_ID as statusId, "
                + "PARTNER_CODE as vendorCode, "
                + "EA_DATE as expectedArrivalDate, "
                + "CONT_NO as containerNo, "
                + "INV_NO as invoiceNo, "
                + "TEXT as description, "
                + "MFR_PART as manufacturerPartNo, "
                + "HSN_CODE as hsnCode, "
                + "ITM_BARCODE as itemBarcode, "
                + "ITM_CASE_QTY as itemCaseQty, "
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
                + "IB_CNF_BY as confirmedBy, "
                + "IB_CNF_ON as confirmedOn, "

                // V2 fields
                + "C_TEXT as companyDescription, "
                + "PLANT_TEXT as plantDescription, "
                + "WH_TEXT as warehouseDescription, "
                + "STATUS_TEXT as statusDescription, "
                + "MFR_CODE as manufacturerCode, "
                + "MFR_NAME as manufacturerName, "
                + "MIDDLEWARE_ID as middlewareId, "
                + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                + "MIDDLEWARE_TABLE as middlewareTable, "
                + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                + "REF_DOC_TYPE as referenceDocumentType, "
                + "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, "
                + "SUPPLIER_NAME as supplierName, "
                + "BRANCH_CODE as branchCode, "
                + "TRANSFER_ORDER_NO as transferOrderNo, "
                + "IS_COMPLETED as isCompleted "
                + "From tblinboundlinev5jain where IS_DELETED = 0 "
        );
//        inboundLineV2Query.cache();

        if (findInboundLineV2.getWarehouseId() != null && !findInboundLineV2.getWarehouseId().isEmpty()) {
            inboundLineV2Query = inboundLineV2Query.filter(col("WH_ID").isin(findInboundLineV2.getWarehouseId().toArray()));
        }
        if (findInboundLineV2.getReferenceField1() != null && !findInboundLineV2.getReferenceField1().isEmpty()) {
            inboundLineV2Query = inboundLineV2Query.filter(col("REF_FIELD_1").isin(findInboundLineV2.getReferenceField1().toArray()));
        }
        if (findInboundLineV2.getStatusId() != null && !findInboundLineV2.getStatusId().isEmpty()) {
            List<String> headerStatusIdStrings = findInboundLineV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            inboundLineV2Query = inboundLineV2Query.filter(col("STATUS_ID").isin(headerStatusIdStrings.toArray()));
        }
        if (findInboundLineV2.getItemCode() != null && !findInboundLineV2.getItemCode().isEmpty()) {
            inboundLineV2Query = inboundLineV2Query.filter(col("ITM_CODE").isin(findInboundLineV2.getItemCode().toArray()));
        }
        if (findInboundLineV2.getManufacturerPartNo() != null && !findInboundLineV2.getManufacturerPartNo().isEmpty()) {
            inboundLineV2Query = inboundLineV2Query.filter(col("MFR_PART").isin(findInboundLineV2.getManufacturerPartNo().toArray()));
        }
        if (findInboundLineV2.getRefDocNumber() != null && !findInboundLineV2.getRefDocNumber().isEmpty()) {
            inboundLineV2Query = inboundLineV2Query.filter(col("REF_DOC_NO").isin(findInboundLineV2.getRefDocNumber().toArray()));
        }
        if (findInboundLineV2.getStartConfirmedOn() != null) {
            Date startDate = findInboundLineV2.getStartConfirmedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            inboundLineV2Query = inboundLineV2Query.filter(col("IB_CNF_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findInboundLineV2.getEndConfirmedOn() != null) {
            Date endDate = findInboundLineV2.getEndConfirmedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            inboundLineV2Query = inboundLineV2Query.filter(col("IB_CNF_ON").$greater$eq(dateFormat.format(endDate)));
        }

        // V2 fields
        if (findInboundLineV2.getLanguageId() != null && !findInboundLineV2.getLanguageId().isEmpty()) {
            inboundLineV2Query = inboundLineV2Query.filter(col("LANG_ID").isin(findInboundLineV2.getLanguageId().toArray()));
        }
        if (findInboundLineV2.getCompanyCodeId() != null && !findInboundLineV2.getCompanyCodeId().isEmpty()) {
            inboundLineV2Query = inboundLineV2Query.filter(col("C_ID").isin(findInboundLineV2.getCompanyCodeId().toArray()));
        }
        if (findInboundLineV2.getPlantId() != null && !findInboundLineV2.getPlantId().isEmpty()) {
            inboundLineV2Query = inboundLineV2Query.filter(col("PLANT_ID").isin(findInboundLineV2.getPlantId().toArray()));
        }


        Encoder<InboundLineV2> inboundLineEncoder = Encoders.bean(InboundLineV2.class);
        Dataset<InboundLineV2> datasetControlGroup = inboundLineV2Query.as(inboundLineEncoder);
        List<InboundLineV2> results = datasetControlGroup.collectAsList();
        return results;
    }
}