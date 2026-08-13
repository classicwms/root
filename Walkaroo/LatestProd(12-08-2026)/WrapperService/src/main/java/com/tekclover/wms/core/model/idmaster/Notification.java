package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.Date;

@Data
public class Notification {

    private Long id;
    private String topic;
    private String message;
    private String userId;
    private String userType;
    private Boolean status;
    private Boolean deletionIndicator;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;

    private String documentNumber;
    private String referenceNumber;
    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    //new Field
    private String storageBin;
    private Boolean newCreated;
}
