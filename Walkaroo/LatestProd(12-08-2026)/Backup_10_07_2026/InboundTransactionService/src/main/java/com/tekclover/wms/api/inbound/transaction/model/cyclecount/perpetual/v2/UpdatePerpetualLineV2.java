package com.tekclover.wms.api.inbound.transaction.model.cyclecount.perpetual.v2;

import lombok.Data;

@Data
public class UpdatePerpetualLineV2 {

	private String itemCode;
	private String cycleCountNo;
	private String manufacturerName;
	private Double InventoryQty;
	private Long lineNo;
}