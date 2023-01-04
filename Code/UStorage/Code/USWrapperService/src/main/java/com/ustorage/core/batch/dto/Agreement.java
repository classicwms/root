package com.ustorage.core.batch.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Agreement {
	private String agreementNumber;
	private String codeId;
	private String quoteNumber;
	private String customerName;
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
	private String size;
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

	private Long deletionIndicator ;
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

	public Agreement(String agreementNumber,
					 String codeId,
					 String quoteNumber,
					 String customerName,
					 String civilIDNumber,
					 String nationality,
					 String address,
					 String phoneNumber,
					 String secondaryNumber,
					 String faxNumber,
					 String itemsToBeStored,
					 Date startDate,
					 Date endDate,
					 String location,
					 String size,
					 String insurance,
					 String rent,
					 String rentPeriod,
					 String totalRent,
					 String paymentTerms,
					 String agreementDiscount,
					 String totalAfterDiscount,
					 String notes, String email, String agreementType, String deposit, String others, String status,
					 String itemsToBeStored1,
					 String itemsToBeStored2,
					 String itemsToBeStored3,
					 String itemsToBeStored4,
					 String itemsToBeStored5,
					 String itemsToBeStored6,
					 String itemsToBeStored7,
					 String itemsToBeStored8,
					 String itemsToBeStored9, String itemsToBeStored10, String itemsToBeStored11,
					 String itemsToBeStored12, Long deletionIndicator , String referenceField1,
					 String referenceField2, String referenceField3, String referenceField4,
					 String referenceField5, String referenceField6, String referenceField7,
					 String referenceField8, String referenceField9, String referenceField10,
					 String createdBy, Date createdOn,String updatedBy, Date updatedOn) {

		this.agreementNumber = agreementNumber;
		this.codeId = codeId;
		this.quoteNumber = quoteNumber;
		this.customerName = customerName;
		this.civilIDNumber = civilIDNumber;
		this.nationality = nationality;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.secondaryNumber = secondaryNumber;
		this.faxNumber = faxNumber;
		this.itemsToBeStored = itemsToBeStored;
		this.startDate = startDate;
		this.endDate = endDate;
		this.location = location;
		this.size = size;
		this.insurance = insurance;
		this.rent = rent;
		this.rentPeriod = rentPeriod;
		this.totalRent = totalRent;
		this.paymentTerms = paymentTerms;
		this.agreementDiscount = agreementDiscount;
		this.totalAfterDiscount = totalAfterDiscount;
		this.notes = notes;

		this.email = email;
		this.agreementType = agreementType;
		this.deposit = deposit;
		this.others = others;

		this.status = status;

		this.itemsToBeStored1 = itemsToBeStored1;
		this.itemsToBeStored2 = itemsToBeStored2;
		this.itemsToBeStored3 = itemsToBeStored3;
		this.itemsToBeStored4 = itemsToBeStored4;
		this.itemsToBeStored5 = itemsToBeStored5;
		this.itemsToBeStored6 = itemsToBeStored6;
		this.itemsToBeStored7 = itemsToBeStored7;
		this.itemsToBeStored8 = itemsToBeStored8;
		this.itemsToBeStored9 = itemsToBeStored9;
		this.itemsToBeStored10 = itemsToBeStored10;
		this.itemsToBeStored11 = itemsToBeStored11;
		this.itemsToBeStored12 = itemsToBeStored12;

		this.deletionIndicator = deletionIndicator ;
		this.referenceField1 = referenceField1;
		this.referenceField2 = referenceField2;
		this.referenceField3 = referenceField3;
		this.referenceField4 = referenceField4;
		this.referenceField5 = referenceField5;
		this.referenceField6 = referenceField6;
		this.referenceField7 = referenceField7;
		this.referenceField8 = referenceField8;
		this.referenceField9 = referenceField9;
		this.referenceField10 = referenceField10;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
	}
}
