package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
//    private String tracking;
    
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
}