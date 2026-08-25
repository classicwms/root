package com.almailem.ams.api.connector.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NumberRange {

	@NotNull(message = "Class ID is mandatory")
	private Long classId;
	
	private String languageId = "EN";
	
	@NotNull(message = "Number Range Code is mandatory")
	private Long numberRangeCode;
	
	@NotEmpty(message = "Number Range Object is mandatory")
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
	private String createdBy;
}
