package com.tekclover.wms.api.transaction.model.warehouse.inbound;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class StoreReturnHeader { 
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String wareHouseId;
	
	@NotBlank(message = "Transfer Order Number is mandatory")
	private String transferOrderNumber;
	
	@JsonIgnore
	private String id;
	
	@JsonIgnore
	private Date returnReceivedOn;
	
	@JsonIgnore
	private Long statusId;
}
