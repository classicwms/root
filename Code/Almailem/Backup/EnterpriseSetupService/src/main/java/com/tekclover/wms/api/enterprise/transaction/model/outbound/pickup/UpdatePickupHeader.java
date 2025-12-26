package com.tekclover.wms.api.enterprise.transaction.model.outbound.pickup;

import java.util.Date;

import lombok.Data;

@Data
public class UpdatePickupHeader {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private String pickupNumber;
	private Long lineNumber;
	private String itemCode;
	private String proposedStorageBin;
	private String proposedPackCode;
	private Long outboundOrderTypeId;
	private String pickToQty;
	private String pickUom;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String manufacturerPartNo;
	private Long statusId;
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
	private String pickConfimedBy;
	private Date pickConfimedOn = new Date();
	private String pickUpdatedBy;
	private Date pickUpdatedOn = new Date();
	private String pickupReversedBy;
	private Date pickupReversedOn = new Date();
	private String assignedPickerId;
}