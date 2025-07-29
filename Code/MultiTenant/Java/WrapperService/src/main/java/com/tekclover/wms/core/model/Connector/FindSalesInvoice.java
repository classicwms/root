package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindSalesInvoice {

    private List<Long> salesInvoiceId;
    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> salesInvoiceNumber;
    private List<String> salesOrderNumber;
    private List<String> pickListNumber;
    private List<String> customerId;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;


}
