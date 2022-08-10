package com.tekclover.wms.api.transaction.model.cyclecount.periodic;

import java.util.List;

import lombok.Data;

@Data
public class SearchPeriodicLine {
	
	private String cycleCountNo;
	private List<Long> lineStatusId;
	private List<String> cycleCounterId;
}
