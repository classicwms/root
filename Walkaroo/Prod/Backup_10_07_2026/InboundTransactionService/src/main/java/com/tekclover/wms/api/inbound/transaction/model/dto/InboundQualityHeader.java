package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class InboundQualityHeader {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String refDocNumber;
    private String preInboundNo;
    private String inboundQualityNumber;
    private String itemCode;
    private String itemDescription;
    private String barcodeId;
    private String batchSerialNumber;
    private Double receivedQuantity;
    private Double sampleQuantity;
    private String impurities;
    private String analysis;
    private String storageSectionId;
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
    private String packBarcodes;
    private String parentProductionOrderNo;
}