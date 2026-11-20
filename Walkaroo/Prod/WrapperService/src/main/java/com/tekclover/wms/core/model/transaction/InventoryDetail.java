package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class InventoryDetail {

	private String storageBin;
	private Double inventoryQty;
}
