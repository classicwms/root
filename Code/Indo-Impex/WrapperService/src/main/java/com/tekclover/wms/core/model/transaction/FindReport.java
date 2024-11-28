package com.tekclover.wms.core.model.transaction;


import lombok.Data;

import java.util.Date;

@Data
public class FindReport {
    private String startDate;
    private String endDate;

    private Long leadTime;
    private Long quantity;
    private Long averageLeadTime;
    private Long productivity;

}
