package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PickupLineResponse {

    private String company;
    private String plant;
    private String warehouse;
    private String customerNo;
    private String customerName;
    private String orderNo;
    private String itemCode;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String barcodeId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Double qty;

}
