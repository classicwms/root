package com.tekclover.wms.api.enterprise.transaction.model.outbound.v2;

import lombok.Data;

@Data
public class OutboundOrderCancelInput {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;

	private String refDocNumber;
	private String preOutboundNo;
	private String remarks;
}