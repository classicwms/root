package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;

@Data

public class Invoice {

	private String invoiceNumber;
	private String codeId;
	private Date invoiceDate;
	private String customerId;
	private String customerType;
	private String sbu;
	private String agreementNumber;
	private Date startDate;
	private Date endDate;
	private String invoiceCurrency;
	private String invoiceAmount;
	private String invoiceDiscount;
	private String totalAfterDiscount;
	private String invoiceDocumentStatus;

	private String remarks;
	private String unit;
	private String quantity;
	private String monthlyRent;
	private String status;

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
