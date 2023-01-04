package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.List;

@Data
public class QuotationStatus {

	private List<String> requirementType;
	private List<String> quoteId;
	private List<String> status;

	private List<String> sbu;
}
