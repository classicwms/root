package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class StockAdjustment {

    private Long stockAdjustmentId;
    private Long stockAdjustmentKey;
    private String companyCode;
    private String branchCode;
    private String branchName;
    private Date dateOfAdjustment;
    private String isCycleCount;
    private String isDamage;
    private String itemCode;
    private String itemDescription;
    private Double adjustmentQty;
    private String unitOfMeasure;
    private String manufacturerCode;
    private String remarks;
    private String amsReferenceNo;
    private String isCompleted;
    private String warehouseId;
    private String palletCode;
    private String caseCode;
    private String packBarcodes;
    private String storageBin;
    private Long stockTypeId;
    private Long statusId;
    private Long specialStockIndicatorId;
    private String referenceOrderNo;
    private String storageMethod;
    private Long binClassId;
    private Date saUpdatedOn;
    private String barcodeId;
    private String cbm;
    private String cbmUnit;
    private String cbmPerQuantity;
    private String manufacturerName;
    private String levelId;
    private String origin;
    private String brand;
    private String referenceDocumentNo;
    private Double inventoryQuantity;
    private Double allocatedQuantity;
    private String inventoryUom;
    private Date manufacturerDate;
    private Date expiryDate;
    private Long deletionIndicator;
    private String referenceField1;
    private String referenceField2;
    private String referenceField3;
    private Double referenceField4;
    private String referenceField5;
    private String referenceField6;
    private String referenceField7;
    private String referenceField8;
    private String referenceField9;
    private String referenceField10;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    private String companyDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String stockTypeDescription;
    private Long middlewareId;
    private String middlewareTable;

    private Double beforeAdjustment;
    private Double afterAdjustment;
}