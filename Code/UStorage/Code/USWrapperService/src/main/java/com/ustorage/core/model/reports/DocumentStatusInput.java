package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DocumentStatusInput {

	private List<String> customerCode;
	private List<String> DocumentType;
	/*private List<String> agreementNumber;
	private List<String> enquiryId;
	private List<String> invoiceNumber;
	private List<String> voucherId;
	private List<String> quoteId;
	private List<String> workOrderId;*/

	private Date startDate;
	private Date endDate;

}
