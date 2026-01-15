package com.mnrclara.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {


    //==========================================Almailem===========================================
    public static String getJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_MT";
    }

    public static String getNamrathaJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_NAMRATHA";
    }

    public static Properties getDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //==========================================WMS CORE==============================================
    public static String getCoreJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_MT";
    }

    public static Properties getNamrathaDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //============================================ WMS Indus Mega Food================================
    public static String getIMFCoreJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_MT";
    }

    public static Properties getIMFCoreDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }


    //============================================ WMS Walkaroo V3================================
    public static String getWalkarooCoreJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_MT";
    }

    public static Properties getWalkarooCoreDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //============================================ WMS Impex UAT V4================================
    public static String getImpexJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_MT";
    }

    public static Properties getImpexDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
    //============================================ WMS Impex Dev V4================================
    public static String getImpexDevJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_MT";
    }

    public static Properties getImpexDevDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //==========================================Reeferon==============================================

    public static String getReeferonJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_MT";
    }

    public static Properties getReeferonDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //==========================================Knowell==============================================

    public static String getKnowellJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_KNOWELL";
    }

    public static Properties getKnowellDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //===============================================FAHAHEEL=======================================

    public static String getFahaeelJdbcUrl() {
        return"jdbc:sqlserver://10.10.22.45;databaseName=WMS_MT";
    }

    public static Properties getFahaeelDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    //===============================================AUTO_LAP=======================================

    public static String getAutoLapJdbcUrl() {
        return"jdbc:sqlserver://10.10.22.45;databaseName=WMS_MT";
    }

    public static Properties getAutoLapDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
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

    //=======================BF=====================================================

    public static String getBFJdbcUrl() {
        return "jdbc:sqlserver://10.10.22.45;databaseName=WMS_BF";
    }

    public static Properties getBFDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "D3pyGJd4uK8sSqM7YbfJp5nYt");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }
}
