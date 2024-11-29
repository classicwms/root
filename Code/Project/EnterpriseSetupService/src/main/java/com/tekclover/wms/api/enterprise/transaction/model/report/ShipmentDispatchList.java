package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

import java.util.Date;

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