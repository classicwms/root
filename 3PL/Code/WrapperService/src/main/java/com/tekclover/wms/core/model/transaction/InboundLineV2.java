package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class InboundLineV2 extends InboundLine {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String manufacturerCode;
    private String manufacturerName;
    private String middlewareId;
    private String middlewareTable;
    private String middlewareHeaderId;
    private String referenceDocumentType;
    private String manufactureFullName;
    private String purchaseOrderNumber;
    private String supplierName;

    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private String sourceBranchCode;
    private String sourceCompanyCode;
    private String AMSSupplierInvoiceNo;
}