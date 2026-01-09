package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ContainerReceiptV2 {
    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String preInboundNo;

    private String refDocNumber;

    private String containerReceiptNo;

    private Timestamp containerReceivedDate;

    private String containerNo;

    private Long statusId;

    private String containerType;

    private String partnerCode;

    private String invoiceNo;

    private String consignmentType;

    private String origin;

    private String numberOfPallets;

    private String numberOfCases;

    private String dockAllocationNo;

    private String remarks;

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

    private Timestamp createdOn;

    private String updatedBy;

    private Timestamp updatedOn;

    private String companyDescription;

    private String plantDescription;

    private String warehouseDescription;

    private String statusDescription;

    private String purchaseOrderNumber;

    private String middlewareId;

    private String middlewareTable;
}
