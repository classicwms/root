package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.Date;
@Data
public class AgreementDetail {

	public String agreementNumber;
	public String agreementStatus;
	public String storeNumber;
	public String size;
	public String storageType;
	public String phase;
	public String modeOfPayment;
	public Date startDate;
	public Date endDate;
	public Date lastPaidDate;
	public Date dueDate;
	public String rentPeriod;
	public String dueDays;
	public String lastPaidVoucherAmount;
	public String dueAmount;
	public String dueStatus;

	public String customerCode;
	public String customerName;
	public String civilId;
	public String mobileNumber;
	public String phoneNumber;

}
