package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.FindOutBoundReversal;
import com.mnrclara.spark.core.model.OutBoundReversal;
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
public class OutBoundReversalService {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public OutBoundReversalService() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("PutAwayHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tbloutboundreversal", connProp)
                .repartition(16);
//                .cache();
        df2.createOrReplaceTempView("tbloutboundreversal");
    }

    /**
     * @param findOutBoundReversal
     * @return
     * @throws ParseException
     */
    public List<OutBoundReversal> findOutBoundReversal(FindOutBoundReversal findOutBoundReversal) throws ParseException {


        Dataset<Row> imOutBoundReversalrQuery = sparkSession.sql("SELECT "
                + "LANG_ID as languageId, "
                + "C_ID as companyCodeId, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "OB_REVERSAL_NO as outboundReversalNo, "
                + "REVERSAL_TYPE as reversalType, "
                + "REF_DOC_NO as refDocNumber, "
                + "PARTNER_CODE as partnerCode, "
                + "ITM_CODE as itemCode,"
                + "PACK_BARCODE as packBarcode," +
                " REV_QTY as reversedQty," +
                "STATUS_ID as statusId," +
                "REF_FIELD_1 as referenceField1," +
                "REF_FIELD_2 as referenceField2," +
                "REF_FIELD_3 as referenceField3," +
                "REF_FIELD_4 as referenceField4," +
                "REF_FIELD_5 as referenceField5," +
                "REF_FIELD_6 as referenceField6," +
                "REF_FIELD_7 as referenceField7," +
                "REF_FIELD_8 as referenceField8," +
                "REF_FIELD_9 as referenceField9," +
                "REF_FIELD_10 as referenceField10," +
                "IS_DELETED as deletionIndicator," +
                "OB_REV_BY as reversedBy," +
                "OB_REV_ON as reversedOn" +
                " FROM tbloutboundreversal WHERE IS_DELETED = 0 ");

        imOutBoundReversalrQuery.cache();

        if (findOutBoundReversal.getOutboundReversalNo() != null && !findOutBoundReversal.getOutboundReversalNo().isEmpty()) {
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("OB_REVERSAL_NO").isin(findOutBoundReversal.getOutboundReversalNo().toArray()));
        }
        if (findOutBoundReversal.getReversalType() != null && !findOutBoundReversal.getReversalType().isEmpty()) {
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("REVERSAL_TYPE").isin(findOutBoundReversal.getReversalType().toArray()));
        }
        if (findOutBoundReversal.getRefDocNumber() != null && !findOutBoundReversal.getRefDocNumber().isEmpty()) {
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("REF_DOC_NO").isin(findOutBoundReversal.getRefDocNumber().toArray()));
        }
        if (findOutBoundReversal.getPartnerCode() != null && !findOutBoundReversal.getPartnerCode().isEmpty()) {
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("PARTNER_CODE").isin(findOutBoundReversal.getPartnerCode().toArray()));
        }
        if (findOutBoundReversal.getItemCode() != null && !findOutBoundReversal.getItemCode().isEmpty()) {
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("ITM_CODE").isin(findOutBoundReversal.getItemCode().toArray()));
        }
        if (findOutBoundReversal.getPackBarcode() != null && !findOutBoundReversal.getPackBarcode().isEmpty()) {
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("PACK_BARCODE").isin(findOutBoundReversal.getPackBarcode().toArray()));
        }
        if (findOutBoundReversal.getReversedBy() != null && !findOutBoundReversal.getReversedBy().isEmpty()) {
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("OB_REV_BY").isin(findOutBoundReversal.getReversedBy().toArray()));
        }
        if (findOutBoundReversal.getStatusId() != null && !findOutBoundReversal.getStatusId().isEmpty()) {
            List<String> statusIdStrings = findOutBoundReversal.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
        }
        if (findOutBoundReversal.getStartReversedOn() != null) {
            Date startDate = findOutBoundReversal.getStartReversedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("OB_REV_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findOutBoundReversal.getEndReversedOn() != null) {
            Date endDate = findOutBoundReversal.getEndReversedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH);
            imOutBoundReversalrQuery = imOutBoundReversalrQuery.filter(col("OB_REV_ON").$less$eq(dateFormat.format(endDate)));
        }

        Encoder<OutBoundReversal> outBoundReversalEncoder = Encoders.bean(OutBoundReversal.class);
        Dataset<OutBoundReversal> dataSetControlGroup = imOutBoundReversalrQuery.as(outBoundReversalEncoder);
        List<OutBoundReversal> result = dataSetControlGroup.collectAsList();
//        imOutBoundReversalrQuery.unpersist();

        return result;
    }

}
