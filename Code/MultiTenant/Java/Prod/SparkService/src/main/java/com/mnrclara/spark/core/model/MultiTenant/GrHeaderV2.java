package com.mnrclara.spark.core.model.MultiTenant;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class GrHeaderV2 {

    private String languageId;
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String preInboundNo;
    private String refDocNumber;
    private String stagingNo;
    private String goodsReceiptNo;
    private String palletCode;
    private String caseCode;
    private Long inboundOrderTypeId;
    private Long statusId;
    private String grMethod;
    private String containerReceiptNo;
    private String dockAllocationNo;
    private String containerNo;
    private String vehicleNo;
    private Timestamp expectedArrivalDate;
    private Timestamp goodsReceiptDate;
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
    private String confirmedBy;
    private Timestamp confirmedOn;

    // v2fields
    private Double acceptedQuantity;
    private Double damagedQuantity;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String middlewareId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String referenceDocumentType;
    /*-------------------------------------------Reeferon-----------------------------------*/

    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCreate;
    private String customerId;
    private String customerName;
}
