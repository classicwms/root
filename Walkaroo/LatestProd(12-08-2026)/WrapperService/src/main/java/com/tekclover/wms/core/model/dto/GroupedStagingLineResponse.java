package com.tekclover.wms.core.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GroupedStagingLineResponse {
    private String refDocNumber;
    private Long totalCount;
    private Long totalBarcodeStatusCount;
    private Long totalHuNumber;
    private Long totalVehicleHuNumber;
    private Long totalVehicleScan;
    private String vehicleNumber;
    private Boolean putAwayFlag;
    private Date createdOn;
}
