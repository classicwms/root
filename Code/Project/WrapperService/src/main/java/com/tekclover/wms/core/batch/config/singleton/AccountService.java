package com.tekclover.wms.core.batch.config.singleton;

import java.util.ArrayList;
import java.util.List;

import com.tekclover.wms.core.batch.scheduler.entity.InventoryMovement;
import com.tekclover.wms.core.batch.scheduler.entity.InventoryMovement2;

public class AccountService {

	private static List<InventoryMovement2> inventoryHolder;
	private InventoryMovement2 inventoryMovement;
	
	public AccountService () {
		inventoryHolder = new ArrayList<>(50000);
	}
	
	public List<InventoryMovement2> getInventoryHolder() {
		return inventoryHolder;
	}
	
	public void setInventoryHolder(List<InventoryMovement2> items) {
		AccountService.inventoryHolder.addAll(items);
	}
	
	public InventoryMovement2 getInventory() {
		return inventoryMovement;
	}
	
	public void setInventory(InventoryMovement2 inventoryMovement) {
		this.inventoryMovement = inventoryMovement;
		AccountService.inventoryHolder.add(inventoryMovement);
	}
}