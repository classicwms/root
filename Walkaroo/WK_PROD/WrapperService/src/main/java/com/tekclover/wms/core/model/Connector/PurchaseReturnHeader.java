package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class PurchaseReturnHeader {

    private Long purchaseReturnHeaderId;

    private String companyCode;

    private String branchCode;

    private String returnOrderNo;

    private Date returnOrderDate;

    private String isCompleted;

    private Date updatedOn;

    private String isCancelled;

    private Long processedStatusId = 0L;

    private Date orderReceivedOn;

    private Date orderProcessedOn;

    private Set<PurchaseReturnLine> purchaseReturnLines;
}