package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import java.util.Date;

@Data
public class DeliveryHeader {

    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private Long deliveryNo;

    private String vehicleNo;

    private String driverId;

    private String noOfAttend;

    private String refDocNumber;

    private String remarks;

    private String driverName;

    private String routeId;

    private String deliveryFailureReason;

    private Long statusId;

    private String companyDescription;

    private String plantDescription;

    private String warehouseDescription;

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

    private Date createdOn = new Date();

    private String updatedBy;

    private Date updatedOn;

}
