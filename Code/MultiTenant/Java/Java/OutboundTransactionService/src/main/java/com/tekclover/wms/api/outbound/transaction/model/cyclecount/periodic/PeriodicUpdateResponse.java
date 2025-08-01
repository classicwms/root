package com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic;

import java.util.List;

import lombok.Data;

@Data
public class PeriodicUpdateResponse {
	private List<PeriodicLine> periodicLines;
	private PeriodicHeader periodicHeader;
}
