package com.tekclover.wms.api.idmaster.model.strategyid;

import java.util.Date;

import lombok.Data;

@Data
public class AddStrategyId {

	private String warehouseId;
	private Long strategyTypeId;
	private String strategyNo;
	private String languageId;
	private String strategyTypeText;
	private String strategyText;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
