package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PalletizationDashBoardResponseList {

    private String company;
    private String plant;
    private String warehouse;
    private String truckNo;
//    private Long count;
    private String partnerItemBarcode;
    private String userId;
    private String name;
    private Timestamp startTime;
    private Timestamp endTime;
    private Long leadTimeMinutes;
    private Long productivityPerHour;

}
