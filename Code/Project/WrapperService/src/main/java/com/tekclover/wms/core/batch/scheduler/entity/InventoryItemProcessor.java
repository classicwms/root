package com.tekclover.wms.core.batch.scheduler.entity;

import org.springframework.batch.item.ItemProcessor;

public class InventoryItemProcessor implements ItemProcessor<Inventory, Inventory> {

    @Override
    public Inventory process(Inventory Inventory) throws Exception {
        return Inventory;
    }
}