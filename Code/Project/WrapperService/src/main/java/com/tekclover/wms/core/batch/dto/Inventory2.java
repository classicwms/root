package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class Inventory2 { 
	
	private String warehouseId;
	private String itemCode;
	private String packBarcodes;
	private String storageBin;
	private Long stockTypeId;
	private String description;
	private Double inventoryQuantity;
	private String inventoryUom;
	private Double allocatedQuantity;
	private Double totalQty;
}
