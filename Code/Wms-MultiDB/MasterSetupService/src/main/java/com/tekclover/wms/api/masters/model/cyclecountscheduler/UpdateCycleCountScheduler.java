package com.tekclover.wms.api.masters.model.cyclecountscheduler;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateCycleCountScheduler {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long cycleCountTypeId;
    private String schedulerNumber;
    private Long levelId;
    private String levelReference;
    private String SubLevelReference;
    private String countFrequency;
    private String dayOfCount;
    private Date countDate=new Date();
    private String statusId;
    private Long deletionIndicator;
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
}
