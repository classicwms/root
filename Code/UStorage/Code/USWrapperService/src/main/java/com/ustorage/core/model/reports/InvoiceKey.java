package com.ustorage.core.model.reports;

import lombok.Data;

@Data
public class InvoiceKey {

	private String invoiceNumber;
	private String invoiceAmount;
	private String invoiceDocumentStatus;
	private String remarks;

}
