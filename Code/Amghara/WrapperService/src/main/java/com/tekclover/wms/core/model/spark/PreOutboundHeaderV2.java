package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PreOutboundHeaderV2 {
        private String languageId;
        private String companyCodeId;
        private String plantId;
        private String warehouseId;
        private String refDocNumber;
        private String preOutboundNo;
        private String partnerCode;
        private Long outboundOrderTypeId;
        private String referenceDocumentType;
        private Long statusId;
        private Timestamp refDocDate;
        private Timestamp requiredDeliveryDate;
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
        private String createdBy;
        private Timestamp createdOn;
        private String updatedBy;
        private Timestamp updatedOn;

        //v2 fields

        private String companyDescription;
        private String plantDescription;
        private String warehouseDescription;
        private String statusDescription;
        private Long middlewareId;
        private String middlewareTable;
        private String salesOrderNumber;
        private String pickListNumber;
        private String tokenNumber;
        private String fromBranchCode;
        private String isCompleted;
        private String isCancelled;
        private Timestamp mUpdatedOn;
        private String targetBranchCode;
        private Integer imsSaleTypeCode;
}