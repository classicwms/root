package com.mnrclara.spark.core.model.Almailem;

import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;


@Data
public class OutBoundReversalV2 {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String outboundReversalNo;
    private String reversalType;
    private String refDocNumber;
    private String partnerCode;
    private String itemCode;
    private String packBarcode;
    private Double reversedQty;
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
    private String reversedBy;
    private Timestamp reversedOn;

    //v2 fields
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private Long middlewareId;
    private String middlewareTable;
    private String referenceDocumentType;
    private String salesOrderNumber;
    private String targetBranchCode;
    private String salesInvoiceNumber;
    private String supplierInvoiceNo;
    private String pickListNumber;
    private String tokenNumber;
    private String manufacturerName;
    private String barcodeId;
    private String customerCode;
    private String TransferRequestType;
}