package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationMessage {

    private Long notificationId;

    private String languageId;

    private String companyId;
    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String title;

    private String message;

    private Boolean menu;

    private Boolean tab;

    private String orderType;

    private Long documentId;

    private Long deletionIndicator;

    private String createdBy;

    private Date createdOn;

    private String updatedBy;

    private Date updatedOn;

    private String userType;
    private String storageBin;
    private boolean newCreated;
    private String documentNumber;
    private String referenceNumber;
    private Boolean status;

}