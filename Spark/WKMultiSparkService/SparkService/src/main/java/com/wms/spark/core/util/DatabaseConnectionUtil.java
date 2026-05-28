package com.wms.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {

    //============================================ WMS Walkaroo PROD V3================================
    public static String getWakMDUJdbcUrl() {
        return "jdbc:sqlserver://10.10.6.30;databaseName=WMS_WK_PRD";
    }

    public static Properties getWakMDUDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "SuHcHQR72nxvyJx6EPpoOsK4V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    public static String getWakCMPJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_CBE";
    }

    public static Properties getWakCMPDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    public static String getWakCHNJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_CHN";
    }

    public static Properties getWakCHNDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    public static String getWakVGAJdbcUrl() {
        return "jdbc:sqlserver://10.10.6.30;databaseName=WMS_VGA";
    }

    public static Properties getWakVGADatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "SuHcHQR72nxvyJx6EPpoOsK4V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    public static String getWakCCLJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_CCL";
    }

    public static Properties getWakCCLDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    public static String getWakHYDJdbcUrl() {
        return "jdbc:sqlserver://10.10.10.61;databaseName=WMS_HYD";
    }

    public static String getWakAHMJdbcUrl() {
        return "jdbc:sqlserver://10.10.10.61;databaseName=WMS_AHM";
    }

    public static Properties getWakHYDDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "4V7lOXaxgAi3i6mgJL7qBUSPM");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    public static Properties getWakAHMDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "4V7lOXaxgAi3i6mgJL7qBUSPM");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
}
