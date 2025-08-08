package com.tekclover.wms.core.model.warehouse.outbound;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class InterWarehouseTransferOutHeader { 
	
	@NotBlank(message = "From Warehouse ID is mandatory")
	private String fromWarehouseID;						// WH_ID
	
	@NotBlank(message = "Transfer OrderNo is mandatory")
	private String transferOrderNo; 					// REF_DOC_NO
	
	@NotBlank(message = "To Warehouse ID is mandatory")
	private String toWarehouseID; 						// PARTNER_CODE
	
	private String storeName; 							// PARTNER_NM
		
	@NotBlank(message = "Required Delivery Date is mandatory")
	private String requiredDeliveryDate; 				//REQ_DEL_DATE
	
	private List<InterWarehouseTransferOutLine> interWarehouseTransferLine;
}
