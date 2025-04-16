package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class StagingLineEntityV2 extends StagingLineEntity {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private Double inventoryQuantity;
    private String manufacturerCode;
    private String manufacturerName;
    private String manufacturerFullName;
    private String origin;
    private String brand;
    private String partner_item_barcode;
    private Double rec_accept_qty;
    private Double rec_damage_qty;

    private String middlewareId;
    private String middlewareTable;
    private String middlewareHeaderId;
    private String purchaseOrderNumber;
    private String referenceDocumentType;

    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private String customerCode;
    private String TransferRequestType;
    private String sourceBranchCode;
    private String AMSSupplierInvoiceNo;
}