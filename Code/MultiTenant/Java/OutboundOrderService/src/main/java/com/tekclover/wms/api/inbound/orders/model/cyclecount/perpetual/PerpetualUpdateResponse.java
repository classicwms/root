package com.tekclover.wms.api.inbound.orders.model.cyclecount.perpetual;

import java.util.List;

import lombok.Data;

@Data
public class PerpetualUpdateResponse {
	private List<PerpetualLine> perpetualLines;
	private PerpetualHeader perpetualHeader;
}
