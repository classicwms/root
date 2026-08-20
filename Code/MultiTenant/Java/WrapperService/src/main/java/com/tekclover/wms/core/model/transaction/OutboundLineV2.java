package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OutboundLineV2 extends OutboundLine {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    private String salesInvoiceNumber;
    private String pickListNumber;
    private Date invoiceDate;
    private String deliveryType;
    private String customerId;
    private String customerName;
    private String address;
    private String phoneNumber;
    private String alternateNo;
    private String status;
    private String manufacturerName;

    private String middlewareId;
    private String middlewareTable;
    private String middlewareHeaderId;
    private String referenceDocumentType;
    private String manufactureFullName;
    private String salesOrderNumber;
    private String tokenNumber;
    private String targetBranchCode;
    private String transferOrderNo;
    private String returnOrderNo;
    private String isCompleted;
    private String isCancelled;
    private String barcodeId;
    private String handlingEquipment;
    private String customerType;

    private String assignedPickerId;
    private Integer imsSaleTypeCode;

    /*----------------REEFERON------------------------------------------------------*/

    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCrate;
    private String deliveredPercentage;
//    private String tracking;
    
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
    private String brand;
    private String reasons;

    /*-------------------------------Namratha-------------------------------*/
    private Date manufacturerDate;
    private Date expiryDate;
}