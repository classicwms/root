package com.tekclover.wms.api.transaction.model.report;

import java.util.Date;

import lombok.Data;

@Data
public class ShipmentDeliverySummaryReport {

	private String printedOn; 				// 02-Dec-2021 3:00:11PM
	private String so;						// REF_DOC_NO
	
	// SummaryList
	private Date expectedDeliveryDate; 		// Expected Delivery Date
	private Date deliveryDateTime; 			// Delivery date/Time
	private String branchCode;				// Branch Code
	private String orderType;				// Order type
	private Long lineOrdered;				// Line Ordered
	private Long lineShipped;				// Line Shipped
	private Double orderedQty;				// Ordered Qty
	private Double shippedQty;				// Shipped Qty
	private Double percentageShipped;		// % Shipped
	
	// Summary List
	private String partnerCode;
	private String type;
	
	// Summary Metrics
	private Double totalOrder;
	private Double lineItems;
	private Double metricsPercentageShipped;

//	private List<SummaryList> summaryList;
//	private List<SummaryMetrics> summaryMetrics;
}
