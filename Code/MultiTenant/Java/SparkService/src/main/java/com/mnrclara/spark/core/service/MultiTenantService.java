package com.mnrclara.spark.core.service;

import com.mnrclara.spark.core.config.dynamicConfig.DataBaseContextHolder;
import com.mnrclara.spark.core.model.MultiTenant.*;
import com.mnrclara.spark.core.repository.DbConfigRepository;
import com.mnrclara.spark.core.util.ConditionUtils;
import com.mnrclara.spark.core.util.DatabaseConnectionUtil;
import com.mnrclara.spark.core.util.SparkSessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

@Service
@Slf4j
public class MultiTenantService {

    // SparkSession
    SparkSession spark = SparkSessionUtil.createSparkSession();

    Properties connProp = DatabaseConnectionUtil.getImpexDevDatabaseConnectionProperties();

    Properties connProp1 = DatabaseConnectionUtil.getNamrathaDatabaseConnectionProperties();

    Properties connRef = DatabaseConnectionUtil.getReeferonDatabaseConnectionProperties();

    Properties connKnow = DatabaseConnectionUtil.getKnowellDatabaseConnectionProperties();

    Properties connFahae = DatabaseConnectionUtil.getFahaeelDatabaseConnectionProperties();

    Properties connAutoLap = DatabaseConnectionUtil.getAutoLapDatabaseConnectionProperties();

    Properties connBF = DatabaseConnectionUtil.getBFDatabaseConnectionProperties();
    Properties connKKF = DatabaseConnectionUtil.getKKFDatabaseConnectionProperties();

    String jdbcUrl = DatabaseConnectionUtil.getImpexDevJdbcUrl();

    String jdbcUrl1 = DatabaseConnectionUtil.getWalkarooCoreJdbcUrl();

    String jdbcUrl2 = DatabaseConnectionUtil.getIMFCoreJdbcUrl();

    String jdbcUrl3 = DatabaseConnectionUtil.getJdbcUrl();

    String jdbcUrl4 = DatabaseConnectionUtil.getNamrathaJdbcUrl();

    String jdbcUrl5 = DatabaseConnectionUtil.getReeferonJdbcUrl();

    String jdbcUrl6 = DatabaseConnectionUtil.getKnowellJdbcUrl();

    String jdbcUrl7 = DatabaseConnectionUtil.getFahaeelJdbcUrl();

    String jdbcUrl8 = DatabaseConnectionUtil.getAutoLapJdbcUrl();

    String jdbcUrl9 = DatabaseConnectionUtil.getBFJdbcUrl();
    String jdbcUrl10 = DatabaseConnectionUtil.getKKFJdbcUrl();

    @Autowired
    private DbConfigRepository dbConfigRepository;

    /**
     * @param findBusinessPartner
     * @return
     * @throws Exception
     */
    public List<BusinessPartnerV2> findBusinessPartner(FindBusinessPartner findBusinessPartner) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findBusinessPartner.getCompanyCodeId(), findBusinessPartner.getPlantId(), findBusinessPartner.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT " +
                    " LANG_ID as languageId, " +
                    " C_ID as companyCodeId, " +
                    " PLANT_ID as plantId, " +
                    " WH_ID as warehouseId, " +
                    " PARTNER_TYP as businessPartnerType, " +
                    " PARTNER_CODE as partnerCode, " +
                    " PARTNER_NM as partnerName, " +
                    " STATUS_ID as statusId, " +
                    " REF_FIELD_5 as referenceField5, " +
                    " CTD_BY as createdBy, " +
                    " CTD_ON as createdOn " +
                    " FROM tblbusinesspartner";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findBusinessPartner.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findBusinessPartner.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findBusinessPartner.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", findBusinessPartner.getWarehouseId());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findBusinessPartner.getPartnerCode());
            ConditionUtils.addCondition(conditions, "PARTNER_NM", findBusinessPartner.getPartnerName());
            ConditionUtils.addCondition(conditions, "CTD_BY", findBusinessPartner.getCreatedBy());

            ConditionUtils.addCondition(conditions, "REF_FIELD_5", findBusinessPartner.getReferenceField5());

            ConditionUtils.numericConditions(conditions, "PARTNER_TYP", findBusinessPartner.getBusinessPartnerType());
            ConditionUtils.numericConditions(conditions, "STATUS_ID", findBusinessPartner.getStatusId());

            ConditionUtils.addDateCondition(conditions, "CTD_ON", findBusinessPartner.getStartCreatedOn(), findBusinessPartner.getEndCreatedOn());
            ConditionUtils.addDateCondition(conditions, "UTD_ON", findBusinessPartner.getStartUpdatedOn(), findBusinessPartner.getEndUpdatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

//            data.show();
            Encoder<BusinessPartnerV2> businessPartnerEncoder = Encoders.bean(BusinessPartnerV2.class);
            Dataset<BusinessPartnerV2> dataset = data.as(businessPartnerEncoder);

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<BusinessPartnerV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }

            //  return dataset.collectAsList();
        } catch (Exception e) {
            log.error("Find Business Partner Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findContainerReceipt
     * @return
     */
    public List<ContainerReceiptV2> findContainerReceipt(FindContainerReceiptV2 findContainerReceipt) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findContainerReceipt.getCompanyCodeId(), findContainerReceipt.getPlantId(), findContainerReceipt.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT " +
                    " LANG_ID as languageId, " +
                    " C_ID as companyCodeId, " +
                    " PLANT_ID as plantId, " +
                    " WH_ID as warehouseId, " +
                    " PRE_IB_NO as preInboundNo, " +
                    " REF_DOC_NO as refDocNumber, " +
                    " CONT_REC_NO as containerReceiptNo, " +
                    " CONT_REC_DATE as containerReceivedDate, " +
                    " CONT_NO as containerNo, " +
                    " STATUS_ID as statusId, " +
                    " CONT_TYP as containerType, " +
                    " PARTNER_CODE as partnerCode, " +
                    " INV_NO as invoiceNo, " +
                    " CONS_TYPE as consignmentType, " +
                    " ORIGIN as origin, " +
                    " PAL_QTY as numberOfPallets, " +
                    " CASE_NO as numberOfCases, " +
                    " DOCK_ALL_NO as dockAllocationNo, " +
                    " REMARK as remarks, " +
                    " IS_DELETED as deletionIndicator, " +
                    " REF_FIELD_1 as referenceField1, " +
                    " REF_FIELD_2 as referenceField2, " +
                    " REF_FIELD_3 as referenceField3, " +
                    " REF_FIELD_4 as referenceField4, " +
                    " REF_FIELD_5 as referenceField5, " +
                    " REF_FIELD_6 as referenceField6, " +
                    " REF_FIELD_7 as referenceField7, " +
                    " REF_FIELD_8 as referenceField8, " +
                    " REF_FIELD_9 as referenceField9, " +
                    " REF_FIELD_10 as referenceField10, " +
                    " REF_FIELD_11 as referenceField11, " +
                    " REF_FIELD_12 as referenceField12, " +
                    " REF_FIELD_13 as referenceField13, " +
                    " REF_FIELD_14 as referenceField14, " +
                    " REF_FIELD_15 as referenceField15, " +
                    " REF_FIELD_16 as referenceField16, " +
                    " REF_FIELD_17 as referenceField17, " +
                    " REF_FIELD_18 as referenceField18, " +
                    " REF_FIELD_19 as referenceField19, " +
                    " REF_FIELD_20 as referenceField20, " +
                    " REF_FIELD_21 as referenceField21, " +
                    " REF_FIELD_22 as referenceField22, " +
                    " REF_FIELD_23 as referenceField23, " +
                    " REF_FIELD_24 as referenceField24, " +
                    " REF_FIELD_25 as referenceField25, " +
                    " REF_FIELD_26 as referenceField26, " +
                    " REF_FIELD_27 as referenceField27, " +
                    " REF_FIELD_28 as referenceField28, " +
                    " REF_FIELD_29 as referenceField29, " +
                    " REF_FIELD_30 as referenceField30, " +
                    " CTD_BY as createdBy, " +
                    " CTD_ON as createdOn, " +
                    " UTD_BY as updatedBy, " +
                    " UTD_ON as updatedOn, " +
                    "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_TABLE as middlewareTable "
                    + "FROM tblcontainerreceipt";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findContainerReceipt.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findContainerReceipt.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findContainerReceipt.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", findContainerReceipt.getWarehouseId());
            ConditionUtils.addCondition(conditions, "CONT_REC_NO", findContainerReceipt.getContainerReceiptNo());
            ConditionUtils.addCondition(conditions, "CONT_NO", findContainerReceipt.getContainerNo());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findContainerReceipt.getPartnerCode());
            ConditionUtils.addCondition(conditions, "REF_FIELD_1", findContainerReceipt.getUnloadedBy());
            ConditionUtils.addCondition(conditions, "ORIGIN", findContainerReceipt.getOrigin());
            ConditionUtils.addCondition(conditions, "INV_NO", findContainerReceipt.getInvoiceNo());
            ConditionUtils.numericConditions(conditions, "STATUS_ID", findContainerReceipt.getStatusId());

            ConditionUtils.addDateCondition(conditions, "CONT_REC_DATE", findContainerReceipt.getStartContainerReceivedDate(), findContainerReceipt.getEndContainerReceivedDate());
            ConditionUtils.addDateCondition(conditions, "CTD_ON", findContainerReceipt.getFromCreatedOn(), findContainerReceipt.getToCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }
//            data.show();
            Encoder<ContainerReceiptV2> containerReceiptEncoder = Encoders.bean(ContainerReceiptV2.class);
            Dataset<ContainerReceiptV2> dataset = data.as(containerReceiptEncoder);

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<ContainerReceiptV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
            //      return dataset.collectAsList();
        } catch (Exception e) {
            log.error("Find ContainerReceipt Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchStorageBin
     * @return
     */
    public List<StorageBin> findStorageBin(SearchStorageBin searchStorageBin) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchStorageBin.getCompanyCodeId(), searchStorageBin.getPlantId(), searchStorageBin.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
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

                    + "CAP_CHECK as capacityCheck, "
                    + "ALLOC_VOL as allocatedVolume, "
                    + "CAP_UNIT as capacityUnit, "
                    + "CAP_UOM as capacityUom, "
                    + "LENGTH as length, "
                    + "WIDTH as width, "
                    + "HEIGHT as height, "
                    + "CAP_UOM as quantity, "
                    + "WEIGHT as weight "
                    + "FROM tblstoragebin";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchStorageBin.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchStorageBin.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchStorageBin.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchStorageBin.getWarehouseId());
            ConditionUtils.addCondition(conditions, "AISLE_ID", searchStorageBin.getAisleNumber());
            ConditionUtils.addCondition(conditions, "ST_BIN", searchStorageBin.getStorageBin());
            ConditionUtils.addCondition(conditions, "ST_SEC_ID", searchStorageBin.getStorageSectionId());
            ConditionUtils.addCondition(conditions, "ROW_ID", searchStorageBin.getRowId());
            ConditionUtils.addCondition(conditions, "SPAN_ID", searchStorageBin.getSpanId());
            ConditionUtils.addCondition(conditions, "SHELF_ID", searchStorageBin.getShelfId());
            ConditionUtils.addCondition(conditions, "CTD_BY", searchStorageBin.getCreatedBy());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", searchStorageBin.getStatusId());
            ConditionUtils.numericConditions(conditions, "FL_ID", searchStorageBin.getFloorId());

            ConditionUtils.addDateCondition(conditions, "UTD_ON", searchStorageBin.getStartUpdatedOn(), searchStorageBin.getEndUpdatedOn());
            ConditionUtils.addDateCondition(conditions, "CTD_ON", searchStorageBin.getStartCreatedOn(), searchStorageBin.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

//            data.show();
            Encoder<StorageBin> storageBinEncoder = Encoders.bean(StorageBin.class);
            Dataset<StorageBin> dataset = data.as(storageBinEncoder);

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<StorageBin>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
//            return dataset.collectAsList();
        } catch (Exception e) {
            log.error("Find StorageBin Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchImBasicData1
     * @return
     */
    public List<ImBasicData1NewV4> searchImBasicData1(SearchImBasicData1 searchImBasicData1) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "UOM_ID as uomId, "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "ITM_CODE as itemCode, "
                    + "MFR_PART as manufacturerPartNo, "
                    + "TEXT as description, "
                    + "MODEL as model, "
                    + "SPEC_01 as specifications1, "
                    + "SPEC_02 as specifications2, "
                    + "EAN_UPC_NO as eanUpcNo, "
                    + "HSN_CODE as hsnCode, "
                    + "ITM_TYP_ID as itemType, "
                    + "ITM_GRP_ID as itemGroup, "
                    + "SUB_ITM_GRP_ID as subItemGroup, "
                    + "ST_SEC_ID as storageSectionId, "
                    + "TOT_STK as totalStock, "
                    + "MIN_STK as minimumStock, "
                    + "MAX_STK as maximumStock, "
                    + "RE_ORD_LVL as reorderLevel, "
                    + "CAP_CHK as capacityCheck, "
                    + "REP_QTY as replenishmentQty, "
                    + "SAFTY_STCK as safetyStock, "
                    + "CAP_UNIT as capacityUnit, "
                    + "CAP_UOM as capacityUom, "
                    + "QUANTITY as quantity, "
                    + "WEIGHT as weight, "
                    + "STATUS_ID as statusId, "
                    + "SHELF_LIFE_IND as shelfLifeIndicator, "
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
                    + "BRAND as brand, "
                    + "SIZE as size, "
                    + "CTD_BY as createdBy, "
                    + "CTD_ON as createdOn, "
                    + "UTD_BY as updatedBy, "
                    + "UTD_ON as updatedOn "
                    + "FROM tblimbasicdata1";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchImBasicData1.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchImBasicData1.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchImBasicData1.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchImBasicData1.getWarehouseId());
            ConditionUtils.addCondition(conditions, "ITM_CODE", searchImBasicData1.getItemCode());
            ConditionUtils.addCondition(conditions, "TEXT", searchImBasicData1.getDescription());
            ConditionUtils.addCondition(conditions, "MFR_PART", searchImBasicData1.getManufacturerPartNo());
            ConditionUtils.addCondition(conditions, "CTD_BY", searchImBasicData1.getCreatedBy());

            ConditionUtils.numericConditions(conditions, "ITM_TYP_ID", searchImBasicData1.getItemType());
            ConditionUtils.numericConditions(conditions, "ITM_GRP_ID", searchImBasicData1.getItemGroup());
            ConditionUtils.numericConditions(conditions, "SUB_ITM_GRP_ID", searchImBasicData1.getSubItemGroup());

            ConditionUtils.addDateCondition(conditions, "UTD_ON", searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
            ConditionUtils.addDateCondition(conditions, "CTD_ON", searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<ImBasicData1NewV4> imBasicData1Encoder = Encoders.bean(ImBasicData1NewV4.class);
            Dataset<ImBasicData1NewV4> dataset = data.as(imBasicData1Encoder);
            //    return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<ImBasicData1NewV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }

        } catch (Exception e) {
            log.error("Find ImBasicData1 Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchImBasicData1
     * @return
     */
    public List<ImBasicData1V4> searchImBasicData1V3(SearchImBasicData1 searchImBasicData1) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "UOM_ID as uomId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "ITM_CODE as itemCode, "
                    + "MFR_PART as manufacturerPartNo, "
                    + "TEXT as description, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "BRAND as brand, "
                    + "SIZE as size, "
                    + "ITM_TYP_ID as itemType, "
                    + "ITM_GRP_ID as itemGroup, "
                    + "itm_typ_text as itemTypeDescription, "
                    + "itm_grp_text as itemGroupDescription, "
                    + "CTD_BY as createdBy, "
                    + "CTD_ON as createdOn "
                    + "FROM tblimbasicdata1";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchImBasicData1.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchImBasicData1.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchImBasicData1.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchImBasicData1.getWarehouseId());
            ConditionUtils.addCondition(conditions, "ITM_CODE", searchImBasicData1.getItemCode());
            ConditionUtils.addCondition(conditions, "TEXT", searchImBasicData1.getDescription());
            ConditionUtils.addCondition(conditions, "MFR_PART", searchImBasicData1.getManufacturerPartNo());
            ConditionUtils.addCondition(conditions, "CTD_BY", searchImBasicData1.getCreatedBy());

            ConditionUtils.numericConditions(conditions, "ITM_TYP_ID", searchImBasicData1.getItemType());
            ConditionUtils.numericConditions(conditions, "ITM_GRP_ID", searchImBasicData1.getItemGroup());
            ConditionUtils.numericConditions(conditions, "SUB_ITM_GRP_ID", searchImBasicData1.getSubItemGroup());

            ConditionUtils.addDateCondition(conditions, "UTD_ON", searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
            ConditionUtils.addDateCondition(conditions, "CTD_ON", searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

//            data.show();
            Encoder<ImBasicData1V4> imBasicData1Encoder = Encoders.bean(ImBasicData1V4.class);
            Dataset<ImBasicData1V4> dataset = data.as(imBasicData1Encoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<ImBasicData1V4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find ImBasicData1V4 Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchGrHeader
     * @return
     * @throws Exception
     */
    public List<GrHeaderV2> findGrHeaderV2(SearchGrHeaderV2 searchGrHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchGrHeader.getCompanyCodeId(), searchGrHeader.getPlantId(), searchGrHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCode, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "PRE_IB_NO as preInboundNo, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "STG_NO as stagingNo, "
                    + "GR_NO as goodsReceiptNo, "
                    + "PAL_CODE as palletCode, "
                    + "CASE_CODE as caseCode, "
                    + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                    + "STATUS_ID as statusId, "
                    + "GR_MTD as grMethod, "
                    + "CONT_REC_NO as containerReceiptNo, "
                    + "DOCK_ALL_NO as dockAllocationNo, "
                    + "CONT_NO as containerNo, "
                    + "VEH_NO as vehicleNo, "
                    + "EA_DATE as expectedArrivalDate, "
                    + "GR_DATE as goodsReceiptDate, "
                    + "IS_DELETED as deletionIndicator, "
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
                    + "GR_CTD_BY as createdBy, "
                    + "GR_CTD_ON as createdOn, "
                    + "GR_UTD_BY as updatedBy, "
                    + "GR_UTD_ON as updatedOn, "
                    + "GR_CNF_BY as confirmedBy, "
                    + "GR_CNF_ON as confirmedOn, "
                    + "CUSTOMER_ID as customerId, "
                    + "CUSTOMER_NAME as customerName, "

                    + "ACCEPT_QTY as acceptedQuantity, "
                    + "DAMAGE_QTY as damagedQuantity, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece, "
                    + "QTY_IN_CREATE as qtyInCreate, "
                    + "REF_DOC_TYPE as referenceDocumentType "
                    + "FROM tblgrheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchGrHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchGrHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchGrHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchGrHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "GR_NO", searchGrHeader.getGoodsReceiptNo());
            ConditionUtils.addCondition(conditions, "PRE_IB_NO", searchGrHeader.getPreInboundNo());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", searchGrHeader.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "CASE_CODE", searchGrHeader.getCaseCode());
            ConditionUtils.addCondition(conditions, "GR_CTD_BY", searchGrHeader.getCreatedBy());

            ConditionUtils.numericConditions(conditions, "IB_ORD_TYP_ID", searchGrHeader.getInboundOrderTypeId());
            ConditionUtils.numericConditions(conditions, "STATUS_ID", searchGrHeader.getStatusId());

            ConditionUtils.addDateCondition(conditions, "GR_CTD_ON", searchGrHeader.getStartCreatedOn(), searchGrHeader.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }


            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            Encoder<GrHeaderV2> grHeaderV2Encoder = Encoders.bean(GrHeaderV2.class);
            Dataset<GrHeaderV2> dataset = data.as(grHeaderV2Encoder);

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<GrHeaderV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }

            //            data.show();
//            Encoder<GrHeaderV2> grHeaderEncoder = Encoders.bean(GrHeaderV2.class);
//            Dataset<GrHeaderV2> dataset = data.as(grHeaderEncoder);
//            return dataset.collectAsList();
        } catch (Exception e) {
            log.error("Find grHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findPreInboundHeader
     * @return
     * @throws Exception
     */
    public List<PreInboundHeaderV2> findPreInboundHeaderv2(FindPreInboundHeaderV2 findPreInboundHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPreInboundHeader.getCompanyCodeId(), findPreInboundHeader.getPlantId(), findPreInboundHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

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
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece, "
                    + "QTY_IN_CREATE as qtyInCreate, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "SOURCE_BRANCH_CODE as sourceBranchCode, "
                    + "SOURCE_COMPANY_CODE as sourceCompanyCode, "
                    + "CUSTOMER_ID as customerId, "
                    + "TRANSFER_ORDER_DATE as transferOrderDate, "
                    + "CUSTOMER_NAME as customerName "
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
            ConditionUtils.addCondition(conditions, "CUSTOMER_ID", findPreInboundHeader.getCustomerId());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<PreInboundHeaderV2> preInboundHeaderEncoder = Encoders.bean(PreInboundHeaderV2.class);
            Dataset<PreInboundHeaderV2> dataset = data.as(preInboundHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PreInboundHeaderV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find preInboundHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    public List<PreInBoundLineV2> findPreInboundLinev2(FindPreInboundLineV2 findPreInboundLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPreInboundLine.getCompanyCodeId(), findPreInboundLine.getPlantId(), findPreInboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID AS languageId, "
                    + "C_ID AS companyCode, "
                    + "PLANT_ID AS plantId, "
                    + "WH_ID AS warehouseId, "
                    + "PRE_IB_NO AS preInboundNo, "
                    + "REF_DOC_NO AS refDocNumber, "
                    + "IB_LINE_NO AS [lineNo], "
                    + "ITM_CODE AS itemCode, "
                    + "IB_ORD_TYP_ID AS inboundOrderTypeId, "
                    + "VAR_ID AS variantCode, "
                    + "VAR_SUB_ID AS variantSubCode, "
                    + "STATUS_ID AS statusId, "
                    + "ITEM_TEXT AS itemDescription, "
                    + "CONT_NO AS containerNo, "
                    + "INV_NO AS invoiceNo, "
                    + "PARTNER_CODE AS businessPartnerCode, "
                    + "PARTNER_ITM_CODE AS partnerItemNo, "
                    + "BRND_NM AS brand, "
                    + "MFR_PART AS manufacturerPartNo, "
                    + "HSN_CODE AS hsnCode, "
                    + "EA_DATE AS expectedArrivalDate, "
                    + "ORD_QTY AS orderQty, "
                    + "ORD_UOM AS orderUom, "
                    + "STCK_TYP_ID AS stockTypeId, "
                    + "SP_ST_IND_ID AS specialStockIndicatorId, "
                    + "PAL_QTY AS numberOfPallets, "
                    + "CASE_NO AS numberOfCases, "
                    + "ITM_PAL_QTY AS itemPerPalletQty, "
                    + "ITM_CASE_QTY AS itemCaseQty, "
                    + "REF_FIELD_1 AS referenceField1, "
                    + "REF_FIELD_2 AS referenceField2, "
                    + "REF_FIELD_3 AS referenceField3, "
                    + "REF_FIELD_4 AS referenceField4, "
                    + "REF_FIELD_5 AS referenceField5, "
                    + "REF_FIELD_6 AS referenceField6, "
                    + "REF_FIELD_7 AS referenceField7, "
                    + "REF_FIELD_8 AS referenceField8, "
                    + "REF_FIELD_9 AS referenceField9, "
                    + "REF_FIELD_10 AS referenceField10, "
                    + "REF_FIELD_11 AS referenceField11, "
                    + "REF_FIELD_12 AS referenceField12, "
                    + "REF_FIELD_13 AS referenceField13, "
                    + "REF_FIELD_14 AS referenceField14, "
                    + "REF_FIELD_15 AS referenceField15, "
                    + "REF_FIELD_16 AS referenceField16, "
                    + "REF_FIELD_17 AS referenceField17, "
                    + "REF_FIELD_18 AS referenceField18, "
                    + "REF_FIELD_19 AS referenceField19, "
                    + "REF_FIELD_20 AS referenceField20, "
                    + "IS_DELETED AS deletionIndicator, "
                    + "CTD_BY AS createdBy, "
                    + "CTD_ON AS createdOn, "
                    + "UTD_BY AS updatedBy, "
                    + "UTD_ON AS updatedOn, "
                    + "MFR_CODE AS manufacturerCode, "
                    + "MFR_NAME AS manufacturerName, "
                    + "ST_SEC_ID AS storageSectionId, "
                    + "ORIGIN AS origin, "
                    + "SUPPLIER_NAME AS supplierName, "
                    + "C_TEXT AS companyDescription, "
                    + "PLANT_TEXT AS plantDescription, "
                    + "WH_TEXT AS warehouseDescription, "
                    + "STATUS_TEXT AS statusDescription, "
                    + "MIDDLEWARE_ID AS middlewareId, "
                    + "MIDDLEWARE_HEADER_ID AS middlewareHeaderId, "
                    + "MIDDLEWARE_TABLE AS middlewareTable, "
                    + "REF_DOC_TYPE AS referenceDocumentType, "
                    + "PURCHASE_ORDER_NUMBER AS purchaseOrderNumber, "
                    + "PARENT_PRODUCTION_ORDER_NO AS parentProductionOrderNo, "
                    + "MANUFACTURER_FULL_NAME AS manufacturerFullName, "
                    + "BRANCH_CODE AS branchCode, "
                    + "TRANSFER_ORDER_NO AS transferOrderNo, "
                    + "IS_COMPLETED AS isCompleted, "
                    + "CSTR_COD AS customerCode, "
                    + "TFR_REQ_TYP AS TransferRequestType, "
                    + "AMS_SUP_INV AS AMSSupplierInvoiceNo, "
                    + "STR_NO AS batchSerialNumber, "
                    + "BARCODE_ID AS barcodeId, "
                    + "MATERIAL_NO AS materialNo, "
                    + "PRICE_SEGMENT AS priceSegment, "
                    + "ARTICLE_NO AS articleNo, "
                    + "GENDER AS gender, "
                    + "COLOR AS color, "
                    + "SIZE AS size, "
                    + "NO_PAIRS AS noPairs, "
                    + "ALT_UOM AS alternateUom, "
                    + "NO_BAGS AS noBags, "
                    + "BAG_SIZE AS bagSize, "
                    + "MRP AS mrp, "
                    + "ITM_TYP AS itemType, "
                    + "ITM_GRP AS itemGroup, "
                    + "STCK_TYP_TXT AS stockTypeDescription "
                    + "FROM tblpreinboundline";



            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findPreInboundLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findPreInboundLine.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findPreInboundLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", findPreInboundLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "PRE_IB_NO", findPreInboundLine.getPreInboundNo());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findPreInboundLine.getRefDocNumber());

            ConditionUtils.numericConditions(conditions, "IB_ORD_TYP_ID", findPreInboundLine.getInboundOrderTypeId());
            ConditionUtils.numericConditions(conditions, "STATUS_ID", findPreInboundLine.getStatusId());

            ConditionUtils.addDateCondition(conditions, "CTD_ON", findPreInboundLine.getStartCreatedOn(), findPreInboundLine.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            Encoder<PreInBoundLineV2> preInboundLineEncoder = Encoders.bean(PreInBoundLineV2.class);
            Dataset<PreInBoundLineV2> dataset = data.as(preInboundLineEncoder);

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PreInBoundLineV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            }finally {
                executor.shutdownNow();
            }

            //            data.show();
//            Encoder<PreInBoundLineV2> preInboundLineEncoder = Encoders.bean(PreInBoundLineV2.class);
//            Dataset<PreInBoundLineV2> dataset = data.as(preInboundLineEncoder);
//            return dataset.collectAsList();
        } catch (Exception e) {
            log.error("Find preInboundLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchPutAwayHeader
     * @return
     * @throws Exception
     */
    public List<PutAwayHeaderV4> findPutAwayHeaderV2(SearchPutAwayHeaderV2 searchPutAwayHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchPutAwayHeader.getCompanyCodeId(), searchPutAwayHeader.getPlantId(), searchPutAwayHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "PRE_IB_NO as preInboundNo, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "GR_NO as goodsReceiptNo, "
                    + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                    + "PAL_CODE as palletCode, "
                    + "CASE_CODE as caseCode, "
                    + "PACK_BARCODE as packBarcodes, "
                    + "PA_NO as putAwayNumber, "
                    + "PROP_ST_BIN as proposedStorageBin, "
                    + "PA_QTY as putAwayQuantity, "
                    + "PA_UOM as putAwayUom, "
                    + "STR_TYP_ID as strategyTypeId, "
                    + "ST_NO as strategyNo, "
                    + "PROP_HE_NO as proposedHandlingEquipment, "
                    + "ASS_USER_ID as assignedUserId, "
                    + "STATUS_ID as statusId, "
                    + "QTY_TYPE as quantityType, "
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
                    + "PA_CTD_BY as createdBy, "
                    + "PA_CTD_ON as createdOn, "
                    + "PA_UTD_BY as updatedBy, "
                    + "PA_UTD_ON as updatedOn, "
                    + "PA_CNF_BY as confirmedBy, "
                    + "PA_CNF_ON as confirmedOn, "
                    + "INV_QTY as inventoryQuantity, "
                    + "BARCODE_ID as barcodeId, "
                    + "MFR_DATE as manufacturerDate, "
                    + "EXP_DATE as expiryDate, "
                    + "MFR_CODE as manufacturerCode, "
                    + "MFR_NAME as manufacturerName, "
                    + "ORIGIN as origin, "
                    + "BRAND as brand, "
                    + "ORD_QTY as orderQty, "
                    + "CBM as cbm, "
                    + "CBM_UNIT as cbmUnit, "
                    + "CBM_QTY as cbmQuantity, "
                    + "APP_STATUS as approvalStatus, "
                    + "REMARK as remark, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "ACTUAL_PACK_BARCODE as actualPackBarcodes, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "REF_DOC_TYPE as referenceDocumentType, "
                    + "TRANSFER_ORDER_DATE as transferOrderDate, "
                    + "IS_COMPLETED as isCompleted, "
                    + "IS_CANCELLED as isCancelled, "
                    + "M_UPDATED_ON as mUpdatedOn, "
                    + "SOURCE_BRANCH_CODE as sourceBranchCode, "
                    + "SOURCE_COMPANY_CODE as sourceCompanyCode, "
                    + "LEVEL_ID as levelId, "
                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "mrp as mrp, "
                    + "itm_typ as itemType, "
                    + "itm_grp as itemGroup, "
                    + "PIECE_QTY as pieceQty, "
                    + "CASE_QTY as caseQty, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece, "
                    + "QTY_IN_CREATE as qtyInCreate, "
                    + "VEHICLE_NO as vehicleNo, "
                    + "VEHICLE_REPORTING_DATE as vehicleReportingDate, "
                    + "VEHICLE_UNLOADING_DATE as vehicleUnloadingDate, "
                    + "RECEIVINGVARIANCE as receivingVariance,"
                    + "STR_NO as batchSerialNumber, "
                    + "CUSTOMER_ID as customerId, "
                    + "CUSTOMER_NAME as customerName "
                    + "FROM tblputawayheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchPutAwayHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchPutAwayHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchPutAwayHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchPutAwayHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", searchPutAwayHeader.getPackBarcodes());
            ConditionUtils.addCondition(conditions, "PA_NO", searchPutAwayHeader.getPutAwayNumber());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", searchPutAwayHeader.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "PROP_ST_BIN", searchPutAwayHeader.getProposedStorageBin());
            ConditionUtils.addCondition(conditions, "PROP_HE_NO", searchPutAwayHeader.getProposedHandlingEquipment());
            ConditionUtils.addCondition(conditions, "BARCODE_ID", searchPutAwayHeader.getBarcodeId());
            ConditionUtils.addCondition(conditions, "PA_CTD_BY", searchPutAwayHeader.getCreatedBy());
            ConditionUtils.addCondition(conditions, "PAL_CODE", searchPutAwayHeader.getPalletCode());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", searchPutAwayHeader.getStatusId());
            ConditionUtils.addCondition(conditions, "CUSTOMER_ID", searchPutAwayHeader.getCustomerId());

            ConditionUtils.addDateCondition(conditions, "PA_CTD_ON", searchPutAwayHeader.getStartCreatedOn(), searchPutAwayHeader.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }
            //            data.show();
            Encoder<PutAwayHeaderV4> putAwayHeaderEncoder = Encoders.bean(PutAwayHeaderV4.class);
            Dataset<PutAwayHeaderV4> dataset = data.as(putAwayHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PutAwayHeaderV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find PutAwayHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findStagingHeader
     * @return
     * @throws Exception
     */
    public List<StagingHeaderV2> findStagingHeader(FindStagingHeaderV2 findStagingHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findStagingHeader.getCompanyCodeId(), findStagingHeader.getPlantId(), findStagingHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCode, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "PRE_IB_NO as preInboundNo, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "STG_NO as stagingNo, "
                    + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                    + "STATUS_ID as statusId, "
                    + "CONT_REC_NO as containerReceiptNo, "
                    + "DOCK_ALL_NO as dockAllocationNo, "
                    + "CONT_NO as containerNo, "
                    + "VEH_NO as vechicleNo, "
                    + "GR_MTD as grMtd, "
                    + "IS_DELETED as deletionIndicator, "
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
                    + "ST_CTD_BY as createdBy, "
                    + "ST_CTD_ON as createdOn, "
                    + "ST_UTD_BY as updatedBy, "
                    + "ST_UTD_ON as updatedOn, "
                    + "ST_CNF_BY as confirmedBy, "
                    + "ST_CNF_ON as confirmedOn, "
                    + "CUSTOMER_ID as customerId, "
                    + "CUSTOMER_NAME as customerName, "

                    //v2 fields
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece, "
                    + "QTY_IN_CREATE as qtyInCreate, "
                    + "REF_DOC_TYPE as referenceDocumentType "
                    + "FROM tblstagingheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findStagingHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findStagingHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findStagingHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", findStagingHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "STG_NO", findStagingHeader.getStagingNo());
            ConditionUtils.addCondition(conditions, "PRE_IB_NO", findStagingHeader.getPreInboundNo());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findStagingHeader.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "ST_CTD_BY", findStagingHeader.getCreatedBy());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findStagingHeader.getStatusId());
            ConditionUtils.numericConditions(conditions, "IB_ORD_TYP_ID", findStagingHeader.getInboundOrderTypeId());

            ConditionUtils.addDateCondition(conditions, "ST_CTD_ON", findStagingHeader.getStartCreatedOn(), findStagingHeader.getEndCreatedOn());
            ConditionUtils.addCondition(conditions, "CUSTOMER_ID", findStagingHeader.getCustomerId());


            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }
            //            data.show();
            Encoder<StagingHeaderV2> stagingHeaderEncoder = Encoders.bean(StagingHeaderV2.class);
            Dataset<StagingHeaderV2> dataset = data.as(stagingHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<StagingHeaderV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find StagingHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findInboundHeader
     * @return
     * @throws Exception
     */
    public List<InboundHeaderV2> findInboundHeader(FindInboundHeaderV2 findInboundHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findInboundHeader.getCompanyCodeId(), findInboundHeader.getPlantId(), findInboundHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCode, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "PRE_IB_NO as preInboundNo, "
                    + "STATUS_ID as statusId, "
                    + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                    + "CONT_NO as containerNo, "
                    + "VEH_NO as vechicleNo, "
                    + "IB_TEXT as headerText, "
                    + "IS_DELETED as deletionIndicator, "
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
                    + "CTD_BY as createdBy, "
                    + "CTD_ON as createdOn, "
                    + "UTD_BY as updatedBy, "
                    + "UTD_ON as updatedOn, "
                    + "IB_CNF_BY as confirmedBy, "
                    + "IB_CNF_ON as confirmedOn, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "REF_DOC_TYPE as referenceDocumentType, "
                    + "COUNT_OF_ORD_LINES as countOfOrderLines, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece, "
                    + "QTY_IN_CREATE as qtyInCreate, "
                    + "RECEIVED_LINES as receivedLines, "
                    + "CUSTOMER_ID as customerId, "
                    + "CUSTOMER_NAME as customerName "
                    + "FROM tblinboundheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findInboundHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findInboundHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findInboundHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", findInboundHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "CONT_NO", findInboundHeader.getContainerNo());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findInboundHeader.getRefDocNumber());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findInboundHeader.getStatusId());
            ConditionUtils.numericConditions(conditions, "IB_ORD_TYP_ID", findInboundHeader.getInboundOrderTypeId());
            ConditionUtils.addCondition(conditions, "CUSTOMER_ID", findInboundHeader.getCustomerId());

            ConditionUtils.addDateCondition(conditions, "CTD_ON", findInboundHeader.getStartCreatedOn(), findInboundHeader.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<InboundHeaderV2> inboundHeaderEncoder = Encoders.bean(InboundHeaderV2.class);
            Dataset<InboundHeaderV2> dataset = data.as(inboundHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<InboundHeaderV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find InboundHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findStagingLine
     * @return
     * @throws Exception
     */
    public List<StagingLineV5> findStagingLineV2(FindStagingLineV2 findStagingLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findStagingLine.getCompanyCodeId(), findStagingLine.getPlantId(), findStagingLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + " LANG_ID as languageId, "
                    + " C_ID as companyCode, "
                    + " PLANT_ID as plantId, "
                    + " WH_ID as warehouseId, "
                    + " PRE_IB_NO as preInboundNo, "
                    + " REF_DOC_NO as refDocNumber, "
                    + "STG_NO as stagingNo, "
                    + "PAL_CODE as palletCode,"
                    + "GR_NO as goodsReceiptNo, "
                    + "CASE_CODE as caseCode, "
                    + "IB_LINE_NO as [lineNo], "
                    + "ITM_CODE as itemCode, "
                    + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                    + "VAR_ID as variantCode, "
                    + "VAR_SUB_ID as variantSubCode, "
                    + "STR_NO as batchSerialNumber, "
                    + "STCK_TYP_ID as stockTypeId, "
                    + "SP_ST_IND_ID as specialStockIndicatorId, "
                    + "ST_MTD as storageMethod, "
                    + "STATUS_ID as statusId, "
                    + "PARTNER_CODE as businessPartnerCode, "
                    + "CONT_NO as containerNo, "
                    + "INV_NO as invoiceNo, "
                    + "ORD_QTY as orderQty, "
                    + "ORD_UOM as orderUom, "
                    + "ITM_PAL_QTY as itemQtyPerPallet, "
                    + "ITM_CASE_QTY as itemQtyPerCase, "
                    + "ASS_USER_ID as assignedUserId, "
                    + "ITEM_TEXT as itemDescription, "
                    + "MFR_PART as manufacturerPartNo, "
                    + "HSN_CODE as hsnCode, "
                    + "VAR_TYP as variantType, "
                    + "SPEC_ACTUAL as specificationActual, "
                    + "ITM_BARCODE as itemBarcode, "
                    + "REF_ORD_NO as referenceOrderNo, "
                    + "REF_ORD_QTY as referenceOrderQty, "
                    + "CROSS_DOCK_ALLOC_QTY as crossDockAllocationQty, "
                    + "REMARK as remarks, "
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
                    + "ST_CTD_BY as createdBy, "
                    + "ST_CTD_ON as createdOn, "
                    + "ST_UTD_BY as updatedBy, "
                    + "ST_UTD_ON as updatedOn, "
                    + "ST_CNF_BY as confirmedBy, "
                    + "ST_CNF_ON as confirmedOn, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "INV_QTY as inventoryQuantity, "
                    + "MFR_CODE as manufacturerCode, "
                    + "MFR_NAME as manufacturerName, "
                    + "ORIGIN as origin, "
                    + "BRAND as brand, "
                    + "PARTNER_ITEM_BARCODE as partner_item_barcode, "
                    + "REC_ACCEPT_QTY as rec_accept_qty, "
                    + "REC_DAMAGE_QTY as rec_damage_qty, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "REF_DOC_TYPE as referenceDocumentType, "
                    + "BRANCH_CODE as branchCode, "
                    + "TRANSFER_ORDER_NO as transferOrderNo, "
                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "SIZE as size, "
                    + "mrp as mrp, "
                    + "itm_typ as itemType, "
                    + "itm_grp as itemGroup, "
                    + "PIECE_QTY as pieceQty, "
                    + "CASE_QTY as caseQty, "
                    + "IS_COMPLETED as isCompleted, "
                    + "MFR_DATE as manufacturerDate, "
                    + "EXP_DATE as expiryDate, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece, "
                    + "QTY_IN_CREATE as qtyInCreate, "
                    + "BARCODE_ID as barcodeId, "
                    + "VEHICLE_NO as vehicleNo, "
                    + "VEHICLE_REPORTING_DATE as vehicleReportingDate, "
                    + "VEHICLE_UNLOADING_DATE as vehicleUnloadingDate, "
                    + "PRINT_LABEL as printLabel, "
                    + "CROSS_DOCK as crossDock, "
                    + "PARENT_PRODUCTION_ORDER_NO as parentProductionOrderNo "
                    + "FROM tblstagingline ";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findStagingLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findStagingLine.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findStagingLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", findStagingLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findStagingLine.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "MFR_CODE", findStagingLine.getManufacturerCode());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findStagingLine.getItemCode());
            ConditionUtils.addCondition(conditions, "MFR_NAME", findStagingLine.getManufacturerName());
            ConditionUtils.addCondition(conditions, "ORIGIN", findStagingLine.getOrigin());
            ConditionUtils.addCondition(conditions, "BRAND", findStagingLine.getBrand());
            ConditionUtils.addCondition(conditions, "PRE_IB_NO", findStagingLine.getPreInboundNo());
            ConditionUtils.addCondition(conditions, "STG_NO", findStagingLine.getStagingNo());
            ConditionUtils.addCondition(conditions, "PAL_CODE", findStagingLine.getPalletCode());
            ConditionUtils.addCondition(conditions, "CASE_CODE", findStagingLine.getCaseCode());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findStagingLine.getStatusId());
            ConditionUtils.numericConditions(conditions, "IB_LINE_NO", findStagingLine.getLineNo());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            Encoder<StagingLineV5> stagingLineEncoder = Encoders.bean(StagingLineV5.class);
            Dataset<StagingLineV5> dataset = data.as(stagingLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<StagingLineV5>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find StagingLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findGrLine
     * @return
     * @throws Exception
     */
    public List<GrLineV4> findGrLineV2(FindGrLineV2 findGrLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findGrLine.getCompanyCodeId(), findGrLine.getPlantId(), findGrLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT " +
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
                    "IB_LINE_NO as lineNumber, " +
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
                    "TPL_CBM as threePLCbm, " +
                    "TPL_UOM as threePLUom, " +
                    "TPL_BILL_STATUS as threePLBillStatus, " +
                    "TPL_LENGTH as threePLLength, " +
                    "TPL_WIDTH as threePLHeight, " +
                    "TPL_HEIGHT as threePLWidth, " +
                    "ACCEPT_TOT_CBM as acceptTotalCbm, " +
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
                    "ALT_UOM as alternateUom, " +
                    "NO_BAGS as noBags, " +
                    "BAG_SIZE as bagSize, " +
                    "mrp as mrp, " +
                    "itm_typ as itemType, " +
                    "itm_grp as itemGroup, " +
                    "PIECE_QTY as pieceQty, " +
                    "CASE_QTY as caseQty, " +
                    "QTY_IN_CASE as qtyInCase, " +
                    "QTY_IN_PIECE as qtyInPiece, " +
                    "QTY_IN_CREATE as qtyInCreate, " +
                    "VEHICLE_NO as vehicleNo, " +
                    "VEHICLE_REPORTING_DATE as vehicleReportingDate, " +
                    "VEHICLE_UNLOADING_DATE as vehicleUnloadingDate, " +
                    "RECEIVINGVARIANCE as receivingVariance," +
                    "IS_COMPLETED as isCompleted " +
                    "FROM tblGrline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findGrLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findGrLine.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findGrLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", findGrLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findGrLine.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "MFR_CODE", findGrLine.getManufacturerCode());
            ConditionUtils.addCondition(conditions, "MFR_NAME", findGrLine.getManufacturerName());
            ConditionUtils.addCondition(conditions, "ORIGIN", findGrLine.getOrigin());
            ConditionUtils.addCondition(conditions, "BRAND", findGrLine.getBrand());
            ConditionUtils.addCondition(conditions, "PRE_IB_NO", findGrLine.getPreInboundNo());
            ConditionUtils.addCondition(conditions, "CASE_CODE", findGrLine.getCaseCode());
            ConditionUtils.addCondition(conditions, "ST_BIN_INTM", findGrLine.getInterimStorageBin());
            ConditionUtils.addCondition(conditions, "REJ_TYPE", findGrLine.getRejectType());
            ConditionUtils.addCondition(conditions, "REJ_REASON", findGrLine.getRejectReason());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", findGrLine.getPackBarcodes());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findGrLine.getStatusId());
            ConditionUtils.numericConditions(conditions, "IB_LINE_NO", findGrLine.getLineNo());

            ConditionUtils.addDateCondition(conditions, "GR_CTD_ON", findGrLine.getStartCreatedOn(), findGrLine.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }
            //            data.show();
            Encoder<GrLineV4> grLineEncoder = Encoders.bean(GrLineV4.class);
            Dataset<GrLineV4> dataset = data.as(grLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<GrLineV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find GrLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findPutAwayLineV2
     * @return
     */
    public List<PutAwayLineCoreV4> getPutAwayLine(FindPutAwayLineV2 findPutAwayLineV2) throws Exception {
        try {

            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPutAwayLineV2.getCompanyCodeId(), findPutAwayLineV2.getPlantId(), findPutAwayLineV2.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT C_TEXT, PLANT_TEXT, WH_TEXT, STATUS_TEXT, MFR_NAME, ITM_CODE, REF_DOC_NO, REF_DOC_TYPE, " +
                    "PROP_ST_BIN, CNF_ST_BIN, BARCODE_ID, PA_QTY, PA_CNF_QTY, PA_CNF_BY, PA_CTD_ON, PA_UTD_ON as PA_CNF_ON, " +
                    "REF_FIELD_1, REF_FIELD_10, ALT_UOM, NO_BAGS, BAG_SIZE, MRP, itm_typ, itm_grp, QTY_IN_PIECE, QTY_IN_CREATE, QTY_IN_CASE FROM tblputawayline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "PA_NO", findPutAwayLineV2.getPutAwayNumber());
            ConditionUtils.addCondition(conditions, "BRAND", findPutAwayLineV2.getBrand());
            ConditionUtils.addCondition(conditions, "C_ID", findPutAwayLineV2.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findPutAwayLineV2.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findPutAwayLineV2.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findPutAwayLineV2.getLanguageId());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findPutAwayLineV2.getItemCode());
            ConditionUtils.addCondition(conditions, "ORIGIN", findPutAwayLineV2.getOrigin());
            ConditionUtils.addCondition(conditions, "BARCODE_ID", findPutAwayLineV2.getBarcodeId());
            ConditionUtils.addCondition(conditions, "CNF_ST_BIN", findPutAwayLineV2.getConfirmedStorageBin());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findPutAwayLineV2.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "GR_NO", findPutAwayLineV2.getGoodsReceiptNo());
            ConditionUtils.addCondition(conditions, "PRE_IB_NO", findPutAwayLineV2.getPreInboundNo());
            ConditionUtils.addCondition(conditions, "PROP_ST_BIN", findPutAwayLineV2.getProposedStorageBin());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", findPutAwayLineV2.getPackBarCodes());
            ConditionUtils.addCondition(conditions, "MFR_CODE", findPutAwayLineV2.getManufacturerCode());
            ConditionUtils.addCondition(conditions, "MFR_NAME", findPutAwayLineV2.getManufacturerName());
            ConditionUtils.addCondition(conditions, "PA_CNF_BY", findPutAwayLineV2.getConfirmedBy());

            ConditionUtils.numericConditions(conditions, "IB_ORD_TYP_ID", findPutAwayLineV2.getInboundOrderTypeId());
            ConditionUtils.numericConditions(conditions, "STATUS_ID", findPutAwayLineV2.getStatusId());
            ConditionUtils.numericConditions(conditions, "IB_LINE_NO", findPutAwayLineV2.getLineNo());

            ConditionUtils.addDateCondition(conditions, "PA_CTD_ON", findPutAwayLineV2.getFromCreatedDate(), findPutAwayLineV2.getToCreatedDate());
            ConditionUtils.addDateCondition(conditions, "PA_CNF_ON", findPutAwayLineV2.getFromConfirmedDate(), findPutAwayLineV2.getToConfirmedDate());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }
//            data.show();
            Encoder<PutAwayLineCoreV4> putAwayLineEncoder = Encoders.bean(PutAwayLineCoreV4.class);
            Dataset<PutAwayLineCoreV4> dataset = data.as(putAwayLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PutAwayLineCoreV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find PutawayLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findInboundLine
     * @return
     */
    public List<InboundLineNewV4> findInboundLineV2(FindInboundLineV2 findInboundLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findInboundLine.getCompanyCodeId(), findInboundLine.getPlantId(), findInboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCode, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "PRE_IB_NO as preInboundNo, "
                    + "IB_LINE_NO as [lineNo], "
                    + "ITM_CODE as itemCode, "
                    + "ORD_QTY as orderQty, "
                    + "ORD_UOM as orderUom, "
                    + "ACCEPT_QTY as acceptedQty, "
                    + "DAMAGE_QTY as damageQty, "
                    + "PA_CNF_QTY as putawayConfirmedQty, "
                    + "VAR_QTY as varianceQty, "
                    + "VAR_ID as variantCode, "
                    + "VAR_SUB_ID as variantSubCode, "
                    + "IB_ORD_TYP_ID as inboundOrderTypeId, "
                    + "STCK_TYP_ID as stockTypeId, "
                    + "SP_ST_IND_ID as specialStockIndicatorId, "
                    + "REF_ORD_NO as referenceOrderNo, "
                    + "STATUS_ID as statusId, "
                    + "PARTNER_CODE as vendorCode, "
                    + "EA_DATE as expectedArrivalDate, "
                    + "CONT_NO as containerNo, "
                    + "INV_NO as invoiceNo, "
                    + "TEXT as description, "
                    + "MFR_PART as manufacturerPartNo, "
                    + "HSN_CODE as hsnCode, "
                    + "ITM_BARCODE as itemBarcode, "
                    + "ITM_CASE_QTY as itemCaseQty, "
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
                    + "IB_CNF_BY as confirmedBy, "
                    + "IB_CNF_ON as confirmedOn, "

                    // V2 fields
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MFR_CODE as manufacturerCode, "
                    + "MFR_NAME as manufacturerName, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "REF_DOC_TYPE as referenceDocumentType, "
                    + "PURCHASE_ORDER_NUMBER as purchaseOrderNumber, "
                    + "SUPPLIER_NAME as supplierName, "
                    + "BRANCH_CODE as branchCode, "
                    + "TRANSFER_ORDER_NO as transferOrderNo, "
                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "mrp as mrp, "
                    + "itm_typ as itemType, "
                    + "itm_grp as itemGroup, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece, "
                    + "QTY_IN_CREATE as qtyInCreate, "
                    + "VEHICLE_NO as vehicleNo, "
                    + "MFR_DATE as manufacturerDate, "
                    + "VEHICLE_REPORTING_DATE as vehicleReportingDate, "
                    + "VEHICLE_UNLOADING_DATE as vehicleUnloadingDate, "
                    + "RECEIVINGVARIANCE as receivingVariance,"
                    + "IS_COMPLETED as isCompleted "
                    + "FROM tblinboundline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findInboundLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findInboundLine.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findInboundLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findInboundLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "REF_FIELD_1", findInboundLine.getReferenceField1());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findInboundLine.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findInboundLine.getItemCode());
            ConditionUtils.addCondition(conditions, "MFR_PART", findInboundLine.getManufacturerPartNo());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findInboundLine.getStatusId());

            ConditionUtils.addDateCondition(conditions, "IB_CNF_ON", findInboundLine.getStartConfirmedOn(), findInboundLine.getEndConfirmedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }
            //            data.show();
            Encoder<InboundLineNewV4> inboundLineEncoder = Encoders.bean(InboundLineNewV4.class);
            Dataset<InboundLineNewV4> dataset = data.as(inboundLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<InboundLineNewV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find InboundLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findPutAwayLine
     * @return
     * @throws Exception
     */
    public List<PutAwayLineV4> findPutAwayLineV2(FindPutAwayLineV2 findPutAwayLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPutAwayLine.getCompanyCodeId(), findPutAwayLine.getPlantId(), findPutAwayLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCode, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "GR_NO as goodsReceiptNo, "
                    + "PRE_IB_NO as preInboundNo, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "PA_NO as putAwayNumber, "
                    + "IB_LINE_NO as lineNumber, "
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
                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "mrp as mrp, "
                    + "itm_typ as itemType, "
                    + "itm_grp as itemGroup, "
                    + "PIECE_QTY as pieceQty, "
                    + "CASE_QTY as caseQty, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece, "
                    + "QTY_IN_CREATE as qtyInCreate, "
                    + "VEHICLE_NO as vehicleNo, "
                    + "VEHICLE_REPORTING_DATE as vehicleReportingDate, "
                    + "VEHICLE_UNLOADING_DATE as vehicleUnloadingDate, "
                    + "RECEIVINGVARIANCE as receivingVariance, "
                    + "IS_COMPLETED as isCompleted "
                    + "FROM tblputawayline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findPutAwayLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findPutAwayLine.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findPutAwayLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findPutAwayLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "GR_NO", findPutAwayLine.getGoodsReceiptNo());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findPutAwayLine.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findPutAwayLine.getItemCode());
            ConditionUtils.addCondition(conditions, "PRE_IB_NO", findPutAwayLine.getPreInboundNo());
            ConditionUtils.addCondition(conditions, "PA_NO", findPutAwayLine.getPutAwayNumber());
            ConditionUtils.addCondition(conditions, "PROP_ST_BIN", findPutAwayLine.getProposedStorageBin());
            ConditionUtils.addCondition(conditions, "CNF_ST_BIN", findPutAwayLine.getConfirmedStorageBin());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", findPutAwayLine.getPackBarCodes());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findPutAwayLine.getStatusId());
            ConditionUtils.numericConditions(conditions, "IB_LINE_NO", findPutAwayLine.getLineNo());

            ConditionUtils.addDateCondition(conditions, "PA_CNF_ON", findPutAwayLine.getFromConfirmedDate(), findPutAwayLine.getToConfirmedDate());
            ConditionUtils.addDateCondition(conditions, "PA_CTD_ON", findPutAwayLine.getFromCreatedDate(), findPutAwayLine.getToCreatedDate());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }
//            data.show();
            Encoder<PutAwayLineV4> putAwayLineV2Encoder = Encoders.bean(PutAwayLineV4.class);
            Dataset<PutAwayLineV4> dataset = data.as(putAwayLineV2Encoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PutAwayLineV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find PutAwayLineV2 Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findPreOutboundHeader
     * @return
     * @throws Exception
     */
    public List<PreOutboundHeaderV2> findPreOutboundHeader(FindPreOutboundHeaderV2 findPreOutboundHeader) throws Exception {
        try {

            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPreOutboundHeader.getCompanyCodeId(), findPreOutboundHeader.getPlantId(), findPreOutboundHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "PRE_OB_NO as preOutboundNo, "
                    + "PARTNER_CODE as partnerCode, "
                    + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                    + "REF_DOC_TYP as referenceDocumentType, "
                    + "STATUS_ID as statusId, "
                    + "REF_DOC_DATE as refDocDate, "
                    + "REQ_DEL_DATE as requiredDeliveryDate, "
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
                    + "PRE_OB_CTD_BY as createdBy, "
                    + "PRE_OB_CTD_ON as createdOn, "
                    + "PRE_OB_UTD_BY as updatedBy, "
                    + "PRE_OB_UTD_ON as updatedOn, "

                    //v2 fields
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "SALES_ORDER_NUMBER as salesOrderNumber, "
                    + "PICK_LIST_NUMBER as pickListNumber, "
                    + "TOKEN_NUMBER as tokenNumber, "
                    + "FROM_BRANCH_CODE as fromBranchCode, "
                    + "IS_COMPLETED as isCompleted, "
                    + "IS_CANCELLED as isCancelled, "
                    + "M_UPDATED_ON as mUpdatedOn, "
                    + "TARGET_BRANCH_CODE as targetBranchCode, "
                    + "CUSTOMER_ID as customerId, "
                    + "CUSTOMER_NAME as customerName,"
                    + "IMS_SALE_TYP_CODE as imsSaleTypeCode "
                    + "FROM tblpreoutboundheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findPreOutboundHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findPreOutboundHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findPreOutboundHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findPreOutboundHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "PRE_OB_NO", findPreOutboundHeader.getPreOutboundNo());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findPreOutboundHeader.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "REF_FIELD_1", findPreOutboundHeader.getSoType());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findPreOutboundHeader.getPartnerCode());
            ConditionUtils.addCondition(conditions, "PRE_OB_CTD_BY", findPreOutboundHeader.getCreatedBy());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findPreOutboundHeader.getStatusId());
            ConditionUtils.numericConditions(conditions, "OB_ORD_TYP_ID", findPreOutboundHeader.getOutboundOrderTypeId());

            ConditionUtils.addDateCondition(conditions, "REQ_DEL_DATE", findPreOutboundHeader.getStartRequiredDeliveryDate(), findPreOutboundHeader.getEndRequiredDeliveryDate());
            ConditionUtils.addDateCondition(conditions, "REF_DOC_DATE", findPreOutboundHeader.getStartOrderDate(), findPreOutboundHeader.getEndOrderDate());
            ConditionUtils.addDateCondition(conditions, "PRE_OB_CTD_ON", findPreOutboundHeader.getStartCreatedOn(), findPreOutboundHeader.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }


            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }
            //            data.show();
            Encoder<PreOutboundHeaderV2> preOutboundHeaderEncoder = Encoders.bean(PreOutboundHeaderV2.class);
            Dataset<PreOutboundHeaderV2> dataset = data.as(preOutboundHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PreOutboundHeaderV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find preOutboundHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findOrderManagementLine
     * @return
     * @throws Exception
     */
    public List<OrderManagementLineV4> findOrderManagementLine(FindOrderManagementLineV2 findOrderManagementLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findOrderManagementLine.getCompanyCodeId(), findOrderManagementLine.getPlantId(), findOrderManagementLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "PRE_OB_NO as preOutboundNo, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "PARTNER_CODE as partnerCode, "
                    + "OB_LINE_NO as lineNumber, "
                    + "ITM_CODE as itemCode, "
                    + "PROP_ST_BIN as proposedStorageBin, "
                    + "PROP_PACK_BARCODE as proposedPackBarCode, "
                    + "PU_NO as pickupNumber, "
                    + "VAR_ID as variantCode, "
                    + "VAR_SUB_ID as variantSubCode, "
                    + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                    + "STATUS_ID as statusId, "
                    + "STCK_TYP_ID as stockTypeId, "
                    + "SP_ST_IND_ID as specialStockIndicatorId, "
                    + "ITEM_TEXT as description, "
                    + "MFR_PART as manufacturerPartNo, "
                    + "HSN_CODE as hsnCode, "
                    + "ITM_BARCODE as itemBarcode, "
                    + "ORD_QTY as orderQty, "
                    + "ORD_UOM as orderUom, "
                    + "INV_QTY as inventoryQty, "
                    + "ALLOC_QTY as allocatedQty, "
                    + "RE_ALLOC_QTY as reAllocatedQty, "
                    + "STR_TYP_ID as strategyTypeId, "
                    + "ST_NO as strategyNo, "
                    + "REQ_DEL_DATE as requiredDeliveryDate, "
                    + "PROP_STR_NO as proposedBatchSerialNumber, "
                    + "PROP_PAL_CODE as proposedPalletCode, "
                    + "PROP_CASE_CODE as proposedCaseCode, "
                    + "PROP_HE_NO as proposedHeNo, "
                    + "PROP_PICKER_ID as proposedPicker, "
                    + "ASS_PICKER_ID as assignedPickerId, "
                    + "REASS_PICKER_ID as reassignedPickerId, "
                    + "IS_DELETED as deletionIndicator, "
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
                    + "RE_ALLOC_BY as reAllocatedBy, "
                    + "RE_ALLOC_ON as reAllocatedOn, "
                    + "PICK_UP_CTD_BY as pickupCreatedBy, "
                    + "PICK_UP_CTD_ON as pickupCreatedOn, "
                    + "PICK_UP_UTD_BY as pickupUpdatedBy, "
                    + "PICK_UP_UTD_ON as pickupUpdatedOn, "
                    + "PICKER_ASSIGN_BY as pickerAssignedBy, "
                    + "PICKER_ASSIGN_ON as pickerAssignedOn, "
                    + "PICKER_REASSIGN_BY as pickerReassignedBy, "
                    + "PICKER_REASSIGN_ON as pickerReassignedOn, "

                    //v2 fields
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
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "TRANSFER_ORDER_NO as transferOrderNo, "
                    + "RET_ORDER_NO as returnOrderNo, "
                    + "TARGET_BRANCH_CODE as targetBranchCode, "
                    + "IS_COMPLETED as isCompleted, "
                    + "IS_CANCELLED as isCancelled, "
                    + "CUSTOMER_ID as customerId, "
                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "mrp as mrp, "
                    + "itm_typ as itemType, "
                    + "itm_grp as itemGroup, "
                    + "CUSTOMER_NAME as customerName, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece,"
                    + "QTY_IN_CRATE as qtyInCrate,"
                    + "MFR_DATE as manufacturerDate,"
                    + "EXP_DATE as expiryDate,"
                    + "PALLET_ID as palletId,"
                    + "IMS_SALE_TYP_CODE as imsSaleTypeCode "
                    + "FROM tblordermangementline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findOrderManagementLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findOrderManagementLine.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findOrderManagementLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findOrderManagementLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "PRE_OB_NO", findOrderManagementLine.getPreOutboundNo());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findOrderManagementLine.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "REF_FIELD_1", findOrderManagementLine.getSoType());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findOrderManagementLine.getPartnerCode());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findOrderManagementLine.getItemCode());
            ConditionUtils.addCondition(conditions, "ITEM_TEXT", findOrderManagementLine.getDescription());
            ConditionUtils.addCondition(conditions, "PALLET_ID", findOrderManagementLine.getPalletId());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findOrderManagementLine.getStatusId());
            ConditionUtils.numericConditions(conditions, "OB_ORD_TYP_ID", findOrderManagementLine.getOutboundOrderTypeId());

            ConditionUtils.addDateCondition(conditions, "REQ_DEL_DATE", findOrderManagementLine.getStartRequiredDeliveryDate(), findOrderManagementLine.getEndRequiredDeliveryDate());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

//            data.show();
            Encoder<OrderManagementLineV4> orderManagementLineEncoder = Encoders.bean(OrderManagementLineV4.class);
            Dataset<OrderManagementLineV4> dataset = data.as(orderManagementLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<OrderManagementLineV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find OrderManagementLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findPickupHeader
     * @return
     * @throws Exception
     */
    public List<PickupHeaderV4> findPickupHeaderV2(FindPickupHeaderV2 findPickupHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPickupHeader.getCompanyCodeId(), findPickupHeader.getPlantId(), findPickupHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
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
                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "mrp as mrp, "
                    + "itm_typ as itemType, "
                    + "itm_grp as itemGroup, "
                    + "STR_NO as batchSerialNumber, "
                    + "CUSTOMER_ID as customerId, "
                    + "IMS_SALE_TYP_CODE as imsSaleTypeCode, "
                    + "NOTI_STATUS as notificationStatus, "
                    + "CSTR_COD as customerCode, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece,"
                    + "QTY_IN_CRATE as qtyInCrate,"
                    + "MFR_DATE as manufacturerDate,"
                    + "EXP_DATE as expiryDate,"
                    + "REMAINING_DAYS as remainingDays,"
                    + "TFR_REQ_TYP as TransferRequestType, "
                    + "CUSTOMER_NAME as customerName "
                    + "FROM tblpickupheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findPickupHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findPickupHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findPickupHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findPickupHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findPickupHeader.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "REF_FIELD_1", findPickupHeader.getSoType());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findPickupHeader.getPartnerCode());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findPickupHeader.getItemCode());
            ConditionUtils.addCondition(conditions, "PU_NO", findPickupHeader.getPickupNumber());
            ConditionUtils.addCondition(conditions, "PROP_ST_BIN", findPickupHeader.getProposedStorageBin());
            ConditionUtils.addCondition(conditions, "PROP_PACK_BARCODE", findPickupHeader.getProposedPackCode());
            ConditionUtils.addCondition(conditions, "ASS_PICKER_ID", findPickupHeader.getAssignedPickerId());
            ConditionUtils.addCondition(conditions, "LEVEL_ID", findPickupHeader.getLevelId());
            ConditionUtils.addCondition(conditions, "REF_FIELD_2", findPickupHeader.getReferenceField2());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findPickupHeader.getStatusId());
            ConditionUtils.numericConditions(conditions, "OB_ORD_TYP_ID", findPickupHeader.getOutboundOrderTypeId());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }
            //            data.show();
            Encoder<PickupHeaderV4> pickupHeaderEncoder = Encoders.bean(PickupHeaderV4.class);
            Dataset<PickupHeaderV4> dataset = data.as(pickupHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PickupHeaderV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find PickupHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchPickupLine
     * @return
     * @throws Exception
     */
    public List<PickupLineNewV4> findPickupLines(SearchPickupLineV2 searchPickupLine) throws Exception {
        try {

            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchPickupLine.getCompanyCodeId(), searchPickupLine.getPlantId(), searchPickupLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
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
                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "mrp as mrp, "
                    + "itm_typ as itemType, "
                    + "itm_grp as itemGroup, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece,"
                    + "QTY_IN_CRATE as qtyInCrate,"
                    + "MFR_DATE as manufacturerDate,"
                    + "EXP_DATE as expiryDate,"
                    + "CUSTOMER_ID as customerId, "
                    + "CUSTOMER_NAME as customerName, "
                    + "PICK_CNF_BARCODE as pickConfirmBarcodeId "
                    + "FROM tblpickupline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchPickupLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchPickupLine.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchPickupLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchPickupLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", searchPickupLine.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", searchPickupLine.getPartnerCode());
            ConditionUtils.addCondition(conditions, "ITM_CODE", searchPickupLine.getItemCode());
            ConditionUtils.addCondition(conditions, "PU_NO", searchPickupLine.getPickupNumber());
            ConditionUtils.addCondition(conditions, "ITM_CODE", searchPickupLine.getItemCode());
            ConditionUtils.addCondition(conditions, "PICK_HE_NO", searchPickupLine.getActualHeNo());
            ConditionUtils.addCondition(conditions, "PICK_ST_BIN", searchPickupLine.getPickedStorageBin());
            ConditionUtils.addCondition(conditions, "PICK_PACK_BARCODE", searchPickupLine.getPickedPackCode());
            ConditionUtils.addCondition(conditions, "ASS_PICKER_ID", searchPickupLine.getAssignedPickerId());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", searchPickupLine.getStatusId());
            ConditionUtils.numericConditions(conditions, "OB_LINE_NO", searchPickupLine.getLineNumber());

            ConditionUtils.addDateCondition(conditions, "PICK_CNF_ON", searchPickupLine.getFromPickConfirmedOn(), searchPickupLine.getToPickConfirmedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<PickupLineNewV4> pickupLineEncoder = Encoders.bean(PickupLineNewV4.class);
            Dataset<PickupLineNewV4> dataset = data.as(pickupLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PickupLineNewV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find PickupLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findQualityHeader
     * @return
     * @throws Exception
     */
    public List<QualityHeaderV4> findQualityHeaderV2(FindQualityHeaderV2 findQualityHeader) throws Exception {
        try {

            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findQualityHeader.getCompanyCodeId(), findQualityHeader.getPlantId(), findQualityHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "PRE_OB_NO as preOutboundNo, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "PARTNER_CODE as partnerCode, "
                    + "PU_NO as pickupNumber, "
                    + "QC_NO as qualityInspectionNo, "
                    + "PICK_HE_NO as actualHeNo, "
                    + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                    + "STATUS_ID as statusId, "
                    + "QC_TO_QTY as qcToQty, "
                    + "QC_UOM as qcUom, "
                    + "MFR_PART as manufacturerPartNo, "
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
                    + "QC_CTD_BY as qualityCreatedBy, "
                    + "QC_CTD_ON as qualityCreatedOn, "
                    + "QC_CNF_BY as qualityConfirmedBy, "
                    + "QC_CNF_ON as qualityConfirmedOn, "
                    + "QC_UTD_BY as qualityUpdatedBy, "
                    + "QC_UTD_ON as qualityUpdatedOn, "
                    + "QC_REV_BY as qualityReversedBy, "
                    + "QC_REV_ON as qualityReversedOn, "

                    // V2 fields
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
                    + "MFR_NAME as manufacturerName, "
                    + "TARGET_BRANCH_CODE as targetBranchCode, "
                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "mrp as mrp, "
                    + "itm_typ as itemType, "
                    + "itm_grp as itemGroup, "
                    + "STR_NO as batchSerialNumber, "
                    + "CUSTOMER_ID as customerId, "
                    + "IMS_SALE_TYP_CODE as imsSaleTypeCode, "
                    + "BRAND as brand, "
                    + "CUSTOMER_NAME as customerName "
                    + "FROM tblqualityheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findQualityHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findQualityHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findQualityHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findQualityHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findQualityHeader.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "REF_FIELD_1", findQualityHeader.getSoType());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findQualityHeader.getPartnerCode());
            ConditionUtils.addCondition(conditions, "QC_NO", findQualityHeader.getQualityInspectionNo());
            ConditionUtils.addCondition(conditions, "PICK_HE_NO", findQualityHeader.getActualHeNo());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findQualityHeader.getStatusId());
            ConditionUtils.numericConditions(conditions, "OB_ORD_TYP_ID", findQualityHeader.getOutboundOrderTypeId());

            ConditionUtils.addDateCondition(conditions, "QC_CTD_ON", findQualityHeader.getStartQualityCreatedOn(), findQualityHeader.getEndQualityCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<QualityHeaderV4> qualityHeaderEncoder = Encoders.bean(QualityHeaderV4.class);
            Dataset<QualityHeaderV4> dataset = data.as(qualityHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<QualityHeaderV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find QualityHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findOutBoundHeader
     * @return
     * @throws Exception
     */
    public List<OutBoundHeaderV2> findOutBoundHeaderV2(FindOutBoundHeaderV2 findOutBoundHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findOutBoundHeader.getCompanyCodeId(), findOutBoundHeader.getPlantId(), findOutBoundHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "PRE_OB_NO as preOutboundNo, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "PARTNER_CODE as partnerCode, "
                    + "DLV_ORD_NO as deliveryOrderNo, "
                    + "REF_DOC_TYP as referenceDocumentType, "
                    + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                    + "STATUS_ID as statusId, "
                    + "REF_DOC_DATE as refDocDate, "
                    + "REQ_DEL_DATE as requiredDeliveryDate, "
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
                    + "DLV_CTD_BY as createdBy, "
                    + "DLV_CTD_ON as createdOn, "
                    + "DLV_CNF_BY as deliveryConfirmedBy, "
                    + "DLV_CNF_ON as deliveryConfirmedOn, "
                    + "DLV_UTD_BY as updatedBy, "
                    + "DLV_UTD_ON as updatedOn, "
                    + "DLV_REV_BY as reversedBy, "
                    + "DLV_REV_ON as reversedOn, "

                    // V2 fields
                    + "INVOICE_NO as invoiceNumber, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "SALES_ORDER_NUMBER as salesOrderNumber, "
                    + "SALES_INVOICE_NUMBER as salesInvoiceNumber, "
                    + "PICK_LIST_NUMBER as pickListNumber, "
                    + "INVOICE_DATE as invoiceDate, "
                    + "DELIVERY_TYPE as deliveryType, "
                    + "CUSTOMER_ID as customerId, "
                    + "CUSTOMER_NAME as customerName, "
                    + "ADDRESS as address, "
                    + "PHONE_NUMBER as phoneNumber, "
                    + "ALTERNATE_NO as alternateNo, "
                    + "STATUS as status, "
                    + "SUPPLIER_INVOICE_NO as supplierInvoiceNo, "
                    + "TOKEN_NUMBER as tokenNumber, "
                    + "FROM_BRANCH_CODE as fromBranchCode, "
                    + "IS_COMPLETED as isCompleted, "
                    + "IS_CANCELLED as isCancelled, "
                    + "M_UPDATED_ON as mUpdatedOn, "
                    + "PICK_LINE_COUNT as countOfPickedLine, "
                    + "SUM_PICK_QTY as sumOfPickedQty "
                    + "FROM tbloutboundheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findOutBoundHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findOutBoundHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findOutBoundHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findOutBoundHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findOutBoundHeader.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "REF_FIELD_1", findOutBoundHeader.getSoType());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findOutBoundHeader.getPartnerCode());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findOutBoundHeader.getStatusId());
            ConditionUtils.numericConditions(conditions, "OB_ORD_TYP_ID", findOutBoundHeader.getOutboundOrderTypeId());

            ConditionUtils.addDateCondition(conditions, "REQ_DEL_DATE", findOutBoundHeader.getStartRequiredDeliveryDate(), findOutBoundHeader.getEndRequiredDeliveryDate());
            ConditionUtils.addDateCondition(conditions, "DLV_CNF_ON", findOutBoundHeader.getStartDeliveryConfirmedOn(), findOutBoundHeader.getEndDeliveryConfirmedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<OutBoundHeaderV2> outBoundHeaderEncoder = Encoders.bean(OutBoundHeaderV2.class);
            Dataset<OutBoundHeaderV2> dataset = data.as(outBoundHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<OutBoundHeaderV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find OutBoundHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findQualityLine
     * @return
     * @throws Exception
     */
    public List<QualityLineV4> findQualityLineV2(FindQualityLineV2 findQualityLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findQualityLine.getCompanyCodeId(), findQualityLine.getPlantId(), findQualityLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT " +
                    "LANG_ID as languageId," +
                    "C_ID as companyCodeId," +
                    "PLANT_ID as plantId," +
                    "WH_ID as warehouseId," +
                    "PRE_OB_NO as preOutboundNo," +
                    "REF_DOC_NO as refDocNumber," +
                    "PARTNER_CODE as partnerCode," +
                    "OB_LINE_NO as lineNumber ," +
                    "QC_NO as qualityInspectionNo," +
                    "ITM_CODE as itemCode ," +
                    "PICK_HE_NO as actualHeNo ," +
                    "PICK_PACK_BARCODE as pickPackBarCode ," +
                    "OB_ORD_TYP_ID as outboundOrderTypeId ," +
                    "STATUS_ID as statusId ," +
                    "STCK_TYP_ID as stockTypeId ," +
                    "SP_ST_IND_ID as specialStockIndicatorId ," +
                    "ITEM_TEXT as description ," +
                    "MFR_PART as manufacturerPartNo ," +
                    "PACK_MT_NO as packingMaterialNo ," +
                    "VAR_ID as variantCode," +
                    "VAR_SUB_ID as variantSubCode ," +
                    "STR_NO as batchSerialNumber ," +
                    "QC_QTY as qualityQty ," +
                    "PICK_CNF_QTY as pickConfirmQty ," +
                    "QC_UOM as qualityConfirmUom ," +
                    "REJ_QTY as rejectQty ," +
                    "REJ_UOM as rejectUom ," +
                    "REF_FIELD_1 as referenceField1 ," +
                    "REF_FIELD_2 as referenceField2 ," +
                    "REF_FIELD_3 as referenceField3 ," +
                    "REF_FIELD_4 as referenceField4 ," +
                    "REF_FIELD_5 as referenceField5 ," +
                    "REF_FIELD_6 as referenceField6 ," +
                    "REF_FIELD_7 as referenceField7 ," +
                    "REF_FIELD_8 as referenceField8 ," +
                    "REF_FIELD_9 as referenceField9 ," +
                    "REF_FIELD_10 as referenceField10 ," +
                    "IS_DELETED as deletionIndicator ," +
                    "QC_CTD_BY as qualityCreatedBy ," +
                    "QC_CTD_ON as qualityCreatedOn ," +
                    "QC_CNF_BY as qualityConfirmedBy ," +
                    "QC_CNF_ON as qualityConfirmedOn ," +
                    "QC_UTD_BY as qualityUpdatedBy ," +
                    "QC_UTD_ON as qualityUpdatedOn ," +
                    "QC_REV_BY as qualityReversedBy ," +
                    "QC_REV_ON as qualityReversedOn ," +
                    "C_TEXT as companyDescription ," +
                    "PLANT_TEXT as plantDescription ," +
                    "WH_TEXT as warehouseDescription ," +
                    "STATUS_TEXT as statusDescription ," +
                    "PARTNER_ITEM_BARCODE as barcodeId ," +
                    "MIDDLEWARE_ID as middlewareId ," +
                    "MIDDLEWARE_HEADER_ID as middlewareHeaderId ," +
                    "MIDDLEWARE_TABLE as middlewareTable ," +
                    "REF_DOC_TYPE as referenceDocumentType ," +
                    "SALES_INVOICE_NUMBER as salesInvoiceNumber ," +
                    "SUPPLIER_INVOICE_NO as supplierInvoiceNo ," +
                    "SALES_ORDER_NUMBER as salesOrderNumber ," +
                    "MANUFACTURER_FULL_NAME as manufacturerFullName ," +
                    "PICK_LIST_NUMBER as pickListNumber ," +
                    "TOKEN_NUMBER as tokenNumber ," +
                    "MANUFACTURER_NAME as manufacturerName, " +
                    "ALT_UOM as alternateUom, " +
                    "NO_BAGS as noBags, " +
                    "BAG_SIZE as bagSize, " +
                    "CUSTOMER_ID as customerId, " +
                    "CUSTOMER_NAME as customerName " +
                    "FROM tblqualityline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findQualityLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findQualityLine.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findQualityLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findQualityLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findQualityLine.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findQualityLine.getPartnerCode());
            ConditionUtils.addCondition(conditions, "QC_NO", findQualityLine.getQualityInspectionNo());
            ConditionUtils.addCondition(conditions, "PRE_OB_NO", findQualityLine.getPreOutboundNo());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findQualityLine.getItemCode());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findQualityLine.getStatusId());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<QualityLineV4> qualityLineEncoder = Encoders.bean(QualityLineV4.class);
            Dataset<QualityLineV4> dataset = data.as(qualityLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<QualityLineV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find QualityLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findPreOutBoundLine
     * @return
     * @throws Exception
     */
    public List<PreOutBoundLineV4> findPreOutBoundLine(FindPreOutBoundLineV2 findPreOutBoundLine) throws Exception {
        try {

            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPreOutBoundLine.getCompanyCodeId(), findPreOutBoundLine.getPlantId(), findPreOutBoundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "PRE_OB_NO as preOutboundNo, "
                    + "PARTNER_CODE as partnerCode, "
                    + "OB_LINE_NO as lineNumber, "
                    + "ITM_CODE as itemCode, "
                    + "PARTNER_ITEM_BARCODE as barcodeId, "
                    + "OB_ORD_TYP_ID as outboundOrderTypeId, "
                    + "VAR_ID as variantCode, "
                    + "VAR_SUB_ID as variantSubCode, "
                    + "STATUS_ID as statusId, "
                    + "STCK_TYP_ID as stockTypeId, "
                    + "SP_ST_IND_ID as specialStockIndicatorId, "
                    + "TEXT as description, "
                    + "MFR_PART as manufacturerPartNo, "
                    + "HSN_CODE as hsnCode, "
                    + "ITM_BARCODE as itemBarcode, "
                    + "ORD_QTY as orderQty, "
                    + "ORD_UOM as orderUom, "
                    + "REQ_DEL_DATE as requiredDeliveryDate, "
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
                    + "PRE_OB_CTD_BY as createdBy, "
                    + "PRE_OB_CTD_ON as createdOn, "
                    + "PRE_OB_UTD_BY as countedBy, "
                    + "PRE_OB_UTD_ON as countedOn, "
                    + "PRE_OB_UTD_BY as updatedBy, "
                    + "PRE_OB_UTD_ON as updatedOn, "
                    + "MFR_CODE as manufacturerCode, "
                    + "MFR_NAME as manufacturerName, "
                    + "ORIGIN as origin, "
                    + "BRAND as brand, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "TOKEN_NUMBER as tokenNumber, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "REF_DOC_TYPE as referenceDocumentType, "
                    + "SALES_INVOICE_NUMBER as salesInvoiceNumber, "
                    + "SUPPLIER_INVOICE_NO as supplierInvoiceNo, "
                    + "SALES_ORDER_NUMBER as salesOrderNumber, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "PICK_LIST_NUMBER as pickListNumber, "
                    + "TRANSFER_ORDER_NO as transferOrderNo, "
                    + "RET_ORDER_NO as returnOrderNo, "
                    + "IS_COMPLETED as isCompleted, "
                    + "IS_CANCELLED as isCancelled, "
                    + "TARGET_BRANCH_CODE as targetBranchCode, "
                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "CUSTOMER_ID as customerId, "
                    + "QTY_IN_CASE as qtyInCase, "
                    + "QTY_IN_PIECE as qtyInPiece,"
                    + "CUSTOMER_NAME as customerName "
                    + "FROM tblpreoutboundline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findPreOutBoundLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findPreOutBoundLine.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findPreOutBoundLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findPreOutBoundLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findPreOutBoundLine.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "PRE_OB_NO", findPreOutBoundLine.getPreOutboundNo());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findPreOutBoundLine.getPartnerCode());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findPreOutBoundLine.getItemCode());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findPreOutBoundLine.getStatusId());
            ConditionUtils.numericConditions(conditions, "OB_LINE_NO", findPreOutBoundLine.getLineNumber());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<PreOutBoundLineV4> preOutBoundLineEncoder = Encoders.bean(PreOutBoundLineV4.class);
            Dataset<PreOutBoundLineV4> dataset = data.as(preOutBoundLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PreOutBoundLineV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find PreOutBoundLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchInventoryMovement
     * @return
     * @throws Exception
     */
    public List<InventoryMovementV2> findInventoryMovement(SearchInventoryMovementV2 searchInventoryMovement) throws Exception {
        try {
            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "MVT_TYP_ID as movementType, "
                    + "SUB_MVT_TYP_ID as submovementType, "
                    + "PAL_CODE as palletCode, "
                    + "CASE_CODE as caseCode, "
                    + "PACK_BARCODE as packBarcodes, "
                    + "ITM_CODE as itemCode, "
                    + "VAR_ID as variantCode, "
                    + "VAR_SUB_ID as variantSubCode, "
                    + "STR_NO as batchSerialNumber, "
                    + "MVT_DOC_NO as movementDocumentNo, "
                    + "MFR_PART as manufacturerName, "
                    + "ST_BIN as storageBin, "
                    + "STR_MTD as storageMethod, "
                    + "TEXT as description, "
                    + "STCK_TYP_ID as stockTypeId, "
                    + "SP_ST_IND_ID as specialStockIndicator, "
                    + "MVT_QTY_VAL as movementQtyValue, "
                    + "MVT_QTY as movementQty, "
                    + "BAL_OH_QTY as balanceOHQty, "
                    + "MVT_UOM as inventoryUom, "
                    + "REF_DOC_NO as refDocNumber, "
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
                    + "IM_CTD_BY as createdBy, "
                    + "IM_CTD_ON as createdOn, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "BARCODE_ID as barcodeId "
                    + "FROM tblinventorymovement";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchInventoryMovement.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchInventoryMovement.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchInventoryMovement.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchInventoryMovement.getLanguageId());
            ConditionUtils.addCondition(conditions, "ITM_CODE", searchInventoryMovement.getItemCode());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", searchInventoryMovement.getPackBarcodes());
            ConditionUtils.addCondition(conditions, "STR_NO", searchInventoryMovement.getBatchSerialNumber());
            ConditionUtils.addCondition(conditions, "MVT_DOC_NO", searchInventoryMovement.getMovementDocumentNo());

            ConditionUtils.numericConditions(conditions, "MVT_TYP_ID", searchInventoryMovement.getMovementType());
            ConditionUtils.numericConditions(conditions, "SUB_MVT_TYP_ID", searchInventoryMovement.getSubmovementType());

            ConditionUtils.addDateCondition(conditions, "IM_CTD_ON", searchInventoryMovement.getFromCreatedOn(), searchInventoryMovement.getToCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = spark.read()
                    .option("fetchSize", "10000")
                    .option("pushDownloadPredicate", true)
                    .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

//            data.show();
            Encoder<InventoryMovementV2> inventoryMovementEncoder = Encoders.bean(InventoryMovementV2.class);
            Dataset<InventoryMovementV2> dataset = data.as(inventoryMovementEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<InventoryMovementV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find InventoryMovement Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchInhouseTransferHeader
     * @return
     * @throws Exception
     */
    public List<InhouseTransferHeaderV4> findInhouseTransferHeader(SearchInhouseTransferHeader searchInhouseTransferHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchInhouseTransferHeader.getCompanyCodeId(), searchInhouseTransferHeader.getPlantId(), searchInhouseTransferHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "TR_NO as transferNumber, "
                    + "TR_TYP_ID as transferTypeId, "
                    + "TR_MTD as transferMethod, "
                    + "STATUS_ID as statusId, "
                    + "REMARK as remarks, "
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
                    + "IT_CTD_BY as createdBy, "
                    + "IT_CTD_ON as createdOn, "
                    + "IT_UTD_BY as updatedBy, "
                    + "IT_UTD_ON as updatedOn, "
                    + "MFR_NAME as manufacturerName "
                    + "STATUS_TEXT as statusDescription, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription "
                    + "FROM tblinhousetransferheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchInhouseTransferHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchInhouseTransferHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchInhouseTransferHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchInhouseTransferHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "TR_NO", searchInhouseTransferHeader.getTransferNumber());

            ConditionUtils.numericConditions(conditions, "TR_TYP_ID", searchInhouseTransferHeader.getTransferTypeId());

            ConditionUtils.addDateCondition(conditions, "IT_CTD_ON", searchInhouseTransferHeader.getStartCreatedOn(), searchInhouseTransferHeader.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<InhouseTransferHeaderV4> inhouseTransferHeaderEncoder = Encoders.bean(InhouseTransferHeaderV4.class);
            Dataset<InhouseTransferHeaderV4> dataset = data.as(inhouseTransferHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<InhouseTransferHeaderV4>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find InhouseTransferHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchInhouseTransferLine
     * @return
     * @throws Exception
     */
    public List<InhouseTransferLine> findInhouseTransferLines(SearchInhouseTransferLine searchInhouseTransferLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchInhouseTransferLine.getCompanyCodeId(), searchInhouseTransferLine.getPlantId(), searchInhouseTransferLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "TR_NO as transferNumber, "
                    + "SRCE_ITM_CODE as sourceItemCode, "
                    + "SRCE_STCK_TYP_ID as sourceStockTypeId, "
                    + "SRCE_ST_BIN as sourceStorageBin, "
                    + "TGT_ITM_CODE as targetItemCode, "
                    + "TGT_STCK_TYP_ID as targetStockTypeId, "
                    + "TGT_ST_BIN as targetStorageBin, "
                    + "TR_ORD_QTY as transferOrderQty, "
                    + "TR_CNF_QTY as transferConfirmedQty, "
                    + "TR_UOM as transferUom, "
                    + "PAL_CODE as palletCode, "
                    + "CASE_CODE as caseCode, "
                    + "PACK_BARCODE as packBarcodes, "
                    + "SP_ST_IND_ID as specialStockIndicatorId, "
                    + "STATUS_ID as statusId, "
                    + "BARCODE_ID as barcodeId, "
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
                    + "IT_CTD_BY as createdBy, "
                    + "IT_CTD_ON as createdOn, "
                    + "IT_CNF_BY as confirmedBy, "
                    + "IT_CNF_ON as confirmedOn, "
                    + "IT_UTD_BY as updatedBy, "
                    + "IT_UTD_ON as updatedOn, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "SRCE_STCK_TYP_TEXT as sourceStockTypeDescription, "
                    + "TGT_STCK_TYP_TEXT as targetStockTypeDescription, "
                    + "MFR_NAME as manufacturerName "
                    + "FROM tblinhousetransferline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchInhouseTransferLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchInhouseTransferLine.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchInhouseTransferLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchInhouseTransferLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "TR_NO", searchInhouseTransferLine.getTransferNumber());
            ConditionUtils.addCondition(conditions, "SRCE_ITM_CODE", searchInhouseTransferLine.getSourceItemCode());
            ConditionUtils.addCondition(conditions, "SRCE_ST_BIN", searchInhouseTransferLine.getSourceStorageBin());
            ConditionUtils.addCondition(conditions, "TGT_ITM_CODE", searchInhouseTransferLine.getTargetItemCode());
            ConditionUtils.addCondition(conditions, "TGT_ST_BIN", searchInhouseTransferLine.getTargetStorageBin());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", searchInhouseTransferLine.getPackBarcodes());
            ConditionUtils.addCondition(conditions, "REMARK", searchInhouseTransferLine.getRemarks());
            ConditionUtils.addCondition(conditions, "IT_CTD_BY", searchInhouseTransferLine.getCreatedBy());
            ConditionUtils.addCondition(conditions, "IT_CNF_BY", searchInhouseTransferLine.getConfirmedBy());
            ConditionUtils.addCondition(conditions, "REF_FIELD_1", searchInhouseTransferLine.getReferenceField1());

            ConditionUtils.numericConditions(conditions, "SRCE_STCK_TYP_ID", searchInhouseTransferLine.getSourceStockTypeId());
            ConditionUtils.numericConditions(conditions, "TGT_STCK_TYP_ID", searchInhouseTransferLine.getTargetStockTypeId());
//            ConditionUtils.numericConditions(conditions, "TR_CNF_QTY", searchInhouseTransferLine.getTransferConfirmedQty());
//            ConditionUtils.numericConditions(conditions, "AVAILABLE_QTY", searchInhouseTransferLine.getAvailableQty());
            ConditionUtils.numericConditions(conditions, "STATUS_ID", searchInhouseTransferLine.getStatusId());

            ConditionUtils.addDateCondition(conditions, "IT_CTD_ON", searchInhouseTransferLine.getStartCreatedOn(), searchInhouseTransferLine.getEndCreatedOn());
            ConditionUtils.addDateCondition(conditions, "IT_CNF_ON", searchInhouseTransferLine.getStartConfirmedOn(), searchInhouseTransferLine.getEndConfirmedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<InhouseTransferLine> inhouseTransferLineEncoder = Encoders.bean(InhouseTransferLine.class);
            Dataset<InhouseTransferLine> dataset = data.as(inhouseTransferLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<InhouseTransferLine>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find InhouseTransferLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findOutBoundReversal
     * @return
     * @throws Exception
     */
    public List<OutBoundReversalV2> findOutBoundReversal(FindOutBoundReversalV2 findOutBoundReversal) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findOutBoundReversal.getCompanyCodeId(), findOutBoundReversal.getPlantId(), findOutBoundReversal.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "OB_REVERSAL_NO as outboundReversalNo, "
                    + "REVERSAL_TYPE as reversalType, "
                    + "REF_DOC_NO as refDocNumber, "
                    + "PARTNER_CODE as partnerCode, "
                    + "ITM_CODE as itemCode,"
                    + "PACK_BARCODE as packBarcode," +
                    " REV_QTY as reversedQty," +
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
                    "OB_REV_BY as reversedBy," +
                    "OB_REV_ON as reversedOn, " +
                    //v2 fields
                    "C_TEXT as companyDescription, " +
                    "PLANT_TEXT as plantDescription, " +
                    "WH_TEXT as warehouseDescription, " +
                    "STATUS_TEXT as statusDescription, " +
                    "MIDDLEWARE_ID as middlewareId, " +
                    "MIDDLEWARE_TABLE as middlewareTable, " +
                    "REF_DOC_TYPE as referenceDocumentType, " +
                    "SALES_ORDER_NUMBER as salesOrderNumber, " +
                    "TARGET_BRANCH_CODE as targetBranchCode, " +
                    "SALES_INVOICE_NUMBER as salesInvoiceNumber," +
                    "SUPPLIER_INVOICE_NO as supplierInvoiceNo, " +
                    "PICK_LIST_NUMBER as pickListNumber, " +
                    "TOKEN_NUMBER as tokenNumber, " +
                    "MFR_NAME as manufacturerName, " +
                    "PARTNER_ITEM_BARCODE as barcodeId " +
                    "FROM tbloutboundreversal";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findOutBoundReversal.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findOutBoundReversal.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findOutBoundReversal.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findOutBoundReversal.getLanguageId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findOutBoundReversal.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findOutBoundReversal.getPartnerCode());
            ConditionUtils.addCondition(conditions, "OB_REVERSAL_NO", findOutBoundReversal.getOutboundReversalNo());
            ConditionUtils.addCondition(conditions, "REVERSAL_TYPE", findOutBoundReversal.getReversalType());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findOutBoundReversal.getPartnerCode());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findOutBoundReversal.getItemCode());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", findOutBoundReversal.getPackBarcode());
            ConditionUtils.addCondition(conditions, "OB_REV_BY", findOutBoundReversal.getReversedBy());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findOutBoundReversal.getStatusId());

            ConditionUtils.addDateCondition(conditions, "OB_REV_ON", findOutBoundReversal.getStartReversedOn(), findOutBoundReversal.getEndReversedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

//            data.show();
            Encoder<OutBoundReversalV2> outBoundReversalEncoder = Encoders.bean(OutBoundReversalV2.class);
            Dataset<OutBoundReversalV2> dataset = data.as(outBoundReversalEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<OutBoundReversalV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find OutBoundReversal Spark Exception : " + e.toString());
            throw e;
        }
    }


    public List<InventoryV4> findInventoryV3(FindInventoryV2 findInventory) throws ExecutionException, InterruptedException {
        try {
            String sqlQuery = "SELECT INV_ID, LANG_ID, C_ID, PLANT_ID, WH_ID, " +
                    "PAL_CODE, CASE_CODE, PACK_BARCODE, ITM_CODE, VAR_ID, VAR_SUB_ID, " +
                    "STR_NO, ST_BIN, STCK_TYP_ID, SP_ST_IND_ID, REF_ORD_NO, STR_MTD, " +
                    "BIN_CL_ID, TEXT, INV_QTY, ALLOC_QTY, INV_UOM, MFR_DATE, EXP_DATE, IS_DELETED, REF_FIELD_1, REF_FIELD_2, " +
                    "REF_FIELD_3, REF_FIELD_4, REF_FIELD_5, REF_FIELD_6, REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, " +
                    "IU_CTD_BY, IU_CTD_ON, UTD_BY, UTD_ON, MFR_CODE, BARCODE_ID, CBM, level_id, CBM_UNIT, CBM_PER_QTY, MFR_NAME, " +
                    "ORIGIN, BRAND, REF_DOC_NO, C_TEXT, PLANT_TEXT, WH_TEXT, STCK_TYP_TEXT, STATUS_TEXT, PARTNER_CODE, " +
                    "ITM_TYP_ID, ITM_TYP_TXT, BATCH_DATE, ALT_UOM, NO_BAGS, BAG_SIZE FROM tblinventory";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "LANG_ID", findInventory.getLanguageId());
            ConditionUtils.addCondition(conditions, "C_ID", findInventory.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findInventory.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findInventory.getWarehouseId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findInventory.getReferenceDocumentNo());
            ConditionUtils.addCondition(conditions, "BARCODE_ID", findInventory.getBarcodeId());
            ConditionUtils.addCondition(conditions, "MFR_CODE", findInventory.getManufacturerCode());
            ConditionUtils.addCondition(conditions, "MFR_NAME", findInventory.getManufacturerName());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", findInventory.getPackBarcodes());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findInventory.getItemCode());
            ConditionUtils.addCondition(conditions, "ST_BIN", findInventory.getStorageBin());
            ConditionUtils.addCondition(conditions, "TEXT", findInventory.getDescription());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findInventory.getPartnerCode());
            ConditionUtils.addCondition(conditions, "REF_FIELD_10", findInventory.getStorageSectionId());
            ConditionUtils.addCondition(conditions, "level_id", findInventory.getLevelId());
            ConditionUtils.addCondition(conditions, "alt_uom", findInventory.getAltUom());
            if (findInventory.getStockTypeId() != null) {
                ConditionUtils.numericConditions(conditions, "STCK_TYP_ID", findInventory.getStockTypeId());
            }
            if (findInventory.getSpecialStockIndicatorId() != null) {
                ConditionUtils.numericConditions(conditions, "SP_ST_IND_ID", findInventory.getSpecialStockIndicatorId());
            }
            if (findInventory.getBinClassId() != null) {
                ConditionUtils.numericConditions(conditions, "BIN_CL_ID", findInventory.getBinClassId());
            }
            if (findInventory.getItemTypeId() != null) {
                ConditionUtils.numericConditions(conditions, "ITM_TYP_ID", findInventory.getItemTypeId());
            }
            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id)";
            }

            Dataset<Row> data = spark.read()
                    .option("fetchSize", "10000")
                    .option("pushDownloadPredicate", true)
                    .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

            Encoder<InventoryV4> inventoryV2CoreEncoder = Encoders.bean(InventoryV4.class);
            Dataset<InventoryV4> inventoryV2CoreDataset = data.as(inventoryV2CoreEncoder);
//            List<InventoryV4> result = inventoryV2CoreDataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<InventoryV4>> future = executor.submit(inventoryV2CoreDataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
//            return result;
        } catch (Exception e) {
            log.error("Find Inventory Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findInventory
     * @return
     */
    public List<InventoryV4> findInventoryV2(FindInventoryV2 findInventory) throws ExecutionException, InterruptedException {
        try {

            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findInventory.getCompanyCodeId(), findInventory.getPlantId(), findInventory.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT INV_ID, LANG_ID, C_ID, PLANT_ID, WH_ID, " +
                    "PAL_CODE, CASE_CODE, PACK_BARCODE, ITM_CODE, VAR_ID, VAR_SUB_ID, " +
                    "STR_NO, ST_BIN, STCK_TYP_ID, SP_ST_IND_ID, REF_ORD_NO, STR_MTD, " +
                    "BIN_CL_ID, TEXT, INV_QTY, ALLOC_QTY, INV_UOM, MFR_DATE, EXP_DATE, IS_DELETED, REF_FIELD_1, REF_FIELD_2, " +
                    "REF_FIELD_3, REF_FIELD_4, REF_FIELD_5, REF_FIELD_6, REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, " +
                    "IU_CTD_BY, IU_CTD_ON, UTD_BY, UTD_ON, MFR_CODE, BARCODE_ID, CBM, level_id, CBM_UNIT, CBM_PER_QTY, MFR_NAME, " +
                    "ORIGIN, BRAND, REF_DOC_NO, C_TEXT, PLANT_TEXT, WH_TEXT, STCK_TYP_TEXT, COLOR, STATUS_TEXT, PARTNER_CODE, " +
                    "ITM_TYP_ID, ITM_TYP_TXT, BATCH_DATE, ALT_UOM, NO_BAGS, BAG_SIZE, CASE_QTY, PIECE_QTY, CRATE_QTY, QTY_IN_CASE, " +
                    "QTY_IN_PIECE, QTY_IN_CREATE, VEHICLE_NO, VEHICLE_REPORTING_DATE, VEHICLE_UNLOADING_DATE, " +
                    "SELF_LIFE, REMAINING_DAYS, REMAINING_SELF_LIFE_PERCENTAGE, RECEIVINGVARIANCE, PALLET_ID, LOOSE_PACK, MRP, TPL_PARTNER_ID, PRICE_SEGMENT, MATERIAL_NO " +
                    "FROM tblinventory";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "LANG_ID", findInventory.getLanguageId());
            ConditionUtils.addCondition(conditions, "C_ID", findInventory.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findInventory.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findInventory.getWarehouseId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findInventory.getReferenceDocumentNo());
            ConditionUtils.addCondition(conditions, "BARCODE_ID", findInventory.getBarcodeId());
            ConditionUtils.addCondition(conditions, "MFR_CODE", findInventory.getManufacturerCode());
            ConditionUtils.addCondition(conditions, "MFR_NAME", findInventory.getManufacturerName());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", findInventory.getPackBarcodes());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findInventory.getItemCode());
            ConditionUtils.addCondition(conditions, "ST_BIN", findInventory.getStorageBin());
            ConditionUtils.addCondition(conditions, "TEXT", findInventory.getDescription());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findInventory.getPartnerCode());
            ConditionUtils.addCondition(conditions, "REF_FIELD_10", findInventory.getStorageSectionId());
            ConditionUtils.addCondition(conditions, "REF_FIELD_7", findInventory.getReferenceField7());
            ConditionUtils.addCondition(conditions, "level_id", findInventory.getLevelId());
            ConditionUtils.addCondition(conditions, "alt_uom", findInventory.getAltUom());
            if (findInventory.getStockTypeId() != null) {
                ConditionUtils.numericConditions(conditions, "STCK_TYP_ID", findInventory.getStockTypeId());
            }
            if (findInventory.getSpecialStockIndicatorId() != null) {
                ConditionUtils.numericConditions(conditions, "SP_ST_IND_ID", findInventory.getSpecialStockIndicatorId());
            }
            if (findInventory.getBinClassId() != null) {
                ConditionUtils.numericConditions(conditions, "BIN_CL_ID", findInventory.getBinClassId());
            }
            if (findInventory.getItemTypeId() != null) {
                ConditionUtils.numericConditions(conditions, "ITM_TYP_ID", findInventory.getItemTypeId());
            }
//            if (!conditions.isEmpty()) {
//                sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
//            } else {
//                sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id)";
//            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND inv_id in (select max(inv_id) from tblinventory WITH (NOLOCK) where is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory WITH (NOLOCK) where is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,pal_code,barcode_id,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                } data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,pal_code,barcode_id,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                } data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            Encoder<InventoryV4> inventoryV2CoreEncoder = Encoders.bean(InventoryV4.class);
            Dataset<InventoryV4> inventoryV2CoreDataset = data.as(inventoryV2CoreEncoder);
//            List<InventoryV4> result = inventoryV2CoreDataset.collectAsList();
//
//            return result;

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<InventoryV4>> future = executor.submit(inventoryV2CoreDataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find Inventory Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findPeriodicHeader
     * @return
     * @throws Exception
     */
    public List<PeriodicHeaderV2> findPeriodicHeaderV2(FindPeriodicHeaderV2 findPeriodicHeader) throws Exception {
        try {

            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPeriodicHeader.getCompanyCodeId(), findPeriodicHeader.getPlantId(), findPeriodicHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCode, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "CC_TYP_ID as cycleCountTypeId, "
                    + "CC_NO as cycleCountNo, "
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
                    + "CC_CTD_BY as createdBy, "
                    + "CC_CTD_ON as createdOn, "
                    + "CC_CNT_BY as countedBy, "
                    + "CC_CNT_ON as countedOn, "
                    + "CC_CNF_BY as confirmedBy, "
                    + "CC_CNF_ON as confirmedOn, "

                    // V2 fields
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "REF_DOC_TYPE as referenceDocumentType, "
                    + "REF_CC_NO as referenceCycleCountNo "
                    + "FROM tblperiodicheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findPeriodicHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findPeriodicHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findPeriodicHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findPeriodicHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "CC_NO", findPeriodicHeader.getCycleCountNo());
            ConditionUtils.addCondition(conditions, "CC_CTD_BY", findPeriodicHeader.getCreatedBy());

            ConditionUtils.numericConditions(conditions, "CC_TYP_ID", findPeriodicHeader.getCycleCountTypeId());
            ConditionUtils.numericConditions(conditions, "STATUS_ID", findPeriodicHeader.getHeaderStatusId());

            ConditionUtils.addDateCondition(conditions, "CC_CTD_ON", findPeriodicHeader.getStartCreatedOn(), findPeriodicHeader.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<PeriodicHeaderV2> periodicHeaderEncoder = Encoders.bean(PeriodicHeaderV2.class);
            Dataset<PeriodicHeaderV2> dataset = data.as(periodicHeaderEncoder);
//            return dataset.collectAsList();
            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PeriodicHeaderV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }


        } catch (Exception e) {
            log.error("Find PeriodicHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findPeriodicLine
     * @return
     * @throws Exception
     */
    public List<PeriodicLineV2> findPeriodicLine(FindPeriodicLineV2 findPeriodicLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPeriodicLine.getCompanyCode(), findPeriodicLine.getPlantId(), findPeriodicLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCode, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "CC_NO as cycleCountNo, "
                    + "ST_BIN as storageBin, "
                    + "ITM_CODE as itemCode, "
                    + "PACK_BARCODE as packBarcodes, "
                    + "VAR_ID as variantCode, "
                    + "VAR_SUB_ID as variantSubCode, "
                    + "STR_NO as batchSerialNumber, "
                    + "STCK_TYP_ID as stockTypeId, "
                    + "SP_ST_IND_ID as specialStockIndicator, "
                    + "INV_QTY as inventoryQuantity, "
                    + "INV_UOM as inventoryUom, "
                    + "CTD_QTY as countedQty, "
                    + "VAR_QTY as varianceQty, "
                    + "COUNTER_ID as cycleCounterId, "
                    + "COUNTER_NM as cycleCounterName, "
                    + "STATUS_ID as statusId, "
                    + "ACTION as cycleCountAction, "
                    + "REF_NO as referenceNo, "
                    + "APP_PROCESS_ID as approvalProcessId, "
                    + "APP_LVL as approvalLevel, "
                    + "APP_CODE as approverCode, "
                    + "APP_STATUS as approvalStatus, "
                    + "REMARK as remarks, "
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
                    + "CC_CTD_BY as createdBy, "
                    + "CC_CTD_ON as createdOn, "
                    + "CC_CNF_BY as confirmedBy, "
                    + "CC_CNF_ON as confirmedOn, "
                    + "CC_CNT_BY as countedBy, "
                    + "CC_CNT_ON as countedOn, "

                    // V2 fields
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MFR_NAME as manufacturerName, "
                    + "PARTNER_ITEM_BARCODE as barcodeId, "
                    + "ITM_TEXT as itemDesc, "
                    + "ST_SEC_ID as storageSectionId, "
                    + "MFR_PART as manufacturerPartNo, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "MFR_CODE as manufacturerCode, "
                    + "REF_DOC_TYPE as referenceDocumentType, "
                    + "FROZENQTY as frozenQty, "
                    + "IB_QTY as inboundQuantity, "
                    + "OB_QTY as outboundQuantity, "

                    + "ALT_UOM as alternateUom, "
                    + "NO_BAGS as noBags, "
                    + "BAG_SIZE as bagSize, "
                    + "SC_LINE_NO as [lineNo], "
                    + "MRP as mrp, "
                    + "ITM_TYP as itemType, "
                    + "ITM_GRP as itemGroup, "
                    + "SIZE as size, "
                    + "BRAND as brand, "

                    + "FIRST_CTD_QTY as firstCountedQty, "
                    + "SECOND_CTD_QTY as secondCountedQty "
                    + "FROM tblperiodicline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findPeriodicLine.getCompanyCode());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findPeriodicLine.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findPeriodicLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findPeriodicLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "CC_NO", findPeriodicLine.getCycleCountNo());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findPeriodicLine.getItemCode());
            ConditionUtils.addCondition(conditions, "COUNTER_ID", findPeriodicLine.getCycleCounterId());
            ConditionUtils.addCondition(conditions, "ST_BIN", findPeriodicLine.getStorageBin());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", findPeriodicLine.getPackBarcodes());
            ConditionUtils.addCondition(conditions, "REF_FIELD_9", findPeriodicLine.getReferenceField9());
            ConditionUtils.addCondition(conditions, "REF_FIELD_10", findPeriodicLine.getReferenceField10());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", findPeriodicLine.getLineStatusId());
            ConditionUtils.numericConditions(conditions, "STCK_TYP_ID", findPeriodicLine.getStockTypeId());

            ConditionUtils.addDateCondition(conditions, "CC_CTD_ON", findPeriodicLine.getStartCreatedOn(), findPeriodicLine.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }


            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<PeriodicLineV2> periodicLineEncoder = Encoders.bean(PeriodicLineV2.class);
            Dataset<PeriodicLineV2> dataset = data.as(periodicLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PeriodicLineV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find PeriodicLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchPerpetualHeader
     * @return
     * @throws Exception
     */
    public List<PerpetualHeader> findPerpetualHeader(SearchPerpetualHeaderV2 searchPerpetualHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchPerpetualHeader.getCompanyCodeId(), searchPerpetualHeader.getPlantId(), searchPerpetualHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId,"
                    + "C_ID as companyCodeId,"
                    + "PLANT_ID as plantId,"
                    + "WH_ID as warehouseId,"
                    + "CC_TYP_ID as cycleCountTypeId,"
                    + "CC_NO as cycleCountNo,"
                    + "MVT_TYP_ID as movementTypeId,"
                    + "SUB_MVT_TYP_ID as subMovementTypeId,"
                    + "STATUS_ID as statusId,"
                    + "REF_FIELD_1 as referenceField1,"
                    + "REF_FIELD_2 as referenceField2,"
                    + "REF_FIELD_3 as referenceField3,"
                    + "REF_FIELD_4 as referenceField4,"
                    + "REF_FIELD_5 as referenceField5,"
                    + "REF_FIELD_6 as referenceField6,"
                    + "REF_FIELD_7 as referenceField7,"
                    + "REF_FIELD_8 as referenceField8,"
                    + "REF_FIELD_9 as referenceField9,"
                    + "REF_FIELD_10 as referenceField10,"
                    + "IS_DELETED as deletionIndicator,"
                    + "CC_CTD_BY as createdBy,"
                    + "CC_CTD_ON as createdOn,"
                    + "CC_CNT_BY as countedBy,"
                    + "CC_CNT_ON as countedOn,"
                    + "CC_CNF_BY as confirmedBy,"
                    + "CC_CNF_ON as confirmedOn,"
                    + "C_TEXT as companyDescription,"
                    + "PLANT_TEXT as plantDescription,"
                    + "WH_TEXT as warehouseDescription,"
                    + "STATUS_TEXT as statusDescription,"
                    + "MIDDLEWARE_ID as middlewareId,"
                    + "MIDDLEWARE_TABLE as middlewareTable,"
                    + "REF_DOC_TYPE as referenceDocumentType,"
                    + "REF_CC_NO as referenceCycleCountNo "
                    + "FROM tblperpetualheader";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchPerpetualHeader.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchPerpetualHeader.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchPerpetualHeader.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchPerpetualHeader.getLanguageId());
            ConditionUtils.addCondition(conditions, "CC_NO", searchPerpetualHeader.getCycleCountNo());
            ConditionUtils.addCondition(conditions, "CC_CTD_BY", searchPerpetualHeader.getCreatedBy());

            ConditionUtils.numericConditions(conditions, "SUB_MVT_TYP_ID", searchPerpetualHeader.getSubMovementTypeId());
            ConditionUtils.numericConditions(conditions, "MVT_TYP_ID", searchPerpetualHeader.getMovementTypeId());
            ConditionUtils.numericConditions(conditions, "CC_TYP_ID", searchPerpetualHeader.getCycleCountTypeId());
            ConditionUtils.numericConditions(conditions, "STATUS_ID", searchPerpetualHeader.getStatusId());

            ConditionUtils.addDateCondition(conditions, "CC_CTD_ON", searchPerpetualHeader.getStartCreatedOn(), searchPerpetualHeader.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<PerpetualHeader> perpetualHeaderEncoder = Encoders.bean(PerpetualHeader.class);
            Dataset<PerpetualHeader> dataset = data.as(perpetualHeaderEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PerpetualHeader>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find PeriodicHeader Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchPerpetualLine
     * @return
     * @throws Exception
     */
    public List<PerpetualLineV2> findPerpetualLine(SearchPerpetualLineV2 searchPerpetualLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchPerpetualLine.getCompanyCodeId(), searchPerpetualLine.getPlantId(), searchPerpetualLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "CC_NO as cycleCountNo, "
                    + "ST_BIN as storageBin, "
                    + "ITM_CODE as itemCode, "
                    + "PACK_BARCODE as packBarcodes, "
                    + "ITM_DESC as itemDesc, "
                    + "MFR_PART as manufacturerPartNo, "
                    + "VAR_ID as variantCode, "
                    + "VAR_SUB_ID as variantSubCode, "
                    + "STR_NO as batchSerialNumber, "
                    + "STCK_TYP_ID as stockTypeId, "
                    + "SP_ST_IND_ID as specialStockIndicator, "
                    + "ST_SEC_ID as storageSectionId, "
                    + "INV_QTY as inventoryQuantity, "
                    + "INV_UOM as inventoryUom, "
                    + "CTD_QTY as countedQty, "
                    + "VAR_QTY as varianceQty, "
                    + "COUNTER_ID as cycleCounterId, "
                    + "COUNTER_NM as cycleCounterName, "
                    + "STATUS_ID as statusId, "
                    + "ACTION as cycleCountAction, "
                    + "REF_NO as referenceNo, "
                    + "APP_PROCESS_ID as approvalProcessId, "
                    + "APP_LVL as approvalLevel, "
                    + "APP_CODE as approverCode, "
                    + "APP_STATUS as approvalStatus, "
                    + "REMARK as remarks, "
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
                    + "CC_CTD_BY as createdBy, "
                    + "CC_CTD_ON as createdOn, "
                    + "CC_CNF_BY as confirmedBy, "
                    + "CC_CNF_ON as confirmedOn, "
                    + "CC_CNT_BY as countedBy, "
                    + "CC_CNT_ON as countedOn, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "STATUS_TEXT as statusDescription, "
                    + "MFR_NAME as manufacturerName, "
                    + "MFR_CODE as manufacturerCode, "
                    + "PARTNER_ITEM_BARCODE as barcodeId, "
                    + "MIDDLEWARE_ID as middlewareId, "
                    + "MIDDLEWARE_HEADER_ID as middlewareHeaderId, "
                    + "MIDDLEWARE_TABLE as middlewareTable, "
                    + "MANUFACTURER_FULL_NAME as manufacturerFullName, "
                    + "REF_DOC_TYPE as referenceDocumentType, "
                    + "FROZEN_QTY as frozenQty, "
                    + "IB_QTY as inboundQuantity, "
                    + "OB_QTY as outboundQuantity, "
                    + "FIRST_CTD_QTY as firstCountedQty, "
                    + "SECOND_CTD_QTY as secondCountedQty "
                    + "FROM tblperpetualline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchPerpetualLine.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchPerpetualLine.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchPerpetualLine.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchPerpetualLine.getLanguageId());
            ConditionUtils.addCondition(conditions, "CC_NO", searchPerpetualLine.getCycleCountNo());
            ConditionUtils.addCondition(conditions, "ITM_CODE", searchPerpetualLine.getItemCode());
            ConditionUtils.addCondition(conditions, "COUNTER_ID", searchPerpetualLine.getCycleCounterId());
            ConditionUtils.addCondition(conditions, "ST_BIN", searchPerpetualLine.getStorageBin());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", searchPerpetualLine.getPackBarcodes());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", searchPerpetualLine.getLineStatusId());

            ConditionUtils.addDateCondition(conditions, "CC_CTD_ON", searchPerpetualLine.getStartCreatedOn(), searchPerpetualLine.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<PerpetualLineV2> perpetualLineEncoder = Encoders.bean(PerpetualLineV2.class);
            Dataset<PerpetualLineV2> dataset = data.as(perpetualLineEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<PerpetualLineV2>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find PeriodicLine Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findStockReport
     * @return
     * @throws Exception
     */
    public List<StockReport> getStockReport(FindStockReport findStockReport) throws Exception {
        try {
            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "ITM_CODE as itemCode, "
                    + "MFR_CODE as manufacturerSKU, "
                    + "INV_QTY as onHandQty, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "BARCODE_ID as barcodeId, "
                    + "MFR_NAME as manufacturerName "
                    + "FROM tblinventory";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", findStockReport.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findStockReport.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findStockReport.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", findStockReport.getLanguageId());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findStockReport.getItemCode());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = spark.read()
                    .option("fetchSize", "10000")
                    .option("pushDownloadPredicate", true)
                    .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

//            data.show();
            Encoder<StockReport> stockReportEncoder = Encoders.bean(StockReport.class);
            Dataset<StockReport> dataset = data.as(stockReportEncoder);
            return dataset.collectAsList();
        } catch (Exception e) {
            log.error("Find StockReport Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param searchOrderStatusReport
     * @return
     * @throws Exception
     */
    public List<OrderStatusReport> findOrderStatusReport(SearchOrderStatusReport searchOrderStatusReport) throws Exception {
        try {
            String sqlQuery = "SELECT "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "DLV_CNF_ON as deliveryConfirmedOn, "
                    + "REF_DOC_NO as soNumber, "
                    + "DLV_ORD_NO as doNumber, "
                    + "PARTNER_CODE as customerCode, "
                    + "PARTNER_NM as customerName, "
                    + "ITM_CODE as sku, "
                    + "ITEM_TEXT as skuDescription, "
                    + "ORD_QTY as orderedQty, "
                    + "DLV_QTY as deliveredQty, "
                    + "REF_DOC_DATE as orderReceivedDate, "
                    + "REQ_DEL_DATE as expectedDeliveryDate, "
                    // + " as percentageOfDelivered, "// <-------------------
                    + "STATUS_ID as statusId, "
                    + "ORDER_TYPE as orderType, "
                    + "C_TEXT as companyDescription, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "ITM_CODE as itemCode, "
                    + "REF_DOC_NO as refDocNumber"
                    + "FROM tbloutboundline";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchOrderStatusReport.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchOrderStatusReport.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchOrderStatusReport.getWarehouseId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchOrderStatusReport.getLanguageId());
            ConditionUtils.addCondition(conditions, "ITM_CODE", searchOrderStatusReport.getItemCode());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", searchOrderStatusReport.getPartnerCode());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", searchOrderStatusReport.getRefDocNumber());
            ConditionUtils.addCondition(conditions, "REF_FIELD_1", searchOrderStatusReport.getOrderType());

            ConditionUtils.numericConditions(conditions, "STATUS_ID", searchOrderStatusReport.getStatusId());

            ConditionUtils.addDateCondition(conditions, "DLV_CNF_ON", searchOrderStatusReport.getFromDeliveryDate(), searchOrderStatusReport.getToDeliveryDate());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = spark.read()
                    .option("fetchSize", "10000")
                    .option("pushDownloadPredicate", true)
                    .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

//            data.show();
            Encoder<OrderStatusReport> orderStatusReportEncoder = Encoders.bean(OrderStatusReport.class);
            Dataset<OrderStatusReport> dataset = data.as(orderStatusReportEncoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<OrderStatusReport>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find StockReport Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findInboundLineV2
     * @return
     */
    public List<InboundLineV4> getInboundLine(FindInboundLine findInboundLineV2) {
        String sqlQuery = "SELECT C_TEXT, PLANT_TEXT, WH_TEXT, STATUS_TEXT, SOURCE_BRANCH_CODE, REF_DOC_NO, IB_LINE_NO, " +
                "REF_DOC_TYPE, MFR_NAME, ITM_CODE, TEXT, ORD_QTY, ACCEPT_QTY, DAMAGE_QTY, VAR_QTY, CTD_ON, IB_CNF_ON, " +
                "ALT_UOM, NO_BAGS, BAG_SIZE FROM tblinboundline ";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "WH_ID", findInboundLineV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "C_ID", findInboundLineV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "PLANT_ID", findInboundLineV2.getPlantId());
        ConditionUtils.addCondition(conditions, "LANG_ID", findInboundLineV2.getLanguageId());
        ConditionUtils.addCondition(conditions, "ITM_CODE", findInboundLineV2.getItemCode());
        ConditionUtils.addCondition(conditions, "MFR_PART", findInboundLineV2.getManufacturerPartNo());
        ConditionUtils.addCondition(conditions, "MFR_NAME", findInboundLineV2.getManufactureName());
        ConditionUtils.addCondition(conditions, "SOURCE_BRANCH_CODE", findInboundLineV2.getSourceBranchCode());
        ConditionUtils.addCondition(conditions, "SOURCE_COMPANY_CODE", findInboundLineV2.getSourceCompanyCode());
        ConditionUtils.addCondition(conditions, "REF_DOC_NO", findInboundLineV2.getRefDocNumber());
        ConditionUtils.addCondition(conditions, "REF_FIELD_1", findInboundLineV2.getReferenceField1());
        ConditionUtils.addDateCondition(conditions, "IB_CNF_ON", findInboundLineV2.getStartConfirmedOn(), findInboundLineV2.getEndConfirmedOn());
        ConditionUtils.addDateCondition(conditions, "CTD_ON", findInboundLineV2.getStartCreatedOn(), findInboundLineV2.getEndCreatedOn());
        ConditionUtils.numericConditions(conditions, "IB_ORD_TYP_ID", findInboundLineV2.getInboundOrderTypeId());
        ConditionUtils.numericConditions(conditions, "STATUS_ID", findInboundLineV2.getStatusId());

        if (!conditions.isEmpty()) {
            sqlQuery += " WHERE IS_DELETED = 0 AND " + String.join(" AND ", conditions);
        } else {
            sqlQuery += " WHERE IS_DELETED =0 ";
        }

        Dataset<Row> data = spark.read()
                .option("fetchSize", "10000")
                .option("pushDownloadPredicate", true)
                .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

        Encoder<InboundLineV4> inboundLineV3Encoder = Encoders.bean(InboundLineV4.class);
        Dataset<InboundLineV4> dsJntwebhook = data.as(inboundLineV3Encoder);
//        return dsJntwebhook.collectAsList();
        // Run collect asynchronously
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<InboundLineV4>> future = executor.submit(dsJntwebhook::collectAsList);

        try {
            return future.get(120, TimeUnit.SECONDS);  // Optional timeout
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            log.error("Spark job timeout!");
            future.cancel(true);
            throw new RuntimeException("Spark job took too long to complete");
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * @param searchPickupLineV2
     * @return
     */
    public List<PickUpLineV4> getPickupLine(SearchPickupLine searchPickupLineV2) {
        String sqlQuery = "SELECT C_TEXT, PLANT_TEXT, WH_TEXT, STATUS_TEXT, REF_DOC_TYPE, REF_DOC_NO, PU_NO, " +
                " OB_LINE_NO, MFR_NAME, ITM_CODE, ITEM_TEXT, PARTNER_ITEM_BARCODE, PICK_ST_BIN, LEVEL_ID, ASS_PICKER_ID, " +
                " PARTNER_CODE, ALLOC_QTY,PICK_CNF_QTY, VAR_QTY, PICK_CTD_ON, PICK_UTD_ON, IMS_SALE_TYP_CODE, " +
                " ALT_UOM, NO_BAGS, BAG_SIZE FROM tblpickupline ";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "LANG_ID", searchPickupLineV2.getLanguageId());

        ConditionUtils.addCondition(conditions, "C_ID", searchPickupLineV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "PLANT_ID", searchPickupLineV2.getPlantId());
        ConditionUtils.addCondition(conditions, "WH_ID", searchPickupLineV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "ITM_CODE", searchPickupLineV2.getItemCode());
        ConditionUtils.addCondition(conditions, "PU_NO", searchPickupLineV2.getPickupNumber());
        ConditionUtils.addCondition(conditions, "ASS_PICKER_ID", searchPickupLineV2.getAssignedPickerId());
        ConditionUtils.addCondition(conditions, "PICK_HE_NO", searchPickupLineV2.getActualHeNo());
        ConditionUtils.addCondition(conditions, "LEVEL_ID", searchPickupLineV2.getLevelId());
        ConditionUtils.addCondition(conditions, "PARTNER_CODE", searchPickupLineV2.getPartnerCode());
        ConditionUtils.addCondition(conditions, "PICK_ST_BIN", searchPickupLineV2.getPickedStorageBin());
        ConditionUtils.addCondition(conditions, "PICK_PACK_BARCODE", searchPickupLineV2.getPickedPackCode());

        ConditionUtils.addCondition(conditions, "PRE_OB_NO", searchPickupLineV2.getPreOutboundNo());
        ConditionUtils.addCondition(conditions, "REF_DOC_NO", searchPickupLineV2.getRefDocNumber());
        ConditionUtils.numericConditions(conditions, "STATUS_ID", searchPickupLineV2.getStatusId());
        ConditionUtils.numericConditions(conditions, "OB_LINE_NO", searchPickupLineV2.getLineNumber());
        ConditionUtils.addDateCondition(conditions, "PICK_CNF_ON", searchPickupLineV2.getFromPickConfirmedOn(), searchPickupLineV2.getToPickConfirmedOn());

        if ((!conditions.isEmpty())) {
            sqlQuery += " WHERE IS_DELETED = 0 AND " + String.join(" AND ", conditions);
        } else {
            sqlQuery += " WHERE IS_DELETED = 0 ";
        }

        Dataset<Row> data = spark.read()
                .option("fetchSize", "10000")
                .option("pushDownloadPredicate", true)
                .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);

        Encoder<PickUpLineV4> pickUpLineV3Encoder = Encoders.bean(PickUpLineV4.class);
        Dataset<PickUpLineV4> dataSet = data.as(pickUpLineV3Encoder);
        dataSet.show();

        // Run collect asynchronously
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<PickUpLineV4>> future = executor.submit(dataSet::collectAsList);

        try {
            return future.get(120, TimeUnit.SECONDS);  // Optional timeout
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            log.error("Spark job timeout!");
            future.cancel(true);
            throw new RuntimeException("Spark job took too long to complete");
        } finally {
            executor.shutdownNow();
        }
//        return dataSet.collectAsList();
    }

    /**
     * @param findOutboundLineV2
     * @return
     */
    public List<OutboundLineV2> getOutBoundLineV3(FindOutboundLineV2 findOutboundLineV2) {
        String joinQueryData = "SELECT ob.PLANT_TEXT, ob.WH_TEXT, ob.REF_DOC_NO, ob.DLV_QTY, ob.REF_DOC_TYPE, ob.SALES_ORDER_NUMBER, ob.TARGET_BRANCH_CODE, \n " +
                "ob.MFR_NAME, ob.ITM_CODE, ob.ITEM_TEXT, ob.SALES_INVOICE_NUMBER, ob.ORD_QTY, ob.STATUS_TEXT, ob.DLV_CTD_ON, COALESCE(qc.QC_QTY, 0) AS QC_QTY, \n " +
                " COALESCE(pick.PICK_CNF_QTY, 0) AS PICK_CNF_QTY, IMS_SALE_TYP_CODE FROM tbloutboundline ob LEFT JOIN (SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, \n " +
                " REF_DOC_NO, PRE_OB_NO, OB_LINE_NO, ITM_CODE, SUM(QC_QTY) AS QC_QTY FROM tblqualityline GROUP BY C_ID, PLANT_ID, LANG_ID, WH_ID, \n " +
                " REF_DOC_NO, PRE_OB_NO, OB_LINE_NO, ITM_CODE) qc ON ob.C_ID = qc.C_ID AND ob.PLANT_ID = qc.PLANT_ID AND ob.LANG_ID = qc.LANG_ID AND \n " +
                " ob.WH_ID = qc.WH_ID AND ob.REF_DOC_NO = qc.REF_DOC_NO AND ob.OB_LINE_NO = qc.OB_LINE_NO AND ob.ITM_CODE = qc.ITM_CODE \n " +
                " LEFT JOIN ( SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_OB_NO, OB_LINE_NO, ITM_CODE, SUM(PICK_CNF_QTY) AS PICK_CNF_QTY \n " +
                " FROM tblpickupline \n " +
                " WHERE IS_DELETED = 0 \n " +
                " GROUP BY C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_OB_NO, OB_LINE_NO, ITM_CODE ) pick ON \n " +
                " ob.C_ID = pick.C_ID AND ob.PLANT_ID = pick.PLANT_ID AND ob.LANG_ID = pick.LANG_ID AND \n " +
                " ob.WH_ID = pick.WH_ID AND ob.REF_DOC_NO = pick.REF_DOC_NO AND ob.OB_LINE_NO = pick.OB_LINE_NO AND ob.ITM_CODE = pick.ITM_CODE " +
                " WHERE ob.IS_DELETED = 0 ";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "ob.WH_ID", findOutboundLineV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "ob.PRE_OB_NO", findOutboundLineV2.getPreOutboundNo());
        ConditionUtils.addCondition(conditions, "ob.REF_DOC_NO", findOutboundLineV2.getRefDocNumber());
        ConditionUtils.addCondition(conditions, "ob.PARTNER_CODE", findOutboundLineV2.getPartnerCode());
        ConditionUtils.addCondition(conditions, "ob.ITM_CODE", findOutboundLineV2.getItemCode());
        ConditionUtils.addCondition(conditions, "ob.REF_DOC_TYPE", findOutboundLineV2.getOrderType());
        ConditionUtils.addCondition(conditions, "ob.LANG_ID", findOutboundLineV2.getLanguageId());
        ConditionUtils.addCondition(conditions, "ob.C_ID", findOutboundLineV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "ob.PLANT_ID", findOutboundLineV2.getPlantId());
        ConditionUtils.addCondition(conditions, "ob.MFR_NAME", findOutboundLineV2.getManufacturerName());
        ConditionUtils.addCondition(conditions, "ob.TARGET_BRANCH_CODE", findOutboundLineV2.getTargetBranchCode());
        ConditionUtils.addCondition(conditions, "ob.SALES_ORDER_NUMBER", findOutboundLineV2.getSalesOrderNumber());
        ConditionUtils.numericConditions(conditions, "ob.STATUS_ID", findOutboundLineV2.getStatusId());
        ConditionUtils.numericConditions(conditions, "ob.OB_LINE_NO", findOutboundLineV2.getLineNumber());
        ConditionUtils.addDateCondition(conditions, "ob.DLV_CTD_ON", findOutboundLineV2.getFromDeliveryDate(), findOutboundLineV2.getToDeliveryDate());

        if (!conditions.isEmpty()) {
            joinQueryData += " AND " + String.join(" AND ", conditions);
        }

        Dataset<Row> outboundLine = spark.read()
                .option("fetchSize", "5000")
                .option("pushDownPredicate", true)
                .jdbc(jdbcUrl, "(" + joinQueryData + ") as tmp", connProp);

        outboundLine.show();
        Encoder<OutboundLineV2> outboundLineV2Encoder = Encoders.bean(OutboundLineV2.class);
        Dataset<OutboundLineV2> resultData = outboundLine.as(outboundLineV2Encoder);

//        return resultData.collectAsList();

        // Run collect asynchronously
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<OutboundLineV2>> future = executor.submit(resultData::collectAsList);

        try {
            return future.get(120, TimeUnit.SECONDS);  // Optional timeout
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            log.error("Spark job timeout!");
            future.cancel(true);
            throw new RuntimeException("Spark job took too long to complete");
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * @param findGrLineV2
     * @return
     */
    public List<GrLineV3> findGrLine(FindGrLine findGrLineV2) {

        String query = "select gl.c_text, gl.plant_text, gl.wh_text, gl.status_text, gl.accept_qty, gl.damage_qty, gl.mfr_name, gl.itm_code, gl.barcode_id, " +
                " gl.ref_doc_no, gl.ref_doc_type, gl.ord_qty, gl.gr_cnf_on, gl.gr_ctd_by, gl.qty_type, gl.ref_field_10, gh.gr_ctd_on from tblgrline gl " +
                " left join (select  gr_no, ref_doc_no, c_id, plant_id, lang_id, wh_id, gr_ctd_on from tblgrheader where is_deleted = 0) gh ON gl.gr_no = gh.gr_no and " +
                " gl.ref_doc_no = gh.ref_doc_no and gl.c_id = gh.c_id and gl.plant_id = gh.plant_id and " +
                " gl.lang_id = gh.lang_id and gl.wh_id = gh.wh_id where gl.is_deleted = 0 ";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "gl.c_id", findGrLineV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "gl.lang_id", findGrLineV2.getLanguageId());
        ConditionUtils.addCondition(conditions, "gl.wh_id", findGrLineV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "gl.plant_id", findGrLineV2.getPlantId());
        ConditionUtils.addCondition(conditions, "gl.brand", findGrLineV2.getBrand());
        ConditionUtils.addCondition(conditions, "gl.barcode_id", findGrLineV2.getBarcodeId());
        ConditionUtils.numericConditions(conditions, "gl.ib_line_no", findGrLineV2.getLineNo());
        ConditionUtils.numericConditions(conditions, "gl.status_id", findGrLineV2.getStatusId());
        ConditionUtils.addCondition(conditions, "gl.case_code", findGrLineV2.getCaseCode());
        ConditionUtils.addCondition(conditions, "gl.itm_code", findGrLineV2.getItemCode());
        ConditionUtils.addCondition(conditions, "gl.st_bin_intm", findGrLineV2.getInterimStorageBin());
        ConditionUtils.addCondition(conditions, "gl.mfr_code", findGrLineV2.getManufacturerCode());
        ConditionUtils.addCondition(conditions, "gl.mfr_name", findGrLineV2.getManufacturerName());
        ConditionUtils.addCondition(conditions, "gl.pack_barcode", findGrLineV2.getPackBarcodes());
        ConditionUtils.addCondition(conditions, "gl.origin", findGrLineV2.getOrigin());
        ConditionUtils.addCondition(conditions, "gl.pre_ib_no", findGrLineV2.getPreInboundNo());
        ConditionUtils.addCondition(conditions, "gl.ref_doc_no", findGrLineV2.getRefDocNumber());
        ConditionUtils.addCondition(conditions, "gl.rej_reason", findGrLineV2.getRejectReason());
        ConditionUtils.addCondition(conditions, "gl.rej_type", findGrLineV2.getRejectType());
        ConditionUtils.numericConditions(conditions, "gl.ib_ord_typ_id", findGrLineV2.getInboundOrderTypeId());
        ConditionUtils.addDateCondition(conditions, "gl.gr_ctd_on", findGrLineV2.getStartCreatedOn(), findGrLineV2.getEndCreatedOn());

        if (!conditions.isEmpty()) {
            query += " AND " + String.join(" AND ", conditions);
        }

        Dataset<Row> grLine = spark.read()
                .option("fetchSize", "5000")
                .option("pushDownPredicate", true)
                .jdbc(jdbcUrl, "(" + query + ") as tmp", connProp);

        grLine.show();
        Encoder<GrLineV3> grLineV3Encoder = Encoders.bean(GrLineV3.class);
        Dataset<GrLineV3> resultData = grLine.as(grLineV3Encoder);

//        return resultData.collectAsList();

        // Run collect asynchronously
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<GrLineV3>> future = executor.submit(resultData::collectAsList);

        try {
            return future.get(120, TimeUnit.SECONDS);  // Optional timeout
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            log.error("Spark job timeout!");
            future.cancel(true);
            throw new RuntimeException("Spark job took too long to complete");
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * @param findOutBoundHeaderV2
     * @return
     */
    public List<OutboundHeaderV3> findOutboundOrderSummaryReport(FindOutBoundHeader findOutBoundHeaderV2) {

        String query = "Select oh.c_text, oh.plant_text, oh.wh_text, oh.status_text, oh.target_branch_code, oh.ref_doc_typ,"
                + " oh.sales_order_number, oh.pre_ob_no, oh.ref_doc_no, oh.ref_doc_date, oh.dlv_cnf_on"
                + " From tbloutboundheader oh Where is_deleted = 0";

        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "oh.lang_id", findOutBoundHeaderV2.getLanguageId());
        ConditionUtils.addCondition(conditions, "oh.c_id", findOutBoundHeaderV2.getCompanyCodeId());
        ConditionUtils.addCondition(conditions, "oh.plant_id", findOutBoundHeaderV2.getPlantId());
        ConditionUtils.addCondition(conditions, "oh.wh_id", findOutBoundHeaderV2.getWarehouseId());
        ConditionUtils.addCondition(conditions, "oh.ref_doc_no", findOutBoundHeaderV2.getRefDocNumber());
        ConditionUtils.addCondition(conditions, "oh.pre_ob_no", findOutBoundHeaderV2.getPreOutboundNo());
        ConditionUtils.addCondition(conditions, "oh.target_branch_code", findOutBoundHeaderV2.getTargetBranchCode());
        ConditionUtils.addCondition(conditions, "oh.ref_field_1", findOutBoundHeaderV2.getSoType());
        ConditionUtils.addCondition(conditions, "oh.partner_code", findOutBoundHeaderV2.getPartnerCode());
        ConditionUtils.numericConditions(conditions, "oh.ob_ord_typ_id", findOutBoundHeaderV2.getOutboundOrderTypeId());
        ConditionUtils.numericConditions(conditions, "oh.status_id", findOutBoundHeaderV2.getStatusId());
        ConditionUtils.addDateCondition(conditions, "oh.req_del_date", findOutBoundHeaderV2.getStartRequiredDeliveryDate(), findOutBoundHeaderV2.getEndRequiredDeliveryDate());
        ConditionUtils.addDateCondition(conditions, "oh.dlv_cnf_on", findOutBoundHeaderV2.getStartDeliveryConfirmedOn(), findOutBoundHeaderV2.getEndDeliveryConfirmedOn());
        ConditionUtils.addDateCondition(conditions, "oh.dlv_ctd_on", findOutBoundHeaderV2.getStartOrderDate(), findOutBoundHeaderV2.getEndOrderDate());

        if (!conditions.isEmpty()) {
            query += " AND " + String.join(" AND ", conditions);
        }

        Dataset<Row> obHeader = spark.read()
                .option("fetchSize", "5000")
                .option("pushDownPredicate", true)
                .jdbc(jdbcUrl, "(" + query + ") as tmp", connProp);

        obHeader.show();
        Encoder<OutboundHeaderV3> obHeaderEncoder = Encoders.bean(OutboundHeaderV3.class);
        Dataset<OutboundHeaderV3> resultData = obHeader.as(obHeaderEncoder);

//        return resultData.collectAsList();

        // Run collect asynchronously
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<OutboundHeaderV3>> future = executor.submit(resultData::collectAsList);

        try {
            return future.get(120, TimeUnit.SECONDS);  // Optional timeout
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            log.error("Spark job timeout!");
            future.cancel(true);
            throw new RuntimeException("Spark job took too long to complete");
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * @param searchImBasicData1
     * @return
     * @throws ParseException
     */
    public List<ImBasicData1> searchImBasicData1V5(SearchImBasicData1 searchImBasicData1) throws ParseException, ExecutionException, InterruptedException {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT "
                    + "UOM_ID as uomId, "
                    + "LANG_ID as languageId, "
                    + "C_ID as companyCodeId, "
                    + "PLANT_ID as plantId, "
                    + "WH_ID as warehouseId, "
                    + "ITM_CODE as itemCode, "
                    + "MFR_PART as manufacturerPartNo, "
                    + "TEXT as description, "
                    + "MODEL as model, "
                    + "SPEC_01 as specifications1, "
                    + "SPEC_02 as specifications2, "
                    + "EAN_UPC_NO as eanUpcNo, "
                    + "HSN_CODE as hsnCode, "
                    + "ITM_TYP_ID as itemType, "
                    + "ITM_GRP_ID as itemGroup, "
                    + "SUB_ITM_GRP_ID as subItemGroup, "
                    + "ST_SEC_ID as storageSectionId, "
                    + "TOT_STK as totalStock, "
                    + "MIN_STK as minimumStock, "
                    + "MAX_STK as maximumStock, "
                    + "RE_ORD_LVL as reorderLevel, "
                    + "CAP_CHK as capacityCheck, "
                    + "REP_QTY as replenishmentQty, "
                    + "SAFTY_STCK as safetyStock, "
                    + "CAP_UNIT as capacityUnit, "
                    + "CAP_UOM as capacityUom, "
                    + "QUANTITY as quantity, "
                    + "WEIGHT as weight, "
                    + "STATUS_ID as statusId, "
                    + "SHELF_LIFE_IND as shelfLifeIndicator, "
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
                    + "C_TEXT as companyDescription, "
                    + "SELF_LIFE as selfLife, "
                    + "PLANT_TEXT as plantDescription, "
                    + "WH_TEXT as warehouseDescription, "
                    + "UTD_ON as updatedOn "
                    + "FROM tblimbasicdata1 ";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "C_ID", searchImBasicData1.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", searchImBasicData1.getPlantId());
            ConditionUtils.addCondition(conditions, "LANG_ID", searchImBasicData1.getLanguageId());
            ConditionUtils.addCondition(conditions, "WH_ID", searchImBasicData1.getWarehouseId());
            ConditionUtils.addCondition(conditions, "ITM_CODE", searchImBasicData1.getItemCode());
            ConditionUtils.addCondition(conditions, "TEXT", searchImBasicData1.getDescription());
            ConditionUtils.addCondition(conditions, "MFR_PART", searchImBasicData1.getManufacturerPartNo());
            ConditionUtils.addCondition(conditions, "CTD_BY", searchImBasicData1.getCreatedBy());
            ConditionUtils.numericConditions(conditions, "SELF_LIFE", searchImBasicData1.getSelfLife());

            ConditionUtils.numericConditions(conditions, "ITM_TYP_ID", searchImBasicData1.getItemType());
            ConditionUtils.numericConditions(conditions, "ITM_GRP_ID", searchImBasicData1.getItemGroup());
            ConditionUtils.numericConditions(conditions, "SUB_ITM_GRP_ID", searchImBasicData1.getSubItemGroup());

            ConditionUtils.addDateCondition(conditions, "UTD_ON", searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
            ConditionUtils.addDateCondition(conditions, "CTD_ON", searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());

            if (!conditions.isEmpty()) {
                sqlQuery += " WHERE IS_DELETED = 0  AND " + String.join(" AND ", conditions);
            } else {
                sqlQuery += " WHERE IS_DELETED = 0 ";
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            //            data.show();
            Encoder<ImBasicData1> imBasicData1Encoder = Encoders.bean(ImBasicData1.class);
            Dataset<ImBasicData1> dataset = data.as(imBasicData1Encoder);
//            return dataset.collectAsList();

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<ImBasicData1>> future = executor.submit(dataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find ImBasicData1 Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param findInventory
     * @return
     */
    public List<InventoryV5> findInventoryV5(FindInventoryV2 findInventory) throws ExecutionException, InterruptedException {
        try {

            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(findInventory.getCompanyCodeId(), findInventory.getPlantId(), findInventory.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "select barcode_id as barcodeId, inv_qty as inventoryQuantity, itm_code as itemCode, alloc_qty allocatedQuantity, ref_field_4 as referenceField4 from tblinventory";


            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "LANG_ID", findInventory.getLanguageId());
            ConditionUtils.addCondition(conditions, "C_ID", findInventory.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findInventory.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findInventory.getWarehouseId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findInventory.getReferenceDocumentNo());
            ConditionUtils.addCondition(conditions, "BARCODE_ID", findInventory.getBarcodeId());
            ConditionUtils.addCondition(conditions, "MFR_CODE", findInventory.getManufacturerCode());
            ConditionUtils.addCondition(conditions, "MFR_NAME", findInventory.getManufacturerName());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", findInventory.getPackBarcodes());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findInventory.getItemCode());
            ConditionUtils.addCondition(conditions, "ST_BIN", findInventory.getStorageBin());
            ConditionUtils.addCondition(conditions, "TEXT", findInventory.getDescription());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findInventory.getPartnerCode());
            ConditionUtils.addCondition(conditions, "REF_FIELD_10", findInventory.getStorageSectionId());
            ConditionUtils.addCondition(conditions, "level_id", findInventory.getLevelId());
            ConditionUtils.addCondition(conditions, "alt_uom", findInventory.getAltUom());

            if (findInventory.getStockTypeId() != null) {
                ConditionUtils.numericConditions(conditions, "STCK_TYP_ID", findInventory.getStockTypeId());
            }
            if (findInventory.getSpecialStockIndicatorId() != null) {
                ConditionUtils.numericConditions(conditions, "SP_ST_IND_ID", findInventory.getSpecialStockIndicatorId());
            }
            if (findInventory.getBinClassId() != null) {
                ConditionUtils.numericConditions(conditions, "BIN_CL_ID", findInventory.getBinClassId());
            }
            if (findInventory.getItemTypeId() != null) {
                ConditionUtils.numericConditions(conditions, "ITM_TYP_ID", findInventory.getItemTypeId());
            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND inv_id in (select max(inv_id) from tblinventory WITH (NOLOCK) where is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory WITH (NOLOCK) where is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,pal_code,barcode_id,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                } data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }

            Encoder<InventoryV5> inventoryV2CoreEncoder = Encoders.bean(InventoryV5.class);
            Dataset<InventoryV5> inventoryV2CoreDataset = data.as(inventoryV2CoreEncoder);

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<InventoryV5>> future = executor.submit(inventoryV2CoreDataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find Inventory Spark Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * New Find Inventory -- BF
     *
     * @param findInventory
     * @return
     */
    public List<InventoryV9> findInventoryV9(FindInventoryV2 findInventory) throws ExecutionException, InterruptedException {
        try {

            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findInventory.getCompanyCodeId(), findInventory.getPlantId(), findInventory.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            String sqlQuery = "SELECT INV_ID, LANG_ID, C_ID, PLANT_ID, WH_ID, " +
                    "PAL_CODE, CASE_CODE, PACK_BARCODE, ITM_CODE, VAR_ID, VAR_SUB_ID, " +
                    "STR_NO, ST_BIN, STCK_TYP_ID, SP_ST_IND_ID, REF_ORD_NO, STR_MTD, " +
                    "BIN_CL_ID, TEXT, INV_QTY, ALLOC_QTY, INV_UOM, MFR_DATE, EXP_DATE, IS_DELETED, REF_FIELD_1, REF_FIELD_2, " +
                    "REF_FIELD_3, REF_FIELD_4, REF_FIELD_5, REF_FIELD_6, REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, " +
                    "IU_CTD_BY, IU_CTD_ON, UTD_BY, UTD_ON, MFR_CODE, BARCODE_ID, level_id, MFR_NAME, " +
                    "REF_DOC_NO, C_TEXT, PLANT_TEXT, WH_TEXT, STCK_TYP_TEXT, STATUS_TEXT, PARTNER_CODE, " +
                    "ITM_TYP_ID, QTY_IN_CASE, QTY_IN_CREATE, " +
                    "SELF_LIFE, REMAINING_DAYS, REMAINING_SELF_LIFE_PERCENTAGE, RECEIVINGVARIANCE " +
                    "FROM tblinventory";

            List<String> conditions = new ArrayList<>();
            ConditionUtils.addCondition(conditions, "LANG_ID", findInventory.getLanguageId());
            ConditionUtils.addCondition(conditions, "C_ID", findInventory.getCompanyCodeId());
            ConditionUtils.addCondition(conditions, "PLANT_ID", findInventory.getPlantId());
            ConditionUtils.addCondition(conditions, "WH_ID", findInventory.getWarehouseId());
            ConditionUtils.addCondition(conditions, "REF_DOC_NO", findInventory.getReferenceDocumentNo());
            ConditionUtils.addCondition(conditions, "BARCODE_ID", findInventory.getBarcodeId());
            ConditionUtils.addCondition(conditions, "MFR_CODE", findInventory.getManufacturerCode());
            ConditionUtils.addCondition(conditions, "MFR_NAME", findInventory.getManufacturerName());
            ConditionUtils.addCondition(conditions, "PACK_BARCODE", findInventory.getPackBarcodes());
            ConditionUtils.addCondition(conditions, "ITM_CODE", findInventory.getItemCode());
            ConditionUtils.addCondition(conditions, "ST_BIN", findInventory.getStorageBin());
            ConditionUtils.addCondition(conditions, "TEXT", findInventory.getDescription());
            ConditionUtils.addCondition(conditions, "PARTNER_CODE", findInventory.getPartnerCode());
            ConditionUtils.addCondition(conditions, "REF_FIELD_10", findInventory.getStorageSectionId());
            ConditionUtils.addCondition(conditions, "level_id", findInventory.getLevelId());
            ConditionUtils.addCondition(conditions, "alt_uom", findInventory.getAltUom());

            if (findInventory.getStockTypeId() != null) {
                ConditionUtils.numericConditions(conditions, "STCK_TYP_ID", findInventory.getStockTypeId());
            }
            if (findInventory.getSpecialStockIndicatorId() != null) {
                ConditionUtils.numericConditions(conditions, "SP_ST_IND_ID", findInventory.getSpecialStockIndicatorId());
            }
            if (findInventory.getBinClassId() != null) {
                ConditionUtils.numericConditions(conditions, "BIN_CL_ID", findInventory.getBinClassId());
            }
            if (findInventory.getItemTypeId() != null) {
                ConditionUtils.numericConditions(conditions, "ITM_TYP_ID", findInventory.getItemTypeId());
            }
//            if (!conditions.isEmpty()) {
//                sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
//            } else {
//                sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id)";
//            }

            Dataset<Row> data = null;
            if (routingDb.equals("IMPEX")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL -->{}", jdbcUrl);
            } else if (routingDb.equals("WK")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl1, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl1);
            } else if (routingDb.equals("MT")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl2, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl2);
            } else if (routingDb.equals("ALM")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND inv_id in (select max(inv_id) from tblinventory WITH (NOLOCK) where is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl3, "(" + sqlQuery + ") as tmp", connProp);
                log.info("JDBC URL check -->{}", jdbcUrl3);
            } else if (routingDb.equals("NAMRATHA")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl4, "(" + sqlQuery + ") as tmp", connProp1);
                log.info("JDBC URL check -->{}", jdbcUrl4);
            } else if (routingDb.equals("REEFERON")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl5, "(" + sqlQuery + ") as tmp", connRef);
                log.info("JDBC URL check -->{}", jdbcUrl5);
            } else if (routingDb.equals("KNOWELL")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl6, "(" + sqlQuery + ") as tmp", connKnow);
                log.info("JDBC URL check -->{}", jdbcUrl6);
            } else if (routingDb.equals("FAHAHEEL")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory WITH (NOLOCK) where is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                }
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl7, "(" + sqlQuery + ") as tmp", connFahae);
                log.info("JDBC URL check -->{}", jdbcUrl7);
            } else if (routingDb.equals("AUTO_LAP")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl8, "(" + sqlQuery + ") as tmp", connAutoLap);
                log.info("JDBC URL check -->{}", jdbcUrl8);
            } else if (routingDb.equals("BF")) {
                if (!conditions.isEmpty()) {
                    sqlQuery += " WHERE IS_DELETED = 0 AND REF_FIELD_4 > 0 AND inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,pal_code,barcode_id,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) AND " + String.join(" AND ", conditions);
                } data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl9, "(" + sqlQuery + ") as tmp", connBF);
                log.info("JDBC URL check -->{}", jdbcUrl9);
            } else if (routingDb.equals("KKF")) {
                data = spark.read()
                        .option("fetchSize", "10000")
                        .option("pushDownloadPredicate", true)
                        .jdbc(jdbcUrl10, "(" + sqlQuery + ") as tmp", connKKF);
                log.info("JDBC URL check -->{}", jdbcUrl10);
            }


            Encoder<InventoryV9> inventoryV2CoreEncoder = Encoders.bean(InventoryV9.class);
            Dataset<InventoryV9> inventoryV2CoreDataset = data.as(inventoryV2CoreEncoder);
//            List<InventoryV4> result = inventoryV2CoreDataset.collectAsList();
//
//            return result;

            // Run collect asynchronously
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<InventoryV9>> future = executor.submit(inventoryV2CoreDataset::collectAsList);

            try {
                return future.get(120, TimeUnit.SECONDS);  // Optional timeout
            } catch (TimeoutException ex) {
                log.error("Spark job timeout!");
                future.cancel(true);
                throw new RuntimeException("Spark job took too long to complete");
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Find Inventory Spark Exception : " + e.toString());
            throw e;
        }
    }

}