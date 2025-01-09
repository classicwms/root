package com.mnrclara.spark.core.service.wmscorev2;


import com.mnrclara.spark.core.model.wmscorev2.FindStagingLineV2;
import com.mnrclara.spark.core.model.wmscorev2.StagingLineV2;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;

@Service
@Slf4j
public class SparkStagingLineV2ServiceV2 {


    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SparkStagingLineV2ServiceV2() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("StagingLine.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS_CORE", "tblstagingline", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblstaginglineV5core");
    }

    public List<StagingLineV2> findStagingLineV2(FindStagingLineV2 findStagingLineV2) throws ParseException {

        Dataset<Row>
                stagingLineQueryV2 =sparkSession.sql( "SELECT " +
                " LANG_ID as languageId, " +
                " C_ID as companyCode, " +
                " PLANT_ID as plantId, " +
                " WH_ID as warehouseId, " +
                " PRE_IB_NO as preInboundNo, " +
                " REF_DOC_NO as refDocNumber, " +
                "STG_NO as stagingNo, " +
                "PAL_CODE as palletCode," +
                "CASE_CODE as caseCode, " +
                "IB_LINE_NO as lineNo, " +
                "ITM_CODE as itemCode, " +
                "IB_ORD_TYP_ID as inboundOrderTypeId, " +
                "VAR_ID as variantCode, " +
                "VAR_SUB_ID as variantSubCode, " +
                "STR_NO as batchSerialNumber, " +
                "STCK_TYP_ID as stockTypeId, " +
                "SP_ST_IND_ID as specialStockIndicatorId, " +
                "ST_MTD as storageMethod, " +
                "STATUS_ID as statusId, " +
                "PARTNER_CODE as businessPartnerCode, " +
                "CONT_NO as containerNo, " +
                "INV_NO as invoiceNo, " +
                "ORD_QTY as orderQty, " +
                "ORD_UOM as orderUom, " +
                "ITM_PAL_QTY as itemQtyPerPallet, " +
                "ITM_CASE_QTY as itemQtyPerCase, " +
                "ASS_USER_ID as assignedUserId, " +
                "ITEM_TEXT as itemDescription, " +
                "MFR_PART as manufacturerPartNo, " +
                "HSN_CODE as hsnCode, " +
                "VAR_TYP as variantType, " +
                "SPEC_ACTUAL as specificationActual, " +
                "ITM_BARCODE as itemBarcode, " +
                "REF_ORD_NO as referenceOrderNo, " +
                "REF_ORD_QTY as referenceOrderQty, " +
                "CROSS_DOCK_ALLOC_QTY as crossDockAllocationQty, " +
                "REMARK as remarks, " +
                "REF_FIELD_1 as referenceField1, " +
                "REF_FIELD_2 as referenceField2, " +
                "REF_FIELD_3 as referenceField3, " +
                "REF_FIELD_4 as referenceField4, " +
                "REF_FIELD_5 as referenceField5, " +
                "REF_FIELD_6 as referenceField6, " +
                "REF_FIELD_7 as referenceField7, " +
                "REF_FIELD_8 as referenceField8, " +
                "REF_FIELD_9 as referenceField9, " +
                "REF_FIELD_10 as referenceField10, " +
                "IS_DELETED as deletionIndicator, " +
                "ST_CTD_BY as createdBy, " +
                "ST_CTD_ON as createdOn, " +
                "ST_UTD_BY as updatedBy, " +
                "ST_UTD_ON as updatedOn, " +
                "ST_CNF_BY as confirmedBy, " +
                "ST_CNF_ON as confirmedOn, " +
                "C_TEXT as companyDescription, " +
                "PLANT_TEXT as plantDescription, " +
                "WH_TEXT as warehouseDescription, " +
                "STATUS_TEXT as statusDescription, " +
                "INV_QTY as inventoryQuantity, " +
                "MFR_CODE as manufacturerCode, " +
                "MFR_NAME as manufacturerName, " +
                "ORIGIN as origin, " +
                "BRAND as brand, " +
                "PARTNER_ITEM_BARCODE as partner_item_barcode, " +
                "REC_ACCEPT_QTY as rec_accept_qty, " +
                "REC_DAMAGE_QTY as rec_damage_qty, " +
                "MIDDLEWARE_ID as middlewareId, " +
                "MIDDLEWARE_HEADER_ID as middlewareHeaderId, " +
                "MIDDLEWARE_TABLE as middlewareTable, " +
                "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, " +
                "MANUFACTURER_FULL_NAME as manufacturerFullName, " +
                "REF_DOC_TYPE as referenceDocumentType, " +
                "BRANCH_CODE as branchCode, " +
                "TRANSFER_ORDER_NO as transferOrderNo, " +
                "IS_COMPLETED as isCompleted " +
                "FROM tblstaginglineV5core WHERE IS_DELETED = 0 ");

//        stagingLineQueryV2.cache();


        if (findStagingLineV2.getLanguageId() != null && !findStagingLineV2.getLanguageId().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("LANG_ID").isin(findStagingLineV2.getLanguageId().toArray()));
        }
        if (findStagingLineV2.getCompanyCodeId() != null && !findStagingLineV2.getCompanyCodeId().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("C_ID").isin(findStagingLineV2.getCompanyCodeId().toArray()));
        }
        if (findStagingLineV2.getPlantId() != null && !findStagingLineV2.getPlantId().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("PLANT_ID").isin(findStagingLineV2.getPlantId().toArray()));
        }
        if (findStagingLineV2.getWarehouseId() != null && !findStagingLineV2.getWarehouseId().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("WH_ID").isin(findStagingLineV2.getWarehouseId().toArray()));
        }

        if (findStagingLineV2.getManufacturerCode() != null && !findStagingLineV2.getManufacturerCode().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("MFR_CODE").isin(findStagingLineV2.getManufacturerCode().toArray()));
        }

        if (findStagingLineV2.getManufacturerName() != null && !findStagingLineV2.getManufacturerName().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("MFR_NAME").isin(findStagingLineV2.getManufacturerName().toArray()));
        }

        if (findStagingLineV2.getOrigin() != null && !findStagingLineV2.getOrigin().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("ORIGIN").isin(findStagingLineV2.getOrigin().toArray()));
        }
        if (findStagingLineV2.getBrand() != null && !findStagingLineV2.getBrand().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("BRAND").isin(findStagingLineV2.getBrand().toArray()));
        }
        if (findStagingLineV2.getPreInboundNo() != null && !findStagingLineV2.getPreInboundNo().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("PRE_IB_NO").isin(findStagingLineV2.getPreInboundNo().toArray()));
        }
        if (findStagingLineV2.getRefDocNumber() != null && !findStagingLineV2.getRefDocNumber().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("REF_DOC_NO").isin(findStagingLineV2.getRefDocNumber().toArray()));
        }
        if (findStagingLineV2.getStagingNo() != null && !findStagingLineV2.getStagingNo().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("STG_NO").isin(findStagingLineV2.getStagingNo().toArray()));
        }
        if (findStagingLineV2.getPalletCode() != null && !findStagingLineV2.getPalletCode().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("PAL_CODE").isin(findStagingLineV2.getPalletCode().toArray()));
        }
        if (findStagingLineV2.getCaseCode() != null && !findStagingLineV2.getCaseCode().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("CASE_CODE").isin(findStagingLineV2.getCaseCode().toArray()));
        }

        if (findStagingLineV2.getLineNo() != null && !findStagingLineV2.getLineNo().isEmpty()) {
            List<String> lineNoString = findStagingLineV2.getLineNo().stream().map(String::valueOf).collect(Collectors.toList());
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("IB_LINE_NO").isin(lineNoString.toArray()));
        }
        if (findStagingLineV2.getItemCode() != null && !findStagingLineV2.getItemCode().isEmpty()) {
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("ITM_CODE").isin(findStagingLineV2.getItemCode().toArray()));
        }
        if (findStagingLineV2.getStatusId() != null && !findStagingLineV2.getStatusId().isEmpty()) {
            List<String> statusString = findStagingLineV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            stagingLineQueryV2 = stagingLineQueryV2.filter(col("STATUS_ID").isin(statusString.toArray()));
        }




        Encoder<StagingLineV2> stagingLineEncoder = Encoders.bean(StagingLineV2.class);
        Dataset<StagingLineV2> dataSetControlGroup = stagingLineQueryV2.as(stagingLineEncoder);
        List<StagingLineV2> result = dataSetControlGroup.collectAsList();

        return result;
    }
}