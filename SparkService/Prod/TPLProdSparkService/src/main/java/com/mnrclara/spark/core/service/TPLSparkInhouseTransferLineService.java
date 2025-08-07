package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.Almailem.InhouseTransferLine;
import com.mnrclara.spark.core.model.Almailem.SearchInhouseTransferLine;
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

import static org.apache.spark.sql.functions.col;

@Service
@Slf4j
public class TPLSparkInhouseTransferLineService {

    Properties conProp = new Properties();
    SparkSession sparkSession = null;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TPLSparkInhouseTransferLineService() throws ParseException {
        // Connection properties
        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conProp.put("user", "sa");
        conProp.put("password", "gQ3PmSL5D7u32Vtw5F74UwWH");

        // Initialize Spark session and read data from SQL table
        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://10.10.18.14;databaseName=WMS_3PL", "tblinhousetransferline", conProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblinhousetransferlinev6");
    }

    /**
     *
     * @param searchInhouseTransferLine
     * @return
     * @throws ParseException
     */
    public List<InhouseTransferLine> findInhouseTransferLines(SearchInhouseTransferLine searchInhouseTransferLine) throws ParseException {
        Dataset<Row> inhouseTransferLineSqlQuery = sparkSession.sql("SELECT "
                + "imv.LANG_ID as languageId, "
                + "imv.C_ID as companyCodeId, "
                + "imv.PLANT_ID as plantId, "
                + "imv.WH_ID as warehouseId, "
                + "imv.TR_NO as transferNumber, "
                + "imv.SRCE_ITM_CODE as sourceItemCode, "
                + "imv.SRCE_STCK_TYP_ID as sourceStockTypeId, "
                + "imv.SRCE_ST_BIN as sourceStorageBin, "
                + "imv.TGT_ITM_CODE as targetItemCode, "
                + "imv.TGT_STCK_TYP_ID as targetStockTypeId, "
                + "imv.TGT_ST_BIN as targetStorageBin, "
                + "imv.TR_ORD_QTY as transferOrderQty, "
                + "imv.TR_CNF_QTY as transferConfirmedQty, "
                + "imv.TR_UOM as transferUom, "
                + "imv.PAL_CODE as palletCode, "
                + "imv.CASE_CODE as caseCode, "
                + "imv.PACK_BARCODE as packBarcodes, "
                + "imv.SP_ST_IND_ID as specialStockIndicatorId, "
                + "imv.STATUS_ID as statusId, "
                + "imv.BARCODE_ID as barcodeId, "
                + "imv.REF_FIELD_1 as referenceField1, "
                + "imv.REF_FIELD_2 as referenceField2, "
                + "imv.REF_FIELD_3 as referenceField3, "
                + "imv.REF_FIELD_4 as referenceField4, "
                + "imv.REF_FIELD_5 as referenceField5, "
                + "imv.REF_FIELD_6 as referenceField6, "
                + "imv.REF_FIELD_7 as referenceField7, "
                + "imv.REF_FIELD_8 as referenceField8, "
                + "imv.REF_FIELD_9 as referenceField9, "
                + "imv.REF_FIELD_10 as referenceField10, "
                + "imv.IS_DELETED as deletionIndicator, "
                + "imv.REMARK as remarks, "
                + "imv.IT_CTD_BY as createdBy, "
                + "imv.IT_CTD_ON as createdOn, "
                + "imv.IT_CNF_BY as confirmedBy, "
                + "imv.IT_CNF_ON as confirmedOn, "
                + "imv.IT_UTD_BY as updatedBy, "
                + "imv.IT_UTD_ON as updatedOn, "
                + "imv.C_TEXT as companyDescription, "
                + "imv.PLANT_TEXT as plantDescription, "
                + "imv.WH_TEXT as warehouseDescription, "
                + "imv.SRCE_STCK_TYP_TEXT as sourceStockTypeDescription, "
                + "imv.TGT_STCK_TYP_TEXT as targetStockTypeDescription, "
                + "imv.MFR_NAME as manufacturerName "
//                + "cDesc.C_TEXT as companyDescription, "
//                + "pDesc.PLANT_TEXT as plantDescription, "
//                + "wDesc.WH_TEXT as warehouseDescription "
                + "FROM tblinhousetransferlinev6 imv "
//                + "JOIN tblcompanyid cDesc ON imv.C_ID = cDesc.C_ID AND imv.LANG_ID = cDesc.LANG_ID "
//                + "JOIN tblplantid pDesc ON imv.C_ID = pDesc.C_ID AND imv.LANG_ID = pDesc.LANG_ID AND imv.PLANT_ID = pDesc.PLANT_ID "
//                + "JOIN tblwarehouseid wDesc ON imv.C_ID = wDesc.C_ID AND imv.LANG_ID = wDesc.LANG_ID AND imv.WH_ID = wDesc.WH_ID "
                + "WHERE imv.IS_DELETED = 0 ");

        // Cache the DataFrame
//        inhouseTransferLineSqlQuery.cache();

        if (searchInhouseTransferLine.getLanguageId() != null && !searchInhouseTransferLine.getLanguageId().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("LANG_ID").isin(searchInhouseTransferLine.getLanguageId().toArray()));
        }
        if (searchInhouseTransferLine.getCompanyCodeId() != null && !searchInhouseTransferLine.getCompanyCodeId().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("C_ID").isin(searchInhouseTransferLine.getCompanyCodeId().toArray()));
        }
        if (searchInhouseTransferLine.getPlantId() != null && !searchInhouseTransferLine.getPlantId().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("PLANT_ID").isin(searchInhouseTransferLine.getPlantId().toArray()));
        }
        if (searchInhouseTransferLine.getWarehouseId() != null && !searchInhouseTransferLine.getWarehouseId().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("WH_ID").isin(searchInhouseTransferLine.getWarehouseId().toArray()));
        }
        if (searchInhouseTransferLine.getTransferNumber() != null && !searchInhouseTransferLine.getTransferNumber().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("TR_NO").isin(searchInhouseTransferLine.getTransferNumber().toArray()));
        }
        if (searchInhouseTransferLine.getSourceItemCode() != null && !searchInhouseTransferLine.getSourceItemCode().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("SRCE_ITM_CODE").isin(searchInhouseTransferLine.getSourceItemCode().toArray()));
        }
        if (searchInhouseTransferLine.getSourceStockTypeId() != null && !searchInhouseTransferLine.getSourceStockTypeId().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("SRCE_STCK_TYP_ID").isin(searchInhouseTransferLine.getSourceStockTypeId().toArray()));
        }
        if (searchInhouseTransferLine.getSourceStorageBin() != null && !searchInhouseTransferLine.getSourceStorageBin().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("SRCE_ST_BIN").isin(searchInhouseTransferLine.getSourceStorageBin().toArray()));
        }
        if (searchInhouseTransferLine.getTargetItemCode() != null && !searchInhouseTransferLine.getTargetItemCode().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("TGT_ITM_CODE").isin(searchInhouseTransferLine.getTargetItemCode().toArray()));
        }
        if (searchInhouseTransferLine.getTargetStockTypeId() != null && !searchInhouseTransferLine.getTargetStockTypeId().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("TGT_STCK_TYP_ID").isin(searchInhouseTransferLine.getTargetStockTypeId().toArray()));
        }
        if (searchInhouseTransferLine.getTargetStorageBin() != null && !searchInhouseTransferLine.getTargetStorageBin().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("TGT_ST_BIN").isin(searchInhouseTransferLine.getTargetStorageBin().toArray()));
        }
        if (searchInhouseTransferLine.getTransferConfirmedQty() != null && !searchInhouseTransferLine.getTransferConfirmedQty().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("TR_CNF_QTY").isin(searchInhouseTransferLine.getTransferConfirmedQty().toArray()));
        }
        if (searchInhouseTransferLine.getPackBarcodes() != null && !searchInhouseTransferLine.getPackBarcodes().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("PACK_BARCODE").isin(searchInhouseTransferLine.getPackBarcodes().toArray()));
        }
        if (searchInhouseTransferLine.getAvailableQty() != null && !searchInhouseTransferLine.getAvailableQty().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("AVAILABLE_QTY").isin(searchInhouseTransferLine.getAvailableQty().toArray()));
        }
        if (searchInhouseTransferLine.getStatusId() != null && !searchInhouseTransferLine.getStatusId().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("STATUS_ID").isin(searchInhouseTransferLine.getStatusId().toArray()));
        }
        if (searchInhouseTransferLine.getRemarks() != null && !searchInhouseTransferLine.getRemarks().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("REMARK").isin(searchInhouseTransferLine.getRemarks().toArray()));
        }
        if (searchInhouseTransferLine.getCreatedBy() != null && !searchInhouseTransferLine.getCreatedBy().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("IT_CTD_BY").isin(searchInhouseTransferLine.getCreatedBy().toArray()));
        }
        if (searchInhouseTransferLine.getConfirmedBy() != null && !searchInhouseTransferLine.getConfirmedBy().isEmpty()) {
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("IT_CNF_BY").isin(searchInhouseTransferLine.getConfirmedBy().toArray()));
        }
        if (searchInhouseTransferLine.getStartCreatedOn() != null) {
            Date startDate = searchInhouseTransferLine.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("IT_CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (searchInhouseTransferLine.getEndCreatedOn() != null) {
            Date endDate = searchInhouseTransferLine.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("IT_CTD_ON").$less$eq(dateFormat.format(endDate)));
        }
        if (searchInhouseTransferLine.getStartConfirmedOn() != null) {
            Date startDate = searchInhouseTransferLine.getStartConfirmedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("IT_CNF_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (searchInhouseTransferLine.getEndConfirmedOn() != null) {
            Date endDate = searchInhouseTransferLine.getEndConfirmedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            inhouseTransferLineSqlQuery = inhouseTransferLineSqlQuery.filter(col("IT_CNF_ON").$less$eq(dateFormat.format(endDate)));
        }

        // Encode the result as InhouseTransferLine objects
        Encoder<InhouseTransferLine> inhouseTransferLineEncoder = Encoders.bean(InhouseTransferLine.class);
        Dataset<InhouseTransferLine> dataSet = inhouseTransferLineSqlQuery.as(inhouseTransferLineEncoder);
        List<InhouseTransferLine> result = dataSet.collectAsList();

        return result;
    }
}
