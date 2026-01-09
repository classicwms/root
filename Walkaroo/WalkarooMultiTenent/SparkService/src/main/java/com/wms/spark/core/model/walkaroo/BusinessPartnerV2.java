package com.wms.spark.core.model.walkaroo;


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
    private Long statusId;
    private String createdBy;
    private Timestamp createdOn;

}
