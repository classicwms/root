package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class PaymentDueStatusReport {

	//private List<CustomerDetailReport> customerDetails;
	private List<AgreementDetail> agreementDetails;
}
