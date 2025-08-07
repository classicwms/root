package com.mnrclara.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {

    // ALM DEV
    public static Properties getDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "SuHcHQR72nxvyJx6EPpoOsK4V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    // DB CONNECTION_AMGHARA
    public static String getJdbcUrlAmghara() {
        return "jdbc:sqlserver://10.0.2.54;databaseName=WMS_ALMPRD";
    }


    // DB CONNECTION
    public static String getJdbcUrl() {
        return "jdbc:sqlserver://10.10.6.30;databaseName=WMS_ALMPRD";
    }

    //============================================ WMS Walkaroo PROD V3================================
    public static String getWalkarooCoreJdbcUrl() {
        return "jdbc:sqlserver://10.0.1.96;databaseName=WMS_WK_PRD";
    }

    public static Properties getWalkarooCoreDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "P@$$w0rd$%%_ert_77809");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
}
