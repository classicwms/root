package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindStagingHeader {

    private List<Long> inboundOrderTypeId;
    private List<String> stagingNo;
    private List<String> preInboundNo;
    private List<String> refDocNumber;
    private List<Long> statusId;
    private List<String> warehouseId;
    private List<String> createdBy;

    private Date startCreatedOn;
    private Date endCreatedOn;
}
