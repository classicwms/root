package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerMaster {

    private Long customerMasterId;

    private String companyCode;

    private String branchCode;

    private String customerCode;

    private String customerName;

    private String homeAddress1;

    private String homeAddress2;

    private String homeTelNumber;

    private String civilIdNumber;

    private String mobileNumber;

    private String createdUsername;

    private Date customerCreationDate;

    private String isNew;

    private String isUpdate;

    private String isCompleted;

    private Date updatedOn;

    private Long processedStatusId;

    private Date orderReceivedOn;

    private Date orderProcessedOn;
//    private String remarks;
}