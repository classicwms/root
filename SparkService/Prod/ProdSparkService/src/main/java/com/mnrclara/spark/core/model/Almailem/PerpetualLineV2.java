package com.mnrclara.spark.core.model.Almailem;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class PerpetualLineV2 {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String cycleCountNo;
    private String storageBin;
    private String itemCode;
    private String packBarcodes;
    private String itemDesc;
    private String manufacturerPartNo;
    private Long variantCode;
    private String variantSubCode;
    private String batchSerialNumber;
    private Long stockTypeId;
    private String specialStockIndicator;
    private String storageSectionId;
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
    private Timestamp createdOn;
    private String confirmedBy;
    private Timestamp confirmedOn;
    private String countedBy;
    private Timestamp countedOn;

    //v2 fields
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String manufacturerName;
    private String manufacturerCode;
    private String barcodeId;
    private String middlewareId;
    private String middlewareHeaderId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private Double frozenQty;
    private Double inboundQuantity;
    private Double outboundQuantity;
    private Double firstCountedQty;
    private Double secondCountedQty;
}
