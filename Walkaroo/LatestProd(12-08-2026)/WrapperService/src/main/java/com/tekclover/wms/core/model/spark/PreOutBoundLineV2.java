package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class PreOutBoundLineV2 {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String refDocNumber;
    private String preOutboundNo;
    private String partnerCode;
    private Long lineNumber;
    private String itemCode;
    private Long outboundOrderTypeId;
    private Long variantCode;
    private String variantSubCode;
    private Long statusId;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String description;
    private String manufacturerPartNo;
    private String hsnCode;
    private String itemBarcode;
    private Double orderQty;
    private String orderUom;
    private String targetBranchCode;
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
    private String createdBy;
    private Timestamp createdOn;
    private String updatedBy;
    private Timestamp updatedOn;

    //v2 fields
    private String manufacturerCode;
    private String manufacturerName;
    private String origin;
    private String brand;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String tokenNumber;
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;
    private String referenceDocumentType;
    private String salesInvoiceNumber;
    private String supplierInvoiceNo;
    private String salesOrderNumber;
    private String manufacturerFullName;
    private String pickListNumber;
    private String transferOrderNo;
    private String returnOrderNo;
    private String isCompleted;
    private String isCancelled;
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