package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class PeriodicHeader {

    private Long periodicHeaderId;

    private String companyCode;

    private String cycleCountNo;

    private String branchCode;

    private String branchName;

    private Date cycleCountCreationDate;

    private String isNew;

    private String isCancelled;

    private String isCompleted;

    private Date updatedOn;

    private Long processedStatusId;

    private Date orderReceivedOn;

    private Date orderProcessedOn;

    private Set<PeriodicLine> periodicLines;
}
