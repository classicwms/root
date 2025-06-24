package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
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
    private String storageSectionId;
    private String parentProductionOrderNo;
    private Long notificationStatus = 0L;
    private String businessPartnerCode;
    private String batchSerialNumber;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;
    private Boolean crossDock;
    /*----------------------REEFERON--------------------------------------------------*/
    private Double pieceQty;
    private Double caseQty;
    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCreate;
    private String vehicleNo;
    private Date vehicleReportingDate;
    private Date vehicleUnloadingDate;
    private String receivingVariance;
    private String customerId;
    private String customerName;

}