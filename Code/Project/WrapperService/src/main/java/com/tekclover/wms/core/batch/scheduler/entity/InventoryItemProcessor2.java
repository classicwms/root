package com.tekclover.wms.core.batch.scheduler.entity;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

import com.tekclover.wms.core.util.CommonUtils;

public class InventoryItemProcessor2 implements ItemProcessor<InventoryMovement, InventoryMovement2> {
	
    @Override
    public InventoryMovement2 process(InventoryMovement invMovement1) throws Exception {
    	InventoryMovement2 invMovement2 = new InventoryMovement2();
//    	invMovement2.setLanguageId(invMovement1.getLanguageId());
//    	invMovement2.setCompanyCodeId(invMovement1.getCompanyCodeId());
//    	invMovement2.setPlantId(invMovement1.getPlantId());
//    	invMovement2.setWarehouseId(invMovement1.getWarehouseId());
    	
    	BeanUtils.copyProperties(invMovement1, invMovement2, CommonUtils.getNullPropertyNames(invMovement1));
        return invMovement2;
    }
}