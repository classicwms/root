package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EfficiencyRecordReport {

    String JobCardType;
    String ProcessedBy;

    String PlannedHours;

    String WorkedHours;

    String CreatedOn;

    String ProcessedTime;
    String LeadTime;
    Date StartDate;
    Date EndDate;
    Date WorkOrderDate;

}
