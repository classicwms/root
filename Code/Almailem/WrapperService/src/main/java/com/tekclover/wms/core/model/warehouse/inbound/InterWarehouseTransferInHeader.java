package com.tekclover.wms.core.model.warehouse.inbound;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class InterWarehouseTransferInHeader { 
	
	@NotBlank(message = "To Warehouse ID is mandatory")
	private String toWarehouseID;
	
	@NotBlank(message = "Transfer Number is mandatory")
	private String transferNumber;
	
	private List<InterWarehouseTransferInLine> interWarehouseTransferLine;
	
}
