package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AgingDashBoardResponse {

    private String company;
    private String plant;
    private String warehouse;
    private String key;
    private String label;
    private Long count;
    private Long totalSkus;
    private Timestamp asOfDate;

}
