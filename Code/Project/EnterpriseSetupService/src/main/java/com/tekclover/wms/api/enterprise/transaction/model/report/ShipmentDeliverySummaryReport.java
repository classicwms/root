package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

import java.util.List;

@Data
public class ShipmentDeliverySummaryReport {
	private List<ShipmentDeliverySummary> shipmentDeliverySummary;
	private List<SummaryMetrics> summaryMetrics;
	
}