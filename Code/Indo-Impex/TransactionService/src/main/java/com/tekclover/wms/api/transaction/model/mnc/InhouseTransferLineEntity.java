package com.tekclover.wms.api.transaction.model.mnc;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class InhouseTransferLineEntity {

    private String languageId;
    private String companyCodeId;
    private String plantId;

    @NotBlank(message = "Warehouse Id is mandatory")
    private String warehouseId;

    @NotBlank(message = "transfer Number is mandatory")
    private String transferNumber;

    @NotBlank(message = "source Item Code is mandatory")
    private String sourceItemCode;

    private Long sourceStockTypeId;
    private String sourceStorageBin;
    private String targetItemCode;
    private Long stockTypeId;
    private String targetStorageBin;
    private Long targetStockTypeId;
    private Double transferOrderQty;
    private Double transferConfirmedQty;
    private String transferUom;
    private String palletCode;
    private String caseCode;
    private String packBarcodes;
    private String barcodeId;
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
    private Long deletionIndicator;
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
    private String sourceStorageSectionId;
    private String targetStorageSectionId;

    //Almailem Code
    private String manufacturerName;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
}