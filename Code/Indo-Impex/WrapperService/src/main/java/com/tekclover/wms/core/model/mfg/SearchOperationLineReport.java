package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchOperationLineReport {

    private List<String> itemCode;
    private List<String> productionOrderNo;
    private List<String> batchNumber;
    private Date startOrderConfirmedOn;
    private Date endOrderConfirmedOn;

    private List<String> statusId;

    private Date startCreatedOn;
    private Date endCreatedOn;
}
