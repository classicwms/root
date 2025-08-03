package com.mnrclara.spark.core.config.dynamicConfig;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


public class DynamicDataSource extends AbstractRoutingDataSource {




//    @Override
//    protected Object determineCurrentLookupKey() {
//        // Get the current context key (database or schema)
//        String databaseKey = DataBaseContextHolder.getCurrentDb();
//        if (databaseKey == null || databaseKey.isEmpty()) {
//            // If no context is set, return a default database key
//            return "primary";
//        }
//        logger.info("Routing to database: " + databaseKey);
//        return databaseKey;
//    }

//    @Override
//    protected Object determineCurrentLookupKey() {
//        String db = DataBaseContextHolder.getCurrentDb();
//        logger.info("Routing to database: " + db);
//        return db;
//    }

//    @Override
//    protected Object determineCurrentLookupKey() {
//        String databaseKey = DataBaseContextHolder.getCurrentDb();
//        logger.info("Determined database context: " + databaseKey); // Ensure this is logging as expected
//        return databaseKey == null || databaseKey.isEmpty() ? "primary" : databaseKey;
//    }

    @Override
    protected Object determineCurrentLookupKey() {
        String db = DataBaseContextHolder.getCurrentDb();
        logger.info("Routing to database: " + db);
        if (db == null || db.isEmpty()) {
            // If no context is set, use the default database key
            DataBaseContextHolder.setCurrentDb("MT");
        }
        return db;
    }





}
