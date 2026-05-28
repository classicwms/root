package com.mnrclara.spark.core.util;

import java.util.Properties;


public class DatabaseConnectionUtil {

    // ALM DEV
    public static Properties getDatabaseConnectionProperties() {
        Properties connProp = new Properties();
        connProp.put("user", "sa");
        connProp.put("password", "4V7lOXaxgAi3i6mgJL7qBUSPM");
        connProp.put("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return connProp;
    }

    // DB CONNECTION_AMGHARA
    public static String getJdbcUrlAmghara() {
        return "jdbc:sqlserver://10.10.10.61;databaseName=WMS_ALMPRD";
    }

}
