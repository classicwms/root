package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class OrderManagementLineV2 extends OrderManagementLine {

    private String manufacturerCode;
    private String manufacturerName;
    private String origin;
    private String brand;
    private String barcodeId;
    private String levelId;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    private Long middlewareId;
    private String middlewareTable;
    private Long middlewareHeaderId;
    private String referenceDocumentType;
    private String manufactureFullName;
    private String salesOrderNumber;
    private String supplierInvoiceNo;
    private String salesInvoiceNumber;
    private String pickListNumber;
    private String tokenNumber;
    private String targetBranchCode;

    private String transferOrderNo;
    private String returnOrderNo;
    private String isCompleted;
    private String isCancelled;
}