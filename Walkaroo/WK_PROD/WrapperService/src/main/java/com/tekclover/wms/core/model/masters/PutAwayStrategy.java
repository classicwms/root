package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.Date;

@Data
public class PutAwayStrategy {
    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String brand;

    private String article;

    private String category;

    private String proposedBin;

    private String companyIdAndDescription;

    private String plantIdAndDescription;

    private String warehouseIdAndDescription;

    private String capacity;

    private String gender;
    private String location;
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

    private String maxCapacity;
    private String superMaxCapacity;


    private String createdBy;

    private Date createdOn = new Date();

    private String updatedBy;

    private Date updatedOn = new Date();
}
