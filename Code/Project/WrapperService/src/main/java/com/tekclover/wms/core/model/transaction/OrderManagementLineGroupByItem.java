package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class OrderManagementLineGroupByItem {
	private String itemCode;
	private String description;
	private String type;
}