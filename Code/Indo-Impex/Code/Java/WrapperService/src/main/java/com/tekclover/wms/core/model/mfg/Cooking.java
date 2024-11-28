package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class Cooking {
    private String bomItem;
    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String receipeId;
    private String productionOrderNo;
    private Long productionOrderLineNo;
    private String operationNumber;
    private String itemCode;
    private String operationDescription;
    private String phaseNumber;
    private String phaseDescription;
    private String referenceOrderNumber;
    private String referenceBatchNumber;
    private String itemType;
    private String itemDescription;
    private String batchNumber;
    private Date cookingStartTime;
    private Date cookingEndTime;
    private Date packingStartTime;
    private Date packingEndTime;
    private Date retortStartTime;
    private Date retortEndTime;
    private String firstPacketTemperatureDuringRetortLoading;
    private Double outputQuantity;
    private String numberOfWorkers;
    private String supervisorName;
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
    private boolean uiProcessConfirm;
    private boolean beProcessConfirm;
}