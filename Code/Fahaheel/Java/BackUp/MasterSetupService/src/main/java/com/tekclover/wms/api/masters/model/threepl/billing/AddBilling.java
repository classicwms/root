package com.tekclover.wms.api.masters.model.threepl.billing;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddBilling {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    @NotBlank(message = "Partner Code is mandatory")
    private String partnerCode;
    private String moduleId;
    private Long billModeId;
    private Long billFrequencyId;
    private Long paymentTermId;
    private Long paymentModeId;
    private String billGenerationIndicator;
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
