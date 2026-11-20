package com.tekclover.wms.api.inbound.transaction.model.cyclecount.perpetual.v2;

import lombok.Data;

import java.util.List;

@Data
public class PerpetualUpdateResponseV2 {
	private List<PerpetualLineV2> perpetualLines;
	private PerpetualHeaderV2 perpetualHeader;
}
