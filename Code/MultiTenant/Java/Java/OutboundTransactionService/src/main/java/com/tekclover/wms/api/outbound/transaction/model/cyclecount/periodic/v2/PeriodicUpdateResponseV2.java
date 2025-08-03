package com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2;

import lombok.Data;

import java.util.List;

@Data
public class PeriodicUpdateResponseV2 {
	private List<PeriodicLineV2> periodicLines;
	private PeriodicHeaderV2 periodicHeader;
}
