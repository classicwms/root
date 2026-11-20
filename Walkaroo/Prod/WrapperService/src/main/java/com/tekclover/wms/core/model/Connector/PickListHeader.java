package com.tekclover.wms.core.model.Connector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Data
public class PickListHeader {

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
