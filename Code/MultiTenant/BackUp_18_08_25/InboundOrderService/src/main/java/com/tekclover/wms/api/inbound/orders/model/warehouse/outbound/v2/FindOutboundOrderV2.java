package com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindOutboundOrderV2 {

    private List<String> branchCode;
    private List<String> companyCode;
    private List<String> warehouseID;
    private List<String> languageId;

    private List<String> orderId;
    private List<String> refDocumentNo;
    private Date fromOrderReceivedOn;
    private Date toOrderReceivedOn;
    private Date fromOrderProcessedOn;
    private Date toOrderProcessedOn;
    private List<Long> processedStatusId;
    private List<String> pickListNumber;
    private List<String> customerId;
    private Date fromSalesInvoiceDate;
    private Date toSalesInvoiceDate;
    private List<String> salesInvoiceNumber;
    private List<Long> middlewareId;
    private List<String> middlewareTable;
}
