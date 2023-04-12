package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPerpetualLine {
	
	private List<String> cycleCountNo;
	private List<Long> lineStatusId;
	private String cycleCounterId;
	private String warehouseId;
	private Date startCreatedOn;
	private Date endCreatedOn;
}
