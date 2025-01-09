package com.mnrclara.spark.core.service.overc360;


import com.mnrclara.spark.core.model.overc360.prealert.FindPreAlert;
import com.mnrclara.spark.core.model.overc360.prealert.PreAlert;
import com.mnrclara.spark.core.util.ConditionUtils;
import com.mnrclara.spark.core.util.DatabaseConnectionUtil;
import com.mnrclara.spark.core.util.SparkSessionUtil;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class PreAlertService {

    SparkSession sparkSession = SparkSessionUtil.createSparkSession();


    public List<PreAlert> findPreAlert(FindPreAlert findPreAlert) {

        String query =
                "SELECT p.C_ID AS companyId, p.LANG_ID AS languageId, p.PARTNER_ID AS partnerId, " +
                        "p.MASTER_AIRWAY_BILL AS masterAirwayBill, p.HOUSE_AIRWAY_BILL AS houseAirwayBill, " +
                        "p.PARTNER_HOUSE_AIRWAY_BILL AS partnerHouseAirwayBill, p.PARTNER_MASTER_AIRWAY_BILL AS partnerMasterAirwayBill, " +
                        "p.C_NAME AS companyName, p.LANG_TEXT AS languageDescription, p.TOTAL_WEIGHT AS totalWeight, " +
                        "p.FLIGHT_NO AS flightNo, p.CONSOLE_INDICATOR AS consoleIndicator, " +
                        "p.CON_VALUE_LOCAL AS consignmentValueLocal, p.MANIFEST_INDICATOR AS manifestIndicator, " +
                        "p.FLIGHT_NAME AS flightName, p.ESTIMATED_TIME_OF_DEPARTURE AS estimatedTimeOfDeparture, " +
                        "p.ESTIMATED_TIME_OF_ARRIVAL AS estimatedTimeOfArrival, p.NO_OF_PIECES AS noOfPieces, " +
                        "p.CONSIGNMENT_VALUE AS consignmentValue, p.EXCHANGE_RATE AS exchangeRate, p.IATA AS iata, " +
                        "p.CUSTOMS_INSURANCE AS customsInsurance, p.DUTY AS duty, p.ADD_IATA AS addIata, p.ADD_INSURANCE AS addInsurance, " +
                        "p.CUSTOMS_VALUE AS customsValue, p.CALCULATED_TOTAL_DUTY AS calculatedTotalDuty, p.BAYAN_HV AS bayanHv, " +
                        "p.CURRENCY AS currency, p.DESCRIPTION AS description, p.CONSIGNEE_NAME AS consigneeName, " +
                        "p.SHIPPER AS shipper, p.ORIGIN AS origin, p.ORIGIN_CODE AS originCode, p.HS_CODE AS hsCode, " +
                        "p.PARTNER_TYPE AS partnerType, p.PARTNER_NAME AS partnerName, p.INCO_TERM AS incoTerm, " +
                        "p.HAWB_TYP AS hawbType, p.HAWB_TYP_ID AS hawbTypeId, p.HAWB_TYP_TXT AS hawbTypeDescription, " +
                        "p.HAWB_TIMESTAMP AS hawbTimeStamp, p.CON_LOCAL_ID AS consignmentLocalId, " +
                        "p.ADD_DESTINATION_DETAILS as addDestinationDetails, p.ADD_ORIGIN_DETAILS as addOriginDetails, " +
                        "p.HUB_CODE AS hubCode, p.HUB_NAME AS hubName,  " +
                        "p.LABOURS AS labours, p.OTHER_CHARGES AS otherCharges, p.OTHERS AS others, " +
                        "p.CUSTOM_DUTY AS customDuty, p.SPECIAL_APPROVALS AS specialApprovals, p.APPROVALS AS approvals, " +
                        "p.TOTAL_COST_PER_SHIPMENT AS totalCostPerShipment, p.TOTAL_VALUE_SHIPMENT AS totalValueShipment, " +
                        "p.NAS_DELIVERY AS nasDelivery, p.GLOBAL AS global, p.HANDLING_FORK AS handlingFork, p.STAMP_CHARGES AS stampCharges, " +
                        "p.HANDLING_FEES AS handlingFees, p.CLEARANCE_CHARGE AS clearanceCharge," +
                        "p.CONSIGNEE_PH_NO AS consigneePhoneNo, p.REF_FIELD_1 AS referenceField1, p.REF_FIELD_2 AS referenceField2, " +
                        "p.REF_FIELD_3 AS referenceField3, p.REF_FIELD_4 AS referenceField4, " +
                        "p.REF_FIELD_5 AS referenceField5, p.REF_FIELD_6 AS referenceField6, " +
                        "p.REF_FIELD_7 AS referenceField7, p.REF_FIELD_8 AS referenceField8, " +
                        "p.REF_FIELD_9 AS referenceField9, p.REF_FIELD_10 AS referenceField10, " +
                        "p.CTD_BY AS createdBy, p.CTD_ON AS createdOn, p.UTD_BY AS updatedBy, " +
                        "p.UTD_ON AS updatedOn, p.SUB_CUSTOMER_ID AS subCustomerId, " +
                        "p.SUB_CUSTOMER_NAME AS subCustomerName,p.INVOICE AS invoice,p.DDP_INVOICE_NO AS ddpInvoiceNo, " +
                        "p.ORIGIN_FLIGHT_COUNTRY AS originFlightCountry, counts.countHawb " +
                        "FROM tblprealert p \n" +
                        "JOIN (\n" +
                        "    SELECT PARTNER_MASTER_AIRWAY_BILL AS partnerMasterAirwayBill,\n" +
                        "           COUNT(HOUSE_AIRWAY_BILL) AS countHawb\n" +
                        "    FROM tblprealert\n" +
                        "    WHERE IS_DELETED = 0\n" +
                        "    GROUP BY PARTNER_MASTER_AIRWAY_BILL\n" +
                        ") counts ON p.PARTNER_MASTER_AIRWAY_BILL = counts.partnerMasterAirwayBill\n" +
                        "WHERE p.IS_DELETED = 0 ";


        List<String> conditions = new ArrayList<>();
        ConditionUtils.addCondition(conditions, "p.C_ID", findPreAlert.getCompanyId());
        ConditionUtils.addCondition(conditions, "p.LANG_ID", findPreAlert.getLanguageId());
        ConditionUtils.addCondition(conditions, "p.PARTNER_MASTER_AIRWAY_BILL", findPreAlert.getPartnerMasterAirwayBill());
        ConditionUtils.addCondition(conditions, "p.PARTNER_HOUSE_AIRWAY_BILL", findPreAlert.getPartnerHouseAirwayBill());
        ConditionUtils.addCondition(conditions, "p.PARTNER_ID", findPreAlert.getPartnerId());
        ConditionUtils.addCondition(conditions, "p.SUB_CUSTOMER_ID", findPreAlert.getSubCustomerId());
        ConditionUtils.addCondition(conditions, "p.HS_CODE", findPreAlert.getHsCode());
        ConditionUtils.addCondition(conditions, "p.DDP_INVOICE_NO", findPreAlert.getDdpInvoiceNo());
        ConditionUtils.addCondition(conditions,  "p.INVOICE", findPreAlert.getDdpInvoiceNo());
        ConditionUtils.addDateCondition(conditions, "p.CTD_ON", findPreAlert.getFromDate(), findPreAlert.getToDate());

        if(!conditions.isEmpty()){
            query += " AND " + String.join( " AND ", conditions);
        }

        Properties conn = DatabaseConnectionUtil.getOverCDatabaseConnectionProperties();
        String jdbcUrl = DatabaseConnectionUtil.getOverCJdbcUrl();
        Dataset<Row> preAlert = sparkSession.read()
                .option("fetchSize", "5000")
                .option("pushDownPredicate", true)
                .jdbc(jdbcUrl, "(" + query + ") as grl", conn);

        preAlert.show();
        Encoder<PreAlert> grLineV3Encoder = Encoders.bean(PreAlert.class);
        Dataset<PreAlert> resultData = preAlert.as(grLineV3Encoder);

        return resultData.collectAsList();
    }

}
