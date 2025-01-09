package com.mnrclara.spark.core.service;


import com.mnrclara.spark.core.model.FindInboundHeader;
import com.mnrclara.spark.core.model.FindPutAwayHeader;
import com.mnrclara.spark.core.model.InboundHeader;
import com.mnrclara.spark.core.model.PutAwayHeader;
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
public class InboundHeaderService {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public InboundHeaderService() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tblinboundheader", connProp)
                .repartition(16)
                .cache();
        df2.createOrReplaceTempView("tblinboundheader");

    }

    /**
     *
     * @param findInboundHeader
     * @return
     * @throws ParseException
     */
    public List<InboundHeader> findInboundHeader(FindInboundHeader findInboundHeader) throws ParseException {


        StringBuilder imInboundHeaderQuery = new StringBuilder("SELECT "
                + "LANG_ID as languageId, "
                + "C_ID as companyCode, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "REF_DOC_NO as refDocNumber, "
                + "PRE_IB_NO as preInboundNo, "
                + "STATUS_ID as statusId, "
                + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                + "CONT_NO as containerNo, "
                + "VEH_NO as vechicleNo, "
                + "IB_TEXT as headerText, "
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
                + "CTD_BY as createdBy, "
                + "CTD_ON as createdOn, "
                + "UTD_BY as updatedBy, "
                + "UTD_ON as updatedOn, "
                + "IB_CNF_BY as confirmedBy, "
                + "IB_CNF_ON as confirmedOn "
                + "FROM tblinboundheader WHERE IS_DELETED =0 ");

        String inboundHeaderStr = imInboundHeaderQuery.toString();
        Dataset<Row> queryDF = sparkSession.sql(inboundHeaderStr);
        queryDF.cache();

        if (findInboundHeader.getWarehouseId() != null && !findInboundHeader.getWarehouseId().isEmpty()) {
            queryDF = queryDF.filter(col("WH_ID").isin(findInboundHeader.getWarehouseId().toArray()));
        }
        if (findInboundHeader.getRefDocNumber() != null && !findInboundHeader.getRefDocNumber().isEmpty()) {
            queryDF = queryDF.filter(col("REF_DOC_NO").isin(findInboundHeader.getRefDocNumber().toArray()));
        }
        if (findInboundHeader.getInboundOrderTypeId() != null && !findInboundHeader.getInboundOrderTypeId().isEmpty()) {
            List<String> inboundOrdTypeString = findInboundHeader.getInboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            queryDF = queryDF.filter(col("IB_ORD_TYP_ID").isin(inboundOrdTypeString.toArray()));
        }
        if (findInboundHeader.getContainerNo() != null && !findInboundHeader.getContainerNo().isEmpty()) {
            queryDF = queryDF.filter(col("CONT_NO").isin(findInboundHeader.getContainerNo().toArray()));
        }
        if (findInboundHeader.getStatusId() != null && !findInboundHeader.getStatusId().isEmpty()) {
            List<String> statusStrings = findInboundHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            queryDF = queryDF.filter(col("STATUS_ID").isin(statusStrings.toArray()));
        }
        if (findInboundHeader.getStartCreatedOn() != null ) {
            Date startDate = findInboundHeader.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            queryDF = queryDF.filter(col("CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if(findInboundHeader.getEndCreatedOn() != null){
            Date endDate = findInboundHeader.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.truncate(endDate,Calendar.DAY_OF_MONTH);
            queryDF = queryDF.filter(col("CTD_ON").$less$eq(dateFormat.format(endDate)));
        }
        if(findInboundHeader.getStartConfirmedOn() !=null){
            Date startConfirmDate = findInboundHeader.getStartConfirmedOn();
            startConfirmDate = org.apache.commons.lang3.time.DateUtils.truncate(startConfirmDate,Calendar.DAY_OF_MONTH);
            queryDF = queryDF.filter(col("IB_CNF_ON").$greater$eq(dateFormat.format(startConfirmDate)));
        }
        if(findInboundHeader.getEndConfirmedOn() != null){
            Date endConfirmDate = findInboundHeader.getEndConfirmedOn();
            endConfirmDate = org.apache.commons.lang3.time.DateUtils.truncate(endConfirmDate, Calendar.DAY_OF_MONTH);
            queryDF = queryDF.filter(col("IB_CNF_ON").$less$eq(dateFormat.format(endConfirmDate)));
        }
        Encoder<InboundHeader> inboundHeaderEncoder = Encoders.bean(InboundHeader.class);
        Dataset<InboundHeader> dataSetControlGroup = queryDF.as(inboundHeaderEncoder);
        List<InboundHeader> results = dataSetControlGroup.collectAsList();
        queryDF.unpersist();

        return results;
    }
}
