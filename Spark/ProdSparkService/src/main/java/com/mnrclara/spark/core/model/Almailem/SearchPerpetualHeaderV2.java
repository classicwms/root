package com.mnrclara.spark.core.model.Almailem;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchPerpetualHeaderV2  {

	private List<String> warehouseId;
	private List<Long> cycleCountTypeId;
	private List<String> cycleCountNo;
	private List<Long> movementTypeId;
	private List<Long> subMovementTypeId;
	private List<Long> statusId;
	private List<String> createdBy;

	private Date startCreatedOn;
	private Date endCreatedOn;

	// From Line table
	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	
}