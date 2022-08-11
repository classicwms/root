package com.tekclover.wms.core.batch.scheduler.entity;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

import com.tekclover.wms.core.batch.dto.Inventory2;
import com.tekclover.wms.core.util.CommonUtils;

public class InventoryItemProcessor implements ItemProcessor<Inventory, Inventory2> {
	
    @Override
    public Inventory2 process(Inventory inventoryInput) throws Exception {
    	Inventory2 inventory2 = new Inventory2();
    	BeanUtils.copyProperties(inventoryInput, inventory2, CommonUtils.getNullPropertyNames(inventoryInput));
    	
    	/*
    	 * private String description;			// ReferenceField8
    	 * private String mfrPartNumber;      	// ReferenceField9
    	 * private String storageSectionId;		// ReferenceField10 
    	 */
    	inventory2.setDescription(inventoryInput.getReferenceField8());
    	inventory2.setMfrPartNumber(inventoryInput.getReferenceField9());
    	inventory2.setStorageSectionId(inventoryInput.getReferenceField10());
    	
    	inventory2.setInventoryQuantity(inventoryInput.getInventoryQuantity() != null ? inventoryInput.getInventoryQuantity() : 0);
    	inventory2.setAllocatedQuantity(inventoryInput.getAllocatedQuantity() != null ? inventoryInput.getAllocatedQuantity() : 0);
		
    	inventory2.setTotalQty(Double.sum(inventoryInput.getInventoryQuantity() != null ? inventoryInput.getInventoryQuantity() : 0,
    			inventoryInput.getAllocatedQuantity() != null ? inventoryInput.getAllocatedQuantity() : 0 ));
        return inventory2;
    }
}