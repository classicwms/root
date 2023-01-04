package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DocumentStatusReport {

	private List<DocumentStatusKey> agreement;
	private List<DocumentStatusKey> invoice;
	private List<DocumentStatusKey> payment;
	private List<DocumentStatusKey> workorder;
	private List<DocumentStatusKey> quote;
	private List<DocumentStatusKey> enquiry;


}
