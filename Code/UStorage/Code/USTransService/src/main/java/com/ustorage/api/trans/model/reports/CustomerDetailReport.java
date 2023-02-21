package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDetailReport {

	public String customerCode;
	public String customerName;
	public String civilId;
	public String mobileNumber;
	public String phoneNumber;
	private List<AgreementDetail> agreementDetails;
}
