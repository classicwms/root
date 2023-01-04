package com.ustorage.api.trans.model.impl;

import java.util.Date;

public interface EfficiencyRecordImpl {

    String getJobCardType();
    String getProcessedBy();

    String getPlannedHours();
    String getWorkedHours();

    String getCreatedOn();

    String getProcessedTime();
    String getLeadTime();
    Date getStartDate();
    Date getEndDate();

    Date getWorkOrderDate();

}
