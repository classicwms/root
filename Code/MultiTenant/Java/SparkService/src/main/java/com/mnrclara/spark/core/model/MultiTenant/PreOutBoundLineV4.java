package com.mnrclara.spark.core.model.MultiTenant;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PreOutBoundLineV4 {

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
    private String targetBranchCode;
    private String customerId;
    private String customerName;

    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    //    private Double mrp;
//    private String itemType;
//    private String itemGroup;
//    private Integer imsSaleTypeCode;
//    private Double mrp;
//    private String itemType;
//    private String itemGroup;
    /*----------------------REEFERON--------------------------------------------------*/
    private Double qtyInCase;
    private Double qtyInPiece;
}