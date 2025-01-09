package com.mnrclara.spark.core.service.test;


import com.mnrclara.spark.core.config.dynamicConfig.DataBaseContextHolder;
import com.mnrclara.spark.core.model.wmscorev2.FindPreInboundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.PreInboundHeaderV2;
import com.mnrclara.spark.core.repository.DbConfigRepository;
import com.mnrclara.spark.core.util.ConditionUtils;
import com.mnrclara.spark.core.util.DatabaseConnectionUtil;
import com.mnrclara.spark.core.util.SparkSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class TestService {

    SparkSession spark = SparkSessionUtil.createSparkSession();

    Properties connProp = DatabaseConnectionUtil.getImpexDevDatabaseConnectionProperties();
    String jdbcUrl = DatabaseConnectionUtil.getImpexDevJdbcUrl();

    String jdbcUrl1 = DatabaseConnectionUtil.getWalkarooCoreJdbcUrl();

    String jdbcUrl2 = DatabaseConnectionUtil.getIMFCoreJdbcUrl();


    @Autowired
    private DbConfigRepository dbConfigRepository;



    public List<PreInboundHeaderV2> findPreInboundHeaderv2(FindPreInboundHeaderV2 findPreInboundHeader) {
        try {
            DataBaseContextHolder.setCurrentDb("db");
            String routingDb = dbConfigRepository.getDbName(findPreInboundHeader.getCompanyCodeId(),findPreInboundHeader.getPlantId(),findPreInboundHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}",routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
//
//            String sqlQuery1 = "SELECT db_name as dbName FROM db_config";
//            List<String> conditions1 = new ArrayList<>();
//            ConditionUtils.addCondition(conditions1, "C_ID", findPreInboundHeader.getCompanyCodeId());
//            ConditionUtils.addCondition(conditions1, "PLANT_ID", findPreInboundHeader.getPlantId());
//            ConditionUtils.addCondition(conditions1, "WAREHOUSE_ID", findPreInboundHeader.getWarehouseId());
//
//            if (!conditions1.isEmpty()) {
//                sqlQuery1 += " WHERE " + String.join(" AND ", conditions1);
//            }
//
//            log.info("sql query --> {}",sqlQuery1);
//
//
//            Dataset<Row> data1 = spark.read()
//                    .option("fetchSize", "10000")
//                    .option("pushDownloadPredicate", true)
//                    .jdbc(jdbcUrl2, "(" + sqlQuery1 + ") as tmp", connProp);


//            data1.show();

//            List<Row> rows = data1.collectAsList();
//
//
//            // Iterate through each row and extract values
//            String column1Value = null;
//            for (Row row : rows) {
//                // Assuming columns are of type String (adjust the type as needed)
//                column1Value = row.getString(0);
////                String column2Value = row.getString(1);  // Access by index (1 for the second column)
//
//                // You can also access columns by name
//                // String column1Value = row.getString("columnName");
//
//                System.out.println("Column 1: " + column1Value);
//            }
//
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb(column1Value);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCode, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "PRE_IB_NO as preInboundNo, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                    + "REF_DOC_TYP as referenceDocumentType, "
                    + "STATUS_ID as statusId, "
                    + "CONT_NO as containerNo, "
                    + "NO_CONTAINERS as noOfContainers, "
                    + "CONT_TYP as containerType, "
                    + "REF_DOC_DATE as refDocDate, "
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
                    + "UTD_BY as updatedBy, "
                    + "UTD_ON as updatedOn, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName "
                    + "FROM tblpreinboundheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findPreInboundHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findPreInboundHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findPreInboundHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", findPreInboundHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "PRE_IB_NO", findPreInboundHeader.getPreInboundNo());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findPreInboundHeader.getRefDocNumber());

            ConditionUtils.numericConditions(conditions, "IB_ORD_TYP_ID", findPreInboundHeader.getInboundOrderTypeId());
            ConditionUtils.numericConditions(conditions, "STATUS_ID", findPreInboundHeader.getStatusId());

            ConditionUtils.addDateCondition(conditions, "CTD_ON", findPreInboundHeader.getStartCreatedOn(), findPreInboundHeader.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }
            Dataset<Row> data=null;
            if(routingDb.equals("db1")){
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}",jdbcUrl);
            }
            else if(routingDb.equals("db2")){
                 data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}",jdbcUrl1);
            }
            else if(routingDb.equals("db")){
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}",jdbcUrl2);
            }

//            Dataset<Row> data = spark.read()
//                    .option("fetchSize", "10000")
//                    .option("pushDownloadPredicate", true)
//                    .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

//            data.show();
            Encoder<PreInboundHeaderV2> preInboundHeaderEncoder = Encoders.bean(PreInboundHeaderV2.class);
            Dataset<PreInboundHeaderV2> dataset = data.as(preInboundHeaderEncoder);
            return dataset.collectAsList();
        } catch (Exception e) {
            log.error("Find preInboundHeader Spark Exception : " + e.toString());
            throw e;
        }
    }
}


