package com.tekclover.wms.api.transaction.model.inbound.gr;

import lombok.Data;

@Data
public class PackBarcode {
	
	private String quantityType;
	private String barcode;
}
