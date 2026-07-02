package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InventoryMovementV2 {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long movementType;
    private Long submovementType;
    private String palletCode;
    private String caseCode;
    private String packBarcodes;
    private String itemCode;
    private Long variantCode;
    private String variantSubCode;
    private String batchSerialNumber;
    private String movementDocumentNo;
    private String manufacturerName;
    private String storageBin;
    private String storageMethod;
    private String description;
    private Long stockTypeId;
    private Long specialStockIndicator;
    private String movementQtyValue;
    private Double movementQty;
    private Double balanceOHQty;
    private String inventoryUom;
    private String refDocNumber;
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
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String barcodeId;
}
