package com.mnrclara.spark.core.service.jaincord;

import com.mnrclara.spark.core.model.wmscorev2.PickupLine;
import com.mnrclara.spark.core.model.wmscorev2.SearchPickupLineV2;
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
public class SparkPickupLineServiceV2JainCord {

    Properties conProp = new Properties();
    SparkSession sparkSession = null;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SparkPickupLineServiceV2JainCord() throws ParseException {
        // Connection properties
        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conProp.put("user", "sa");
        conProp.put("password", "TTPL@123");

        // Initialize Spark session and read data from SQL table
        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        val df2 = sparkSession.read().jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblpickupline", conProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblpickuplinev5jain");
    }

    /**
     *
     * @param searchPickupLine
     * @return
     * @throws ParseException
     */
    public List<PickupLine> findPickupLines(SearchPickupLineV2 searchPickupLine) throws ParseException {


        Dataset<Row> pickupLineSqlQuery = sparkSession.sql("SELECT "
                + "LANG_ID as languageId, "
                + "C_ID as companyCodeId, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "PRE_OB_NO as preOutboundNo, "
                + "REF_DOC_NO as refDocNumber, "
                + "PARTNER_CODE as partnerCode, "
                + "OB_LINE_NO as lineNumber, "
                + "PU_NO as pickupNumber, "
                + "ITM_CODE as itemCode, "
                + "PICK_HE_NO as actualHeNo, "
                + "PICK_ST_BIN as pickedStorageBin, "
                + "PICK_PACK_BARCODE as pickedPackCode, "
                + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                + "VAR_ID as variantCode, "
                + "VAR_SUB_ID as variantSubCode, "
                + "STR_NO as batchSerialNumber, "
                + "PICK_CNF_QTY as pickConfirmQty, "
                + "ALLOC_QTY as allocatedQty, "
                + "PICK_UOM as pickUom, "
                + "STCK_TYP_ID as stockTypeId, "
                + "SP_ST_IND_ID as specialStockIndicatorId, "
                + "ITEM_TEXT as description, "
                + "MFR_PART as manufacturerPartNo, "
                + "ASS_PICKER_ID as assignedPickerId, "
                + "PICK_PAL_CODE as pickPalletCode, "
                + "PICK_CASE_CODE as pickCaseCode, "
                + "STATUS_ID as statusId, "
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
                + "PICK_CTD_BY as pickupCreatedBy, "
                + "PICK_CTD_ON as pickupCreatedOn, "
                + "PICK_UTD_BY as pickupUpdatedBy, "
                + "PICK_UTD_ON as pickupUpdatedOn, "
                + "PICK_CNF_BY as pickupConfirmedBy, "
                + "PICK_CNF_ON as pickupConfirmedOn, "
                + "PICK_REV_BY as pickupReversedBy, "
                + "PICK_REV_ON as pickupReversedOn, "
                + "INV_QTY as inventoryQuantity, "
                + "PICK_CBM as pickedCbm, "
                + "CBM_UNIT as cbmUnit, "
                + "MFR_CODE as manufacturerCode, "
                + "MFR_NAME as manufacturerName, "
                + "ORIGIN as origin, "
                + "BRAND as brand, "
                + "PARTNER_ITEM_BARCODE as partnerItemBarcode, "
                + "LEVEL_ID as levelId, "
                + "C_TEXT as companyDescription, "
                + "PLANT_TEXT as plantDescription, "
                + "WH_TEXT as warehouseDescription, "
                + "STATUS_TEXT as statusDescription, "
                + "MIDDLEWARE_ID as middlewareId, "
                + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                + "MIDDLEWARE_TABLE as middlewareTable, "
                + "REF_DOC_TYPE as referenceDocumentType, "
                + "SUPPLIER_INVOICE_NO as supplierInvoiceNo, "
                + "SALES_ORDER_NUMBER as salesOrderNumber, "
                + "PICK_LIST_NUMBER as pickListNumber, "
                + "TOKEN_NUMBER as tokenNumber, "
                + "SALES_INVOICE_NUMBER as salesInvoiceNumber, "
                + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                + "TARGET_BRANCH_CODE as targetBranchCode, "
                + "VAR_QTY as varianceQuantity, "
                + "PICK_CBM as pickCbm, "
                + "CUSTOMER_ID as customerId, "
                + "CUSTOMER_NAME as customerName "
                + "FROM tblpickuplinev5jain WHERE IS_DELETED = 0 ");

        // Cache the DataFrame
//        pickupLineSqlQuery.cache();


        if (searchPickupLine.getLanguageId() != null && !searchPickupLine.getLanguageId().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("LANG_ID").isin(searchPickupLine.getLanguageId().toArray()));
        }
        if (searchPickupLine.getCompanyCodeId() != null && !searchPickupLine.getCompanyCodeId().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("C_ID").isin(searchPickupLine.getCompanyCodeId().toArray()));
        }
        if (searchPickupLine.getPlantId() != null && !searchPickupLine.getPlantId().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("PLANT_ID").isin(searchPickupLine.getPlantId().toArray()));
        }
        if (searchPickupLine.getWarehouseId() != null && !searchPickupLine.getWarehouseId().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("WH_ID").isin(searchPickupLine.getWarehouseId().toArray()));
        }
        if (searchPickupLine.getPreOutboundNo() != null && !searchPickupLine.getPreOutboundNo().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("PRE_OB_NO").isin(searchPickupLine.getPreOutboundNo().toArray()));
        }
        if (searchPickupLine.getRefDocNumber() != null && !searchPickupLine.getRefDocNumber().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("REF_DOC_NO").isin(searchPickupLine.getRefDocNumber().toArray()));
        }
        if (searchPickupLine.getPartnerCode() != null && !searchPickupLine.getPartnerCode().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("PARTNER_CODE").isin(searchPickupLine.getPartnerCode().toArray()));
        }
        if (searchPickupLine.getLineNumber() != null && !searchPickupLine.getLineNumber().isEmpty()) {
            List<String> lineNumberString = searchPickupLine.getLineNumber().stream().map(String::valueOf).collect(Collectors.toList());
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("OB_LINE_NO").isin(lineNumberString.toArray()));
        }
        if (searchPickupLine.getItemCode() != null && !searchPickupLine.getItemCode().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("ITM_CODE").isin(searchPickupLine.getItemCode().toArray()));
        }
        if (searchPickupLine.getActualHeNo() != null && !searchPickupLine.getActualHeNo().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("PICK_HE_NO").isin(searchPickupLine.getActualHeNo().toArray()));
        }
        if (searchPickupLine.getPickedStorageBin() != null && !searchPickupLine.getPickedStorageBin().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("PICK_ST_BIN").isin(searchPickupLine.getPickedStorageBin().toArray()));
        }
        if (searchPickupLine.getPickedPackCode() != null && !searchPickupLine.getPickedPackCode().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("PICK_PACK_BARCODE").isin(searchPickupLine.getPickedPackCode().toArray()));
        }
        if (searchPickupLine.getAssignedPickerId() != null && !searchPickupLine.getAssignedPickerId().isEmpty()) {
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("ASS_PICKER_ID").isin(searchPickupLine.getAssignedPickerId().toArray()));
        }
        if (searchPickupLine.getStatusId() != null && !searchPickupLine.getStatusId().isEmpty()) {
            List<String> statusIdString = searchPickupLine.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("STATUS_ID").isin(statusIdString.toArray()));
        }
        if (searchPickupLine.getFromPickConfirmedOn() != null) {
            Date startDate = searchPickupLine.getFromPickConfirmedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("PICK_CNF_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (searchPickupLine.getToPickConfirmedOn() != null) {
            Date endDate = searchPickupLine.getToPickConfirmedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            pickupLineSqlQuery = pickupLineSqlQuery.filter(col("PICK_CNF_ON").$less$eq(dateFormat.format(endDate)));
        }

        // Encode the result as PickupLine objects
        Encoder<PickupLine> pickupLineEncoder = Encoders.bean(PickupLine.class);
        Dataset<PickupLine> dataSet = pickupLineSqlQuery.as(pickupLineEncoder);
        List<PickupLine> result = dataSet.collectAsList();

        return result;
    }
}