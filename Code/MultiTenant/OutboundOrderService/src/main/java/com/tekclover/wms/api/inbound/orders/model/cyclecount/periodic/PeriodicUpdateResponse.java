package com.tekclover.wms.api.inbound.orders.model.cyclecount.periodic;

import java.util.List;

import lombok.Data;

@Data
public class PeriodicUpdateResponse {
	private List<PeriodicLine> periodicLines;
	private PeriodicHeader periodicHeader;
}
