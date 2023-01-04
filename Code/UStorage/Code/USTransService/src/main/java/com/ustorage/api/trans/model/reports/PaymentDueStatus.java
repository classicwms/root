package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PaymentDueStatus {
	private List<String> agreementNumber;
	private List<String> customerName;
	private List<String> customerCode;
	private List<String> phoneNumber;
	private List<String> secondaryNumber;
	private List<String> civilId;
	private Date startDate;
	private Date endDate;
}
