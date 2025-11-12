package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class SummaryMetrics {
	private String partnerCode;
	private String type;
	private Long referenceId;
	private MetricsSummary metricsSummary;
}
