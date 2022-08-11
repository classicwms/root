package com.tekclover.wms.core.batch.scheduler.entity;

import org.springframework.batch.item.ItemProcessor;

public class InventoryItemProcessor implements ItemProcessor<Inventory, Inventory> {

    @Override
    public Inventory process(Inventory inventory) throws Exception {
    	
    	inventory.setInventoryQuantity(inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0);
		inventory.setAllocatedQuantity(inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0);
		
    	inventory.setReferenceField10(String.valueOf(Double.sum(inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0,
    			inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0 ) ));
        return inventory;
    }
}