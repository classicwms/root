package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;

@Data
public class MasterReceipe {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String receipeId;
    private String itemCode;
    private String bomNumber;
    private String operationNumber;
    private String phaseNumber;
    private String childItemCode;
    private Double requiredQuantity;
    private String uom;
    private String operationDescription;
    private String itemDescription;
    private String remarks;
    private Long statusId;
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
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String phaseDescription;
}