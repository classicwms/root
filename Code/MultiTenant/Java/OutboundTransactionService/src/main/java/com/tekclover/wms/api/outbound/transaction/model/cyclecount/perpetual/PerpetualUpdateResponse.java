package com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual;

import java.util.List;

import lombok.Data;

@Data
public class PerpetualUpdateResponse {
	private List<PerpetualLine> perpetualLines;
	private PerpetualHeader perpetualHeader;
}
