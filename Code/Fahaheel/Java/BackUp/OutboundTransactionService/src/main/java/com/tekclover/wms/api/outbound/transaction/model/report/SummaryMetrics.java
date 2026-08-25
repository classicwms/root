package com.tekclover.wms.api.outbound.transaction.model.report;

import lombok.Data;

@Data
public class SummaryMetrics {
	private String partnerCode;
	private String type;
	private MetricsSummary metricsSummary;
}
