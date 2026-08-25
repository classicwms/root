package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

@Data
public class MetricsSummary {
	private Long totalOrder;
	private Double lineItems;
	private Double percentageShipped;
	private Double lineItemPicked;
	private Long orderedQty;
	private Long deliveryQty;
	private Double shippedLines;
}