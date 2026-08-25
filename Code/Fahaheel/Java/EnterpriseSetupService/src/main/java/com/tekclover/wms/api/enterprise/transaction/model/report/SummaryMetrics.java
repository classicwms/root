package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

@Data
public class SummaryMetrics {
	private String partnerCode;
	private String type;
	private MetricsSummary metricsSummary;
}