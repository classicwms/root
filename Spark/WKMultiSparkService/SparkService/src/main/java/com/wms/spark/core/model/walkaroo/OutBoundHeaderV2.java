package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OutBoundHeaderV2 {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String partnerCode;
    private String deliveryOrderNo;
    private String referenceDocumentType;
    private Long outboundOrderTypeId;
    private Long statusId;
    private Timestamp refDocDate;
    private Timestamp requiredDeliveryDate;
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
    private String createdBy;
    private Timestamp createdOn;
    private String deliveryConfirmedBy;
    private Timestamp deliveryConfirmedOn;
    private String updatedBy;
    private Timestamp updatedOn;
    private String reversedBy;
    private Timestamp reversedOn;

    // V2 fields
    private String invoiceNumber;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private Long middlewareId;
    private String middlewareTable;
    private String salesOrderNumber;
    private String salesInvoiceNumber;
    private String supplierInvoiceNo;
    private String pickListNumber;
    private String tokenNumber;

    private Timestamp invoiceDate;
    private String deliveryType;
    private String customerId;
    private String customerName;
    private String address;
    private String phoneNumber;
    private String alternateNo;
    private String status;

    private String fromBranchCode;
    private String isCompleted;
    private String isCancelled;
    private Timestamp mUpdatedOn;
    private String countOfPickedLine;
    private String sumOfPickedQty;
}
