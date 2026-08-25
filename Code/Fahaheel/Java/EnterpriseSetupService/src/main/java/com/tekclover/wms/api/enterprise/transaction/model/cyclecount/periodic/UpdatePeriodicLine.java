package com.tekclover.wms.api.enterprise.transaction.model.cyclecount.periodic;

import java.util.Date;

import lombok.Data;

@Data
public class UpdatePeriodicLine {
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String cycleCountNo;
	private String storageBin;
	private String itemCode;
	private String packBarcodes;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private Long stockTypeId;
	private String specialStockIndicator;
	private Double inventoryQuantity;
	private String inventoryUom;
	private Double countedQty;
	private Double varianceQty;
	private String cycleCounterId;
	private String cycleCounterName;
	private Long statusId;
	private String cycleCountAction;
	private String referenceNo;
	private Long approvalProcessId;
	private String approvalLevel;
	private String approverCode;
	private String approvalStatus;
	private String remarks;
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
	private String createdBy;
	private Date createdOn = new Date();
	private String confirmedBy;
	private Date confirmedOn = new Date();
	private String countedBy;
	private Date countedOn = new Date();
}