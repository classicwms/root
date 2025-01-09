package com.mnrclara.spark.core.service.jaincord;

import com.mnrclara.spark.core.model.Almailem.ImBasicData1V3;
import com.mnrclara.spark.core.model.Almailem.SearchImBasicData1;
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
public class SparkImBasicData1V5JainCordService {


    Properties connProp = new Properties();
    SparkSession sparkSession = null;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SparkImBasicData1V5JainCordService() throws ParseException {

        int SPARK_CORES = 4;
        String SPARK_MASTER = "yarn";

        // connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
//        sparkSession = SparkSession.builder().master("local[*]").appName("ImBasicData1.com").config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();

        sparkSession = SparkSession.builder()
                .master(SPARK_MASTER)
                .appName("ImBasicData1.com")
                .config("spark.executor.memory", "8g")
                .config("spark.executor.cores", SPARK_CORES)
                .config("spark.sql.shuffle.partitions", 400) // Increase partitions
                .getOrCreate();

        // Read from Sql Table
        val df2 = sparkSession.read().option("fetchSize", "50000").jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblimbasicdata1", connProp)
                .repartition(400);
        df2.createOrReplaceTempView("tblimbasicdata1v8jain");
    }

    /**
     *
     * @param searchImBasicData1
     * @return
     * @throws ParseException
     */
    public List<ImBasicData1V3> searchImBasicData1(SearchImBasicData1 searchImBasicData1) throws ParseException {

        Dataset<Row> imBasicData1Query = sparkSession.sql("SELECT "
                + "UOM_ID as uomId, "
//                + "LANG_ID as languageId, "
                + "C_ID as companyCodeId, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "ITM_CODE as itemCode, "
                + "MFR_PART as manufacturerPartNo, "
                + "TEXT as description, "
                + "ITM_TYP_ID as itemType, "
                + "C_TEXT as companyDescription, "
                + "PLANT_TEXT as plantDescription, "
                + "WH_TEXT as warehouseDescription, "
//                + "IS_DELETED as deletionIndicator, "
                + "CTD_BY as createdBy, "
                + "CTD_ON as createdOn "
//                + "UTD_BY as updatedBy, "
//                + "UTD_ON as updatedOn "
                + "FROM tblimbasicdata1v8jain WHERE IS_DELETED = 0 ");

        if (searchImBasicData1.getWarehouseId() != null && !searchImBasicData1.getWarehouseId().isEmpty()) {
            imBasicData1Query = imBasicData1Query.filter(col("WH_ID").isin(searchImBasicData1.getWarehouseId().toArray()));
        }
        if (searchImBasicData1.getItemCode() != null && !searchImBasicData1.getItemCode().isEmpty()) {
            imBasicData1Query = imBasicData1Query.filter(col("ITM_CODE").isin(searchImBasicData1.getItemCode().toArray()));
        }
        if (searchImBasicData1.getDescription() != null && !searchImBasicData1.getDescription().isEmpty()) {
            imBasicData1Query = imBasicData1Query.filter(col("TEXT").isin(searchImBasicData1.getDescription().toArray()));
        }
        if (searchImBasicData1.getItemType() != null && !searchImBasicData1.getItemType().isEmpty()) {
            List<String> itemTypeStrings = searchImBasicData1.getItemType().stream().map(String::valueOf).collect(Collectors.toList());
            imBasicData1Query = imBasicData1Query.filter(col("ITM_TYP_ID").isin(itemTypeStrings.toArray()));
        }
        if (searchImBasicData1.getItemGroup() != null && !searchImBasicData1.getItemGroup().isEmpty()) {
            List<String> itemGroups = searchImBasicData1.getItemGroup().stream().map(String::valueOf).collect(Collectors.toList());
            imBasicData1Query = imBasicData1Query.filter(col("ITM_GRP_ID").isin(itemGroups.toArray()));
        }
        if (searchImBasicData1.getSubItemGroup() != null && !searchImBasicData1.getSubItemGroup().isEmpty()) {
            List<String> subItemGroups = searchImBasicData1.getSubItemGroup().stream().map(String::valueOf).collect(Collectors.toList());
            imBasicData1Query = imBasicData1Query.filter(col("SUB_ITM_GRP_ID").isin(subItemGroups.toArray()));
        }
        if (searchImBasicData1.getCompanyCodeId() != null && !searchImBasicData1.getCompanyCodeId().isEmpty()) {
            imBasicData1Query = imBasicData1Query.filter(col("C_ID").isin(searchImBasicData1.getCompanyCodeId().toArray()));
        }
        if (searchImBasicData1.getManufacturerPartNo() != null && !searchImBasicData1.getManufacturerPartNo().isEmpty()) {
            imBasicData1Query = imBasicData1Query.filter(col("MFR_PART").isin(searchImBasicData1.getManufacturerPartNo().toArray()));
        }
        if (searchImBasicData1.getPlantId() != null && !searchImBasicData1.getPlantId().isEmpty()) {
            imBasicData1Query = imBasicData1Query.filter(col("PLANT_ID").isin(searchImBasicData1.getPlantId().toArray()));
        }
        if (searchImBasicData1.getLanguageId() != null && !searchImBasicData1.getLanguageId().isEmpty()) {
            imBasicData1Query = imBasicData1Query.filter(col("LANG_ID").isin(searchImBasicData1.getLanguageId().toArray()));
        }
        if (searchImBasicData1.getStartCreatedOn() != null) {
            Date startCreatedOn = searchImBasicData1.getStartCreatedOn();
            startCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(startCreatedOn, Calendar.DAY_OF_MONTH);
            imBasicData1Query = imBasicData1Query.filter(col("CTD_ON").$greater$eq(dateFormat.format(startCreatedOn)));
        }
        if (searchImBasicData1.getEndCreatedOn() != null) {
            Date endCreatedOn = searchImBasicData1.getEndCreatedOn();
            endCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(endCreatedOn, Calendar.DAY_OF_MONTH);
            imBasicData1Query = imBasicData1Query.filter(col("CTD_ON").$less$eq(dateFormat.format(endCreatedOn)));
        }
        if (searchImBasicData1.getStartUpdatedOn() != null) {
            Date startUpdatedOn = searchImBasicData1.getStartUpdatedOn();
            startUpdatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(startUpdatedOn, Calendar.DAY_OF_MONTH);
            imBasicData1Query = imBasicData1Query.filter(col("UTD_ON").$greater$eq(dateFormat.format(startUpdatedOn)));
        }
        if (searchImBasicData1.getEndUpdatedOn() != null) {
            Date endUpdatedOn = searchImBasicData1.getEndUpdatedOn();
            endUpdatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(endUpdatedOn, Calendar.DAY_OF_MONTH);
            imBasicData1Query = imBasicData1Query.filter(col("UTD_ON").$greater$eq(dateFormat.format(endUpdatedOn)));
        }

        Encoder<ImBasicData1V3> imBasicData1Encoder = Encoders.bean(ImBasicData1V3.class);
        Dataset<ImBasicData1V3> dataSetControlGroup = imBasicData1Query.as(imBasicData1Encoder);
        List<ImBasicData1V3> result = dataSetControlGroup.collectAsList();

        return result;
    }


}