package com.almailem.ams.api.connector.model.stockreceipt;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchStockReceiptHeader {

    private List<Long> stockReceiptHeaderId;
    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> receiptNo;

    private Date fromOrderReceivedOn;

    private Date toOrderReceivedOn;

    private Date fromOrderProcessedOn;

    private Date toOrderProcessedOn;

    private List<Long> processedStatusId;

}
