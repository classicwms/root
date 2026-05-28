package com.mnrclara.spark.core.model.wmscorev2;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InventoryV2 {

    private Long inventoryId;
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String palletCode;
    private String caseCode;
    private String packBarcodes;
    private String itemCode;
    private Long variantCode;
    private String variantSubCode;
    private String batchSerialNumber;
    private String storageBin;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String referenceOrderNo;
    private String storageMethod;
    private Long binClassId;
    private String description;
    private Double inventoryQuantity;
    private Double allocatedQuantity;
    private String inventoryUom;
    private Timestamp manufacturerDate;
    private Timestamp expiryDate;
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
    private Timestamp createdOn;
    private String updatedBy;
    private Timestamp updatedOn;
    private String manufacturerCode;

    // V2 fields
    private String barcodeId;
    private String cbm;
    private String cbmUnit;
    private String cbmPerQuantity;
    private String manufacturerName;
    private String levelId;
    private String origin;
    private String brand;
    private String referenceDocumentNo;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String stockTypeDescription;
}
