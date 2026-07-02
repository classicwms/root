package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TransferInHeader {

    private Long transferInHeaderId;

    private String sourceCompanyCode;

    private String targetCompanyCode;

    private String transferOrderNo;

    private String sourceBranchCode;

    private String targetBranchCode;

    private Date transferOrderDate;

    private String isCompleted;

    private Date updatedOn;

    //ProcessedStatusIdOrderByOrderReceivedOn
    private Long processedStatusId = 0L;

    private Date orderReceivedOn;

    private Date orderProcessedOn;

    private List<TransferInLine> transferInLines;
}