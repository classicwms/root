package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InboundLineV2 {

    private String languageId;
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String refDocNumber;
    private String preInboundNo;
    private Long lineNo;
    private String itemCode;
    private Double orderQty;
    private String orderUom;
    private Double acceptedQty;
    private Double damageQty;
    private Double putawayConfirmedQty;
    private Double varianceQty;
    private Long variantCode;
    private String variantSubCode;
    private Long inboundOrderTypeId;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String referenceOrderNo;
    private Long statusId;
    private String vendorCode;
    private Timestamp expectedArrivalDate;
    private String containerNo;
    private String invoiceNo;
    private String description;
    private String manufacturerPartNo;
    private String hsnCode;
    private String itemBarcode;
    private Double itemCaseQty;
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
    private String confirmedBy;
    private Timestamp confirmedOn;

    // V2 fields
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String manufacturerCode;
    private String manufacturerName;
    private String middlewareId;
    private String middlewareHeaderId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private String purchaseOrderNumber;
    private String supplierName;
    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
}
