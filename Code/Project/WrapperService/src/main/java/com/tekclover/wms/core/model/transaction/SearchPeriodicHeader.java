package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPeriodicHeader {
	
	/*
	 * WH_ID
	 * CC_TYP_ID
	 * CC_NO
	 * STATUS_ID
	 * CC_CTD_BY
	 * CC_CTD_ON
	 */
	 
	private List<String> warehouseId;
	private List<Long> cycleCountTypeId;
	private List<String> cycleCountNo;
	private List<String> statusId;
	private List<String> createdBy;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
	
}
