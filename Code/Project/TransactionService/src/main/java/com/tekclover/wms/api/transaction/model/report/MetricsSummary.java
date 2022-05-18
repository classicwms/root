package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

@Data
public class MetricsSummary {
	private Double totalOrder;
	private Double lineItems;
	private Double percentageShipped;
}
