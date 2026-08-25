package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class PickListHeaderV2 {

    private Long pickListHeaderId;

    private String companyCode;

    private String branchCode;

    private String salesOrderNo;

    private String tokenNumber;

    private String pickListNo;

    private Date pickListdate;

    private String isCompleted;

    private Date updatedOn;

    private String isCancelled;

    private Long processedStatusId;

    private Date orderReceivedOn;

    private Date orderProcessedOn;

    private Set<PickListLine> pickListLines;
}
