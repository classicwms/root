package com.tekclover.wms.core.batch.config.singleton;

import java.util.ArrayList;
import java.util.List;

import com.tekclover.wms.core.batch.dto.Inventory2;

public class AccountService {

	private static List<Inventory2> inventoryHolder;
	private Inventory2 inventory;
	
	public AccountService () {
		inventoryHolder = new ArrayList<>(50000);
	}
	
	public List<Inventory2> getInventoryHolder() {
		return inventoryHolder;
	}
	
	public void setInventoryHolder(List<Inventory2> items) {
		AccountService.inventoryHolder.addAll(items);
	}
	
	public Inventory2 getInventory() {
		return inventory;
	}
	
	public void setInventory(Inventory2 inventory) {
		this.inventory = inventory;
		AccountService.inventoryHolder.add(inventory);
	}
}