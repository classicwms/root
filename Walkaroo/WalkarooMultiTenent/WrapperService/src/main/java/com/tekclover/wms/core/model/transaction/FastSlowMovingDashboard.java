package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class FastSlowMovingDashboard {
	private String itemCode;
	private String itemText;
	private Double deliveryQuantity;
	private String type;
}