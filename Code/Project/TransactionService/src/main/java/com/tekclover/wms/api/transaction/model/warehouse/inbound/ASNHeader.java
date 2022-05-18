package com.tekclover.wms.api.transaction.model.warehouse.inbound;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ASNHeader { 
	
	@NotBlank(message = "Warehouse ID is mandatory")
	private String wareHouseId;
	
	@NotBlank(message = "ASN Number is mandatory")
	private String asnNumber;
	
	@JsonIgnore
	private String id;
	
	@JsonIgnore
	private Date orderReceivedOn;
	
	@JsonIgnore
	private Long statusId;
}
