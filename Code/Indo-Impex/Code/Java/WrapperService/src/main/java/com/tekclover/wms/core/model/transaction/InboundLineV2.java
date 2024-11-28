package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
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
    private String batchSerialNumber;
    private String parentProductionOrderNo;
    
    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
	private String barcodeId;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;
    private String brand;
}