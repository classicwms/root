package com.mnrclara.spark.core.model.MultiTenant;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class BusinessPartnerV2 {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long businessPartnerType;
    private String partnerCode;
    private String partnerName;
    private String referenceField5;
    private Long statusId;
    private String createdBy;
    private Timestamp createdOn;

}
