package com.tekclover.wms.api.inbound.transaction.model.outbound.pickup;

import lombok.Data;

import java.util.Date;

@Data

	public class UpdatePickupLine {
	private String languageId;
	private Long companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String pickupNumber;
	private Double pickConfirmQty;
	private String itemCode;
	private String actualHeNo;
	private String pickedStorageBin;
	private String pickedPackCode;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private String pickQty;
	private String pickUom;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String description;
	private String manufacturerPartNo;
	private String assignedPickerId;
	private String pickPalletCode;
	private String pickCaseCode;
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
	private String pickupCreatedBy;
	private Date pickupCreatedOn = new Date();
	private String pickupConfirmedBy;
	private Date pickupConfirmedOn = new Date();
	private String pickupUpdatedBy;
	private Date pickupupdatedOn = new Date();
	private String pickupReversedBy;
	private Date pickupReversedOn = new Date();
}
