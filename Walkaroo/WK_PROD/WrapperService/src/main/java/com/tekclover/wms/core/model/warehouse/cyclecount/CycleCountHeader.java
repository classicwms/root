package com.tekclover.wms.core.model.warehouse.cyclecount;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class CycleCountHeader {

    private String orderId;

    private String companyCode;
    private String cycleCountNo;
    private String branchCode;
    private String branchName;
    private Date cycleCountCreationDate;
    private String isNew;
    private Date orderProcessedOn;

    private Date orderReceivedOn;
    private Long processedStatusId;

    //MiddleWare Fields
    private String isCompleted;
    private String isCancelled;
    private String stockCountType;
    private Date updatedOn;

    private Set<CycleCountLine> lines;

}