package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class PeriodicUpdateResponseV2 {
	private List<PeriodicLineV2> periodicLines;
	private PeriodicHeaderV2 periodicHeader;
}
