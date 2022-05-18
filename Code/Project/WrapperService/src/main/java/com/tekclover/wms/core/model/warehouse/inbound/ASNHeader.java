package com.tekclover.wms.core.model.warehouse.inbound;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ASNHeader { 
	
	@NotBlank(message = "Receipt Warehouse ID is mandatory")
	private String receiptWarehouseID;
	
	@NotBlank(message = "ASN Number is mandatory")
	private String asnNumber;
	
	private List<ASNLine> asnLine;
	
}
