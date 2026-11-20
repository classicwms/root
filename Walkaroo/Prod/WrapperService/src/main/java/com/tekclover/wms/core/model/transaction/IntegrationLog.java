package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class IntegrationLog {

    private Long orderId;

    private String orderTypeId;

    private Date orderDate;

    private Long middlewareId;

    private String middlewareTable;

    private String errorMessage;

    private String companyCode;

    private String branchCode;

    private String referenceField1;

    private String referenceField2;

    private String referenceField3;

    private String referenceField4;

    private String referenceField5;

    private String createdBy;

    private Date createdOn;

    private String updatedBy;

    private Date updatedOn;

}
