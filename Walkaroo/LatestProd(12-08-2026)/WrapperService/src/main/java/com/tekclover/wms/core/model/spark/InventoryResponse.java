package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InventoryResponse {

    private String company;
    private String plant;
    private String warehouse;
    private Long age;
    private String storageBin;
    private String itemCode;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String barcodeId;
    private Timestamp inwardDate;
    private Double qty;

}
