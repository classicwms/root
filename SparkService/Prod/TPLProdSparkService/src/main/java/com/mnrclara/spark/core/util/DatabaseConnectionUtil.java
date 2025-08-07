package com.mnrclara.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {


    //==========================================3PL===========================================
    public static String getThreePLJdbcUrl() {
        return "jdbc:sqlserver://10.10.18.14;databaseName=WMS_3PL";
    }

    public static Properties getDatabaseConnectionPropertiesThreePL() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "gQ3PmSL5D7u32Vtw5F74UwWH");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
}