package com.iweb2b.core.model.integration;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateEvent {

    private String type;

    private String eventDescription;

    private Date eventTime;

    private String hubName;

    private String hubCode;

    private String failureReason;

    private String workerName;

    private String workerCode;

    private String employeeCode;

    private String employeeName;

    private String latitude;

    private String longitude;

    private String customerUpdate;

    private String executionStatus;

    private String statusExternal;

    private String carrierLocationCode;

    private String popImage;

    private String pocImage;


    private Long deletionIndicator = 0L;
    private String referenceField1;
    private String referenceField2;
    private String referenceField3;
    private String referenceField4;
    private String referenceField5;
    private String referenceField6;
    private String referenceField7;
    private String referenceField8;
    private String referenceField9;
    private String referenceField10;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
}
