package com.mnrclara.spark.core.service.jaincord;

import com.mnrclara.spark.core.model.wmscorev2.FindPickupHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.PickupHeaderV2;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;

@Service
@Slf4j
public class SparkPickupHeaderServiceV2JainCord {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;

    public SparkPickupHeaderServiceV2JainCord() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
        sparkSession = SparkSession.builder().master("local[*]").appName("PickupHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read()
                .option("fetchSize", "10000").jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblpickupheader", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblpickupheaderv5jain");
    }

    /**
     * @param findPickupHeaderV2
     * @return
     * @throws ParseException
     */
    public List<PickupHeaderV2> findPickupHeaderV2(FindPickupHeaderV2 findPickupHeaderV2) throws ParseException {

        Dataset<Row> imPickupHeaderQuery = sparkSession.sql("SELECT "
                + "LANG_ID as languageId, "
                + "C_ID as companyCodeId, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "PRE_OB_NO as preOutboundNo, "
                + "REF_DOC_NO as refDocNumber, "
                + "PARTNER_CODE as partnerCode, "
                + "PU_NO as pickupNumber, "
                + "OB_LINE_NO as lineNumber, "
                + "ITM_CODE as itemCode, "
                + "PROP_ST_BIN as proposedStorageBin, "
                + "PROP_PACK_BARCODE as proposedPackBarCode, "
                + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                + "PICK_TO_QTY as pickToQty, "
                + "PICK_UOM as pickUom, "
                + "STCK_TYP_ID as stockTypeId, "
                + "SP_ST_IND_ID as specialStockIndicatorId, "
                + "MFR_PART as manufacturerPartNo, "
                + "STATUS_ID as statusId, "
                + "ASS_PICKER_ID as assignedPickerId, "
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
                + "PICK_CTD_BY as pickupCreatedBy, "
                + "PICK_CTD_ON as pickupCreatedOn, "
                + "PICK_CNF_BY as pickConfimedBy, "
                + "PICK_CNF_ON as pickConfimedOn, "
                + "PICK_UTD_BY as pickUpdatedBy, "
                + "PICK_UTD_ON as pickUpdatedOn, "
                + "PICK_REV_BY as pickupReversedBy, "
                + "PICK_REV_ON as pickupReversedOn, "

                // V2 fields
                + "INV_QTY as inventoryQuantity, "
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
                + "MIDDLEWARE_TABLE as middlewareTable, "
                + "REF_DOC_TYPE as referenceDocumentType, "
                + "SALES_ORDER_NUMBER as salesOrderNumber, "
                + "SALES_INVOICE_NUMBER as salesInvoiceNumber, "
                + "SUPPLIER_INVOICE_NO as supplierInvoiceNo, "
                + "PICK_LIST_NUMBER as pickListNumber, "
                + "TOKEN_NUMBER as tokenNumber, "
                + "FROM_BRANCH_CODE as fromBranchCode, "
                + "IS_COMPLETED as isCompleted, "
                + "IS_CANCELLED as isCancelled, "
                + "M_UPDATED_ON as mUpdatedOn, "
                + "TARGET_BRANCH_CODE as targetBranchCode, "
                + "STR_NO as batchSerialNumber, "
                + "CUSTOMER_ID as customerId, "
                + "CUSTOMER_NAME as customerName "
                + "FROM tblpickupheaderv5jain WHERE IS_DELETED = 0 ");

//        String pickupHeaderString = imPickupHeaderQuery.toString();
//        Dataset<Row> queryDF = sparkSession.sql(imPickupHeaderQuery);
//        queryDF.cache();

//        imPickupHeaderQuery.cache();

        if (findPickupHeaderV2.getWarehouseId() != null && !findPickupHeaderV2.getWarehouseId().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("WH_ID").isin(findPickupHeaderV2.getWarehouseId().toArray()));
        }
        if (findPickupHeaderV2.getRefDocNumber() != null && !findPickupHeaderV2.getRefDocNumber().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("REF_DOC_NO").isin(findPickupHeaderV2.getRefDocNumber().toArray()));
        }
        if (findPickupHeaderV2.getPartnerCode() != null && !findPickupHeaderV2.getPartnerCode().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("PARTNER_CODE").isin(findPickupHeaderV2.getPartnerCode().toArray()));
        }
        if (findPickupHeaderV2.getPickupNumber() != null && !findPickupHeaderV2.getPickupNumber().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("PU_NO").isin(findPickupHeaderV2.getPickupNumber().toArray()));
        }
        if (findPickupHeaderV2.getItemCode() != null && !findPickupHeaderV2.getItemCode().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("ITM_CODE").isin(findPickupHeaderV2.getItemCode().toArray()));
        }
        if (findPickupHeaderV2.getProposedStorageBin() != null && !findPickupHeaderV2.getProposedStorageBin().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("PROP_ST_BIN").isin(findPickupHeaderV2.getProposedStorageBin().toArray()));
        }
        if (findPickupHeaderV2.getProposedPackCode() != null && !findPickupHeaderV2.getProposedPackCode().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("PROP_PACK_BARCODE").isin(findPickupHeaderV2.getProposedPackCode().toArray()));
        }
        if (findPickupHeaderV2.getOutboundOrderTypeId() != null && !findPickupHeaderV2.getOutboundOrderTypeId().isEmpty()) {
            List<String> outboundStrings = findPickupHeaderV2.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("OB_ORD_TYP_ID").isin(outboundStrings.toArray()));
        }
        if (findPickupHeaderV2.getStatusId() != null && !findPickupHeaderV2.getStatusId().isEmpty()) {
            List<String> statusString = findPickupHeaderV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findPickupHeaderV2.getSoType() != null && !findPickupHeaderV2.getSoType().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("REF_FIELD_1").isin(findPickupHeaderV2.getSoType()));
        }
        if (findPickupHeaderV2.getAssignedPickerId() != null && !findPickupHeaderV2.getAssignedPickerId().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("ASS_PICKER_ID").isin(findPickupHeaderV2.getAssignedPickerId()));
        }
        if (findPickupHeaderV2.getLevelId() != null && !findPickupHeaderV2.getLevelId().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("LEVEL_ID").isin(findPickupHeaderV2.getLevelId()));
        }

        // V2 fields
        if (findPickupHeaderV2.getLanguageId() != null && !findPickupHeaderV2.getLanguageId().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("LANG_ID").isin(findPickupHeaderV2.getLanguageId().toArray()));
        }
        if (findPickupHeaderV2.getCompanyCodeId() != null && !findPickupHeaderV2.getCompanyCodeId().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("C_ID").isin(findPickupHeaderV2.getCompanyCodeId().toArray()));
        }
        if (findPickupHeaderV2.getPlantId() != null && !findPickupHeaderV2.getPlantId().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("PLANT_ID").isin(findPickupHeaderV2.getPlantId().toArray()));
        }

        Encoder<PickupHeaderV2> pickupHeaderEncoder = Encoders.bean(PickupHeaderV2.class);
        Dataset<PickupHeaderV2> dataSetControlGroup = imPickupHeaderQuery.as(pickupHeaderEncoder);
        List<PickupHeaderV2> result = dataSetControlGroup.collectAsList();
        return result;
    }
}