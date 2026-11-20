package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StockReceiptHeader {

    private Long stockReceiptHeaderId;

    private String companyCode;

    private String branchCode;

    private String receiptNo;

    private String isCompleted;

    private Date updatedOn;

    //ProcessedStatusIdOrderByOrderReceivedOn
    private Long processedStatusId = 0L;

    private Date orderReceivedOn;

    private Date orderProcessedOn;

    private List<StockReceiptLine> stockReceiptLines;
}
