package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class QualityHeaderV3 {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String partnerCode;
    private String pickupNumber;
    private String qualityInspectionNo;
    private String actualHeNo;
    private Long outboundOrderTypeId;
    private Long statusId;
    private String qcToQty;
    private String qcUom;
    private String manufacturerPartNo;
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
    private String remarks;
    private String qualityCreatedBy;
    private Timestamp qualityCreatedOn;
    private String qualityConfirmedBy;
    private Timestamp qualityConfirmedOn;
    private String qualityUpdatedBy;
    private Timestamp qualityUpdatedOn;
    private String qualityReversedBy;
    private Timestamp qualityReversedOn;

    // V2 fields
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private Long middlewareId;
    private String middlewareTable;
    private String referenceDocumentType;
    private String salesOrderNumber;
    private String salesInvoiceNumber;
    private String supplierInvoiceNo;
    private String pickListNumber;
    private String tokenNumber;
    private String manufacturerName;
    private String targetBranchCode;
    private String batchSerialNumber;
    private String customerId;
    private String customerName;
    //Walkaroo Fields
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
    private String shipToCode;
    private String shipToParty;
    private String specialStock;
    private String mtoNumber;
}
