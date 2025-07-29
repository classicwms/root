package com.tekclover.wms.api.outbound.transaction.model.outbound.v2;

import lombok.Data;

@Data
public class InboundReversalInput {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;

	private String manufacturerName;
	private String itemCode;
	private String putAwayNumber;
	private String refDocNumber;
	private String packBarcodes;
}