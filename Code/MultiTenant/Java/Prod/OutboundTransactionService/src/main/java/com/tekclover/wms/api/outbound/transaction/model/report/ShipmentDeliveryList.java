package com.tekclover.wms.api.outbound.transaction.model.report;

import lombok.Data;

@Data
public class ShipmentDeliveryList {

	private String customerRef;
	private String commodity;
	private String description;
	private String manfCode;
	private Double quantity;
}
