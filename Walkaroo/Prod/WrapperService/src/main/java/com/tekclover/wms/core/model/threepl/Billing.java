package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
@Data
public class Billing {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String partnerCode;
    private String moduleId;
    private Long billModeId;
    private Long billFrequencyId;
    private Long paymentTermId;
    private Long paymentModeId;
    private String companyIdAndDescription;
    private String plantIdAndDescription;
    private String warehouseIdAndDescription;
    private String moduleIdAndDescription;
    private String billModeIdAndDescription;
    private String billFrequencyIdAndDescription;
    private String paymentTermIdAndDescription;
    private String paymentModeIdAndDescription;
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
    private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
    private Date updatedOn = new Date();
}
