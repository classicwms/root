package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.FindPreOutboundHeader;
import com.mnrclara.spark.core.model.GrHeader;
import com.mnrclara.spark.core.model.PreOutboundHeader;
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
public class PreOutboundHeaderService {

    Properties conProp = new Properties();

    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PreOutboundHeaderService() throws ParseException {
        //connection properties
        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conProp.put("user", "sa");
        conProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("PreOutboundHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tblpreoutboundheader", conProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblpreoutboundheader");
    }

    /**
     *
     * @param findPreObHeader
     * @return
     * @throws ParseException
     */
    public List<PreOutboundHeader> findPreOutboundHeader(FindPreOutboundHeader findPreObHeader) throws ParseException {

        Dataset<Row> preObHeaderQuery = sparkSession.sql("SELECT "
                + "LANG_ID as languageId, "
                + "C_ID as companyCodeId, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "REF_DOC_NO as refDocNumber, "
                + "PRE_OB_NO as preOutboundNo, "
                + "PARTNER_CODE as partnerCode, "
                + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                + "REF_DOC_TYP as referenceDocumentType, "
                + "STATUS_ID as statusId, "
                + "REF_DOC_DATE as refDocDate, "
                + "REQ_DEL_DATE as requiredDeliveryDate, "
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
                + "REMARK as remarks, "
                + "PRE_OB_CTD_BY as createdBy, "
                + "PRE_OB_CTD_ON as createdOn, "
                + "PRE_OB_UTD_BY as updatedBy, "
                + "PRE_OB_UTD_ON as updatedOn "
                + "FROM tblpreoutboundheader WHERE IS_DELETED=0 ");

        preObHeaderQuery.cache();


        if (findPreObHeader.getWarehouseId() != null && !findPreObHeader.getWarehouseId().isEmpty()) {
            preObHeaderQuery = preObHeaderQuery.filter(col("WH_ID").isin(findPreObHeader.getWarehouseId().toArray()));
        }
        if (findPreObHeader.getPreOutboundNo() != null && !findPreObHeader.getPreOutboundNo().isEmpty()) {
            preObHeaderQuery = preObHeaderQuery.filter(col("PRE_OB_NO").isin(findPreObHeader.getPreOutboundNo().toArray()));
        }
        if (findPreObHeader.getOutboundOrderTypeId() != null && !findPreObHeader.getOutboundOrderTypeId().isEmpty()) {
            List<String> outboundOrderTypeIdStrings = findPreObHeader.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            preObHeaderQuery = preObHeaderQuery.filter(col("OB_ORD_TYP_ID").isin(outboundOrderTypeIdStrings.toArray()));
        }
        if(findPreObHeader.getSoType() != null && !findPreObHeader.getSoType().isEmpty()){
            preObHeaderQuery = preObHeaderQuery.filter(col("REF_FIELD_1").isin(findPreObHeader.getSoType().toArray()));
        }
        if(findPreObHeader.getSoNumber() != null && !findPreObHeader.getSoNumber().isEmpty()){
            preObHeaderQuery = preObHeaderQuery.filter(col("REF_DOC_NO").isin(findPreObHeader.getSoNumber().toArray()));
        }
        if(findPreObHeader.getPartnerCode() != null && !findPreObHeader.getPartnerCode().isEmpty()){
            preObHeaderQuery = preObHeaderQuery.filter(col("PARTNER_CODE").isin(findPreObHeader.getPartnerCode().toArray()));
        }
        if (findPreObHeader.getStatusId() != null && !findPreObHeader.getStatusId().isEmpty()) {
            List<String> statusIdStrings = findPreObHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            preObHeaderQuery = preObHeaderQuery.filter(col("STATUS_ID").isin(statusIdStrings.toArray()));
        }
        if(findPreObHeader.getCreatedBy() != null && !findPreObHeader.getCreatedBy().isEmpty()){
            preObHeaderQuery = preObHeaderQuery.filter(col("PRE_OB_CTD_BY").isin(findPreObHeader.getCreatedBy().toArray()));
        }
        if (findPreObHeader.getStartRequiredDeliveryDate() != null) {
            Date startRequireDeliveryDate = findPreObHeader.getStartRequiredDeliveryDate();
            startRequireDeliveryDate = org.apache.commons.lang3.time.DateUtils.truncate(startRequireDeliveryDate, Calendar.DAY_OF_MONTH);
            preObHeaderQuery = preObHeaderQuery.filter(col("REQ_DEL_DATE").$greater$eq(dateFormat.format(startRequireDeliveryDate)));
        }
        if (findPreObHeader.getEndRequiredDeliveryDate() != null) {
            Date endRequireDeliveryDate = findPreObHeader.getEndRequiredDeliveryDate();
            endRequireDeliveryDate = org.apache.commons.lang3.time.DateUtils.truncate(endRequireDeliveryDate, Calendar.DAY_OF_MONTH);
            preObHeaderQuery = preObHeaderQuery.filter(col("REQ_DEL_DATE").$less$eq(dateFormat.format(endRequireDeliveryDate)));
        }
        if (findPreObHeader.getStartOrderDate() != null) {
            Date startOrderDate = findPreObHeader.getStartOrderDate();
            startOrderDate = org.apache.commons.lang3.time.DateUtils.truncate(startOrderDate, Calendar.DAY_OF_MONTH);
            preObHeaderQuery = preObHeaderQuery.filter(col("REF_DOC_DATE").$greater$eq(dateFormat.format(startOrderDate)));
        }
        if (findPreObHeader.getEndOrderDate() != null) {
            Date endOrderDate = findPreObHeader.getEndOrderDate();
            endOrderDate = org.apache.commons.lang3.time.DateUtils.truncate(endOrderDate, Calendar.DAY_OF_MONTH);
            preObHeaderQuery = preObHeaderQuery.filter(col("REF_DOC_DATE").$less$eq(dateFormat.format(endOrderDate)));
        }
        if (findPreObHeader.getStartCreatedOn() != null) {
            Date startCreatedOn = findPreObHeader.getStartCreatedOn();
            startCreatedOn = org.apache.commons.lang3.time.DateUtils.truncate(startCreatedOn, Calendar.DAY_OF_MONTH);
            preObHeaderQuery = preObHeaderQuery.filter(col("PRE_OB_CTD_ON").$greater$eq(dateFormat.format(startCreatedOn)));
        }
        if (findPreObHeader.getEndCreatedOn() != null) {
            Date endCreatedOn = findPreObHeader.getEndCreatedOn();
            endCreatedOn = org.apache.commons.lang3.time.DateUtils.truncate(endCreatedOn, Calendar.DAY_OF_MONTH);
            preObHeaderQuery = preObHeaderQuery.filter(col("PRE_OB_CTD_ON").$less$eq(dateFormat.format(endCreatedOn)));
        }

        Encoder<PreOutboundHeader> preOutboundHeaderEncoder = Encoders.bean(PreOutboundHeader.class);
        Dataset<PreOutboundHeader> dataSetControlGroup = preObHeaderQuery.as(preOutboundHeaderEncoder);
        List<PreOutboundHeader> result = dataSetControlGroup.collectAsList();
//        preObHeaderQuery.unpersist();

        return result;
    }
}
