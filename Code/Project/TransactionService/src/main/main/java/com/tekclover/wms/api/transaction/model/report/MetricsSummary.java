package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

@Data
public class MetricsSummary {
	private Long totalOrder;
	private Double lineItems;
	private Double percentageShipped;
}
