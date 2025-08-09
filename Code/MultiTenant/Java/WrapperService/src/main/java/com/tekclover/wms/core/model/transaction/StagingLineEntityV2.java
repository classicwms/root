package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

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
    private String barcodeId;
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
    private String AMSSupplierInvoiceNo;
    private String storageSectionId;
    private String parentProductionOrderNo;
    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;

    /*--------------------------REEFERON--------------------------*/
    private Double pieceQty;
    private Double caseQty;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;

    /*--------------------------------------Namratha-------------------------------*/
    private Boolean crossDock;
    private String goodsReceiptNo;
    private String sourceBranchCode;
    /*-------------------------------------------Reeferon-----------------------------------*/
    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCreate;
    private String vehicleNo;
    private Date vehicleReportingDate;
    private Date vehicleUnloadingDate;
}