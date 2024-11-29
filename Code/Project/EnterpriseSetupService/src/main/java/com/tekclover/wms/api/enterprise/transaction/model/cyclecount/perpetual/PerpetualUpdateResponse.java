package com.tekclover.wms.api.enterprise.transaction.model.cyclecount.perpetual;

import lombok.Data;

import java.util.List;

@Data
public class PerpetualUpdateResponse {
	private List<PerpetualLine> perpetualLines;
	private PerpetualHeader perpetualHeader;
}