package com.tekclover.wms.api.idmaster.model.threepl.billingmodeid;


import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class AddBillingModeId {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    @NotBlank(message = "Warehouse Id is mandatory")
    private String warehouseId;
    @NotNull(message = "Bill Mode Id is mandatory")
    private Long billModeId;
    private String description;
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