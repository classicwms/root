package com.tekclover.wms.api.outbound.transaction.model.inbound.gr;

import lombok.Data;

@Data
public class PackBarcode {
	
	private String quantityType;
	private String barcode;

	//V2
	private Double cbm;
	private Double cbmQuantity;
}
