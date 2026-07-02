package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class InboundOrderCancelInput {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;

	private String refDocNumber;
	private String preInboundNo;
	private String remarks;
}