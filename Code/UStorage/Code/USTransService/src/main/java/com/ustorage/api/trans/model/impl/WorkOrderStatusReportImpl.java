package com.ustorage.api.trans.model.impl;

import java.util.Date;
import java.util.List;

public interface WorkOrderStatusReportImpl {
    String getWorkOrderStatus();
    String getProcessedBy();
    String getWorkOrderSbu();

    Date getCreatedOn();
    Date getWorkOrderDate();

    String getWorkOrderId();
    String getCustomerId();
    String getCustomerName();
    String getStatus();
    String getRemarks();
    String getCreated();
    String getProcessedTime();
    String getLeadTime();

}
