package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class B2bTransferIn {
	
	@Valid
	private B2bTransferInHeader b2bTransferInHeader;
	
	@Valid
	private List<B2bTransferInLine> b2bTransferLine;
}
