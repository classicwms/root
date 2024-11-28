package com.tekclover.wms.core.model.mfg;

import lombok.Data;
import java.util.Date;

@Data
public class MasterOperation {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String operationNumber;
    private String phaseNumber;
    private String operationDescription;
    private String phaseDescription;
    private String workCenterId;
    private String workCenterName;
    private String setupTime;
    private String machineTime;
    private String loadingTime;
    private String leadTime;
    private String timeUnit;
    private String remarks;
    private Long statusId;
    private String statusDescription;
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
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
}