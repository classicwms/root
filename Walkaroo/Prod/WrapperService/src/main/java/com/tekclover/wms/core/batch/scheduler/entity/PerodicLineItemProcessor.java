package com.tekclover.wms.core.batch.scheduler.entity;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

import com.tekclover.wms.core.util.CommonUtils;

public class PerodicLineItemProcessor implements ItemProcessor<Inventory, PeriodicLine> {
	
    @Override
    public PeriodicLine process(Inventory inventoryInput) throws Exception {
    	PeriodicLine periodicLine = new PeriodicLine();
    	BeanUtils.copyProperties(inventoryInput, periodicLine, CommonUtils.getNullPropertyNames(inventoryInput));
    	
    	periodicLine.setItemCode("'" + inventoryInput.getItemCode() );
    	periodicLine.setPackBarcodes("'" + inventoryInput.getPackBarcodes());
    	periodicLine.setItemDesc(CommonUtils.escapeComma(inventoryInput.getReferenceField8()));
    	periodicLine.setManufacturerPartNo(inventoryInput.getReferenceField9());
    	periodicLine.setStorageSectionId(inventoryInput.getReferenceField10());
    	periodicLine.setInventoryUom(inventoryInput.getInventoryUom());
        return periodicLine;
    }
}