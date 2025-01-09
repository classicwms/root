package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.FindPreInboundHeader;
import com.mnrclara.spark.core.model.PreInboundHeader;
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
public class PreInboundHeaderService {
    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PreInboundHeaderService() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("PreInboundHeader.com") .config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tblpreinboundheader", connProp)
                .repartition(16);
//                .cache();
        df2.createOrReplaceTempView("tblpreinboundheader");

    }

    public List<PreInboundHeader> findPreInboundHeader(FindPreInboundHeader findPreInboundHeader) throws ParseException {
        Dataset<Row> imPreInboundHeaderQuery = sparkSession.sql("SELECT "
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
                + "UTD_ON as updatedOn "

                + "FROM tblpreinboundheader WHERE IS_DELETED = 0");
                imPreInboundHeaderQuery.cache();


        if (findPreInboundHeader.getWarehouseId() != null && !findPreInboundHeader.getWarehouseId().isEmpty()) {
            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("WH_ID").isin(findPreInboundHeader.getWarehouseId().toArray()));
        }
        if (findPreInboundHeader.getPreInboundNo() != null && !findPreInboundHeader.getPreInboundNo().isEmpty()) {
            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("PRE_IB_NO").isin(findPreInboundHeader.getPreInboundNo().toArray()));
        }
        if (findPreInboundHeader.getInboundOrderTypeId() != null && !findPreInboundHeader.getInboundOrderTypeId().isEmpty()) {
            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("IB_ORD_TYP_ID").isin(findPreInboundHeader.getInboundOrderTypeId().toArray()));
        }
        if (findPreInboundHeader.getRefDocNumber() != null && !findPreInboundHeader.getRefDocNumber().isEmpty()) {
            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("REF_DOC_NO").isin(findPreInboundHeader.getRefDocNumber().toArray()));
        }
        if (findPreInboundHeader.getStatusId() != null && !findPreInboundHeader.getStatusId().isEmpty()) {
            List<String> statusIdStrings = findPreInboundHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
        }
        if (findPreInboundHeader.getStartCreatedOn() != null) {
            Date startDate = findPreInboundHeader.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findPreInboundHeader.getEndCreatedOn() != null) {
            Date endDate = findPreInboundHeader.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH);
            imPreInboundHeaderQuery = imPreInboundHeaderQuery.filter(col("CTD_ON").$less$eq(dateFormat.format(endDate)));
        }

        Encoder<PreInboundHeader> PreInboundHeaderEncoder = Encoders.bean(PreInboundHeader.class);
        Dataset<PreInboundHeader> dataSetControlGroup = imPreInboundHeaderQuery.as(PreInboundHeaderEncoder);
        List<PreInboundHeader> result = dataSetControlGroup.collectAsList();
//        imPreInboundHeaderQuery.unpersist();

        return result;
    }
}
