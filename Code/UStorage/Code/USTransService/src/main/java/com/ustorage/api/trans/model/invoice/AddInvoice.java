package com.ustorage.api.trans.model.invoice;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

import java.util.Date;

@Data
public class AddInvoice {
		
	private Date invoiceDate;

	private String codeId;

	@NotBlank(message = "Customer ID is mandatory")	
	private String customerId;

	private String sbu;

	private String documentNumber;

	private Date documentStartDate;

	private Date documentEndDate;

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