package com.tekclover.wms.api.masters.model.workcenter;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class AddWorkCenter {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long workCenterId;
    private String workCenterType;
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

}
