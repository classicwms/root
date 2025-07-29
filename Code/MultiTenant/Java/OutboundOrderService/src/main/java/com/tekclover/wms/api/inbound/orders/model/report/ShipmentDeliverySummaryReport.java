package com.tekclover.wms.api.inbound.orders.model.report;

import java.util.List;

import lombok.Data;

@Data
public class ShipmentDeliverySummaryReport {
	private List<ShipmentDeliverySummary> shipmentDeliverySummary;
	private List<SummaryMetrics> summaryMetrics;
	
}
