package com.tekclover.wms.core.model.masters;

import lombok.Data;

@Data
public class AddImBatchSerial {

    private String itemCode;
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String storageMethod;
    private String maintenance;
    private String rangeFrom;
    private String rangeTo;
    private String currentNo;
    private Boolean shelfLifeIndicator;
    private Double totalShelfLife;
    private String shelfLifePeriod;
    private Double shelfLifeNotification;
    private String shelfLifeNotificationPeriod;
    private String statusId;
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
    private Long deletionIndicator;
    private Long sequenceIndicator;
}
