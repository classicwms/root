package com.mnrclara.spark.core.model.MultiTenant;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPeriodicHeaderV2 {

    private List<String> warehouseId;
    private List<Long> cycleCountTypeId;
    private List<String> cycleCountNo;
    private List<Long> headerStatusId;
    private List<Long> lineStatusId;
    private List<String> createdBy;
    private List<String> cycleCounterId;

    private Date startCreatedOn;
    private Date endCreatedOn;

    // V2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
}