
package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.FindOutBoundHeader;
import com.mnrclara.spark.core.model.OutboundHeaderOutput;
import com.mnrclara.spark.core.model.QualityHeader;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.*;

@Slf4j
@Service
public class OutBoundOrderHeaderService {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public OutBoundOrderHeaderService() throws ParseException {
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder()
                .master("local[*]")
                .appName("SparkByExample.com")
                .config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4")
                .getOrCreate();

        // Read from Sql Tables
        val headerDF = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tbloutboundheader", connProp)
                .repartition(16);
//                .cache();
        headerDF.createOrReplaceTempView("tbloutboundheader");

        val lineDF = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tbloutboundline", connProp)
                .repartition(16);
//                .cache();
        lineDF.createOrReplaceTempView("tbloutboundline");
    }


    public List<OutboundHeaderOutput> findOutBoundHeader(FindOutBoundHeader findOutBoundHeader) throws ParseException {
        // Build the Spark SQL query based on the provided native query
        Dataset<Row> outboundHeaderQuery = sparkSession.sql("SELECT " +
                "oh.lang_id as languageId, " +
                "oh.c_id as companyCodeId, " +
                "oh.plant_id as plantId, " +
                "oh.wh_id as warehouseId, " +
                "oh.pre_ob_no as preOutboundNo, " +
                "oh.ref_doc_no as refDocNumber, " +
                "oh.partner_code as partnerCode, " +
                "oh.dlv_ord_no as deliveryOrderNo, " +
                "oh.ref_doc_typ as referenceDocumentType, " +
                "oh.ob_ord_typ_id as outboundOrderTypeId, " +
                "oh.status_id as statusId, " +
//                "CAST(DATE_ADD(oh.ref_doc_date, 3) AS TIMESTAMP) as refDocDate" +
                "date_add(oh.ref_doc_date, 3) as refDocDate, " +
                "date_add(oh.req_del_date, 3) as requiredDeliveryDate, " +
                "oh.ref_field_1 as referenceField1, " +
                "oh.ref_field_2 as referenceField2, " +
                "oh.ref_field_3 as referenceField3, " +
                "oh.ref_field_4 as referenceField4, " +
                "oh.ref_field_5 as referenceField5, " +
                "oh.ref_field_6 as referenceField6, " +
                "oh.ref_field_7 as referenceField7, " +
                "oh.ref_field_8 as referenceField8, " +
                "oh.ref_field_9 as referenceField9, " +
                "oh.ref_field_10 as referenceField10, " +
                "oh.is_deleted as deletionIndicator, " +
                "oh.remark as remarks, " +
                "oh.dlv_ctd_by as createdBy, " +
                "date_add(oh.dlv_ctd_on, 3) as createdOn, " +
                "oh.dlv_cnf_by as deliveryConfirmedBy, " +
                "date_add(oh.dlv_cnf_on, 3) as deliveryConfirmedOn, " +
                "oh.dlv_utd_by as updatedBy, " +
                "date_add(oh.dlv_utd_on, 3) as updatedOn, " +
                "oh.dlv_rev_by as reversedBy, " +
                "date_add(oh.dlv_rev_on, 3) as reversedOn, " +
                "ol.dlv_qty as deliveryQty," +
                "SUM(CASE WHEN ol.dlv_qty IS NOT NULL THEN ol.dlv_qty ELSE 0 END) as referenceField7, \" +\n" +
                " COUNT(CASE WHEN ol.dlv_qty IS NOT NULL AND ol.dlv_qty > 0 THEN ol.dlv_qty END) as referenceField8, \" +\n" +
                " SUM(ol.ord_qty) as referenceField9, " +
                "COUNT(ol.ord_qty) as referenceField10 " +
                "FROM tbloutboundheader oh " +
                "JOIN tbloutboundline ol ON ol.ref_doc_no = oh.ref_doc_no ");
////                "GROUP BY " +
////                "oh.lang_id, oh.c_id, oh.plant_id, oh.wh_id, oh.pre_ob_no, oh.ref_doc_no, " +
//                "  GROUP BY " +
//                "    oh.lang_id, oh.c_id, oh.plant_id, oh.wh_id, oh.pre_ob_no, oh.ref_doc_no, " +
//                "    oh.partner_code, oh.dlv_ctd_by, oh.dlv_ctd_on, oh.is_deleted, " +
//                "    oh.dlv_cnf_by, oh.dlv_cnf_on, oh.dlv_ord_no, oh.ob_ord_typ_id, oh.ref_doc_date," +
//                "    oh.ref_doc_typ, oh.remark, oh.req_del_date, oh.dlv_rev_by, oh.dlv_rev_on, " +
//                "    oh.status_id, oh.dlv_utd_by, oh.dlv_utd_on, oh.ref_field_1, oh.ref_field_2," +
//                "    oh.ref_field_3, oh.ref_field_4, oh.ref_field_5, oh.ref_field_6, oh.ref_field_7 , oh.ref_field_8," +
//                "   oh.ref_field_9, oh.ref_field_10, ol.dlv_qty, ol.ord_qty ");

        log.info("OutboundHeaderData "+outboundHeaderQuery);
        if (findOutBoundHeader.getWarehouseId() != null && !findOutBoundHeader.getWarehouseId().isEmpty()) {
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.wh_id").isin(findOutBoundHeader.getWarehouseId().toArray()));
        }
        if (findOutBoundHeader.getRefDocNumber() != null && !findOutBoundHeader.getRefDocNumber().isEmpty()) {
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.ref_doc_no").isin(findOutBoundHeader.getRefDocNumber().toArray()));
        }
        if (findOutBoundHeader.getPartnerCode() != null && !findOutBoundHeader.getPartnerCode().isEmpty()) {
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.partner_code").isin(findOutBoundHeader.getPartnerCode().toArray()));
        }
        if (findOutBoundHeader.getOutboundOrderTypeId() != null && !findOutBoundHeader.getOutboundOrderTypeId().isEmpty()) {
            List<String> outBoundHeaderStrings = findOutBoundHeader.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.ob_ord_typ_id").isin(outBoundHeaderStrings.toArray()));
        }
        if (findOutBoundHeader.getStatusId() != null && !findOutBoundHeader.getStatusId().isEmpty()) {
            List<String> statusString = findOutBoundHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.status_id").isin(statusString.toArray()));
        }
        if (findOutBoundHeader.getSoType() != null && !findOutBoundHeader.getSoType().isEmpty()) {
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.ref_field_1").isin(findOutBoundHeader.getSoType().toArray()));
        }
        if (findOutBoundHeader.getStartRequiredDeliveryDate() != null) {
            Date startDeliveryDate = findOutBoundHeader.getStartRequiredDeliveryDate();
            startDeliveryDate = org.apache.commons.lang3.time.DateUtils.truncate(startDeliveryDate, Calendar.DAY_OF_MONTH);
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.REQ_DEL_DATE").$greater$eq(dateFormat.format(startDeliveryDate)));
        }
        if (findOutBoundHeader.getEndRequiredDeliveryDate() != null) {
            Date endDeliveryDate = findOutBoundHeader.getEndRequiredDeliveryDate();
            endDeliveryDate = org.apache.commons.lang3.time.DateUtils.truncate(endDeliveryDate, Calendar.DAY_OF_MONTH);
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.REQ_DEL_DATE").$less$eq(dateFormat.format(endDeliveryDate)));
        }
        if (findOutBoundHeader.getStartDeliveryConfirmedOn() != null) {
            Date startDeliveryOn = findOutBoundHeader.getStartDeliveryConfirmedOn();
            startDeliveryOn = org.apache.commons.lang3.time.DateUtils.truncate(startDeliveryOn, Calendar.DAY_OF_MONTH);
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.DLV_CNF_ON").$greater$eq(dateFormat.format(startDeliveryOn)));
        }
        if (findOutBoundHeader.getEndDeliveryConfirmedOn() != null) {
            Date endDeliveryOn = findOutBoundHeader.getEndDeliveryConfirmedOn();
            endDeliveryOn = org.apache.commons.lang3.time.DateUtils.truncate(endDeliveryOn, Calendar.DAY_OF_MONTH);
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.DLV_CNF_ON").$less$eq(dateFormat.format(endDeliveryOn)));
        }
        if (findOutBoundHeader.getStartOrderDate() != null) {
            Date startOrderDate = findOutBoundHeader.getStartOrderDate();
            startOrderDate = org.apache.commons.lang3.time.DateUtils.truncate(startOrderDate, Calendar.DAY_OF_MONTH);
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.DLV_CTD_ON").$greater$eq(dateFormat.format(startOrderDate)));
        }
        if (findOutBoundHeader.getEndOrderDate() != null) {
            Date endOrderDate = findOutBoundHeader.getEndOrderDate();
            endOrderDate = org.apache.commons.lang3.time.DateUtils.truncate(endOrderDate, Calendar.DAY_OF_MONTH);
            outboundHeaderQuery = outboundHeaderQuery.filter(col("oh.DLV_CTD_ON").$less$eq(dateFormat.format(endOrderDate)));
        }

        // Collect the result
        Encoder<OutboundHeaderOutput> outboundHeaderOutputEncoder = Encoders.bean(OutboundHeaderOutput.class);
        Dataset<OutboundHeaderOutput> dataSetControlGroup = outboundHeaderQuery.groupBy(
                        "languageId", "companyCodeId", "plantId", "warehouseId",
                        "preOutboundNo", "refDocNumber", "partnerCode", "deliveryOrderNo",
                        "referenceDocumentType", "outboundOrderTypeId", "statusId",
                        "refDocDate", "requiredDeliveryDate", "referenceField1", "referenceField2",
                        "referenceField3", "referenceField4", "referenceField5", "referenceField6",
                        "referenceField7", "referenceField8", "referenceField9", "referenceField10",
                        "deletionIndicator", "remarks", "createdBy", "createdOn",
                        "deliveryConfirmedBy", "deliveryConfirmedOn", "updatedBy", "updatedOn",
                        "reversedBy", "reversedOn"
                )
                .agg(
                        first("preOutboundNo").as("preOutboundNo"),
                        // ... (other columns) ...
                        sum("sumDeliveryQty").as("sumDeliveryQty"),
                        count("countDeliveryQty").as("countDeliveryQty"),
                        sum("sumOrderQty").as("sumOrderQty"),
                        count("countOrderQty").as("countOrderQty")
                )
                .as(outboundHeaderOutputEncoder);
//        Dataset<OutboundHeaderOutput> dataSetControlGroup = outboundHeaderQuery.as(outboundHeaderOutputEncoder);
        List<OutboundHeaderOutput> result = dataSetControlGroup.collectAsList();

        // Unpersist the cached DataFrame
//        outboundHeaderQuery.unpersist();


        return result;
    }




}
