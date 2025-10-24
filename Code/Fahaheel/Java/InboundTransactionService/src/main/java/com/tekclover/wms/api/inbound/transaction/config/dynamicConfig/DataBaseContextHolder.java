package com.tekclover.wms.api.inbound.transaction.config.dynamicConfig;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataBaseContextHolder {

    private static final ThreadLocal<String> dbContextHolder = new ThreadLocal<>();

    public void setCurrentDb(String dbType) {
        dbContextHolder.set(dbType);
    }

    public String getCurrentDb() {
        return dbContextHolder.get();
    }

    public void clear() {
        dbContextHolder.remove();
    }
}
