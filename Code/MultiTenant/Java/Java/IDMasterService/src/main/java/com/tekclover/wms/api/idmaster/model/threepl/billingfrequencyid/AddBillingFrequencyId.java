package com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class AddBillingFrequencyId {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    @NotBlank(message = "Warehouse Id is mandatory")
    private String warehouseId;
    @NotNull(message = "Bill Frequency Id is mandatory")
    private Long billFrequencyId;
    private String description;
    private Long statusId;
    private String dayForBillGeneration;
    private String dateOfBillingGeneration;
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
