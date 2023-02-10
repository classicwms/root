package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Agreement {

	private String agreementNumber;
	private String codeId;
	private String quoteNumber;
	private String customerName;
	private String sbu;
	private String civilIDNumber;
	private String nationality;
	private String address;
	private String phoneNumber;
	private String secondaryNumber;
	private String faxNumber;
	private String itemsToBeStored;
	private Date startDate;
	private Date endDate;
	private String location;
	private List<StoreNumber> storeNumbers;
	//private String StoreNumbers;
	//private List<StoreNumber> size;
	private String insurance;
	private String rent;
	private String rentPeriod;
	private String totalRent;
	private String paymentTerms;
	private String agreementDiscount;
	private String totalAfterDiscount;
	private String notes;

	private String email;
	private String agreementType;
	private String deposit;
	private String others;

	private String status;

	private String itemsToBeStored1;
	private String itemsToBeStored2;
	private String itemsToBeStored3;
	private String itemsToBeStored4;
	private String itemsToBeStored5;
	private String itemsToBeStored6;
	private String itemsToBeStored7;
	private String itemsToBeStored8;
	private String itemsToBeStored9;
	private String itemsToBeStored10;
	private String itemsToBeStored11;
	private String itemsToBeStored12;

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
