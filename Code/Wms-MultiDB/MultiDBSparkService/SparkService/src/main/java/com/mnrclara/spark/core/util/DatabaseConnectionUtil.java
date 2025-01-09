package com.mnrclara.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {


    //==========================================Almailem===========================================
    public static String getJdbcUrl() {
        return "jdbc:sqlserver://3.109.20.248;databaseName=WMS_ALMDEV";
    }

    public static Properties getDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //==========================================WMS CORE==============================================
    public static String getCoreJdbcUrl() {
        return "jdbc:sqlserver://35.154.84.178;databaseName=WMS_CORE";
    }

    public static Properties getCoreDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "30NcyBuK");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //============================================ WMS Indus Mega Food================================
    public static String getIMFCoreJdbcUrl() {
        return "jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMF";
    }

    public static Properties getIMFCoreDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    // ======================================OverC =======================================================
    public static String getOverCJdbcUrl() {
        return "jdbc:sqlserver://13.202.151.79;databaseName=AG01_DB";
    }

    public static Properties getOverCDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "zj3ar5WHoU5080Lb4X3");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //============================================ WMS Walkaroo V3================================
    public static String getWalkarooCoreJdbcUrl() {
        return "jdbc:sqlserver://3.111.134.239;databaseName=WMS_WK";
    }

    public static Properties getWalkarooCoreDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //============================================ WMS Impex UAT V4================================
    public static String getImpexJdbcUrl() {
        return "jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMPEX_UAT";
    }

    public static Properties getImpexDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
    //============================================ WMS Impex Dev V4================================
    public static String getImpexDevJdbcUrl() {
        return "jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMPEX";
    }

    public static Properties getImpexDevDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
    //==========================================WMS JAIN_CORD==============================================
    public static String getJainCordJdbcUrl() {
        return "jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD";
    }

    public static Properties getJainCordDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "TTPL@123");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
}