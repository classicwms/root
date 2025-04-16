package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class InhouseTransferLine {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String transferNumber;
    private String sourceItemCode;
    private Long sourceStockTypeId;
    private String sourceStorageBin;
    private String targetItemCode;
    private Long targetStockTypeId;
    private String targetStorageBin;
    private Double transferOrderQty;
    private Double transferConfirmedQty;
    private String transferUom;
    private String palletCode;
    private String caseCode;
    private String packBarcodes;
    private String sourceBarcodeId;
    private String targetBarcodeId;
    private Long specialStockIndicatorId;
    private Long statusId;
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
    private Long deletionIndicator = 0L;
    private String remarks;
    private String createdBy;
    private Date createdOn = new Date();
    private String confirmedBy;
    private Date confirmedOn = new Date();
    private String updatedBy;
    private Date updatedOn = new Date();

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String sourceStockTypeDescription;
    private String targetStockTypeDescription;
    private String statusDescription;

    //Almailem Code
    private String manufacturerName;

}
