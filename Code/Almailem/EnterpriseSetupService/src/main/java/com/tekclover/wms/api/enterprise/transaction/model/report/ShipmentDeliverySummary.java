package com.tekclover.wms.api.enterprise.transaction.model.report;

import java.util.Date;

import lombok.Data;

@Data
public class ShipmentDeliverySummary {

	private String so;						// REF_DOC_NO
	
	// SummaryList
	private Date expectedDeliveryDate; 		// Expected Delivery Date
	private Date deliveryDateTime; 			// Delivery date/Time
	private String branchCode;				// Branch Code
	private String branchDesc;				// Branch Desc
	private String orderType;				// Order type
	private Double lineOrdered;				// Line Ordered
	private Double lineShipped;				// Line Shipped
	private Double orderedQty;				// Ordered Qty
	private Double shippedQty;				// Shipped Qty
	private Double pickedQty;				// Picked Qty
	private Double percentageShipped;		// % Shipped
}