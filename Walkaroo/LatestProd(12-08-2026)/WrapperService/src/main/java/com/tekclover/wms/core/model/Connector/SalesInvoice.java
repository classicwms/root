package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;

@Data
public class SalesInvoice {

    private Long salesInvoiceId;

    private String companyCode;

    private String branchCode;

    private String salesInvoiceNumber;

    private String deliveryType;

    private String salesOrderNumber;

    private String pickListNumber;

    private Date invoiceDate;

    private String customerId;

    private String customerName;

    private String address;

    private String phoneNumber;

    private String alternateNo;

    private String status;

    private Long processedStatusId;

    private Date orderReceivedOn;

    private Date orderProcessedOn;
}