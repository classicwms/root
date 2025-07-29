package com.tekclover.wms.core.model.spark;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindBusinessPartner {

    private List<String> warehouseId;
    private List<String>companyCodeId;
    private List<String>plantId;
    private List<String>languageId;
    private List<Long> businessPartnerType;
    private List<String> partnerCode;
    private List<String> partnerName;
    private List<Long> statusId;

    private List<String> createdBy;
    private List<String> updatedBy;

    private Date startCreatedOn;
    private Date endCreatedOn;

    private Date startUpdatedOn;
    private Date endUpdatedOn;
}
