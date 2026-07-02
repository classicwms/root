package com.tekclover.wms.api.inbound.transaction.model.inbound.v2;

import lombok.Data;

@Data
public class PutAwayLineConfirm {

	private String preInboundNo;
	private String refDocNumber;
}