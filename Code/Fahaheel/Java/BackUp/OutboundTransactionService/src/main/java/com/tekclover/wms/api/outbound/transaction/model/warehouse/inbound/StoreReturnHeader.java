package com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound;

import java.util.Date;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class StoreReturnHeader {


	@Column(nullable = false)
	@NotBlank(message = "Warehouse Id is mandatory")
	private String wareHouseId;

	@Column(nullable = false)
	@NotBlank(message = "Transfer Order Number is mandatory")
	private String transferOrderNumber;
	
	@JsonIgnore
	private String id;
	
	@JsonIgnore
	private Date returnReceivedOn;
	
	@JsonIgnore
	private Long statusId;
}
