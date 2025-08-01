package com.tekclover.wms.api.inbound.transaction.model.report;

import java.util.Date;

import lombok.Data;

@Data
public class ShipmentDeliveryReport {

	private String deliveryTo;
	private String partnerName;
	private Date deliveryDate;
	private String orderType;
	//---------------------------------
	private String customerRef;
	private String commodity;
	private String description;
	private String manfCode;
	private String targetBranch;
	private Double quantity;
	private Double total;
}
