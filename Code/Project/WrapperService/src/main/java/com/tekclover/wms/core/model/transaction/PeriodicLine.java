package com.tekclover.wms.core.model.transaction;

import java.util.Date;

import lombok.Data;

@Data
public class PeriodicLine { 
	private String languageId;
	private String companyCode;
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
	private Date createdOn;
	private String confirmedBy;
	private Date confirmedOn;
	private String countedBy;
	private Date countedOn;
	
	public PeriodicLine(String languageId,
			String companyCode,
			String plantId,
			String warehouseId,
			String cycleCountNo,
			String storageBin,
			String itemCode,
			String packBarcodes,
			Long variantCode,
			String variantSubCode,
			String batchSerialNumber,
			Long stockTypeId,
			String specialStockIndicator,
			Double inventoryQuantity,
			String inventoryUom,
			Double countedQty,
			Double varianceQty,
			String cycleCounterId,
			String cycleCounterName,
			Long statusId,
			String cycleCountAction,
			String referenceNo,
			Long approvalProcessId,
			String approvalLevel,
			String approverCode,
			String approvalStatus,
			String remarks,
			String referenceField1,
			String referenceField2,
			String referenceField3,
			String referenceField4,
			String referenceField5,
			String referenceField6,
			String referenceField7,
			String referenceField8,
			String referenceField9,
			String referenceField10,
			Long deletionIndicator,
			String createdBy,
			Date createdOn,
			String confirmedBy,
			Date confirmedOn,
			String countedBy,
			Date countedOn) {
				this.languageId = languageId;
				this.companyCode = companyCode;
				this.plantId = plantId;
				this.warehouseId = warehouseId;
				this.cycleCountNo = cycleCountNo;
				this.storageBin = storageBin;
				this.itemCode = itemCode;
				this.packBarcodes = packBarcodes;
				this.variantCode = variantCode;
				this.variantSubCode = variantSubCode;
				this.batchSerialNumber = batchSerialNumber;
				this.stockTypeId = stockTypeId;
				this.specialStockIndicator = specialStockIndicator;
				this.inventoryQuantity = inventoryQuantity;
				this.inventoryUom = inventoryUom;
				this.countedQty = countedQty;
				this.varianceQty = varianceQty;
				this.cycleCounterId = cycleCounterId;
				this.cycleCounterName = cycleCounterName;
				this.statusId = statusId;
				this.cycleCountAction = cycleCountAction;
				this.referenceNo = referenceNo;
				this.approvalProcessId = approvalProcessId;
				this.approvalLevel = approvalLevel;
				this.approverCode = approverCode;
				this.approvalStatus = approvalStatus;
				this.remarks = remarks;
				this.referenceField1 = referenceField1;
				this.referenceField2 = referenceField2;
				this.referenceField3 = referenceField3;
				this.referenceField4 = referenceField4;
				this.referenceField5 = referenceField5;
				this.referenceField6 = referenceField6;
				this.referenceField7 = referenceField7;
				this.referenceField8 = referenceField8;
				this.referenceField9 = referenceField9;
				this.referenceField10 = referenceField10;
				this.deletionIndicator = deletionIndicator;
				this.createdBy = createdBy;
				this.createdOn = createdOn;
				this.confirmedBy = confirmedBy;
				this.confirmedOn = confirmedOn;
				this.countedBy = countedBy;
				this.countedOn = countedOn;
	}
}
