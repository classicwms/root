package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class PickingProductivityReport {

	private Double leadTime;
	private Double partsPerHr;
	private Double avgLeadTime;
	private Double parts;
	private Double orders;
	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String assignedPickerId;
	private Double totalPartsPerHr;
	private Double totalLeadTime;
	private Double avgTotalLeadTime;
	private Double totalParts;
	private Double totalOrders;

	private Double inventoryQuantity;
	private Double pickedCbm;
	private String cbmUnit;
	private String manufacturerCode;
	private String manufacturerName;
	private String origin;
	private String brand;
	private String barcodeId;
	private String levelId;
	private String statusDescription;
	private Long middlewareId;
	private Long middlewareHeaderId;
	private String middlewareTable;
	private String referenceDocumentType;
	private String salesOrderNumber;
	private String pickListNumber;
	private String tokenNumber;
	private String salesInvoiceNumber;
	private String supplierInvoiceNo;
	private String manufacturerFullName;
	private String targetBranchCode;
	private Double varianceQuantity;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String pickupNumber;
	private String itemCode;
	private String actualHeNo;
	private String pickedStorageBin;
	private String pickedPackCode;
	private Long outboundOrderTypeId;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private Double pickConfirmQty;
	private Double allocatedQty;
	private String pickUom;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String description;
	private String manufacturerPartNo;
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
	private Date pickupCreatedOn;
	private String pickupConfirmedBy;
	private Date pickupConfirmedOn;
	private String pickupUpdatedBy;
	private Date pickupUpdatedOn;
	private String pickupReversedBy;
	private Date pickupReversedOn;

}
