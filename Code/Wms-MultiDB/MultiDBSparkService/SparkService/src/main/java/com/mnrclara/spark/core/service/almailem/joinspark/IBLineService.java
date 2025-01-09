package com.mnrclara.spark.core.service.almailem.joinspark;

import com.mnrclara.spark.core.model.Almailem.FindInboundLineV2;
import com.mnrclara.spark.core.model.Almailem.joinspark.InboundLineV3;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;

@Service
@Slf4j
public class IBLineService {

    private Properties connProp;
    private SparkSession sparkSession;

    private Dataset<Row> inboundHeader;
    private Dataset<Row> inboundLine;
    private Dataset<Row> joined2 = null;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public IBLineService() {
        sparkSession = SparkSession.builder().master("local[*]").appName("JntHubCode.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        // Connection Properties
        connProp = new Properties();
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");

        // Read from SQL Server Table
        inboundLine = readJdbcTable("tblinboundline");
        inboundHeader = readJdbcTable("tblinboundheader");
        inboundLine.createOrReplaceTempView("tblinboundlinev2");
        inboundHeader.createOrReplaceTempView("tblinboundheaderv2");
    }

    private Dataset<Row> readJdbcTable(String tableName) {
        return sparkSession.read().jdbc("jdbc:sqlserver://3.109.20.248;databaseName=WMS_ALMDEV", tableName, connProp);
    }

    // FindInboundLine
    public List<InboundLineV3> findInboundLine(FindInboundLineV2 findInboundLineV2) throws Exception {

        joined2 = inboundLine.join(
                inboundHeader,
                inboundLine.col("REF_DOC_NO").equalTo(inboundHeader.col("REF_DOC_NO")),
                "left"
        ).select(
//                inboundLine.col("C_ID").alias("companyCode"),
//                inboundLine.col("PLANT_ID").alias("plantId"),
//                inboundLine.col("LANG_ID").alias("languageId"),
//                inboundLine.col("WH_ID").alias("warehouseId"),
                inboundLine.col("REF_DOC_NO").alias("refDocNumber"),
//                inboundLine.col("PRE_IB_NO").alias("preInboundNo"),
                inboundLine.col("IB_LINE_NO").alias("lineNo"),
                inboundLine.col("ITM_CODE").alias("itemCode"),
                inboundLine.col("ORD_QTY").alias("orderQty"),
//                inboundLine.col("ORD_UOM").alias("orderUom"),
                inboundLine.col("ACCEPT_QTY").alias("acceptedQty"),
                inboundLine.col("DAMAGE_QTY").alias("damageQty"),
//                inboundLine.col("PA_CNF_QTY").alias("putawayConfirmedQty"),
                inboundLine.col("VAR_QTY").alias("varianceQty"),
//                inboundLine.col("VAR_ID").alias("variantCode"),
//                inboundLine.col("VAR_SUB_ID").alias("variantSubCode"),
//                inboundLine.col("IB_ORD_TYP_ID").alias("inboundOrderTypeId"),
//                inboundLine.col("STCK_TYP_ID").alias("stockTypeId"),
//                inboundLine.col("SP_ST_IND_ID").alias("specialStockIndicatorId"),
//                inboundLine.col("REF_ORD_NO").alias("referenceOrderNo"),
//                inboundLine.col("STATUS_ID").alias("statusId"),
//                inboundLine.col("PARTNER_CODE").alias("vendorCode"),
//                inboundLine.col("EA_DATE").alias("expectedArrivalDate"),
//                inboundLine.col("CONT_NO").alias("containerNo"),
//                inboundLine.col("INV_NO").alias("invoiceNo"),
                inboundLine.col("TEXT").alias("description"),
//                inboundLine.col("MFR_PART").alias("manufacturerPartNo"),
//                inboundLine.col("HSN_CODE").alias("hsnCode"),
//                inboundLine.col("ITM_BARCODE").alias("itemBarcode"),
//                inboundLine.col("ITM_CASE_QTY").alias("itemCaseQty"),
//                inboundLine.col("CTD_BY").alias("createdBy"),
                inboundLine.col("CTD_ON").alias("createdOn"),
//                inboundLine.col("UTD_BY").alias("updatedBy"),
//                inboundLine.col("UTD_ON").alias("updatedOn"),
//                inboundLine.col("IB_CNF_BY").alias("confirmedBy"),
                inboundLine.col("IB_CNF_ON").alias("confirmedOn"),
                inboundLine.col("C_TEXT").alias("companyDescription"),
                inboundLine.col("PLANT_TEXT").alias("plantDescription"),
                inboundLine.col("WH_TEXT").alias("warehouseDescription"),
                inboundLine.col("STATUS_TEXT").alias("statusDescription"),
//                inboundLine.col("MFR_CODE").alias("manufacturerCode"),
                inboundLine.col("MFR_NAME").alias("manufacturerName"),
//                inboundLine.col("MIDDLEWARE_ID").alias("middlewareId"),
//                inboundLine.col("MIDDLEWARE_HEADER_ID").alias("middlewareHeaderId"),
//                inboundLine.col("MIDDLEWARE_TABLE").alias("middlewareTable"),
//                inboundLine.col("MANUFACTURER_FULL_NAME").alias("manufacturerFullName"),
                inboundLine.col("REF_DOC_TYPE").alias("referenceDocumentType"),
//                inboundLine.col("PURCHASE_ORDER_NUMBER").alias("purchaseOrderNumber"),
//                inboundLine.col("SUPPLIER_NAME").alias("supplierName"),
//                inboundLine.col("BRANCH_CODE").alias("branchCode"),
//                inboundLine.col("TRANSFER_ORDER_NO").alias("transferOrderNo"),
//                inboundLine.col("IS_COMPLETED").alias("isCompleted"),
                inboundHeader.col("SOURCE_BRANCH_CODE").alias("sourceBranch")
        );
        joined2 = joined2.distinct();

        if (findInboundLineV2.getCompanyCodeId() != null && !findInboundLineV2.getCompanyCodeId().isEmpty()) {
            joined2 = joined2.filter(col("C_ID").isin(findInboundLineV2.getCompanyCodeId().toArray(new String[0]))).toDF();
        }
        if (findInboundLineV2.getPlantId() != null && !findInboundLineV2.getPlantId().isEmpty()) {
            joined2 = joined2.filter(col("PLANT_ID").isin(findInboundLineV2.getPlantId().toArray(new String[0]))).toDF();
        }
        if (findInboundLineV2.getLanguageId() != null && !findInboundLineV2.getLanguageId().isEmpty()) {
            joined2 = joined2.filter(col("LANG_ID").isin(findInboundLineV2.getLanguageId().toArray(new String[0]))).toDF();
        }
        if (findInboundLineV2.getWarehouseId() != null && !findInboundLineV2.getWarehouseId().isEmpty()) {
            joined2 = joined2.filter(col("WH_ID").isin(findInboundLineV2.getWarehouseId().toArray(new String[0]))).toDF();
        }
        if (findInboundLineV2.getManufacturerPartNo() != null && !findInboundLineV2.getManufacturerPartNo().isEmpty()) {
            joined2 = joined2.filter(col("MFR_PART").isin(findInboundLineV2.getManufacturerPartNo().toArray(new String[0]))).toDF();
        }
        if (findInboundLineV2.getItemCode() != null && !findInboundLineV2.getItemCode().isEmpty()) {
            joined2 = joined2.filter(col("itemCode").isin(findInboundLineV2.getItemCode().toArray(new String[0]))).toDF();
        }
        if (findInboundLineV2.getRefDocNumber() != null && !findInboundLineV2.getRefDocNumber().isEmpty()) {
            joined2 = joined2.filter(col("refDocNumber").isin(findInboundLineV2.getRefDocNumber().toArray(new String[0]))).toDF();
        }
        if (findInboundLineV2.getReferenceField1() != null && !findInboundLineV2.getReferenceField1().isEmpty()) {
            joined2 = joined2.filter(col("REF_FIELD_1").isin(findInboundLineV2.getReferenceField1().toArray(new String[0]))).toDF();
        }
//        if (findInboundLineV2.getSourceBranch() != null && !findInboundLineV2.getSourceBranch().isEmpty()) {
//            joined2 = joined2.filter(col("sourceBranch").isin(findInboundLineV2.getSourceBranch().toArray(new String[0]))).toDF();
//        }
        if (findInboundLineV2.getInboundOrderTypeId() != null && !findInboundLineV2.getInboundOrderTypeId().isEmpty()) {
            List<String> headerOrderTypeStrings = findInboundLineV2.getInboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            joined2 = joined2.filter(col("IB_ORD_TYP_ID").isin(headerOrderTypeStrings.toArray(new String[0]))).toDF();
        }
        if (findInboundLineV2.getStatusId() != null && !findInboundLineV2.getStatusId().isEmpty()) {
            List<String> headerStatusIdStrings = findInboundLineV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            joined2 = joined2.filter(col("STATUS_ID").isin(headerStatusIdStrings.toArray(new String[0]))).toDF();
        }
        if (findInboundLineV2.getStartConfirmedOn() != null) {
            Date startDate = findInboundLineV2.getStartConfirmedOn();
            startDate = DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            joined2 = joined2.filter(col("confirmedOn").$greater$eq(dateFormat.format(startDate)));
        }
        if (findInboundLineV2.getEndConfirmedOn() != null) {
            Date endDate = findInboundLineV2.getEndConfirmedOn();
            endDate = DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            joined2 = joined2.filter(col("confirmedOn").$less$eq(dateFormat.format(endDate)));
        }
        Encoder<InboundLineV3> encJntwebhook = Encoders.bean(InboundLineV3.class);
        Dataset<InboundLineV3> dsJntwebhook = joined2.as(encJntwebhook);
        return dsJntwebhook.collectAsList();

    }
}