package com.tekclover.wms.api.inbound.orders.model.warehouse.outbound;

import java.util.Date;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class InterWarehouseTransferOutHeader {

	@Column(nullable = false)
	@NotBlank(message = "From Warehouse ID is mandatory")
	private String fromWhsID;							// WH_ID

	@Column(nullable = false)
	@NotBlank(message = "Transfer Order Number is mandatory")
	private String transferOrderNumber; 				// REF_DOC_NO

	@Column(nullable = false)
	@NotBlank(message = "To Warehouse ID is mandatory")
	private String toWhsID; 							// PARTNER_CODE
	
	private String storeName; 							// PARTNER_NM

	@Column(nullable = false)
	@NotBlank(message = "Required Delivery Date is mandatory")
	private String requiredDeliveryDate; 				//REQ_DEL_DATE
	
	@JsonIgnore
	private String id;
	
	@JsonIgnore
	private Date orderReceivedOn;
	
	@JsonIgnore
	private Long statusId;
}
