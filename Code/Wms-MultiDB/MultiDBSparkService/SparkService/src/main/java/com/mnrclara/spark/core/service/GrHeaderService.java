package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.FindGrHeader;
import com.mnrclara.spark.core.model.GrHeader;
import com.mnrclara.spark.core.model.PutAwayHeader;
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

@Slf4j
@Service
public class GrHeaderService {

    Properties conProp = new Properties();

    SparkSession sparkSession = null;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GrHeaderService() throws ParseException {
        //connection properties
        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conProp.put("user", "sa");
        conProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tblgrheader", conProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblgrheader");
    }

    public List<GrHeader> findGrHeader(FindGrHeader findGrHeader) throws ParseException {

        Dataset<Row> imgrHeaderQuery = sparkSession.sql("SELECT "
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
                + "GR_CNF_ON as confirmedOn "
                + "FROM tblgrheader WHERE IS_DELETED = 0");

        imgrHeaderQuery.cache();


        if (findGrHeader.getInboundOrderTypeId() != null && !findGrHeader.getInboundOrderTypeId().isEmpty()) {
            List<String> inboundOrderIdStrings = findGrHeader.getInboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            imgrHeaderQuery = imgrHeaderQuery.filter(col("IB_ORD_TYP_ID").isin(inboundOrderIdStrings.toArray()));
        }
        if (findGrHeader.getGoodsReceiptNo() != null && !findGrHeader.getGoodsReceiptNo().isEmpty()) {
            imgrHeaderQuery = imgrHeaderQuery.filter(col("GR_NO").isin(findGrHeader.getGoodsReceiptNo().toArray()));
        }
        if (findGrHeader.getPreInboundNo() != null && !findGrHeader.getPreInboundNo().isEmpty()) {
            imgrHeaderQuery = imgrHeaderQuery.filter(col("PRE_IB_NO").isin(findGrHeader.getPreInboundNo().toArray()));
        }
        if (findGrHeader.getRefDocNumber() != null && !findGrHeader.getRefDocNumber().isEmpty()) {
            imgrHeaderQuery = imgrHeaderQuery.filter(col("REF_DOC_NO").isin(findGrHeader.getRefDocNumber().toArray()));
        }
        if (findGrHeader.getStatusId() != null && !findGrHeader.getStatusId().isEmpty()) {
            List<String> statusIdStrings = findGrHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            imgrHeaderQuery = imgrHeaderQuery.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
        }
        if (findGrHeader.getWarehouseId() != null && !findGrHeader.getWarehouseId().isEmpty()) {
            imgrHeaderQuery = imgrHeaderQuery.filter(col("WH_ID").isin(findGrHeader.getWarehouseId().toArray()));
        }
        if (findGrHeader.getCaseCode() != null && !findGrHeader.getCaseCode().isEmpty()) {
            imgrHeaderQuery = imgrHeaderQuery.filter(col("CASE_CODE").isin(findGrHeader.getCaseCode().toArray()));
        }
        if (findGrHeader.getCreatedBy() != null && !findGrHeader.getCreatedBy().isEmpty()) {
            imgrHeaderQuery = imgrHeaderQuery.filter(col("GR_CTD_BY").isin(findGrHeader.getCreatedBy().toArray()));
        }
        if (findGrHeader.getStartCreatedOn() != null) {
            Date startDate = findGrHeader.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            imgrHeaderQuery = imgrHeaderQuery.filter(col("GR_CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findGrHeader.getEndCreatedOn() != null) {
            Date endDate = findGrHeader.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            imgrHeaderQuery = imgrHeaderQuery.filter(col("GR_CTD_ON").$less$eq(dateFormat.format(endDate)));
        }

        Encoder<GrHeader> GrHeaderEncoder = Encoders.bean(GrHeader.class);
        Dataset<GrHeader> dataSetControlGroup = imgrHeaderQuery.as(GrHeaderEncoder);
        List<GrHeader> result = dataSetControlGroup.collectAsList();
        imgrHeaderQuery.unpersist();

        return result;
    }
}
