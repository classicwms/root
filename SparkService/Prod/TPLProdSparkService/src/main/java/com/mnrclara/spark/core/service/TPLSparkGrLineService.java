package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.Almailem.FindGrLineV2;
import com.mnrclara.spark.core.model.threepl.GrLineV4;
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
public class TPLSparkGrLineService {


    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TPLSparkGrLineService() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "4V7lOXaxgAi3i6mgJL7qBUSPM");
        sparkSession = SparkSession.builder().master("local[*]").appName("GrLine.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://10.10.10.61;databaseName=WMS_3PL", "tblGrline", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblGrlineV6");

    }

    public List<GrLineV4> findGrLineV2(FindGrLineV2 findGrLineV2) throws ParseException {

        Dataset<Row>
                grLineQueryV2 = sparkSession.sql("SELECT " +
                " LANG_ID as languageId, " +
                " C_ID as companyCode, " +
                " PLANT_ID as plantId, " +
                " WH_ID as warehouseId, " +
                " PRE_IB_NO as preInboundNo, " +
                " REF_DOC_NO as refDocNumber, " +
                " GR_NO as goodsReceiptNo, " +
                " PAL_CODE as palletCode," +
                "CASE_CODE as caseCode, " +
                " PACK_BARCODE as packBarcodes, " +
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
                "ITEM_TEXT as itemDescription, " +
                "MFR_PART as manufacturerPartNo, " +
                "HSN_CODE as hsnCode, " +
                "VAR_TYP as variantType, " +
                "SPEC_ACTUAL as specificationActual, " +
                "ITM_BARCODE as itemBarcode, " +
                "ORD_QTY as orderQty, " +
                "ORD_UOM as orderUom, " +
                "GR_QTY as goodReceiptQty, " +
                "GR_UOM as grUom, " +
                "ACCEPT_QTY as acceptedQty, " +
                "DAMAGE_QTY as damageQty, " +
                "QTY_TYPE as quantityType, " +
                "ASS_USER_ID as assignedUserId, " +
                "PAWAY_HE_NO as putAwayHandlingEquipment, " +
                "PA_CNF_QTY as confirmedQty, " +
                "REM_QTY as remainingQty, " +
                "REF_ORD_NO as referenceOrderNo, " +
                "REF_ORD_QTY as referenceOrderQty, " +
                "CROSS_DOCK_ALLOC_QTY as crossDockAllocationQty, " +
                "MFR_DATE as manufacturerDate, " +
                "EXP_DATE as expiryDate, " +
                "STR_QTY as storageQty, " +
                "REMARK as remark, " +
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
                "GR_CTD_BY as createdBy, " +
                "GR_CTD_ON as createdOn, " +
                "GR_UTD_BY as updatedBy, " +
                "GR_UTD_ON as updatedOn, " +
                "GR_CNF_BY as confirmedBy, " +
                "GR_CNF_ON as confirmedOn, " +
                "INV_QTY as inventoryQuantity, " +
                "BARCODE_ID as barcodeId, " +
                "CBM as cbm, " +
                "CBM_UNIT as cbmUnit, " +
                "MFR_CODE as manufacturerCode, " +
                "MFR_NAME as manufacturerName, " +
                "ORIGIN as origin, " +
                "BRAND as brand, " +
                "REJ_TYPE as rejectType, " +
                "REJ_REASON as rejectReason, " +
                "CBM_QTY as cbmQuantity, " +
                "C_TEXT as companyDescription, " +
                "PLANT_TEXT as plantDescription, " +
                "WH_TEXT as warehouseDescription, " +
                "ST_BIN_INTM as interimStorageBin, " +
                "STATUS_TEXT as statusDescription, " +
                "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, " +
                "MIDDLEWARE_ID as middlewareId, " +
                "MIDDLEWARE_HEADER_ID as middlewareHeaderId, " +
                "MIDDLEWARE_TABLE as middlewareTable, " +
                "MANUFACTURER_FULL_NAME as manufacturerFullName, " +
                "REF_DOC_TYPE as referenceDocumentType, " +
                "BRANCH_CODE as branchCode, " +
                "TRANSFER_ORDER_NO as transferOrderNo, " +
                "AMS_SUP_INV as AMSSupplierInvoiceNo, " +
                "IS_COMPLETED as isCompleted, " +
                "TPL_CBM as threePLCbm, " +
                "TPL_UOM as threePLUom, " +
                "TPL_BILL_STATUS as threePLBillStatus, " +
                "TPL_LENGTH as threePLLength, " +
                "TPL_HEIGHT as threePLHeight, " +
                "TPL_WIDTH as threePLWidth, " +
                "RATE as rate, " +
                "CURRENCY as currency, " +
                "TOTAL_TPL_CBM as totalThreePLCbm," +
                "TOTAL_RATE as totalRate, " +
                "PARTNER_TEXT as partnerText " +
                "FROM tblGrlineV6 WHERE IS_DELETED = 0 ");

//        grLineQueryV2.cache();

        if (findGrLineV2.getLanguageId() != null && !findGrLineV2.getLanguageId().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("LANG_ID").isin(findGrLineV2.getLanguageId().toArray()));
        }
        if (findGrLineV2.getCompanyCodeId() != null && !findGrLineV2.getCompanyCodeId().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("C_ID").isin(findGrLineV2.getCompanyCodeId().toArray()));
        }
        if (findGrLineV2.getPlantId() != null && !findGrLineV2.getPlantId().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("PLANT_ID").isin(findGrLineV2.getPlantId().toArray()));
        }
        if (findGrLineV2.getWarehouseId() != null && !findGrLineV2.getWarehouseId().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("WH_ID").isin(findGrLineV2.getWarehouseId().toArray()));
        }

        if (findGrLineV2.getManufacturerCode() != null && !findGrLineV2.getManufacturerCode().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("MFR_CODE").isin(findGrLineV2.getManufacturerCode().toArray()));
        }

        if (findGrLineV2.getManufacturerName() != null && !findGrLineV2.getManufacturerName().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("MFR_NAME").isin(findGrLineV2.getManufacturerName().toArray()));
        }

        if (findGrLineV2.getOrigin() != null && !findGrLineV2.getOrigin().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("ORIGIN").isin(findGrLineV2.getOrigin().toArray()));
        }
        if (findGrLineV2.getBrand() != null && !findGrLineV2.getBrand().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("BRAND").isin(findGrLineV2.getBrand().toArray()));
        }
        if (findGrLineV2.getPreInboundNo() != null && !findGrLineV2.getPreInboundNo().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("PRE_IB_NO").isin(findGrLineV2.getPreInboundNo().toArray()));
        }
        if (findGrLineV2.getRefDocNumber() != null && !findGrLineV2.getRefDocNumber().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("REF_DOC_NO").isin(findGrLineV2.getRefDocNumber().toArray()));
        }

        if (findGrLineV2.getCaseCode() != null && !findGrLineV2.getCaseCode().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("CASE_CODE").isin(findGrLineV2.getCaseCode().toArray()));
        }

        if (findGrLineV2.getLineNo() != null && !findGrLineV2.getLineNo().isEmpty()) {
            List<String> lineNoString = findGrLineV2.getLineNo().stream().map(String::valueOf).collect(Collectors.toList());
            grLineQueryV2 = grLineQueryV2.filter(col("IB_LINE_NO").isin(lineNoString.toArray()));
        }
        if (findGrLineV2.getItemCode() != null && !findGrLineV2.getItemCode().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("ITM_CODE").isin(findGrLineV2.getItemCode().toArray()));
        }
        if (findGrLineV2.getStatusId() != null && !findGrLineV2.getStatusId().isEmpty()) {
            List<String> statusString = findGrLineV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            grLineQueryV2 = grLineQueryV2.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findGrLineV2.getInterimStorageBin() != null && !findGrLineV2.getInterimStorageBin().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("ST_BIN_INTM").isin(findGrLineV2.getInterimStorageBin().toArray()));
        }
        if (findGrLineV2.getRejectType() != null && !findGrLineV2.getRejectType().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("REJ_TYPE").isin(findGrLineV2.getRejectType().toArray()));
        }
        if (findGrLineV2.getRejectReason() != null && !findGrLineV2.getRejectReason().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("REJ_REASON").isin(findGrLineV2.getRejectReason().toArray()));
        }
        if (findGrLineV2.getPackBarcodes() != null && !findGrLineV2.getPackBarcodes().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("PACK_BARCODE").isin(findGrLineV2.getPackBarcodes().toArray()));
        }
        if (findGrLineV2.getThreePLCbm() != null && !findGrLineV2.getThreePLCbm().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("TPL_CBM").isin(findGrLineV2.getThreePLCbm().toArray()));
        }
        if (findGrLineV2.getThreePLUom() != null && !findGrLineV2.getThreePLUom().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("TPL_UOM").isin(findGrLineV2.getThreePLUom().toArray()));
        }
        if (findGrLineV2.getThreePLBillStatus() != null && !findGrLineV2.getThreePLBillStatus().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("TPL_BILL_STATUS").isin(findGrLineV2.getThreePLBillStatus().toArray()));
        }
        if (findGrLineV2.getThreePLWidth() != null && !findGrLineV2.getThreePLWidth().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("TPL_WIDTH").isin(findGrLineV2.getThreePLWidth().toArray()));
        }
        if (findGrLineV2.getThreePLHeight() != null && !findGrLineV2.getThreePLHeight().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("TPL_HEIGHT").isin(findGrLineV2.getThreePLHeight().toArray()));
        }
        if (findGrLineV2.getThreePLLength() != null && !findGrLineV2.getThreePLLength().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("TPL_LENGTH").isin(findGrLineV2.getThreePLLength().toArray()));
        }
        if (findGrLineV2.getBusinessPartnerCode() != null && !findGrLineV2.getBusinessPartnerCode().isEmpty()) {
            grLineQueryV2 = grLineQueryV2.filter(col("PARTNER_CODE").isin(findGrLineV2.getBusinessPartnerCode().toArray()));
        }
        if (findGrLineV2.getStartCreatedOn() != null) {
            Date startDate = findGrLineV2.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            grLineQueryV2 = grLineQueryV2.filter(col("GR_CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (findGrLineV2.getEndCreatedOn() != null) {
            Date endDate = findGrLineV2.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            grLineQueryV2 = grLineQueryV2.filter(col("GR_CTD_ON").$less$eq(dateFormat.format(endDate)));
        }
        Encoder<GrLineV4> grLineEncoder = Encoders.bean(GrLineV4.class);
        Dataset<GrLineV4> dataSetControlGroup = grLineQueryV2.as(grLineEncoder);
        List<GrLineV4> result = dataSetControlGroup.collectAsList();

        return result;


    }
}