package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;

@Data
public class OperationConsumption {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String productionOrderNo;
    private Long productionOrderLineNo;
    private String itemCode;
    private String itemType;
    private String itemGroup;
    private String receipeId;
    private String operationNumber;
    private String phaseNumber;
    private String operationDescription;
    private String phaseDescription;
    private String itemDescription;
    private String bomItem;
    private Double bomQuantity;
    private Double receipeQuantity;
    private Double issuedQuantity;
    private Double consumedQuantity;
    private Double yield;
    private Double loss;
    private String uom;
    private String issuedBy;
    private Date issuedOn;
    private String batchNumber;
    private Date batchDate;
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
    private String orderConfirmedBy;
    private Date orderConfirmedOn;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private boolean uiProcessConfirm;
    private boolean beProcessConfirm;
    private String bomNumber;
    private String parentProductionOrderNo;
    private String productionOrderType;
    private String itemTypeDescription;
    private String itemGroupDescription;
}