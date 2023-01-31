package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WorkOrderStatusReport {

    Date CreatedOn;
    Date WorkOrderDate;
    String WorkOrderStatus;
    String WorkOrderId;
    String WorkOrderSbu;
    String CustomerId;
    String CustomerName;
    String Status;
    String Remarks;
    String Created;
    String ProcessedBy;
    String ProcessedTime;
    String LeadTime;

}
