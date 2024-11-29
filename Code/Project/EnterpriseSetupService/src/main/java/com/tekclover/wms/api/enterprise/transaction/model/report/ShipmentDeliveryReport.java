package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

import java.util.Date;

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
	private Double quantity;
	private Double total;
}