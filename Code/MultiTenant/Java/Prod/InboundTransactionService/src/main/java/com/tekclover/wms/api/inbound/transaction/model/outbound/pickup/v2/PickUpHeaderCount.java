package com.tekclover.wms.api.inbound.transaction.model.outbound.pickup.v2;

import lombok.Data;

import java.util.Date;


@Data
public class PickUpHeaderCount {

    private String languageId;

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String partnerCode;
    private String pickupNumber;
    private Long lineNumber;
    private String itemCode;
    private String proposedStorageBin;
    private String proposedPackBarCode;
    private Long outboundOrderTypeId;
    private Double pickToQty;
    private String pickUom;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String manufacturerPartNo;
    private Long statusId;
    private String assignedPickerId;
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
    private String remarks;
    private String pickupCreatedBy;
    private Date pickupCreatedOn;
    private String pickConfimedBy;
    private String pickUpdatedBy;
    private Date pickUpdatedOn;
    private String pickupReversedBy;
    private Date pickupReversedOn;
    private Double inventoryQuantity;
    private String manufacturerCode;
    private String manufacturerName;
    private String storageSectionId;
    private String origin;
    private String brand;
    private String barcodeId;
    private String levelId;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    private String middlewareId;
    private String middlewareTable;
    private String referenceDocumentType;
    private String salesOrderNumber;
    private String tokenNumber;
    private String targetBranchCode;

    private String fromBranchCode;
    private String isCompleted;
    private String isCancelled;
    private Date mUpdatedOn;
}
