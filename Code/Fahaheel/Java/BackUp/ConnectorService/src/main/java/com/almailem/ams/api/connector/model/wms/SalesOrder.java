package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SalesOrder {
	
	@Valid
	private SalesOrderHeader salesOrderHeader;
	
	@Valid
	private List<SalesOrderLine> salesOrderLine;
}
