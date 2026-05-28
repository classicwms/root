package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPreOutboundHeader {

    private List<String> warehouseId;
    private List<String> preOutboundNo;
    private List<Long> outboundOrderTypeId;
    private List<String> soType;
    private List<String> soNumber;
    private List<String> partnerCode;
    private List<Long> statusId;
    private List<String> createdBy;

    private Date startRequiredDeliveryDate;
    private Date endRequiredDeliveryDate;
    private Date startOrderDate;
    private Date endOrderDate;
    private Date startCreatedOn;
    private Date endCreatedOn;
}
