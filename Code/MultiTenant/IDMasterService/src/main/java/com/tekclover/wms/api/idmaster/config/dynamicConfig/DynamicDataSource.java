package com.tekclover.wms.api.idmaster.config.dynamicConfig;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String db = DataBaseContextHolder.getCurrentDb();
        logger.info("Routing to database: " + db);
        if (db == null || db.isEmpty()) {
            // If no context is set, use the default database key
            DataBaseContextHolder.setCurrentDb("IMF");
        }
        return db;
    }





}
