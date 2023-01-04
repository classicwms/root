package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindWorkOrder {

    private List<String> workOrderId;

    private List<String> codeId;

    private List<String> customerId;

    private List<String> jobCard;

    private List<String> processedBy;

    private List<String> created;

    private List<String> jobCardType;

    private List<String> status;

    private Date startDate;
    private Date endDate;

    //private Date workOrderDate;
    private List<String> workOrderNumber;
    private List<String> workOrderSbu;

   // private Boolean isActive;
}
