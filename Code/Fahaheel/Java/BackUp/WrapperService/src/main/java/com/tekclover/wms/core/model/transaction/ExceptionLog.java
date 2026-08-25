package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class ExceptionLog {

    private Long orderId;

    private String orderTypeId;

    private Date orderDate;

    private String errorMessage;

    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String refDocNumber;

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

//    private String updatedBy;
//    private Date updatedOn;
}
