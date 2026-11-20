package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class PutawayResponse {

    private String company;
    private String plant;
    private String warehouse;
    private String truckNo;
    private String orderId;
    private String itemCode;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String barcodeId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String userId;
    private Double qty;

}
