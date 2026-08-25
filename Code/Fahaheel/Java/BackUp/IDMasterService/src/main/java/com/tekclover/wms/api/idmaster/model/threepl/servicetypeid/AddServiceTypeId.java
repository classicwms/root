package com.tekclover.wms.api.idmaster.model.threepl.servicetypeid;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class AddServiceTypeId {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    @NotBlank(message = "Warehouse Id is mandatory")
    private String warehouseId;
    @NotBlank(message = "moduleId  is mandatory")
    private String moduleId;
    @NotNull(message = "serviceTypeId  is mandatory")
    private Long serviceTypeId;
    private String serviceTypeDescription;
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
