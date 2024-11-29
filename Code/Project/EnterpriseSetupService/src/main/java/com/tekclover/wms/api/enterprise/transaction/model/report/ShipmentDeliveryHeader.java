package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

import java.util.Date;

@Data
public class ShipmentDeliveryHeader {

	private String deliveryTo;
	private Date deliveryDate;
	private Long orderType;
}