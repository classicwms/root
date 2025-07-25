package com.tekclover.wms.api.idmaster.model.pdfreport;

import lombok.Data;

@Data
public class MetricsSummary {
	private Double totalOrder;
	private Double lineItems;
	private Double percentageShipped;
	private Double lineItemPicked;
	private Long orderedQty;
	private Long deliveryQty;
	private Double shippedLines;
}
