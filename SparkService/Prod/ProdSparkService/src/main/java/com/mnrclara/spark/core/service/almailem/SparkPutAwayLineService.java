package com.mnrclara.spark.core.service.almailem;

import com.mnrclara.spark.core.model.Almailem.FindPutAwayLineV2;
import com.mnrclara.spark.core.model.Almailem.PutAwayLineV2;
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
public class SparkPutAwayLineService {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SparkPutAwayLineService() {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "SuHcHQR72nxvyJx6EPpoOsK4V");
        sparkSession = SparkSession.builder().master("local[*]").appName("PutAwayLine.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://10.10.6.30;databaseName=WMS_ALMPRD", "tblputawayline", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblputawaylinev2");
    }

    public List<PutAwayLineV2> findPutAwayLineV2(FindPutAwayLineV2 findPutAwayLineV2) throws ParseException {

        Dataset<Row> putAwayLineV2Query = sparkSession.sql("Select "
                + "LANG_ID as languageId, "
                + "C_ID as companyCode, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "GR_NO as goodsReceiptNo, "
                + "PRE_IB_NO as preInboundNo, "
                + "REF_DOC_NO as refDocNumber, "
                + "PA_NO as putAwayNumber, "
                + "IB_LINE_NO as lineNo, "
                + "ITM_CODE as itemCode, "
                + "PROP_ST_BIN as proposedStorageBin, "
                + "CNF_ST_BIN as confirmedStorageBin, "
                + "PACK_BARCODE as packBarcodes, "
                + "PA_QTY as putAwayQuantity, "
                + "PA_UOM as putAwayUom, "
                + "PA_CNF_QTY as putawayConfirmedQty, "
                + "VAR_ID as variantCode, "
                + "VAR_SUB_ID as variantSubCode, "
                + "ST_MTD as storageMethod, "
                + "STR_NO as batchSerialNumber, "
                + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                + "STCK_TYP_ID as stockTypeId, "
                + "SP_ST_IND_ID as specialStockIndicatorId, "
                + "REF_ORD_NO as referenceOrderNo, "
                + "STATUS_ID as statusId, "
                + "TEXT as description, "
                + "SPEC_ACTUAL as specificationActual, "
                + "VEN_CODE as vendorCode, "
                + "MFR_PART as manufacturerPartNo, "
                + "HSN_CODE as hsnCode, "
                + "ITM_BARCODE as itemBarcode, "
                + "MFR_DATE as manufacturerDate, "
                + "EXP_DATE as expiryDate, "
                + "STR_QTY as storageQty, "
                + "ST_TEMP as storageTemperature, "
                + "ST_UOM as storageUom, "
                + "QTY_TYPE as quantityType, "
                + "PROP_HE_NO as proposedHandlingEquipment, "
                + "ASS_USER_ID as assignedUserId, "
                + "WRK_CTR_ID as workCenterId, "
                + "PAWAY_HE_NO as putAwayHandlingEquipment, "
                + "PAWAY_EMP_ID as putAwayEmployeeId, "
                + "CREATE_REMARK as createRemarks, "
                + "CNF_REMARK as cnfRemarks, "
                + "IS_DELETED as deletionIndicator,"
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
                + "PA_CTD_BY as createdBy, "
                + "PA_CTD_ON as createdOn, "
                + "PA_CNF_BY as confirmedBy, "
                + "PA_CNF_ON as confirmedOn, "
                + "PA_UTD_BY as updatedBy, "
                + "PA_UTD_ON as updatedOn, "

                // V2 fields
                + "INV_QTY as inventoryQuantity, "
                + "BARCODE_ID as barcodeId, "
                + "MFR_CODE as manufacturerCode, "
                + "MFR_NAME as manufacturerName, "
                + "ORIGIN as origin, "
                + "BRAND as brand, "
                + "ORD_QTY as orderQty, "
                + "CBM as cbm, "
                + "CBM_UNIT as cbmUnit, "
                + "CBM_QTY as cbmQuantity, "
                + "C_TEXT as companyDescription, "
                + "PLANT_TEXT as plantDescription, "
                + "WH_TEXT as warehouseDescription, "
                + "STATUS_TEXT as statusDescription, "
                + "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, "
                + "MIDDLEWARE_ID as middlewareId, "
                + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                + "MIDDLEWARE_TABLE as middlewareTable, "
                + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                + "REF_DOC_TYPE as referenceDocumentType, "
                + "BRANCH_CODE as branchCode, "
                + "TRANSFER_ORDER_NO as transferOrderNo, "
                + "AMS_SUP_INV as AMSSupplierInvoiceNo, "
                + "IS_COMPLETED as isCompleted "
                + "From tblputawaylinev2 Where IS_DELETED = 0 "
        );
//        putAwayLineV2Query.cache();

        if (findPutAwayLineV2.getWarehouseId() != null && !findPutAwayLineV2.getWarehouseId().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("WH_ID").isin(findPutAwayLineV2.getWarehouseId().toArray()));
        }
        if (findPutAwayLineV2.getGoodsReceiptNo() != null && !findPutAwayLineV2.getGoodsReceiptNo().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("GR_NO").isin(findPutAwayLineV2.getGoodsReceiptNo().toArray()));
        }
        if (findPutAwayLineV2.getPreInboundNo() != null && !findPutAwayLineV2.getPreInboundNo().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("PRE_IB_NO").isin(findPutAwayLineV2.getPreInboundNo().toArray()));
        }
        if (findPutAwayLineV2.getRefDocNumber() != null && !findPutAwayLineV2.getRefDocNumber().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("REF_DOC_NO").isin(findPutAwayLineV2.getRefDocNumber().toArray()));
        }
        if (findPutAwayLineV2.getPutAwayNumber() != null && !findPutAwayLineV2.getPutAwayNumber().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("PA_NO").isin(findPutAwayLineV2.getPutAwayNumber().toArray()));
        }
        if (findPutAwayLineV2.getLineNo() != null && !findPutAwayLineV2.getLineNo().isEmpty()) {
            List<String> lineToString = findPutAwayLineV2.getLineNo().stream().map(String::valueOf).collect(Collectors.toList());
            putAwayLineV2Query = putAwayLineV2Query.filter(col("IB_LINE_NO").isin(lineToString.toArray()));
        }
        if (findPutAwayLineV2.getStatusId() != null && !findPutAwayLineV2.getStatusId().isEmpty()) {
            List<String> statusString = findPutAwayLineV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            putAwayLineV2Query = putAwayLineV2Query.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findPutAwayLineV2.getItemCode() != null && !findPutAwayLineV2.getItemCode().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("ITM_CODE").isin(findPutAwayLineV2.getItemCode().toArray()));
        }
        if (findPutAwayLineV2.getProposedStorageBin() != null && !findPutAwayLineV2.getProposedStorageBin().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("PROP_ST_BIN").isin(findPutAwayLineV2.getProposedStorageBin().toArray()));
        }
        if (findPutAwayLineV2.getConfirmedStorageBin() != null && !findPutAwayLineV2.getConfirmedStorageBin().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("CNF_ST_BIN").isin(findPutAwayLineV2.getConfirmedStorageBin().toArray()));
        }
        if (findPutAwayLineV2.getPackBarCodes() != null && !findPutAwayLineV2.getPackBarCodes().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("PACK_BARCODE").isin(findPutAwayLineV2.getPackBarCodes().toArray()));
        }
        if (findPutAwayLineV2.getFromConfirmedDate() != null) {
            Date startDate = findPutAwayLineV2.getFromConfirmedDate();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            putAwayLineV2Query = putAwayLineV2Query.filter(col("PA_CNF_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findPutAwayLineV2.getToConfirmedDate() != null) {
            Date endDate = findPutAwayLineV2.getToConfirmedDate();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            putAwayLineV2Query = putAwayLineV2Query.filter(col("PA_CNF_ON").$less$eq(dateFormat.format(endDate)));
        }
        if (findPutAwayLineV2.getFromCreatedDate() != null) {
            Date startDate = findPutAwayLineV2.getFromCreatedDate();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            putAwayLineV2Query = putAwayLineV2Query.filter(col("PA_CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findPutAwayLineV2.getToCreatedDate() != null) {
            Date endDate = findPutAwayLineV2.getToCreatedDate();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            putAwayLineV2Query = putAwayLineV2Query.filter(col("PA_CTD_ON").$less$eq(dateFormat.format(endDate)));
        }

        // V2 fields
        if (findPutAwayLineV2.getLanguageId() != null && !findPutAwayLineV2.getLanguageId().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("LANG_ID").isin(findPutAwayLineV2.getLanguageId().toArray()));
        }
        if (findPutAwayLineV2.getCompanyCodeId() != null && !findPutAwayLineV2.getCompanyCodeId().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("C_ID").isin(findPutAwayLineV2.getCompanyCodeId().toArray()));
        }
        if (findPutAwayLineV2.getPlantId() != null && !findPutAwayLineV2.getPlantId().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("PLANT_ID").isin(findPutAwayLineV2.getPlantId().toArray()));
        }
        if (findPutAwayLineV2.getBarcodeId() != null && !findPutAwayLineV2.getBarcodeId().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("BARCODE_ID").isin(findPutAwayLineV2.getBarcodeId().toArray()));
        }
        if (findPutAwayLineV2.getManufacturerCode() != null && !findPutAwayLineV2.getManufacturerCode().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("MFR_CODE").isin(findPutAwayLineV2.getManufacturerCode().toArray()));
        }
        if (findPutAwayLineV2.getManufacturerName() != null && !findPutAwayLineV2.getManufacturerName().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("MFR_NAME").isin(findPutAwayLineV2.getManufacturerName().toArray()));
        }
        if (findPutAwayLineV2.getOrigin() != null && !findPutAwayLineV2.getOrigin().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("ORIGIN").isin(findPutAwayLineV2.getOrigin().toArray()));
        }
        if (findPutAwayLineV2.getBrand() != null && !findPutAwayLineV2.getBrand().isEmpty()) {
            putAwayLineV2Query = putAwayLineV2Query.filter(col("BRAND").isin(findPutAwayLineV2.getBrand().toArray()));
        }

        Encoder<PutAwayLineV2> putAwayLineEncoder = Encoders.bean(PutAwayLineV2.class);
        Dataset<PutAwayLineV2> datasetControlGroup = putAwayLineV2Query.as(putAwayLineEncoder);
        List<PutAwayLineV2> results = datasetControlGroup.collectAsList();
        return results;
    }
}
