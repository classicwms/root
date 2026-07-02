package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

@Data
public class ShipmentDispatchHeader {
	private String partnerCode;
	
	/*
	 * Total Logic
	 * ------------------
	 * 1. Total Lines ordered by PARTNER_CODE
	 * 2. Total Lines shipped by PARTNER_CODE
	 * 3. Total Ordered qty by PARTNER_CODE
	 * 4. Total Shipped Qty by PARTNER_CODE
	 * 5. Avg %
	 */
	private Double totalLinesOrdered;
	private Double totalLinesShipped;
	private Double totalOrderedQty;
	private Double totalShippedQty;
	private Double averagePercentage;
	private Double totalPickedQty;
	
}
