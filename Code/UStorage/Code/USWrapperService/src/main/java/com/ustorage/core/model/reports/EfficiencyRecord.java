package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EfficiencyRecord {

	private List<String> jobCardType;
	private List<String> processedBy;
	private Date startDate;
	private Date endDate;
}
