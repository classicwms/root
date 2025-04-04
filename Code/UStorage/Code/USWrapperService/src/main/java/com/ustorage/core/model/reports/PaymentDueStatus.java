package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PaymentDueStatus {

	private List<String> dueStatus;
	private List<String> storeNumber;
	private List<String> agreementNumber;
	private List<String> customerName;
	private List<String> customerCode;
	private List<String> phoneNumber;
	private List<String> secondaryNumber;
	private List<String> civilId;
}
