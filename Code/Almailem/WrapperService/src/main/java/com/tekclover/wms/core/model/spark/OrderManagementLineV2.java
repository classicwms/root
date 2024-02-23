package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OrderManagementLineV2 {

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
    private String proposedPackBarCode;
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
    private Timestamp requiredDeliveryDate;
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
    private Timestamp reAllocatedOn;
    private String pickupCreatedBy;
    private Timestamp pickupCreatedOn;
    private String pickupUpdatedBy;
    private Timestamp pickupUpdatedOn;
    private String pickerAssignedBy;
    private Timestamp pickerAssignedOn;
    private String pickerReassignedBy;
    private Timestamp pickerReassignedOn;

    //v2 fields
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
    private String supplierInvoiceNo;
    private String salesOrderNumber;
    private String pickListNumber;
    private String tokenNumber;
    private String manufacturerFullName;
    private String transferOrderNo;
    private String returnOrderNo;
    private String isCompleted;
    private String isCancelled;
    private String targetBranchCode;
}
