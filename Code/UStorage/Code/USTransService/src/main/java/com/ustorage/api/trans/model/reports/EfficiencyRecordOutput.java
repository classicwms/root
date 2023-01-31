package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EfficiencyRecordOutput {

    private String jobCardType;
    private List<String> processedBy;
    private String plannedHours;
    private String workedHours;
    private String createdOn;
    private String processedTime;
    private String leadTime;
    private Date startDate;
    private Date endDate;
    private Date workOrderDate;

}
