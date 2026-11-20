package com.tekclover.wms.api.transaction.model.warehouse.inbound;

import java.util.Date;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class InterWarehouseTransferInHeader {


	@Column(nullable = false)
	@NotBlank(message = "To Warehouse Id is mandatory")
	private String toWhsId;

	@Column(nullable = false)
	@NotBlank(message = "Transfer Order Number is mandatory")
	private String transferOrderNumber;
	
	@JsonIgnore
	private String id;
	
	@JsonIgnore
	private Date orderReceivedOn;
	
	@JsonIgnore
	private Long statusId;
}
