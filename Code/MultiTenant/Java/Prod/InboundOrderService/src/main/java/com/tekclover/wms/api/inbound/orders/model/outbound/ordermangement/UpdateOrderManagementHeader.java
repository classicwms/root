package com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateOrderManagementHeader {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private String referenceDocumentType;
	private Long outboundOrderTypeId;
	private Long statusId;
	private Date orderReceiptDate = new Date();
	private Date requiredDeliveryDate;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private Long deletionIndicator;
	private String remarks;
	private String pickupCreatedBy;
	private Date pickupCreatedOn = new Date();
	private String pickupUpdatedBy;
	private Date pickupupdatedOn = new Date();
	private String reAllocatedBy;
	private Date reAllocatedOn = new Date();
	private String pickerAssignedBy;
	private Date pickerAssignedOn = new Date();
}
