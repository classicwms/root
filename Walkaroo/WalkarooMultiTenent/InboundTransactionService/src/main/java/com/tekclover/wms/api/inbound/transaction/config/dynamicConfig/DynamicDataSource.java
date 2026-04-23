package com.tekclover.wms.api.inbound.transaction.config.dynamicConfig;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


public class DynamicDataSource extends AbstractRoutingDataSource {

//    @Override
//    protected Object determineCurrentLookupKey() {
//        String db = DataBaseContextHolder.getCurrentDb();
//        logger.info("Routing to database: " + db);
//        if (db == null || db.isEmpty()) {
//            // If no context is set, use the default database key
//            DataBaseContextHolder.setCurrentDb("WK");
//        }
//        return db;
//    }

    @Override
    protected Object determineCurrentLookupKey() {
        String db = DataBaseContextHolder.getCurrentDb();

        if (db == null || db.isEmpty()) {
            db = "WK";  // ✅ Use WK as fallback key
            // If no context is set, use the default database key
            DataBaseContextHolder.setCurrentDb("WK");
        }

        logger.info("Routing to database: " + db);
        return db;  // ✅ Return correct key every time
    }


}
