package com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement;

import java.util.Date;

import lombok.Data;

@Data
public class AddOrderManagementLine {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String itemCode;
	private String proposedStorageBin;
	private String proposedPackCode;
	private String pickupNumber;
	private Long variantCode;
	private String variantSubCode;
	private Long outboundOrderTypeId;
	private Long statusId;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String description;
	private String manufacturerPartNo;
	private String hsnCode;
	private String itemBarcode;
	private Double orderQty;
	private String orderUom;
	private Double inventoryQty;
	private Double allocatedQty;
	private Double reAllocatedQty;
	private Long strategyTypeId;
	private String strategyNo;
	private Date requiredDeliveryDate;
	private String proposedBatchSerialNumber;
	private String proposedPalletCode;
	private String proposedCaseCode;
	private String proposedHeNo;
	private String proposedPicker;
	private String assignedPickerId;
	private String reassignedPickerId;
	private Long deletionIndicator;
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
	private String reAllocatedBy;
	private Date reAllocatedOn = new Date();
	private String pickupCreatedBy;
	private Date pickupCreatedOn = new Date();
	private String pickupUpdatedBy;
	private Date pickupupdatedOn = new Date();
	private String pickerAssignedBy;
	private Date pickerAssignedOn = new Date();
	private String pickerReassignedBy;
	private Date pickerReassignedOn = new Date();
}