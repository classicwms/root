package com.tekclover.wms.api.idmaster.model.strategyid;


import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Data
public class AddStrategyId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	@NotNull(message = "Strategy Type Id is mandatory")
	private Long strategyTypeId;
	private String strategyNo;
	private String languageId;
	private String strategyTypeText;
	private String strategyText;
	private Long deletionIndicator;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private String maxCapacity;
	private String superMaxCapacity;


}
