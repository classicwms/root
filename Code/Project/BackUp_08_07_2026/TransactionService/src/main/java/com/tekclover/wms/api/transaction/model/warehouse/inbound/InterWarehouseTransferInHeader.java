package com.tekclover.wms.api.transaction.model.warehouse.inbound;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class InterWarehouseTransferInHeader { 
	
	@NotBlank(message = "To Warehouse Id is mandatory")
	private String toWhsId;
	
	@NotBlank(message = "Transfer Order Number is mandatory")
	private String transferOrderNumber;
	
	@JsonIgnore
	private String id;
	
	@JsonIgnore
	private Date orderReceivedOn;
	
	@JsonIgnore
	private Long statusId;
}
