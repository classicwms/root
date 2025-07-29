package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class DriverRemark {

    private String driverRemarkNo;

    private String preOutboundNo;

    private String refDocNumber;

    private String vehicleNO;

    private String driverName;

    private String companyCodeId;

    private String plantId;

    private String languageId;

    private String warehouseId;

    private String remarks;

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

    private Long statusId;

    private String createdBy;

    private Date createdOn = new Date();

    private String updatedBy;
    private Date updatedOn;

}
