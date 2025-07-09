package com.tekclover.wms.api.idmaster.model.pdfreport;

import lombok.Data;

import java.util.Date;

@Data
public class ShipmentDeliverySummary {

	private String so;						// REF_DOC_NO
	
	// SummaryList
	private Date expectedDeliveryDate; 		// Expected Delivery Date
	private Date deliveryDateTime; 			// Delivery date/Time
	private String branchCode;				// Branch Code
	private String branchDesc;				// Branch Desc
	private String orderType;				// Order type
	private Long lineOrdered;				// Line Ordered
	private Long lineShipped;				// Line Shipped
	private Double orderedQty;				// Ordered Qty
	private Double shippedQty;				// Shipped Qty
	private Double pickedQty;				// Picked Qty
	private Double percentageShipped;		// % Shipped
}
