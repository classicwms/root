package com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

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