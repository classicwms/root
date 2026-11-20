package com.tekclover.wms.api.masters.model.impalletization;

import lombok.Data;

@Data
public class UpdateImPalletization {
    private Boolean palletizationIndicator;
    private Double palletLength;
    private Double palletWidth;
    private Double palletHeight;
    private String palletDimensionUom;
    private Double itemPerPalletQuantity;
    private Double caseLength;
    private Double caseWidth;
    private Double caseHeight;
    private String caseDimensionUom;
    private Double itemCaseQuantity;
    private Double casesPerPallet;
    private String statusId;
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
}
