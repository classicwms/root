package com.tekclover.wms.api.transaction.model.warehouse.outbound;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ReturnPOHeader { 
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String wareHouseId;							// WH_ID
	
	@NotBlank(message = "PO Number is mandatory")
	private String poNumber; 							// REF_DOC_NO;
	
	@NotBlank(message = "Store ID is mandatory")
	private String storeID; 							// PARTNER_CODE;
	
	private String storeName; 							// PARTNER_NM;
		
	@NotBlank(message = "Required Delivery Date is mandatory")
	private String requiredDeliveryDate; 				//REQ_DEL_DATE
	
	@JsonIgnore
	private String id;
	
	@JsonIgnore
	private Date orderReceivedOn;
	
	@JsonIgnore
	private Long statusId;
}
