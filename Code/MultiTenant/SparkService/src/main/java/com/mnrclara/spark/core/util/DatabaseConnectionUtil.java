package com.mnrclara.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {


    //==========================================Almailem===========================================
    public static String getJdbcUrl() {
        return "jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
    }

    public static String getNamrathaJdbcUrl() {
        return "jdbc:sqlserver://10.0.1.11;databaseName=WMS_NAMRATHA";
    }

    public static Properties getDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "hyPIznYE4ajeeeAj");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //==========================================WMS CORE==============================================
    public static String getCoreJdbcUrl() {
        return "jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
    }

    public static Properties getCoreDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "hyPIznYE4ajeeeAj");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //============================================ WMS Indus Mega Food================================
    public static String getIMFCoreJdbcUrl() {
        return "jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
    }

    public static Properties getIMFCoreDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "hyPIznYE4ajeeeAj");
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
        return "jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
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
        return "jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
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
        return "jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
    }

    public static Properties getImpexDevDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //==========================================Reeferon==============================================

    public static String getReeferonJdbcUrl() {
        return "jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
    }

    public static Properties getReeferonDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //==========================================Knowell==============================================

    public static String getKnowellJdbcUrl() {
        return "jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
    }

    public static Properties getKnowellDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //===============================================FAHAHEEL=======================================

    public static String getFahaeelJdbcUrl() {
        return"jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
    }

    public static Properties getFahaeelDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //===============================================AUTO_LAP=======================================

    public static String getAutoLapJdbcUrl() {
        return"jdbc:sqlserver://10.0.1.11;databaseName=WMS_MT";
    }

    public static Properties getAutoLapDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //==========================================WMS JAIN_CORD==============================================
//    public static String getJainCordJdbcUrl() {
//        return "jdbc:sqlserver://43.230.156.162;databaseName=JAIN_CORD";
//
//    }
//
//    public static Properties getJainCordDatabaseConnectionProperties() {
//        Properties connProp = new Properties();
//        connProp.put("user", "sa");
//        connProp.put("password", "TTPL@123");
//        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        return connProp;
//    }
}
