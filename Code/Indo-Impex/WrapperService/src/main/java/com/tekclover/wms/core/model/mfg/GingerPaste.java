package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;

@Data
public class GingerPaste {

    private String companyCodeId;

    private String languageId;

    private String plantId;

    private String warehouseId;

    private String productionOrderNo;

    private Long productionOrderLineNo;

    private String receipeId;

    private String operationNumber;

    private String itemCode;

    private String languageDescription;

    private String companyDescription;

    private String plantDescription;

    private String warehouseDescription;

    private String operationDescription;

    private String phaseNumber;

    private String phaseDescription;

    private String referenceOrderNumber;

    private String referenceBatchNumber;

    private String itemType;

    private String itemDescription;

    private String batchNumber;

    private Double issuedQuantity;

    private Double waterQuantity;

    private Double outputQuantity;

    private String storageLocation;

    private Date pastePreparationStart;

    private Date pastePreparationEnd;

    private String noOfWorkers;

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

    private Date createdOn ;

    private String updatedBy;

    private Date updatedOn;

    private boolean uiProcessConfirm;
    private boolean beProcessConfirm;

}