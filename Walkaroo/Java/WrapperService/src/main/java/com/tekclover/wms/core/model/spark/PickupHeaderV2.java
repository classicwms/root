package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PickupHeaderV2 {

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
    private Timestamp pickupCreatedOn;
    private String pickConfimedBy;
    private Timestamp pickConfimedOn;
    private String pickUpdatedBy;
    private Timestamp pickUpdatedOn;
    private String pickupReversedBy;
    private Timestamp pickupReversedOn;

    // V2 fields
    private Double inventoryQuantity;
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
    private String middlewareTable;
    private String referenceDocumentType;
    private String salesOrderNumber;
    private String salesInvoiceNumber;
    private String supplierInvoiceNo;
    private String pickListNumber;
    private String tokenNumber;
    private String fromBranchCode;
    private String isCompleted;
    private String isCancelled;
    private Timestamp mUpdatedOn;
    private String targetBranchCode;
    private String batchSerialNumber;
    private String customerId;
    private String customerName;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
    private String barcodeId;

    private String shipToCode;
    private String shipToParty;
    private String specialStock;
    private String mtoNumber;
}