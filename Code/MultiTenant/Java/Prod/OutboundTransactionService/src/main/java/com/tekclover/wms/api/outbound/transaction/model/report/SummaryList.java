package com.tekclover.wms.api.outbound.transaction.model.report;

import java.util.Date;

import lombok.Data;

@Data
public class SummaryList {

	private Date expectedDeliveryDate; 		// Expected Delivery Date
	private Date deliveryDateTime; 		// Delivery date/Time
	private String branchCode;				// Branch Code
	private String orderType;				// Order type
	private Long lineOrdered;				// Line Ordered
	private Long lineShipped;				// Line Shipped
	private Double orderedQty;				// Ordered Qty
	private Double shippedQty;				// Shipped Qty
	private Double percentageShipped;		// % Shipped
}
