package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class PaymentDueStatusReportOutput {

//	private List<CustomerDetailReport> customerDetails;
	private List<AgreementDetail> agreementDetails;
}
