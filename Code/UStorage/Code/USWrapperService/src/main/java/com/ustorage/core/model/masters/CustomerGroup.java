package com.ustorage.core.model.masters;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerGroup {

	private String code;
	private String codeId;
	private String description;
	private String rent;
	private Long deletionIndicator = 0L;
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
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}