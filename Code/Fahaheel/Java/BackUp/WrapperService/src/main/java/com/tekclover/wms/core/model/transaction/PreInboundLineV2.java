package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
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
    /*-------------------------------------------Reeferon-----------------------------------*/
    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCreate;
    private Date manufacturerDate;
    private String vehicleNo;
    private Date vehicleReportingDate;
    private Date vehicleUnloadingDate;

}