package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class NewPickupHeaderRes {

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String customerId;
    private String customerName;
    private String pickListNumber;
    private String shipToCode;
    private String shipToParty;
    private String statusDescription;
    private Timestamp pickupCreatedOn;
    private String languageId;
    private String pickupNumber;
    private String referenceField2;

    private String refDocNumber;
    private String barcodeId;
    private String itemCode;
    private String materialNo;
    private String priceSegment;
    private Long lineNumber;
    private String referenceDocumentType;
    private String storageBin;
    private Double pickToQuantity;
}