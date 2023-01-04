package com.ustorage.api.master.model.numberrange;

import lombok.Data;

@Data
public class UpdateNumberRange {

	private String documentCode;
	private String documentName;

	private Long numberRangeCode;
	private String description;
	private Long numberRangeFrom;
	private Long numberRangeTo;
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
}
