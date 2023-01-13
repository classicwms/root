package com.ustorage.core.model.trans;

import lombok.Data;

@Data
public class UpdateEnquiry {

	private String enquiryName;
	private String codeId;
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
	private String addressFrom;
	private String addressTo;
	private String numberOfTrips;
	private Float packingCost;
	private String jobcardType;

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
}
