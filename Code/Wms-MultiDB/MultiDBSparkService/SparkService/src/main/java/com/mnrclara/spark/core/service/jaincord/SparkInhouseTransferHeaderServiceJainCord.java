package com.mnrclara.spark.core.service.jaincord;

import com.mnrclara.spark.core.model.wmscorev2.InhouseTransferHeader;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class SparkInhouseTransferHeaderServiceJainCord {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;

    public SparkInhouseTransferHeaderServiceJainCord() throws ParseException {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
        sparkSession = SparkSession.builder().master("local[*]").appName("InhouseTransferHeader.com").config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4").getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD", "tblinhousetransferheader", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblinhousetransferheaderv5jain");
    }

    /**
     * @return
     * @throws ParseException
     */
    public List<InhouseTransferHeader> getAllInhouseTransferHeader() throws ParseException {

        Dataset<Row> inhouseTransferHeaderQuery = sparkSession.sql("SELECT "
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
                + "From tblinhousetransferheaderv5jain where IS_DELETED = 0 "
        );
//        inhouseTransferHeaderQuery.cache();


        Encoder<InhouseTransferHeader> inhouseTransferHeaderEncoder = Encoders.bean(InhouseTransferHeader.class);
        Dataset<InhouseTransferHeader> datasetControlGroup = inhouseTransferHeaderQuery.as(inhouseTransferHeaderEncoder);
        List<InhouseTransferHeader> results = datasetControlGroup.collectAsList();
        return results;
    }
}