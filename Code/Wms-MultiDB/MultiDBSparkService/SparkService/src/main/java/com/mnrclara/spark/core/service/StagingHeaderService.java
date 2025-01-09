package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.FindStagingHeader;
import com.mnrclara.spark.core.model.PutAwayHeader;
import com.mnrclara.spark.core.model.StagingHeader;
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
public class StagingHeaderService {

    Properties conProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public StagingHeaderService() throws ParseException {
        //connection properties
        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conProp.put("user", "sa");
        conProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("StagingHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tblstagingheader", conProp)
                .repartition(16);
//                .cache();
        df2.createOrReplaceTempView("tblstagingheader");

    }


    public List<StagingHeader> findStagingHeader(FindStagingHeader findStagingHeader) throws ParseException {


            Dataset<Row> stagingHeaderQuery = sparkSession.sql("SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCode, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "PRE_IB_NO as preInboundNo, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "STG_NO as stagingNo, "
                    + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                    + "STATUS_ID as statusId, "
                    + "CONT_REC_NO as containerReceiptNo, "
                    + "DOCK_ALL_NO as dockAllocationNo, "
                    + "CONT_NO as containerNo, "
                    + "VEH_NO as vechicleNo, "
                    + "GR_MTD as grMtd, "
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
                    + "ST_CTD_BY as createdBy, "
                    + "ST_CTD_ON as createdOn, "
                    + "ST_UTD_BY as updatedBy, "
                    + "ST_UTD_ON as updatedOn, "
                    + "ST_CNF_BY as confirmedBy, "
                    + "ST_CNF_ON as confirmedOn "
                    + "FROM tblstagingheader WHERE IS_DELETED = 0");

        stagingHeaderQuery.cache();

        if (findStagingHeader.getInboundOrderTypeId() != null && !findStagingHeader.getInboundOrderTypeId().isEmpty()) {
            List<String> inboundOrderTypeString = findStagingHeader.getInboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            stagingHeaderQuery = stagingHeaderQuery.filter(col("IB_ORD_TYP_ID").isin(inboundOrderTypeString.toArray()));
        }
        if (findStagingHeader.getStagingNo() != null && !findStagingHeader.getStagingNo().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("STG_NO").isin(findStagingHeader.getStagingNo().toArray()));
        }
        if (findStagingHeader.getPreInboundNo() != null && !findStagingHeader.getPreInboundNo().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("PRE_IB_NO").isin(findStagingHeader.getPreInboundNo().toArray()));
        }
        if (findStagingHeader.getRefDocNumber() != null && !findStagingHeader.getRefDocNumber().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("REF_DOC_NO").isin(findStagingHeader.getRefDocNumber().toArray()));
        }
        if (findStagingHeader.getStatusId() != null && !findStagingHeader.getStatusId().isEmpty()) {
            List<String> statusString = findStagingHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            stagingHeaderQuery = stagingHeaderQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findStagingHeader.getWarehouseId() != null && !findStagingHeader.getWarehouseId().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("WH_ID").isin(findStagingHeader.getWarehouseId().toArray()));
        }
        if (findStagingHeader.getCreatedBy() != null && !findStagingHeader.getCreatedBy().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("ST_CTD_BY").isin(findStagingHeader.getCreatedBy().toArray()));
        }
        if (findStagingHeader.getStartCreatedOn() != null) {
            Date startDate = findStagingHeader.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            stagingHeaderQuery = stagingHeaderQuery.filter(col("ST_CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findStagingHeader.getEndCreatedOn() != null) {
            Date endDate = findStagingHeader.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH);
            stagingHeaderQuery = stagingHeaderQuery.filter(col("ST_CTD_ON").$less$eq(dateFormat.format(endDate)));
        }

        Encoder<StagingHeader> stagingHeaderEncoder = Encoders.bean(StagingHeader.class);
        Dataset<StagingHeader> dataSetControlGroup = stagingHeaderQuery.as(stagingHeaderEncoder);
        List<StagingHeader> result = dataSetControlGroup.collectAsList();
//        stagingHeaderQuery.unpersist();

        return result;
    }
}
