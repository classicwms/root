package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
public class PutAwayHeaderV2 extends PutAwayHeader {

    private Double inventoryQuantity;
    private String barcodeId;
    private Date manufacturerDate;
    private Date expiryDate;
    private String manufacturerCode;
    private String manufacturerName;
    private String origin;
    private String brand;
    private Double orderQty;
    private String cbm;
    private String cbmUnit;
    private Double cbmQuantity;
    private String approvalStatus;
    private String remark;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String actualPackBarcodes;

    private String middlewareId;
    private String middlewareTable;
    private String referenceDocumentType;

    private Date transferOrderDate;
    private String isCompleted;
    private String isCancelled;
    private Date mUpdatedOn;
    private String sourceBranchCode;
    private String sourceCompanyCode;
    private String levelId;
    private String customerCode;
    private String TransferRequestType;
    private String AMSSupplierInvoiceNo;
    // 3PL
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
    private Long putAwayInvoice = 0L;
}