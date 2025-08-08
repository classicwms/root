package com.tekclover.wms.api.transaction.model.inbound.v2;

import lombok.Data;

@Data
public class PutAwayLineConfirm {

	private String preInboundNo;
	private String refDocNumber;
}