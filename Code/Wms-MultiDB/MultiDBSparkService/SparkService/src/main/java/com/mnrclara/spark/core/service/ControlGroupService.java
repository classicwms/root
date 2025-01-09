package com.mnrclara.spark.core.service;


import com.mnrclara.spark.core.model.ControlGroupResponse;
import com.mnrclara.spark.core.model.ControlGroupType;
import com.mnrclara.spark.core.model.FindControlGroup;
import com.mnrclara.spark.core.model.FindResult;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.apache.parquet.example.Paper.schema;
import static org.apache.spark.sql.functions.col;

@Service
@Slf4j
public class ControlGroupService {

    Properties connProp = new Properties();

    SparkSession spark = null;

    public ControlGroupService() {
        // Connection Properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        spark = SparkSession.builder().master("local[*]").appName("SparkByExamples.com").getOrCreate();

        // Read from MySQL Table
//        val df2 = spark.read().jdbc("jdbc:sqlserver://10.0.2.233;databaseName=CGCLARA", "tblcontrolgrouptype", connProp);
//        df2.createOrReplaceTempView("tblcontrolgrouptype");
    }

    /**
     *
     * @param findControlGroup
     * @return
     * @throws Exception
     */
    public List<ControlGroupType> findControlGroupType(FindControlGroup findControlGroup) throws Exception {
        /*
         * @Column(name = "") private Date ;
         */
        Dataset<Row> queryDF = spark.sql("SELECT VERSION_NO as versionNumber, LANG_ID as languageId, \r\n"
                + "	C_ID as companyId, GRP_TYP_ID as groupTypeId, \r\n"
                + "	GRP_TYP_NM as groupTypeName, STATUS_ID as statusId,\r\n"
                + "	VALID_DATE_FROM as validityDateFrom, VALID_DATE_TO as validityDateTo,\r\n"
                + " CTD_ON as createdOn \r\n" +  " FROM tblcontrolgrouptype");

        String fromDate = "2023-08-01 00:00:00";
        String toDate = "2023-09-14 23:59:59";
        Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromDate);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toDate);
//		log.info("convertStringToDate-------> : " + date1);

//		 queryDF
//		.filter(a->a.getTimestamp(2).after(date1) && a.getTimestamp(2).before(date2))
//		.show();
//		 Date[] dateConv = DateUtils.addTimeToDates (searchInvoiceHeader.getStartInvoiceDate(), searchInvoiceHeader.getEndInvoiceDate());

//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String strDate1 = dateFormat.format(searchInvoiceHeader.getStartInvoiceDate());
//		String strDate2 = dateFormat.format(searchInvoiceHeader.getEndInvoiceDate());
//		Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate1);
//		Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate2);

        log.info("dateConv[0]-------> : " + date1);
        log.info("dateConv[1]-------> : " + date2);

        Encoder<ControlGroupType> controlGroupTypeEncoder = Encoders.bean(ControlGroupType.class);
        Dataset<ControlGroupType> dataSetControlGroup = queryDF.filter(a -> a.getTimestamp(8).after(date1) && a.getTimestamp(8).before(date2))
                .as(controlGroupTypeEncoder);
        return dataSetControlGroup.collectAsList();
    }

    public ControlGroupResponse calculateTotalLanguageId(FindResult findResult) throws Exception {

        String fromDate = "2023-08-01 00:00:00";
        String toDate = "2023-09-14 23:59:59";
        Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromDate);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toDate);

        Dataset<Row> queryDF = spark.read().jdbc("jdbc:sqlserver://10.0.2.233;databaseName=CGCLARA", "tblcontrolgrouptype", connProp);

        Timestamp ts1 = new Timestamp(date1.getTime());
        Timestamp ts2 = new Timestamp(date2.getTime());

        queryDF = queryDF.filter(col("CTD_ON").between(ts1, ts2));

        Long totalLanguageId = queryDF.agg(functions.sum("LANG_ID").cast(DataTypes.LongType)).first().getLong(0);

        ControlGroupResponse controlGroupResponse = new ControlGroupResponse();
        controlGroupResponse.setTotalLanguageId(totalLanguageId);


        return controlGroupResponse;
    }

//    public ControlGroupResponse calculateTotalCompanyId(FindResult findResult)throws Exception{
//
//        String fromDate = "2023-08-01 00:00:00";
//        String toDate = "2023-09-14 23:59:59";
//        Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromDate);
//        Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toDate);
//
//        Dataset<Row> queryDF = spark.read().jdbc("jdbc:sqlserver://10.0.2.233;databaseName=CGCLARA", "tblcontrolgrouptype", connProp);
//
//        queryDF = queryDF.filter(col("CTD_ON").between(date1,date2));
//
//        ControlGroupResponse controlGroupResponse = new ControlGroupResponse();
//        Long totalCompanyId = queryDF.agg(functions.sum("companyId").cast(DataTypes.LongType)).first().getLong(0);
//
//        controlGroupResponse.setTotalCompanyCodeId(totalCompanyId);
//        return controlGroupResponse;
//    }

    public ControlGroupResponse calculateTotalCompanyId(FindResult findResult) throws Exception {

        String fromDate = "2023-08-01 00:00:00";
        String toDate = "2023-09-14 23:59:59";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = dateFormat.parse(fromDate);
        Date date2 = dateFormat.parse(toDate);

        Dataset<Row> queryDF = spark.read().jdbc("jdbc:sqlserver://10.0.2.233;databaseName=CGCLARA", "tblcontrolgrouptype", connProp);

        Timestamp ts1 = new Timestamp(date1.getTime());
        Timestamp ts2 = new Timestamp(date2.getTime());

        // Filter the DataFrame using Timestamps
        queryDF = queryDF.filter(col("CTD_ON").between(ts1, ts2));

        ControlGroupResponse controlGroupResponse = new ControlGroupResponse();
        Long totalCompanyId = queryDF.agg(functions.sum("C_ID").cast(DataTypes.LongType)).first().getLong(0);

        controlGroupResponse.setTotalCompanyCodeId(totalCompanyId);
        return controlGroupResponse;
    }

}
