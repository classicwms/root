package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QuotationStatus {

	private List<String> requirementType;
	private List<String> quoteId;
	private List<String> status;

	private List<String> sbu;

}
