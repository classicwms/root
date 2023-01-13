package com.tekclover.wms.core.model.warehouse.inbound;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ASNHeader { 
	
	@NotBlank(message = "Warehouse ID is mandatory")
	private String wareHouseId;
	
	@NotBlank(message = "ASN Number is mandatory")
	private String asnNumber;
}
