package com.tekclover.wms.api.idmaster.model.numberrange;

import lombok.Data;

@Data
public class UpdateNumberRange {
	
	private Long classId;
	private String languageId = "EN";
	private Long numberRangeCode;
	private String numberRangeObject;
	private String numberRangeFrom;
	private String numberRangeTo;
	private String numberRangeCurrent;
	private String numberRangeStatus;
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
	private String updatedBy;
}
