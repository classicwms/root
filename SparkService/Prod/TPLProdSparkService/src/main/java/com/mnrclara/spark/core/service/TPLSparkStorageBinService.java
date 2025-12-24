package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.model.Almailem.SearchStorageBin;
import com.mnrclara.spark.core.model.Almailem.StorageBin;
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
public class TPLSparkStorageBinService {

    Properties conProp = new Properties();

    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public TPLSparkStorageBinService() throws ParseException {
        //connection properties
        conProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conProp.put("user", "sa");
        conProp.put("password", "4V7lOXaxgAi3i6mgJL7qBUSPM");
        sparkSession = SparkSession.builder().master("local[*]").appName("StorageBin.com") .config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "10000").jdbc("jdbc:sqlserver://10.10.10.61;databaseName=WMS_3PL", "tblstoragebin", conProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblstoragebinv6");

    }



    /**
     *
     * @param searchStorageBin
     * @return
     * @throws ParseException
     */
    public List<StorageBin> findStorageBin(SearchStorageBin searchStorageBin) throws ParseException {


        Dataset<Row> storageBinQuery = sparkSession.sql("SELECT "
                + "ST_BIN as storageBin, "
                + "LANG_ID as languageId, "
                + "C_ID as companyCodeId, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "FL_ID as floorId, "
                + "LEVEL_ID as levelId, "
                + "ST_SEC_ID as storageSectionId, "
                + "ROW_ID as rowId, "
                + "AISLE_ID as aisleNumber, "
                + "SPAN_ID as spanId, "
                + "SHELF_ID as shelfId, "
                + "BIN_SECTION_ID as binSectionId, "
                + "ST_TYP_ID as storageTypeId, "
                + "BIN_CL_ID as binClassId, "
                + "OCC_VOL as occupiedVolume, "
                + "OCC_WT as occupiedWeight, "
                + "OCC_QTY as occupiedQuantity, "
                + "REMAIN_VOL as remainingVolume, "
                + "REMAIN_WT as remainingWeight, "
                + "REMAIN_QTY as remainingQuantity, "
                + "TOT_VOL as totalVolume, "
                + "TOT_QTY as totalQuantity, "
                + "TOT_WT as totalWeight, "
                + "ST_BIN_TEXT as description, "
                + "BIN_BAR as binBarcode, "
                + "PUTAWAY_BLOCK as putawayBlock, "
                + "PICK_BLOCK as pickingBlock, "
                + "BLK_REASON as blockReason, "
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
                + "CTD_BY as createdBy, "
                + "CTD_ON as createdOn, "
                + "UTD_ON as updatedOn, "
                + "UTD_BY as updatedBy, "
                // v2 fields
                + "CAP_CHECK as capacityCheck, "
                + "ALLOC_VOL as allocatedVolume, "
                + "CAP_UNIT as capacityUnit, "
                + "CAP_UOM as capacityUom, "
                + "LENGTH as length, "
                + "WIDTH as width, "
                + "HEIGHT as height, "
                + "CAP_UOM as quantity, "
                + "WEIGHT as weight "
                + "FROM tblstoragebinv6 WHERE IS_DELETED = 0");

//        storageBinQuery.cache();

        if (searchStorageBin.getAisleNumber() != null && !searchStorageBin.getAisleNumber().isEmpty()) {
            storageBinQuery = storageBinQuery.filter(col("AISLE_ID").isin(searchStorageBin.getAisleNumber().toArray()));
        }
        if (searchStorageBin.getStorageBin() != null && !searchStorageBin.getStorageBin().isEmpty()) {
            storageBinQuery = storageBinQuery.filter(col("ST_BIN").isin(searchStorageBin.getStorageBin().toArray()));
        }
        if (searchStorageBin.getWarehouseId() != null && !searchStorageBin.getWarehouseId().isEmpty()) {
            storageBinQuery = storageBinQuery.filter(col("WH_ID").isin(searchStorageBin.getWarehouseId().toArray()));
        }
        if (searchStorageBin.getFloorId() != null && !searchStorageBin.getFloorId().isEmpty()) {
            storageBinQuery = storageBinQuery.filter(col("FL_ID").isin(searchStorageBin.getFloorId().toArray()));
        }
        if (searchStorageBin.getStorageSectionId() != null && !searchStorageBin.getStorageSectionId().isEmpty()) {
            storageBinQuery = storageBinQuery.filter(col("ST_SEC_ID").isin(searchStorageBin.getStorageSectionId().toArray()));
        }
        if (searchStorageBin.getRowId() != null && !searchStorageBin.getRowId().isEmpty()) {
            storageBinQuery = storageBinQuery.filter(col("ROW_ID").isin(searchStorageBin.getRowId().toArray()));
        }
        if (searchStorageBin.getLanguageId() != null && !searchStorageBin.getLanguageId().isEmpty()) {
            storageBinQuery = storageBinQuery.filter(col("LANG_ID").isin(searchStorageBin.getLanguageId().toArray()));
        }
        if(searchStorageBin.getCompanyCodeId() != null && !searchStorageBin.getCompanyCodeId().isEmpty()){
            storageBinQuery = storageBinQuery.filter(col("C_ID").isin(searchStorageBin.getCompanyCodeId().toArray()));
        }
        if(searchStorageBin.getPlantId() != null && !searchStorageBin.getPlantId().isEmpty()){
            storageBinQuery = storageBinQuery.filter(col("PLANT_ID").isin(searchStorageBin.getPlantId().toArray()));
        }
        if(searchStorageBin.getSpanId() != null && !searchStorageBin.getSpanId().isEmpty()){
            storageBinQuery = storageBinQuery.filter(col("SPAN_ID").isin(searchStorageBin.getSpanId().toArray()));
        }
        if(searchStorageBin.getShelfId() != null && !searchStorageBin.getShelfId().isEmpty()){
            storageBinQuery = storageBinQuery.filter(col("SHELF_ID").isin(searchStorageBin.getShelfId().toArray()));
        }

        if (searchStorageBin.getStatusId() != null && !searchStorageBin.getStatusId().isEmpty()) {
            List<String> stringList = searchStorageBin.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            storageBinQuery = storageBinQuery.filter(col("STATUS_ID").isin(stringList.toArray()));
        }

        if (searchStorageBin.getCreatedBy() != null && !searchStorageBin.getCreatedBy().isEmpty()) {
            storageBinQuery = storageBinQuery.filter(col("CTD_BY").isin(searchStorageBin.getCreatedBy().toArray()));
        }

        if (searchStorageBin.getStartCreatedOn() != null) {
            Date startDate = searchStorageBin.getStartCreatedOn();
            startDate = org.apache.commons.lang3.time.DateUtils.ceiling(startDate, Calendar.DAY_OF_MONTH);
            storageBinQuery = storageBinQuery.filter(col("CTD_ON").$greater$eq(dateFormat.format(startDate)));
        }
        if (searchStorageBin.getEndCreatedOn() != null) {
            Date endDate = searchStorageBin.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            storageBinQuery = storageBinQuery.filter(col("CTD_ON").$less$eq(dateFormat.format(endDate)));
        }
        if (searchStorageBin.getStartUpdatedOn() != null) {
            Date endDate = searchStorageBin.getStartUpdatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH);
            storageBinQuery = storageBinQuery.filter(col("UTD_ON").$less$eq(dateFormat.format(endDate)));
        }
        if (searchStorageBin.getEndCreatedOn() != null) {
            Date endDate = searchStorageBin.getEndCreatedOn();
            endDate = org.apache.commons.lang3.time.DateUtils.ceiling(endDate, Calendar.DAY_OF_MONTH);
            storageBinQuery = storageBinQuery.filter(col("UTD_ON").$less$eq(dateFormat.format(endDate)));
        }

        Encoder<StorageBin> stagingHeaderEncoder = Encoders.bean(StorageBin.class);
        Dataset<StorageBin> dataSetControlGroup = storageBinQuery.as(stagingHeaderEncoder);
        List<StorageBin> result = dataSetControlGroup.collectAsList();

        return result;
    }

}
