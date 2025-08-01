package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class PackBarcode {
	
	private String quantityType;
	private String barcode;

	//v2
	private Double cbm;
	private Double cbmQuantity;
	private Double acceptTotalCbm;
}
