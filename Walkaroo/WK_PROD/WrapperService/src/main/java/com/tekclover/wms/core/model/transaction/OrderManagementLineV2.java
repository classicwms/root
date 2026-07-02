package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
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
    private String customerId;
    private String customerName;
    
    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;

    private String shipToCode;
    private String shipToParty;
    private String specialStock;
    private String mtoNumber;
    private String stagingArea;
}