package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.Date;


@Data
public class NotificationMessage {

    private Long notificationId;

    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String processId;

    private String userId;

    private String title;

    private String message;

    private Boolean menu = false;

    private Boolean tab = false;

    private String orderType;

    private Long documentId = 0L;

    private Long deletionIndicator = 0L;

    private String createdBy;

    private Date createdOn = new Date();

    private String updatedBy;

    private Date updatedOn = new Date();

    private String userType;

    private String storageBin;

    private boolean newCreated;

    private String documentNumber;

    private String referenceNumber;

    private Boolean status;

}
