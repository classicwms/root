package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SaleOrderReturn {

	@Valid
	private SOReturnHeader soReturnHeader;
	
	@Valid
	private List<SOReturnLine> soReturnLine;
}
