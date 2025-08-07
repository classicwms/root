package com.mnrclara.spark.core.model.Almailem.joinspark;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class InboundHeaderV3 {

    private String languageId;
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String refDocNumber;
    private String preInboundNo;
    private Long statusId;
    private Long inboundOrderTypeId;
    private String containerNo;
    private String vechicleNo;
    private String headerText;
//    private String is_deleted;
//    private String ref_field_1;
//    private String ref_field_2;
//    private String ref_field_3;
//    private String ref_field_4;
//    private String ref_field_5;
//    private String ref_field_6;
//    private String ref_field_7;
//    private String ref_field_8;
//    private String ref_field_9;
//    private String ref_field_10;
    private String createdBy;
    private Timestamp createdOn;
    private String updatedBy;
    private Timestamp updatedOn;
    private String confirmedBy;
    private Timestamp confirmedOn;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String purchaseOrderNumber;
//    private String middleware_id;
//    private String middleware_table;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private Long countOfOrderLines;
    private Long receivedLines;
    private Timestamp transferOrderDate;
//    private String is_cancelled;
//    private Timestamp m_updated_on;
    private String sourceBranchCode;
    private String sourceCompanyCode;
    private Double totalOrderQty;
    private Double totalAcceptedQty;
    private Double totalDamageQty;



}
