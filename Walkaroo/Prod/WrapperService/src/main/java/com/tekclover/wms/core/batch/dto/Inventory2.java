package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class Inventory2 { 
	
	private String warehouseId;
	private String itemCode;
	private String packBarcodes;
	private String storageBin;
	private Long stockTypeId;
	private String description;			// ReferenceField8
	private String mfrPartNumber;       // ReferenceField9
	private String storageSectionId;	// ReferenceField10 
	private Double inventoryQuantity;
	private String inventoryUom;
	private Double allocatedQuantity;
	private Double totalQty;
}
