package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class DocumentStatus {

	private List<DocumentStatusKey> agreement;
	private List<DocumentStatusKey> invoice;
	private List<DocumentStatusKey> payment;
	private List<DocumentStatusKey> workorder;
	private List<DocumentStatusKey> quote;
	private List<DocumentStatusKey> enquiry;

}
