package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

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
	private Double mrp;
	private Double noOfBags;
	private String remarks;
	private String driverName;
	private String vehicleNo;
	private Double orderQty;
}
