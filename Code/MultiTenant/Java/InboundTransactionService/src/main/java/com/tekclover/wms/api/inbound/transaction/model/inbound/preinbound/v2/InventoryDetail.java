package com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2;

import lombok.Data;

@Data
public class InventoryDetail {

	private String storageBin;
	private Double inventoryQty;
}
