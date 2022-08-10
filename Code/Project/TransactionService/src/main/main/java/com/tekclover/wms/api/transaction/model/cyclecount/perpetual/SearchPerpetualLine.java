package com.tekclover.wms.api.transaction.model.cyclecount.perpetual;

import java.util.List;

import lombok.Data;

@Data
public class SearchPerpetualLine {
	
	private String cycleCountNo;
	private List<Long> lineStatusId;
	private List<String> cycleCounterId;
}
