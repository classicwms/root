package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class PutAwayDashBoardUserListRes {

    private String company;
    private String plant;
    private String warehouse;
    private String truckNo;
    private String barcodeId;
    private String userId;
    private String name;
    private Timestamp startTime;
    private Timestamp endTime;
    private Long leadTimeMinutes;
    private BigDecimal productivityPerHour;
    private Long totalPallet;
    private Long personProductivity;
    private Long palletProductivity;

}
