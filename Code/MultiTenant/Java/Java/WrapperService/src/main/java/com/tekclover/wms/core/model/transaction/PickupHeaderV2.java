package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PickupHeaderV2 extends PickupHeader {

    private Double inventoryQuantity;
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
    private String referenceDocumentType;
    private String salesOrderNumber;
    private String tokenNumber;
    private String targetBranchCode;

    private String fromBranchCode;
    private String isCompleted;
    private String isCancelled;
    private Date mUpdatedOn;
    private Integer imsSaleTypeCode;

    private String salesInvoiceNumber;
    private String supplierInvoiceNo;
    private String pickListNumber;
    private Long notificationStatus = 0L;
    private String customerCode;
    private String TransferRequestType;
    private String customerName;
     private String batchSerialNumber;

    private String customerId;
    
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
    /*----------------REEFERON------------------------------------------------------*/

    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCrate;
    private Date manufacturerDate;
    private Date expiryDate;
    private String remainingDays;

}