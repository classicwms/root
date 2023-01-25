package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class PaymentVoucher {

	private String voucherId;
	private String codeId;
	private String serviceType;
	private String sbu;
	private String storeNumber;
	private String customerName;
	private String contractNumber;
	private String period;
	private Date startDate;
	private Date endDate;
	private Date voucherDate;
	private Date paidDate;
	private String voucherAmount;
	private String modeOfPayment;
	private String paymentReference;
	private String remarks;
	private String voucherStatus;
	private String documentType;
	private String bank;
	private String status;

	private Long deletionIndicator;
	private String referenceField1;
	private String referenceField2;
	private Set<ReferenceField3> referenceField3;
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
