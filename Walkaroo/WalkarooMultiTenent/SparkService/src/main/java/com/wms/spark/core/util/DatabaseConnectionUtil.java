package com.wms.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {

    //============================================ WMS Walkaroo PROD V3================================
    public static String getWakMDUJdbcUrl() {
        return "jdbc:sqlserver://10.10.14.24;databaseName=WMS_WK_PRD";
    }

    public static Properties getWakMDUDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Sd2se5y3mPD9BLr3QzZMyNU1V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    public static String getWakCMPJdbcUrl() {
        return "jdbc:sqlserver://10.10.10.61;databaseName=WMS_CBE";
    }

    public static Properties getWakCMPDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "4V7lOXaxgAi3i6mgJL7qBUSPM");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
}
