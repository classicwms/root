package com.tekclover.wms.api.inbound.orders.model.trans;

import lombok.Data;

import java.io.Serializable;

@Data
public class InventoryTransCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String packBarcodes;
	private String itemCode;
	private String storageBin;
	private Long specialStockIndicatorId;
}
