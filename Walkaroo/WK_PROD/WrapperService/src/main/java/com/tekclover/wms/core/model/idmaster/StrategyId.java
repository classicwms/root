package com.tekclover.wms.core.model.idmaster;
import java.util.Date;
import lombok.Data;

import javax.persistence.Column;

@Data
public class StrategyId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long strategyTypeId;
	private String strategyNo;
	private String languageId;
	private String strategyTypeText;
	private String strategyText;
	private String plantIdAndDescription;
	private String companyIdAndDescription;
	private String warehouseIdAndDescription;
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

	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
