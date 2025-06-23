package com.tekclover.wms.api.outbound.transaction.model.report;

import java.util.Date;

import lombok.Data;

@Data
public class ShipmentDispatchList {
	
	private String soNumber;			// REF_DOC_NO
	private Date orderReceiptTime;	// REF_DOC_DATE
	private Double linesOrdered;
	private Double linesShipped;
	private Double orderedQty;
	private Double shippedQty;
	private Double pickedQty;
	private Double perentageShipped;
}