package com.tekclover.wms.core.model.spark;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class PickupLine {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
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
    private Timestamp pickupCreatedOn;
    private String pickupConfirmedBy;
    private Timestamp pickupConfirmedOn;
    private String pickupUpdatedBy;
    private Timestamp pickupUpdatedOn;
    private String pickupReversedBy;
    private Timestamp pickupReversedOn;
    //v2fields
    private Double inventoryQuantity;
    private Double pickedCbm;
    private String cbmUnit;
    private String manufacturerCode;
    private String manufacturerName;
    private String origin;
    private String brand;
    private String partnerItemBarcode;
    private String levelId;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
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
    private Integer imsSaleTypeCode;
       private Double pickCbm;
    private String customerId;
    private String customerName;
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;
    private Timestamp manufacturerDate;
    private Timestamp expiryDate;
    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCrate;

}