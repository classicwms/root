package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SalesReturnHeader {

    private Long salesReturnHeaderId;

    private String companyCode;

    private String branchCodeOfReceivingWarehouse;

    private String branchCode;

    private String returnOrderNo;

    private String isCompleted;

    private Date updatedOn;

    private String isCancelled;

    private Long processedStatusId;

    private Date orderReceivedOn;

    private Date orderProcessedOn;

    private List<SalesReturnLine> salesReturnLines;
}
