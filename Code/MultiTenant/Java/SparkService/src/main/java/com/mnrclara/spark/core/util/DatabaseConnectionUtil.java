package com.mnrclara.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {


    //============= WMS Impex Dev V4================================
    public static String getImpexDevJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_MT";
    }

    public static Properties getImpexDevDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //=============== NAMRATHA ========================

    public static String getNamrathaJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_NAMRATHA";
    }

    public static Properties getNamrathaDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //=========Knowell==================

    public static String getKnowellJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_KNOWELL";
    }

    public static Properties getKnowellDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //=========SPAREX=================
    public static String getSPAREXJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_SPAREX";
    }

    public static Properties getSPAREXDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //=========BF && KKF && KSP==================

    public static String getBFJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_BF";
    }

    public static Properties getBFDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    public static String getKKFJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_BF";
    }

    public static Properties getKKFDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    public static String getKSPJdbcUrl() {
        return "jdbc:sqlserver://10.20.0.19;databaseName=WMS_KSP";
    }

    public static Properties getKSPDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TvHcGBR84nxvyJx6EPpoOsL5V");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

}