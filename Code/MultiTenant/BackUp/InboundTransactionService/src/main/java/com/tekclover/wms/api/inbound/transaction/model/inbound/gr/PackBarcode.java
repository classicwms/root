package com.tekclover.wms.api.inbound.transaction.model.inbound.gr;

import lombok.Data;

@Data
public class PackBarcode {
	
	private String quantityType;
	private String barcode;

	//V2
	private Double cbm;
	private Double cbmQuantity;
	private Double acceptTotalCbm;
}
