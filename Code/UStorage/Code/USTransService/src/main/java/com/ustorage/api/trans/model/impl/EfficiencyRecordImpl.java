package com.ustorage.api.trans.model.impl;

import java.util.Date;
import java.util.List;

public interface EfficiencyRecordImpl {

    String getJobCardType();
    String getWorkOrderId();
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
