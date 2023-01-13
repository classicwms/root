package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Quote {

	private String quoteId;
	private String codeId;
	private String enquiryReferenceNumber;
	private String customerName;
	private String mobileNumber;
	private String requirement;
	private String sbu;
	private String email;
	private String customerGroup;
	private String storeSize;
	private String rent;
	private String notes;
	private String documentStatus;
	private String requirementType;
	private String status;
	private String customerCode;
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
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
