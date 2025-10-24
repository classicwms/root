package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class TransferOutHeader {

    private Long transferOutHeaderId;

    private String sourceCompanyCode;

    private String targetCompanyCode;

    private String transferOrderNumber;

    private String sourceBranchCode;

    private String targetBranchCode;

    private Date transferOrderDate;

    private String fulfilmentMethod;

    private String isCompleted;

    private Date updatedOn;

    private Long processedStatusId;

    private Date orderReceivedOn;

    private Date orderProcessedOn;

    private Set<TransferOutLine> transferOutLines;
}
