package com.mnrclara.spark.core.service.joinspark;


import com.mnrclara.spark.core.model.Almailem.FindInboundHeaderV2;
import com.mnrclara.spark.core.model.Almailem.joinspark.InboundHeaderV3;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.sum;


@Service
@Slf4j
public class TPLIBHeaderJoinService {

    private Properties connProp;
    private SparkSession sparkSession;
    private Dataset<Row> inboundHeader;
    private Dataset<Row> inboundLine;
    private Dataset<Row> joined1;
    private Dataset<Row> filterVal;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TPLIBHeaderJoinService() {

        sparkSession = SparkSession.builder().master("local[*]")
                .appName("JntHubCode.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        // Connection Properties
        connProp = new Properties();
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "4V7lOXaxgAi3i6mgJL7qBUSPM");

        // Read from SQL Server Table
        inboundHeader = readJdbcTable("tblinboundheader");
        inboundLine = readJdbcTable("tblinboundline");
        inboundLine.createOrReplaceTempView("tblinboundlinev2");
        inboundHeader.createOrReplaceTempView("tblinboundheaderv2");
    }

    private Dataset<Row> readJdbcTable(String tableName) {
        return sparkSession.read().option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://10.10.10.61;databaseName=WMS_3PL", tableName, connProp)
                .repartition(16);
    }

    // FindInboundHeader
    public List<InboundHeaderV3> findInboundHeader(FindInboundHeaderV2 findInboundHeaderV2) throws Exception {

        joined1 = inboundHeader.join(
                inboundLine,
                inboundHeader.col("REF_DOC_NO").equalTo(inboundLine.col("REF_DOC_NO")),
                "left"
        ).groupBy(
                inboundLine.col("REF_DOC_NO")
        ).agg(
                sum(inboundLine.col("ORD_QTY")).alias("totalOrderQty"),
                sum(inboundLine.col("ACCEPT_QTY")).alias("totalAcceptedQty"),
                sum(inboundLine.col("DAMAGE_QTY")).alias("totalDamageQty")

        ).select(
                inboundHeader.col("C_ID").alias("companyCode"),
                inboundHeader.col("PLANT_ID").alias("plantId"),
                inboundHeader.col("LANG_ID").alias("languageId"),
                inboundHeader.col("WH_ID").alias("warehouseId"),
                inboundHeader.col("REF_DOC_NO").alias("refDocNumber"),
                inboundHeader.col("PRE_IB_NO").alias("preInboundNo"),
                inboundHeader.col("STATUS_ID").alias("statusId"),
                inboundHeader.col("IB_ORD_TYP_ID").alias("inboundOrderTypeId"),
                inboundHeader.col("CONT_NO").alias("containerNo"),
                inboundHeader.col("VEH_NO").alias("vechicleNo"),
                inboundHeader.col("IB_TEXT").alias("headerText"),
                inboundHeader.col("CTD_BY").alias("createdBy"),
                inboundHeader.col("CTD_ON").alias("createdOn"),
                inboundHeader.col("UTD_BY").alias("updatedBy"),
                inboundHeader.col("UTD_ON").alias("updatedOn"),
                inboundHeader.col("IB_CNF_BY").alias("confirmedBy"),
                inboundHeader.col("IB_CNF_ON").alias("confirmedOn"),
                inboundHeader.col("C_TEXT").alias("companyDescription"),
                inboundHeader.col("PLANT_TEXT").alias("plantDescription"),
                inboundHeader.col("WH_TEXT").alias("warehouseDescription"),
                inboundHeader.col("STATUS_TEXT").alias("statusDescription"),
                inboundHeader.col("PURCHASE_ORDER_NUMBER").alias("purchaseOrderNumber"),
//                inboundHeader.col("MIDDLEWARE_ID").alias("middlewareId"),
//                inboundHeader.col("MIDDLEWARE_TABLE").alias("middlewareTable"),
                inboundHeader.col("MANUFACTURER_FULL_NAME").alias("manufacturerFullName"),
                inboundHeader.col("REF_DOC_TYPE").alias("referenceDocumentType"),
                inboundHeader.col("COUNT_OF_ORD_LINES").alias("countOfOrderLines"),
                inboundHeader.col("RECEIVED_LINES").alias("receivedLines"),
                inboundHeader.col("TRANSFER_ORDER_DATE").alias("transferOrderDate"),
//                inboundHeader.col("IS_COMPLETED").alias("isCompleted"),
//                inboundHeader.col("IS_CANCELLED").alias("isCancelled"),
//                inboundHeader.col("M_UPDATED_ON").alias("mUpdatedOn"),
                inboundHeader.col("SOURCE_BRANCH_CODE").alias("sourceBranchCode"),
                inboundHeader.col("SOURCE_COMPANY_CODE").alias("sourceCompanyCode")
        );
        log.info("totalOrderQty " + inboundLine.col("ORD_QTY"));
        inboundLine.show();

        filterVal = joined1;

        if (findInboundHeaderV2.getCompanyCodeId() != null && !findInboundHeaderV2.getCompanyCodeId().isEmpty()) {
            filterVal = filterVal.filter(col("companyCode").isin(findInboundHeaderV2.getCompanyCodeId().toArray(new String[0]))).toDF();
        }
        if (findInboundHeaderV2.getPlantId() != null && !findInboundHeaderV2.getPlantId().isEmpty()) {
            filterVal = filterVal.filter(col("plantId").isin(findInboundHeaderV2.getPlantId().toArray(new String[0]))).toDF();
        }
        if (findInboundHeaderV2.getLanguageId() != null && !findInboundHeaderV2.getLanguageId().isEmpty()) {
            filterVal = filterVal.filter(col("languageId").isin(findInboundHeaderV2.getLanguageId().toArray(new String[0]))).toDF();
        }
        if (findInboundHeaderV2.getWarehouseId() != null && !findInboundHeaderV2.getWarehouseId().isEmpty()) {
            filterVal = filterVal.filter(col("warehouseId").isin(findInboundHeaderV2.getWarehouseId().toArray(new String[0]))).toDF();
        }
        if (findInboundHeaderV2.getContainerNo() != null && !findInboundHeaderV2.getContainerNo().isEmpty()) {
            filterVal = filterVal.filter(col("containerNo").isin(findInboundHeaderV2.getContainerNo().toArray(new String[0]))).toDF();
        }
        if (findInboundHeaderV2.getInboundOrderTypeId() != null && !findInboundHeaderV2.getInboundOrderTypeId().isEmpty()) {
            List<String> ibOrderString = findInboundHeaderV2.getInboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            filterVal = filterVal.filter(col("inboundOrderTypeId").isin(ibOrderString.toArray(new String[0]))).toDF();
        }
        if (findInboundHeaderV2.getStatusId() != null && !findInboundHeaderV2.getStatusId().isEmpty()) {
            List<String> statusIdString = findInboundHeaderV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            filterVal = filterVal.filter(col("statusId").isin(statusIdString.toArray(new String[0]))).toDF();
        }
        if (findInboundHeaderV2.getRefDocNumber() != null && !findInboundHeaderV2.getRefDocNumber().isEmpty()) {
            filterVal = filterVal.filter(col("refDocNumber").isin(findInboundHeaderV2.getRefDocNumber().toArray(new String[0]))).toDF();
        }
        if (findInboundHeaderV2.getStartConfirmedOn() != null) {
            Date startDate = findInboundHeaderV2.getStartConfirmedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            filterVal = filterVal.filter(col("confirmedOn").$greater$eq(dateFormat.format(startDate)));
        }
        if (findInboundHeaderV2.getEndConfirmedOn() != null) {
            Date endDate = findInboundHeaderV2.getEndConfirmedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            filterVal = filterVal.filter(col("confirmedOn").$less$eq(dateFormat.format(endDate)));
        }
        if (findInboundHeaderV2.getStartCreatedOn() != null) {
            Date startCreatedOn = findInboundHeaderV2.getStartCreatedOn();
            startCreatedOn = org.apache.commons.lang3.time.DateUtils.truncate(startCreatedOn, Calendar.DAY_OF_MONTH);
            filterVal = filterVal.filter(col("createdOn").$greater$eq(dateFormat.format(startCreatedOn)));
        }
        if (findInboundHeaderV2.getEndCreatedOn() != null) {
            Date endCreatedOn = findInboundHeaderV2.getEndCreatedOn();
            endCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(endCreatedOn, Calendar.DAY_OF_MONTH);
            filterVal = filterVal.filter(col("createdOn").$less$eq(dateFormat.format(endCreatedOn)));
        }

        Encoder<InboundHeaderV3> inboundHeaderV2Encoder = Encoders.bean(InboundHeaderV3.class);
        Dataset<InboundHeaderV3> inboundHeaderV2Dataset = filterVal.as(inboundHeaderV2Encoder);
        return inboundHeaderV2Dataset.collectAsList();

    }


}
