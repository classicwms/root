package com.tekclover.wms.api.inbound.orders.config;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import org.springframework.core.task.TaskDecorator;

public class TenantAwareTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        String currentDb = DataBaseContextHolder.getCurrentDb();  // your custom holder

        return () -> {
            try {
                DataBaseContextHolder.setCurrentDb(currentDb);
                runnable.run();
            } finally {
                DataBaseContextHolder.clear();
            }
        };
    }
}
