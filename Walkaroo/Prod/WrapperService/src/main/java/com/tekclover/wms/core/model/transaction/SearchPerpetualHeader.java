package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPerpetualHeader {
	
	/*
	 * WH_ID
	 * CC_TYP_ID
	 * CC_NO
	 * MVT_TYP_ID
	 * SUB_MVT_TYP_ID
	 * STATUS_ID
	 * CC_CTD_BY
	 * CC_CTD_ON
	 */
	private List<String> warehouseId;
	private List<Long> cycleCountTypeId;
	private List<String> cycleCountNo;
	private List<Long> movementTypeId;
	private List<Long> subMovementTypeId;
	private List<Long> headerStatusId;
	private List<Long> lineStatusId;
	private List<String> createdBy;
	private Date startCreatedOn;
	private Date endCreatedOn;
	
	// From Line table
	private List<String> cycleCounterId;
}
