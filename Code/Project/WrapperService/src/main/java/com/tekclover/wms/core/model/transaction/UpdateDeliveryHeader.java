package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class UpdateDeliveryHeader {

    private String vehicleNo;

    private String driverId;

    private String driverName;

    private String routeId;

    private String deliveryFailureReason;

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
