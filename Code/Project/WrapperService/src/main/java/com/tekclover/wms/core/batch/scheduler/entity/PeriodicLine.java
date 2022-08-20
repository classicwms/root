package com.tekclover.wms.core.batch.scheduler.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class PeriodicLine { 
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String cycleCountNo;
	private String storageBin;
	private String itemCode;
	private String itemDesc;
	private String packBarcodes;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private Long stockTypeId;
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
	private String manufacturerPartNo;
	private String storageSectionId;
	private Long specialStockIndicator;
}
