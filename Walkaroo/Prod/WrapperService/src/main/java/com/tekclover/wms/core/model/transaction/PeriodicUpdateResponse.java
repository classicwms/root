package com.tekclover.wms.core.model.transaction;

import java.util.List;

import lombok.Data;

@Data
public class PeriodicUpdateResponse {
	private List<PeriodicLine> periodicLines;
	private PeriodicHeader periodicHeader;
}
