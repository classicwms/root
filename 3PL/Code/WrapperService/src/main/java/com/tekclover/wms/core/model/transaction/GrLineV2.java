package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;

@Data
@ToString(callSuper = true)
public class GrLineV2 extends GrLine {

    private Double inventoryQuantity;
    private String barcodeId;
    private Double cbm;
    private String cbmUnit;
    private String manufacturerCode;
    private String manufacturerName;
    private String origin;
    private String brand;
    private String rejectType;
    private String rejectReason;
    private Double cbmQuantity;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String interimStorageBin;
    private String middlewareId;
    private String middlewareTable;
    private String manufactureFullName;
    private String middlewareHeaderId;
    private String purchaseOrderNumber;
    private String referenceDocumentType;

    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private Long lineNumber;
    private String AMSSupplierInvoiceNo;

    //threePl
    private Double threePLCbm;
    private String threePLUom;
    private String threePLBillStatus;
    private Double threePLLength;
    private Double threePLHeight;
    private Double threePLWidth;
    private Double rate;
    private String currency;
    private Double totalThreePLCbm;
    private Double totalRate;
    private Long grInvoice = 0L;
    private String partnerText;

}