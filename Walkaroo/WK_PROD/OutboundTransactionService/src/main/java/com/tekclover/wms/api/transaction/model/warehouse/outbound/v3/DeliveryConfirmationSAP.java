package com.tekclover.wms.api.transaction.model.warehouse.outbound.v3;

import lombok.Data;

import java.util.List;

@Data
public class DeliveryConfirmationSAP {
	/*
		1	salesOrderNumber
		2	barcodeId		
		3	materialNo		
		4	priceSegment		
		5	branchCode		
		6	warehouseId		
		7	sku	
	 */
	
	private String salesOrderNumber;  
	private String barcodeId; 			//HUSERIAL_NO 
	private String materialNo;
	private String priceSegement;  
	private String branchCode;
	private String warehouseId;
	private String sku;  				// sku
}
