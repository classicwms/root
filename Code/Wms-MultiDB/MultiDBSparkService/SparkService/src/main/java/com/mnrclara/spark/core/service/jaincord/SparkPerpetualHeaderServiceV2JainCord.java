package com.mnrclara.spark.core.service.jaincord;


import com.mnrclara.spark.core.model.wmscorev2.PerpetualHeader;
import com.mnrclara.spark.core.model.wmscorev2.SearchPerpetualHeaderV2;
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
public class SparkPerpetualHeaderServiceV2JainCord {


    Properties connProp = new Properties();
    SparkSession sparkSession = null;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SparkPerpetualHeaderServiceV2JainCord() {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
        sparkSession = SparkSession.builder().master("local[*]").appName("PerpetualHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblperpetualheader", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblperpetualheader5jain");
    }

    //FindPerpetualHeader
    public List<PerpetualHeader> findPerpetualHeader(SearchPerpetualHeaderV2 searchPerpetualHeaderV2) throws ParseException {

        Dataset<Row> perpetualheaderQuery =sparkSession.sql( "SELECT " +
                "LANG_ID as languageId," +
                "C_ID as companyCodeId," +
                "PLANT_ID as plantId," +
                "WH_ID as warehouseId," +
                "CC_TYP_ID as cycleCountTypeId," +
                "CC_NO as cycleCountNo," +
                "MVT_TYP_ID as movementTypeId," +
                "SUB_MVT_TYP_ID as subMovementTypeId," +
                "STATUS_ID as statusId," +
                "REF_FIELD_1 as referenceField1," +
                "REF_FIELD_2 as referenceField2," +
                "REF_FIELD_3 as referenceField3," +
                "REF_FIELD_4 as referenceField4," +
                "REF_FIELD_5 as referenceField5," +
                "REF_FIELD_6 as referenceField6," +
                "REF_FIELD_7 as referenceField7," +
                "REF_FIELD_8 as referenceField8," +
                "REF_FIELD_9 as referenceField9," +
                "REF_FIELD_10 as referenceField10," +
                "IS_DELETED as deletionIndicator," +
                "CC_CTD_BY as createdBy," +
                "CC_CTD_ON as createdOn," +
                "CC_CNT_BY as countedBy," +
                "CC_CNT_ON as countedOn," +
                "CC_CNF_BY as confirmedBy," +
                "CC_CNF_ON as confirmedOn," +
                "C_TEXT as companyDescription," +
                "PLANT_TEXT as plantDescription," +
                "WH_TEXT as warehouseDescription," +
                "STATUS_TEXT as statusDescription," +
                "MIDDLEWARE_ID as middlewareId," +
                "MIDDLEWARE_TABLE as middlewareTable," +
                "REF_DOC_TYPE as referenceDocumentType," +
                "REF_CC_NO as referenceCycleCountNo " +
                "FROM tblperpetualheader5jain WHERE IS_DELETED = 0 ");

//        perpetualheaderQuery.cache();

        if (searchPerpetualHeaderV2.getLanguageId() != null && !searchPerpetualHeaderV2.getLanguageId().isEmpty()) {
            perpetualheaderQuery = perpetualheaderQuery.filter(col("LANG_ID").isin(searchPerpetualHeaderV2.getLanguageId().toArray()));
        }
        if (searchPerpetualHeaderV2.getCompanyCodeId() != null && !searchPerpetualHeaderV2.getCompanyCodeId().isEmpty()) {
            perpetualheaderQuery = perpetualheaderQuery.filter(col("C_ID").isin(searchPerpetualHeaderV2.getCompanyCodeId().toArray()));
        }
        if (searchPerpetualHeaderV2.getPlantId() != null && !searchPerpetualHeaderV2.getPlantId().isEmpty()) {
            perpetualheaderQuery = perpetualheaderQuery.filter(col("PLANT_ID").isin(searchPerpetualHeaderV2.getPlantId().toArray()));
        }
        if (searchPerpetualHeaderV2.getWarehouseId() != null && !searchPerpetualHeaderV2.getWarehouseId().isEmpty()) {
            perpetualheaderQuery = perpetualheaderQuery.filter(col("WH_ID").isin(searchPerpetualHeaderV2.getWarehouseId().toArray()));
        }
        if (searchPerpetualHeaderV2.getCycleCountTypeId() != null && !searchPerpetualHeaderV2.getCycleCountTypeId().isEmpty()) {
            List<String> cycleCountString = searchPerpetualHeaderV2.getCycleCountTypeId().stream().map(String::valueOf).collect(Collectors.toList());
            perpetualheaderQuery = perpetualheaderQuery.filter(col("CC_TYP_ID").isin(cycleCountString.toArray()));
        }
        if (searchPerpetualHeaderV2.getCycleCountNo() != null && !searchPerpetualHeaderV2.getCycleCountNo().isEmpty()) {
            perpetualheaderQuery = perpetualheaderQuery.filter(col("CC_NO").isin(searchPerpetualHeaderV2.getCycleCountNo().toArray()));
        }
        if (searchPerpetualHeaderV2.getMovementTypeId() != null && !searchPerpetualHeaderV2.getMovementTypeId().isEmpty()) {
            perpetualheaderQuery = perpetualheaderQuery.filter(col("PARTNER_CODE").isin(searchPerpetualHeaderV2.getMovementTypeId().toArray()));
        }
        if (searchPerpetualHeaderV2.getSubMovementTypeId() != null && !searchPerpetualHeaderV2.getSubMovementTypeId().isEmpty()) {
            perpetualheaderQuery = perpetualheaderQuery.filter(col("SUB_MVT_TYP_ID").isin(searchPerpetualHeaderV2.getSubMovementTypeId().toArray()));
        }
        if (searchPerpetualHeaderV2.getStatusId() != null && !searchPerpetualHeaderV2.getStatusId().isEmpty()) {
            List<String> statusString = searchPerpetualHeaderV2.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
            perpetualheaderQuery = perpetualheaderQuery.filter(col("STATUS_ID").isin(statusString.toArray()));
        }
        if (searchPerpetualHeaderV2.getCreatedBy() != null && !searchPerpetualHeaderV2.getCreatedBy().isEmpty()) {
            perpetualheaderQuery = perpetualheaderQuery.filter(col("CC_CTD_BY").isin(searchPerpetualHeaderV2.getCreatedBy().toArray()));
        }
        if(searchPerpetualHeaderV2.getStartCreatedOn() != null){
            Date fromCreatedOn = searchPerpetualHeaderV2.getStartCreatedOn();
            fromCreatedOn = org.apache.commons.lang3.time.DateUtils.truncate(fromCreatedOn, Calendar.DAY_OF_MONTH);
            perpetualheaderQuery = perpetualheaderQuery.filter(col("CC_CTD_ON").$greater$eq(dateFormat.format(fromCreatedOn)));
        }
        if(searchPerpetualHeaderV2.getEndCreatedOn() != null){
            Date toCreatedOn = searchPerpetualHeaderV2.getEndCreatedOn();
            toCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(toCreatedOn, Calendar.DAY_OF_MONTH);
            perpetualheaderQuery = perpetualheaderQuery.filter(col("CC_CTD_ON").$less$eq(dateFormat.format(toCreatedOn)));
        }
        Encoder<PerpetualHeader> perpetualHeaderEncoder = Encoders.bean(PerpetualHeader.class);
        Dataset<PerpetualHeader> perpetualHeaderDataset = perpetualheaderQuery.as(perpetualHeaderEncoder);
        List<PerpetualHeader> result = perpetualHeaderDataset.collectAsList();

        return result;
    }


}