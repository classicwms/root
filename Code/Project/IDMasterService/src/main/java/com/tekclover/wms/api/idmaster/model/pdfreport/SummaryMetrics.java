package com.tekclover.wms.api.idmaster.model.pdfreport;

import lombok.Data;

@Data
public class SummaryMetrics {
	private String partnerCode;
	private String type;
	private MetricsSummary metricsSummary;
}
