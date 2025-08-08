package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OutboundHeaderV2 extends OutboundHeader {

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

    private String countOfPickedLine;
    private String sumOfPickedQty;

    private String customerType;
    private String shipToCode;
    private String shipToParty;

}