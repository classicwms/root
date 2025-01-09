//package com.mnrclara.spark.core.service.clara;
//
//
//import com.mnrclara.spark.core.model.mnrclara.ClientGeneral;
//import com.mnrclara.spark.core.model.mnrclara.FindClientGeneral;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.apache.spark.sql.*;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
//import static org.apache.spark.sql.functions.col;
//
//@Service
//@Slf4j
//public class ClientGeneralNewService {
//
//    Properties connProp = new Properties();
//    SparkSession spark = null;
//
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public ClientGeneralNewService() {
//        // Connection Properties
//        connProp.setProperty("driver", "com.mysql.cj.jdbc.Driver");
//        connProp.put("user", "root");
//        connProp.put("password", "30NcyBuK");
//
//        spark = SparkSession.builder()
//                .master("local[*]")
//                .appName("ContainerReceipt.com")
//                .config("spark.executor.memory", "4g")
//                .config("spark.executor.cores", "4").getOrCreate();
//
//
//        // Read from MySQL Table
//        val df2 = spark.read().option("fetchSize", "10000").jdbc("jdbc:mysql://35.154.84.178:3306/MNRCLARA", "tblclientgeneralid", connProp)
//                .repartition(16);
//        df2.createOrReplaceTempView("tblclientgeneralid");
//    }
//
//    // FindClientGeneral
//    public List<ClientGeneral> findClientGeneral(FindClientGeneral findClientGeneral) throws Exception{
//
//        Dataset<Row> clientGeneralQuery = spark.sql(" SELECT \n"
//                + " CLIENT_ID as clientId,  "
//                + " CLASS_ID as classId,  "
//                + " FIRST_LAST_NM as firstNameLastName,  "
//                + " CORP_CLIENT_ID as corporationClientId,  "
//                + " EMail_ID as emailId, "
//                + " CONT_NO as contactNumber,  "
////                + " ADDRESS_LINE1" + " || SUITE_NO || ' ' || CITY || ' ' || STATE || ' ' || ZIP_CODE  as addressLine1,  "
//                + " COALESCE(ADDRESS_LINE1, '') || COALESCE(SUITE_NO, '') || ' ' || COALESCE(CITY, '') || ' ' || COALESCE(STATE, '') || ' ' || COALESCE(ZIP_CODE, '') as addressLine1,  "
//                + " IT_FORM_NO as intakeFormNumber,  "
//                + " IT_FORM_ID as intakeFormId, "
//                + " STATUS_ID as statusId, "
//                + " CTD_ON as createdOnString  "
//                + " FROM tblclientgeneralid WHERE IS_DELETED = 0");
//
//        if (findClientGeneral.getClientId() != null && !findClientGeneral.getClientId().isEmpty()) {
//            clientGeneralQuery = clientGeneralQuery.filter(col("client_id").isin(findClientGeneral.getClientId().toArray()));
//        }
//        if (findClientGeneral.getIntakeFormNumber() != null && !findClientGeneral.getIntakeFormNumber().isEmpty()) {
//            clientGeneralQuery = clientGeneralQuery.filter(col("it_form_no").isin(findClientGeneral.getIntakeFormNumber().toArray()));
//        }
//        if (findClientGeneral.getContactNumber() != null && !findClientGeneral.getContactNumber().isEmpty()) {
//            clientGeneralQuery = clientGeneralQuery.filter(col("cont_no").isin(findClientGeneral.getContactNumber()));
//        }
//        if (findClientGeneral.getClassId() != null && !findClientGeneral.getClassId().isEmpty()) {
//            List<String> classString = findClientGeneral.getClientId().stream().map(String::valueOf).collect(Collectors.toList());
//            clientGeneralQuery = clientGeneralQuery.filter(col("class_id").isin(classString.toArray()));
//        }
//        if (findClientGeneral.getFirstNameLastName() != null && !findClientGeneral.getFirstNameLastName().isEmpty()) {
//            clientGeneralQuery = clientGeneralQuery.filter(col("first_last_nm").isin(findClientGeneral.getFirstNameLastName()));
//        }
//        if (findClientGeneral.getEmailId() != null && !findClientGeneral.getEmailId().isEmpty()) {
//            clientGeneralQuery = clientGeneralQuery.filter(col("email_id").isin(findClientGeneral.getEmailId()));
//        }
//        if (findClientGeneral.getStatusId() != null && !findClientGeneral.getStatusId().isEmpty()) {
//            List<String> statusString = findClientGeneral.getStatusId().stream().map(String::valueOf).collect(Collectors.toList());
//            clientGeneralQuery = clientGeneralQuery.filter(col("status_id").isin(statusString.toArray()));
//        }
//
//        if(findClientGeneral.getStartDate() != null){
//            Date fromCreatedOn = findClientGeneral.getStartDate();
//            fromCreatedOn = org.apache.commons.lang3.time.DateUtils.truncate(fromCreatedOn, Calendar.DAY_OF_MONTH);
//            clientGeneralQuery = clientGeneralQuery.filter(col("ctd_on").$greater$eq(dateFormat.format(fromCreatedOn)));
//        }
//        if(findClientGeneral.getEndDate() != null){
//            Date toCreatedOn = findClientGeneral.getEndDate();
//            toCreatedOn = org.apache.commons.lang3.time.DateUtils.ceiling(toCreatedOn, Calendar.DAY_OF_MONTH);
//            clientGeneralQuery = clientGeneralQuery.filter(col("ctd_on").$less$eq(dateFormat.format(toCreatedOn)));
//        }
//
//        Encoder<ClientGeneral> clientGeneralEncoder = Encoders.bean(ClientGeneral.class);
//        Dataset<ClientGeneral> dataClientGeneral = clientGeneralQuery.as(clientGeneralEncoder);
//        List<ClientGeneral> result = dataClientGeneral.collectAsList();
////        clientGeneralQuery.unpersist();
//
//        return result;
//    }
//}