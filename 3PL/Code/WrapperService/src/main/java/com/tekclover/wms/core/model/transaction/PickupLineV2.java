package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class PickupLineV2 extends PickupLine {

    private Double inventoryQuantity;
    private Double pickedCbm;
    private String cbmUnit;
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

    private String middlewareId;
    private String middlewareTable;
    private String middlewareHeaderId;
    private String referenceDocumentType;
    private String manufactureFullName;
    private String salesOrderNumber;
    private String supplierInvoiceNo;
    private String salesInvoiceNumber;
    private String pickListNumber;
    private String tokenNumber;
    private String targetBranchCode;
    private Double varianceQuantity;
    private Integer imsSaleTypeCode;
    private Double rate;
    private Double threePLCbm;
    private String threePLUom;
    private Double threePLCbmPerQty;
    private Double threePLRatePerQty;
    private String currency;
    private Double totalThreePLCbm;
    private Double totalRate;
    private Long pickingInvoice = 0L;
}