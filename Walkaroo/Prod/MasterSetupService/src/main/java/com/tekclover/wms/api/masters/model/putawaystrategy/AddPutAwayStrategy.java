package com.tekclover.wms.api.masters.model.putawaystrategy;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class AddPutAwayStrategy {

    @NotBlank(message = "Language Id is mandatory")
    private String languageId;
    @NotBlank(message = "Company Code Id is mandatory")
    private String companyCodeId;
    @NotBlank(message = "Plant Id is mandatory")
    private String plantId;
    @NotBlank(message = "Warehouse Id is mandatory")
    private String warehouseId;
    @NotBlank(message = "Brand is mandatory")
    private String brand;
    @NotBlank(message = "Article is mandatory")
    private String article;
    @NotBlank(message = "Category is mandatory")
    private String category;
    private String gender;
    private String proposedBin;
    private String capacity;
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

    private String maxCapacity;
    private String superMaxCapacity;

}
