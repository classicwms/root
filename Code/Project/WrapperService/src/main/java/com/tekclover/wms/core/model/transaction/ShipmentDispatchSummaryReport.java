package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ShipmentDispatchSummaryReport {

	private String printedOn; 	// 02-Dec-2021 3:00:11PM
	
	// Header
	private Double totalLinesOrdered;
	private Double totalLinesShipped;
	private Double totalOrderedQty;
	private Double totalShippedQty;
	private Double averagePercentage;
	
	// List
	private String soNumber;			// REF_DOC_NO
	private Date orderReceiptTime;		// REF_DOC_DATE
	private Double linesOrdered;
	private Double linesShipped;
	private Double orderedQty;
	private Double shippedQty;
	private Double perentageShipped;
	
//	private List<ShipmentDispatch> shipmentDispatch;
}
