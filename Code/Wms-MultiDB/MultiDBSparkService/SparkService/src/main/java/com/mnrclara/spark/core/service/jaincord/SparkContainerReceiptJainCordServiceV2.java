package com.mnrclara.spark.core.service.jaincord;

import com.mnrclara.spark.core.model.wmscorev2.ContainerReceiptV2;
import com.mnrclara.spark.core.model.wmscorev2.FindContainerReceiptV2;
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
public class SparkContainerReceiptJainCordServiceV2 {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SparkContainerReceiptJainCordServiceV2() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
        sparkSession = SparkSession.builder().master("local[*]").appName("ContainerReceipt.com") .config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblcontainerreceipt", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblcontainerreceiptjain");

    }

    public List<ContainerReceiptV2> findContainerReceiptV2(FindContainerReceiptV2 findContainerReceiptV2) throws ParseException{
        Dataset<Row>
                containerReceiptQueryV2 =sparkSession.sql( "SELECT " +
                " LANG_ID as languageId, " +
                " C_ID as companyCodeId, " +
                " PLANT_ID as plantId, " +
                " WH_ID as warehouseId, " +
                " PRE_IB_NO as preInboundNo, " +
                " REF_DOC_NO as refDocNumber, " +
                " CONT_REC_NO as containerReceiptNo, " +
                " CONT_REC_DATE as containerReceivedDate, " +
                " CONT_NO as containerNo, " +
                " STATUS_ID as statusId, " +
                " CONT_TYP as containerType, " +
                " PARTNER_CODE as partnerCode, " +
                " INV_NO as invoiceNo, " +
                " CONS_TYPE as consignmentType, " +
                " ORIGIN as origin, " +
                " PAL_QTY as numberOfPallets, " +
                " CASE_NO as numberOfCases, " +
                " DOCK_ALL_NO as dockAllocationNo, " +
                " REMARK as remarks, " +
                " IS_DELETED as deletionIndicator, " +
                " REF_FIELD_1 as referenceField1, " +
                " REF_FIELD_2 as referenceField2, " +
                " REF_FIELD_3 as referenceField3, " +
                " REF_FIELD_4 as referenceField4, " +
                " REF_FIELD_5 as referenceField5, " +
                " REF_FIELD_6 as referenceField6, " +
                " REF_FIELD_7 as referenceField7, " +
                " REF_FIELD_8 as referenceField8, " +
                " REF_FIELD_9 as referenceField9, " +
                " REF_FIELD_10 as referenceField10, " +
                " CTD_BY as createdBy, " +
                " CTD_ON as createdOn, " +
                " UTD_BY as updatedBy, " +
                " UTD_ON as updatedOn, " +
                 "C_TEXT as companyDescription, "
                + "PLANT_TEXT as plantDescription, "
                + "WH_TEXT as warehouseDescription, "
                + "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, "
                + "STATUS_TEXT as statusDescription, "
                + "MIDDLEWARE_ID as middlewareId, "
                + "MIDDLEWARE_TABLE as middlewareTable "
                + "FROM tblcontainerreceiptjain WHERE IS_DELETED = 0 ");

//        containerReceiptQueryV2.cache();

        if (findContainerReceiptV2.getLanguageId() != null && !findContainerReceiptV2.getLanguageId().isEmpty()) {
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("LANG_ID").isin(findContainerReceiptV2.getLanguageId().toArray()));
        }
        if (findContainerReceiptV2.getCompanyCodeId() != null && !findContainerReceiptV2.getCompanyCodeId().isEmpty()) {
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("C_ID").isin(findContainerReceiptV2.getCompanyCodeId().toArray()));
        }
        if (findContainerReceiptV2.getPlantId() != null && !findContainerReceiptV2.getPlantId().isEmpty()) {
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("PLANT_ID").isin(findContainerReceiptV2.getPlantId().toArray()));
        }
        if (findContainerReceiptV2.getWarehouseId() != null && !findContainerReceiptV2.getWarehouseId().isEmpty()) {
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("WH_ID").isin(findContainerReceiptV2.getWarehouseId().toArray()));
        }
        if (findContainerReceiptV2.getContainerReceiptNo() != null && !findContainerReceiptV2.getContainerReceiptNo().isEmpty()) {
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("CONT_REC_NO").isin(findContainerReceiptV2.getContainerReceiptNo().toArray()));
        }
        if (findContainerReceiptV2.getContainerNo() != null && !findContainerReceiptV2.getContainerNo().isEmpty()) {
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("CONT_NO").isin(findContainerReceiptV2.getContainerNo().toArray()));
        }
        if (findContainerReceiptV2.getPartnerCode() != null && !findContainerReceiptV2.getPartnerCode().isEmpty()) {
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("PARTNER_CODE").isin(findContainerReceiptV2.getPartnerCode().toArray()));
        }
        if (findContainerReceiptV2.getUnloadedBy() != null && !findContainerReceiptV2.getUnloadedBy().isEmpty()) {
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("REF_FIELD_1").isin(findContainerReceiptV2.getUnloadedBy().toArray()));
        }
        if (findContainerReceiptV2.getStatusId() != null && !findContainerReceiptV2.getStatusId().isEmpty()) {
            List<String> statusString = findContainerReceiptV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findContainerReceiptV2.getStartContainerReceivedDate() != null) {
            Date startContainerReceiveDate = findContainerReceiptV2.getStartContainerReceivedDate();
            startContainerReceiveDate = org.apache.commons.lang3.time.DateUtils.ceiling(startContainerReceiveDate, Calendar.DAY_OF_MONTH);
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("CONT_REC_DATE").$greater$eq(dateFormat.format(startContainerReceiveDate)));
        }
        if (findContainerReceiptV2.getEndContainerReceivedDate() != null) {
            Date endContainerReceiveDate = findContainerReceiptV2.getEndContainerReceivedDate();
            endContainerReceiveDate = org.apache.commons.lang3.time.DateUtils.ceiling(endContainerReceiveDate, Calendar.DAY_OF_MONTH);
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("CONT_REC_DATE").$less$eq(dateFormat.format(endContainerReceiveDate)));
        }
        if(findContainerReceiptV2.getFromCreatedOn() != null){
            Date fromCreatedOn = findContainerReceiptV2.getFromCreatedOn();
            fromCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(fromCreatedOn, Calendar.DAY_OF_MONTH);
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("CTD_ON").$greater$eq(dateFormat.format(fromCreatedOn)));
        }
        if(findContainerReceiptV2.getToCreatedOn() != null){
            Date toCreatedOn = findContainerReceiptV2.getToCreatedOn();
            toCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(toCreatedOn, Calendar.DAY_OF_MONTH);
            containerReceiptQueryV2 = containerReceiptQueryV2.filter(col("CTD_ON").$less$eq(dateFormat.format(toCreatedOn)));
        }
        Encoder<ContainerReceiptV2> containerReceiptEncoder = Encoders.bean(ContainerReceiptV2.class);
        Dataset<ContainerReceiptV2> dataSetControlGroup = containerReceiptQueryV2.as(containerReceiptEncoder);
        List<ContainerReceiptV2> result = dataSetControlGroup.collectAsList();

        return result;
    }

}