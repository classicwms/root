package com.tekclover.wms.api.inbound.transaction.model.report;


import lombok.Data;

import java.util.Date;

@Data
public class FindReport {

    private Date startConfirmedOn;
    private Date endConfirmedOn;

    private String startDate;
    private String endDate;

    private Long leadTime;
    private Long quantity;
    private Long averageLeadTime;
    private Long productivity;

}
