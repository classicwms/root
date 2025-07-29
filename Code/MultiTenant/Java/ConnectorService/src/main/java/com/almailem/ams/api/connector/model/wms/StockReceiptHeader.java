package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
public class StockReceiptHeader {

    @Column(nullable = false)
    private String companyCode;

    @Column(nullable = false)
    private String branchCode;

    @Column(nullable = false)
    private String receiptNo;

    private String isCompleted;
    private Date updatedOn;

    //ProcessedStatusIdOrderByOrderReceivedOn
    @Column(name = "processedStatusId", columnDefinition = "bigint default'0'")
    private Long processedStatusId = 0L;

    @Column(name = "orderReceivedOn", columnDefinition = "datetime2 default getdate()")
    private Date orderReceivedOn;

    private Date orderProcessedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;

    private List<StockReceiptLine> stockReceiptLines;
}
