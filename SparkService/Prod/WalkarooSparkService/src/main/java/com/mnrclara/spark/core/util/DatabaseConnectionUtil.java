package com.mnrclara.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {

    // ALM DEV
//    public static Properties getDatabaseConnectionProperties() {
//        Properties connProp = new Properties();
//        connProp.put("user", "sa");
//        connProp.put("password", "AL.8B/XoM>D3vR]Q4s41?.8hP>|");
//        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        return connProp;
//    }
//
//    // DB CONNECTION_AMGHARA
//    public static String getJdbcUrlAmghara() {
//        return "jdbc:sqlserver://10.0.2.54;databaseName=WMS_ALMPRD";
//    }
//
//
//    // DB CONNECTION
//    public static String getJdbcUrl() {
//        return "jdbc:sqlserver://10.0.2.8;databaseName=WMS_ALMPRD";
//    }

    //============================================ WMS Walkaroo PROD V3================================
    public static String getWalkarooCoreJdbcUrl() {
        return "jdbc:sqlserver://10.10.14.24;databaseName=WMS_WK_PRD";
    }

    public static Properties getWalkarooCoreDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Sd2se5y3mPD9BLr3QzZMyNU1V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
}
