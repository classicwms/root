package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;

@Data

public class Enquiry {

	private String enquiryId;
	private String codeId;
	private String enquiryName;
	private String enquiryMobileNumber;
	private String requirementDetail;
	private String sbu;
	private String email;
	private String customerGroup;
	private String enquiryStoreSize;
	private String enquiryStatus;
	private String enquiryRemarks;
	private String requirementType;
	private String rentType;

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
