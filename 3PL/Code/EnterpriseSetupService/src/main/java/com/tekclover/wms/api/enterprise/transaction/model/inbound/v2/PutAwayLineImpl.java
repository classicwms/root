package com.tekclover.wms.api.enterprise.transaction.model.inbound.v2;

import lombok.Data;

@Data
public class PutAwayLineImpl {

	private String putawayNumber;
	private Double putawayQty;
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private Long lineNo;
	private String itemCode;
	private String manufacturerName;
	private String preInboundNo;
	private String refDocNumber;
}