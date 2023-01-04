package com.ustorage.api.trans.model.impl;

import java.util.Date;

public interface WorkOrderStatusReportImpl {
    String getWorkOrderStatus();

    String getWorkOrderSbu();

    Date getCreatedOn();
    Date getWorkOrderDate();

    String getWorkOrderId();
    String getCustomerId();
    String getCustomerName();
    String getStatus();
    String getRemarks();
    String getCreated();
    String getProcessedBy();
    String getProcessedTime();
    String getLeadTime();

}
