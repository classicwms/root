package com.tekclover.wms.core.model.warehouse.outbound;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SalesOrderHeader { 
	
	@NotBlank(message = "From Warehouse ID is mandatory")
	private String fromWarehouseID;						// WH_ID
	
	@NotBlank(message = "Sales Order No is mandatory")
	private String salesOrderNo; 						// REF_DOC_NO;
	
	@NotBlank(message = "Store ID is mandatory")
	private String storeID; 							// PARTNER_CODE;
	
	private String storeName; 							// PARTNER_NM;
		
	@NotBlank(message = "Required Delivery Date is mandatory")
	private String requiredDeliveryDate; 				//REQ_DEL_DATE
	
	private List<SalesOrderLine> salesOrderLine;
	
}
