package com.tekclover.wms.api.inbound.transaction.model.report;

import lombok.Data;

@Data
public class ReceiptHeader {

	private String supplier;
	private String supplierName;
	private String orderNumber;
	private String containerNo;
	private String orderType;
	
	/*
	 * Total logic
	 */
	private Double expectedQtySum; 		// Sum of Expected Qty in the List
	private Double acceptedQtySum; 	// Sum of Accepted Qty in the List
	private Double damagedQtySum; 		// Sum of Damaged Qty in the List
	private Double missingORExcessSum; 	// Sum of Missing/Excess in the List
	private  Double noOfBagsSum;  // Sum of Numbers Of Bags in the List
}
