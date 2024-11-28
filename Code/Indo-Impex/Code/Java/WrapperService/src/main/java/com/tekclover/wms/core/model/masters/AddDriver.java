package com.tekclover.wms.core.model.masters;

import lombok.Data;


@Data
public class AddDriver {

    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private Long driverId;

    private String driverName;

    private String phoneNUmber;

    private String eMailId;

    private String address1;

    private String address2;

    private String userId;

    private String password;

    private String confirmPassword;

    private Boolean resetPassword;

    private Boolean newDelivery;

    private Boolean inTransit;

    private Boolean completed;

    private Boolean reAttempt;

    private Boolean returns;

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
