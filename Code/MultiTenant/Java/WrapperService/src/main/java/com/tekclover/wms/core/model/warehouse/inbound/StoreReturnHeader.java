package com.tekclover.wms.core.model.warehouse.inbound;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class StoreReturnHeader { 
	
	@NotBlank(message = "Receipt Warehouse ID is mandatory")
	private String receiptWarehouseID;
	
	@NotBlank(message = "Transfer Number is mandatory")
	private String transferNumber;
	
	private List<StoreReturnLine> storeReturnLine;
	
}
