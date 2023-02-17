package com.iweb2b.core.model.integration;

import lombok.Data;

import java.util.Date;

@Data
public class OriginDetail {

    private Long id;
    private Long consignmentTrackingId;
    private Long softDataUploadId;
    private String addressHubCode;
    private String name;
    private String phone;
    private String alternatePhone;
    private String addressLine1;
    private String addressLine2;
    private String pincode;
    private String district;
    private String city;
    private String state;
    private String country;
    private String latitude;
    private String longitude;

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
