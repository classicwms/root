package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindOrderManagementLine {

    private List<String> warehouseId;
    private List<String> preOutboundNo;
    private List<String> refDocNumber;
    private List<String> partnerCode;
    private List<String> itemCode;
    private List<Long> outboundOrderTypeId;
    private List<Long> statusId;
    private List<String> description;
    private List<String> soType;

    private Date startRequiredDeliveryDate;
    private Date endRequiredDeliveryDate;

}
