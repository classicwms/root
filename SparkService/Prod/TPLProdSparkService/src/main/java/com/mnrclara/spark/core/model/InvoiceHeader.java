package com.mnrclara.spark.core.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class InvoiceHeader {

	private String languageId;
	private Long classId;
	private String matterNumber;
	private String clientId;
	private Long caseCategoryId;
	private Long caseSubCategoryId;
	private String invoiceNumber;
	private String invoiceFiscalYear;
	private String invoicePeriod;
	private String preBillBatchNumber;
	private String preBillNumber;
	private Timestamp postingDate;
	
//	private Date preBillDate;

//	private Date invoiceDate;
//	
//	private String referenceText;
//	private String partnerAssigned;
//	private Double totalBillableHours;
//	private Double invoiceAmount;
//	private String currency;
//	private String arAccountNumber;
//	private String paymentPlanNumber;
//	private Date billStartDate;
//	private Date costCutDate;
//	private Date paymentCutDate;
//	private Date trustCutoffDate;	
//	private Double totalPaidAmount;	
//	private Double remainingBalance;
//	private Long statusId;
//	private Long deletionIndicator;
//	private String referenceField1;
//	private String referenceField2;	
//	private String referenceField3;
//	private String referenceField4;
//	private String referenceField5;
//	private String referenceField6;
//	private String referenceField7;
//	private String referenceField8;
//	private String referenceField9;
//	private String referenceField10;
//	private String createdBy;
//	private Date createdOn = new Date();
//	private String updatedBy;
//	private Date updatedOn = new Date();
}
