package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

@Data
public class UpdateStockCountLine {

	private String itemCode;
	private String cycleCountNo;
	private String manufacturerName;
	private Double InventoryQty;

}