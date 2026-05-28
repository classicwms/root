package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindPreInboundHeader {
    private List<String> warehouseId;
    private List<String> preInboundNo;
    private List<Long> inboundOrderTypeId;
    private List<String> refDocNumber;
    private List<Long> statusId;

    private Date startRefDocDate;
    private Date endRefDocDate;

    private Date startCreatedOn;
    private Date endCreatedOn;
}