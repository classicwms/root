package com.tekclover.wms.api.masters.model.threepl.cbminbound;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddCbmInbound {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    @NotBlank(message = "Ware House Id  is mandatory")
    private String warehouseId;
    @NotBlank(message = "Item Code  is mandatory")
    private String itemCode;
    private Long packType;
    private String uomId;
    private Double length;
    private Double width;
    private Double height;
    private Double cbm;
    private Double packQty;
    private Long statusId;
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

}
