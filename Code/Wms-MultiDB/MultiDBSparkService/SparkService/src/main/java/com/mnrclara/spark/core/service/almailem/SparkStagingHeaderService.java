package com.mnrclara.spark.core.service.almailem;

import com.mnrclara.spark.core.model.Almailem.FindStagingHeaderV2;
import com.mnrclara.spark.core.model.Almailem.StagingHeaderV2;
import com.mnrclara.spark.core.model.FindStagingHeader;
import com.mnrclara.spark.core.model.StagingHeader;
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
public class SparkStagingHeaderService {

    Properties conProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SparkStagingHeaderService() throws ParseException {
        //connection properties
        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conProp.put("user", "sa");
        conProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        sparkSession = SparkSession.builder().master("local[*]").appName("StagingHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://3.109.20.248;databaseName=WMS_ALMDEV", "tblstagingheader", conProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblstagingheaderv2");

    }


    /**
     *
     * @param findStagingHeaderV2
     * @return
     * @throws ParseException
     */
    public List<StagingHeaderV2> findStagingHeader(FindStagingHeaderV2 findStagingHeaderV2) throws ParseException {


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
                                                                   + "ST_CNF_ON as confirmedOn, "

                                                                   //v2 fields
                                                                   + "C_TEXT as companyDescription, "
                                                                   + "PLANT_TEXT as plantDescription, "
                                                                   + "WH_TEXT as warehouseDescription, "
                                                                   + "STATUS_TEXT as statusDescription, "
                                                                   + "MIDDLEWARE_ID as middlewareId, "
                                                                   + "MIDDLEWARE_TABLE as middlewareTable, "
                                                                   + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                                                                   + "CSTR_COD as customerCode, "
                                                                   + "TFR_REQ_TYP as TransferRequestType, "
                                                                   + "REF_DOC_TYPE as referenceDocumentType "
                                                                   + "FROM tblstagingheaderv2 WHERE IS_DELETED = 0");


        if (findStagingHeaderV2.getInboundOrderTypeId() != null && !findStagingHeaderV2.getInboundOrderTypeId().isEmpty()) {
            List<String> inboundOrderTypeString = findStagingHeaderV2.getInboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            stagingHeaderQuery = stagingHeaderQuery.filter(col("IB_ORD_TYP_ID").isin(inboundOrderTypeString.toArray()));
        }
        if (findStagingHeaderV2.getStagingNo() != null && !findStagingHeaderV2.getStagingNo().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("STG_NO").isin(findStagingHeaderV2.getStagingNo().toArray()));
        }
        if (findStagingHeaderV2.getPreInboundNo() != null && !findStagingHeaderV2.getPreInboundNo().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("PRE_IB_NO").isin(findStagingHeaderV2.getPreInboundNo().toArray()));
        }
        if (findStagingHeaderV2.getRefDocNumber() != null && !findStagingHeaderV2.getRefDocNumber().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("REF_DOC_NO").isin(findStagingHeaderV2.getRefDocNumber().toArray()));
        }
        if (findStagingHeaderV2.getStatusId() != null && !findStagingHeaderV2.getStatusId().isEmpty()) {
            List<String> statusString = findStagingHeaderV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            stagingHeaderQuery = stagingHeaderQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findStagingHeaderV2.getWarehouseId() != null && !findStagingHeaderV2.getWarehouseId().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("WH_ID").isin(findStagingHeaderV2.getWarehouseId().toArray()));
        }
        if (findStagingHeaderV2.getCreatedBy() != null && !findStagingHeaderV2.getCreatedBy().isEmpty()) {
            stagingHeaderQuery = stagingHeaderQuery.filter(col("ST_CTD_BY").isin(findStagingHeaderV2.getCreatedBy().toArray()));
        }
        if(findStagingHeaderV2.getCompanyCodeId() != null && !findStagingHeaderV2.getCompanyCodeId().isEmpty()){
            stagingHeaderQuery = stagingHeaderQuery.filter(col("C_ID").isin(findStagingHeaderV2.getCompanyCodeId().toArray()));
        }
        if(findStagingHeaderV2.getPlantId() != null && !findStagingHeaderV2.getPlantId().isEmpty()){
            stagingHeaderQuery = stagingHeaderQuery.filter(col("PLANT_ID").isin(findStagingHeaderV2.getPlantId().toArray()));
        }
        if(findStagingHeaderV2.getLanguageId() != null && !findStagingHeaderV2.getLanguageId().isEmpty()){
            stagingHeaderQuery = stagingHeaderQuery.filter(col("LANG_ID").isin(findStagingHeaderV2.getLanguageId().toArray()));
        }

        if (findStagingHeaderV2.getStartCreatedOn() != null) {
            Date startDate = findStagingHeaderV2.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            stagingHeaderQuery = stagingHeaderQuery.filter(col("ST_CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findStagingHeaderV2.getEndCreatedOn() != null) {
            Date endDate = findStagingHeaderV2.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            stagingHeaderQuery = stagingHeaderQuery.filter(col("ST_CTD_ON").$less$eq(dateFormat.format(endDate)));
        }

        Encoder<StagingHeaderV2> stagingHeaderEncoder = Encoders.bean(StagingHeaderV2.class);
        Dataset<StagingHeaderV2> dataSetControlGroup = stagingHeaderQuery.as(stagingHeaderEncoder);
        List<StagingHeaderV2> result = dataSetControlGroup.collectAsList();

        return result;
    }
}