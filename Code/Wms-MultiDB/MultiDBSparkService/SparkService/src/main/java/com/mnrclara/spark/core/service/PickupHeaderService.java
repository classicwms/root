package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.FindPickupHeader;
import com.mnrclara.spark.core.model.PickupHeader;
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
public class PickupHeaderService {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;

    public PickupHeaderService() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        sparkSession = SparkSession.builder().master("local[*]").appName("SparkByExample.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://35.154.84.178;databaseName=WMS", "tblpickupheader", connProp)
                 .repartition(16);
        df2.createOrReplaceTempView("tblpickupheader");
    }

    /**
     * @param findPickupHeader
     * @return
     * @throws ParseException
     */
    public List<PickupHeader> findPickupHeader(FindPickupHeader findPickupHeader) throws ParseException {

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
                + "PICK_REV_ON as pickupReversedOn "
                + "FROM tblpickupheader WHERE IS_DELETED = 0 ");

//        String pickupHeaderString = imPickupHeaderQuery.toString();
//        Dataset<Row> queryDF = sparkSession.sql(imPickupHeaderQuery);
//        queryDF.cache();

        imPickupHeaderQuery.cache();

        if (findPickupHeader.getWarehouseId() != null && !findPickupHeader.getWarehouseId().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("WH_ID").isin(findPickupHeader.getWarehouseId().toArray()));
        }
        if (findPickupHeader.getRefDocNumber() != null && !findPickupHeader.getRefDocNumber().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("REF_DOC_NO").isin(findPickupHeader.getRefDocNumber().toArray()));
        }
        if (findPickupHeader.getPartnerCode() != null && !findPickupHeader.getPartnerCode().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("PARTNER_CODE").isin(findPickupHeader.getPartnerCode().toArray()));
        }
        if (findPickupHeader.getPickupNumber() != null && !findPickupHeader.getPickupNumber().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("PU_NO").isin(findPickupHeader.getPickupNumber().toArray()));
        }
        if (findPickupHeader.getItemCode() != null && !findPickupHeader.getItemCode().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("ITM_CODE").isin(findPickupHeader.getItemCode().toArray()));
        }
        if (findPickupHeader.getProposedStorageBin() != null && !findPickupHeader.getProposedStorageBin().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("PROP_ST_BIN").isin(findPickupHeader.getProposedStorageBin().toArray()));
        }
        if (findPickupHeader.getProposedPackCode() != null && !findPickupHeader.getProposedPackCode().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("PROP_PACK_BARCODE").isin(findPickupHeader.getProposedPackCode().toArray()));
        }
        if (findPickupHeader.getOutboundOrderTypeId() != null && !findPickupHeader.getOutboundOrderTypeId().isEmpty()) {
            List<String> outboundStrings = findPickupHeader.getOutboundOrderTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("OB_ORD_TYP_ID").isin(outboundStrings.toArray()));
        }
        if (findPickupHeader.getStatusId() != null && !findPickupHeader.getStatusId().isEmpty()) {
            List<String> statusString = findPickupHeader.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (findPickupHeader.getSoType() != null && !findPickupHeader.getSoType().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("REF_FIELD_1").isin(findPickupHeader.getSoType()));
        }
        if (findPickupHeader.getAssignedPickerId() != null && !findPickupHeader.getAssignedPickerId().isEmpty()) {
            imPickupHeaderQuery = imPickupHeaderQuery.filter(col("ASS_PICKER_ID").isin(findPickupHeader.getAssignedPickerId()));
        }
        Encoder<PickupHeader> pickupHeaderEncoder = Encoders.bean(PickupHeader.class);
        Dataset<PickupHeader> dataSetControlGroup = imPickupHeaderQuery.as(pickupHeaderEncoder);
        List<PickupHeader> result = dataSetControlGroup.collectAsList();
//        imPickupHeaderQuery.unpersist();
        return result;
    }
}
