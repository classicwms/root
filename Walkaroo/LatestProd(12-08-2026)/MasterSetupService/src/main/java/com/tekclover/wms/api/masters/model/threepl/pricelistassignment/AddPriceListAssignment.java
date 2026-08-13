package com.tekclover.wms.api.masters.model.threepl.pricelistassignment;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddPriceListAssignment {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    @NotBlank(message = "Warehouse Id is mandatory")
    private String warehouseId;
    @NotBlank(message = "Partner Code  is mandatory")
    private String  partnerCode;
    @NotNull(message = "Price List Id  is mandatory")
    private Long priceListId;
    private Long statusId;
    private String moduleId;
    private Long serviceTypeId;
    private Long chargeRangeId;
    private Long businessPartnerType;
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
