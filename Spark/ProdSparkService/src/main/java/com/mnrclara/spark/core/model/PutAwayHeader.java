package com.mnrclara.spark.core.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class PutAwayHeader {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preInboundNo;
    private String refDocNumber;
    private String goodsReceiptNo;
    private Long inboundOrderTypeId;
    private String palletCode;
    private String caseCode;
    private String packBarcodes;
    private String putAwayNumber;
    private String proposedStorageBin;
    private Double putAwayQuantity;
    private String putAwayUom;
    private Long strategyTypeId;
    private String strategyNo;
    private String proposedHandlingEquipment;
    private String assignedUserId;
    private Long statusId;
    private String quantityType;
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
    private Long deletionIndicator;
    private String createdBy;
    private Timestamp createdOn;
    private String updatedBy;
    private Timestamp updatedOn;
    private String confirmedBy;
    private Timestamp confirmedOn;
}
