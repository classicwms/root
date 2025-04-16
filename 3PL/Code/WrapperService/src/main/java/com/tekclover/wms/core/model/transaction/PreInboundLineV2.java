package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class PreInboundLineV2 extends PreInboundLine {

    private String manufacturerCode;
    private String manufacturerName;
    private String origin;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    private String middlewareId;
    private String middlewareTable;
    private String middlewareHeaderId;
    private String purchaseOrderNumber;
    private String manufactureFullName;
    private String referenceDocumentType;
    private String supplierName;

    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private String AMSSupplierInvoiceNo;
}