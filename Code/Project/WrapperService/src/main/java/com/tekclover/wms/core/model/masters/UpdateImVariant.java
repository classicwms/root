package com.tekclover.wms.core.model.masters;

import lombok.Data;

@Data
public class UpdateImVariant {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
    private Long variantCode;
    private String variantType;
    private String variantSubCode;
    private Boolean variantIndicator;
    private String specificationFrom;
    private String specificationTo;
    private String specificationUom;
    private String variantBarcode;
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
