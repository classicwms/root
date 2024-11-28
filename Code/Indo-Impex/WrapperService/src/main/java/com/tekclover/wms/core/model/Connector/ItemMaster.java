package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;

@Data
public class ItemMaster {

    private Long itemMasterId;

    private String companyCode;

    private String branchCode;

    private String itemCode;

    private String itemDescription;

    private String unitOfMeasure;

    private Long itemGroupId;

    private Long secondaryItemGroupId;

    private String manufacturerCode;

    private String manufacturerShortName;

    private String manufacturerFullName;

    private String createdUsername;

    private Date itemCreationDate;

    private String isNew;

    private String isUpdate;

    private String isCompleted;

    private Date updatedOn;

    private Long processedStatusId;

    private Date orderReceivedOn;

    private Date orderProcessedOn;
//    private String remarks;
}
