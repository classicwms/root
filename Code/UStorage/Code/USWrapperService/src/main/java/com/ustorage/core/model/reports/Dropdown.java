package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class Dropdown {

	private List<KeyValuePair> customer;
	private List<AgreementKey> agreement;
	private List<InvoiceKey> invoice;
	private List<PaymentKey> payment;
	private List<WorkorderKey> workorder;
	private List<QuoteKey> quote;
	private List<EnquiryKey> enquiry;

}
