package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AddPaymentVoucher {

	private String serviceType;
	private String codeId;
	private String storeNumber;
	private String sbu;
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
	private List<String> referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
}
