package com.tekclover.wms.api.transaction.model.dto;

import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import lombok.Data;

import java.util.Date;

@Data
public class GroupedStagingLineResponse {
    private String refDocNumber;
    private Long totalCount;
    private Long totalBarcodeStatusCount;
    private Long totalVehicleHuNumber;
    private Long totalHuNumber;
    private Long totalVehicleScan;
    private String vehicleNumber;
    private Boolean putAwayFlag;
    private Date createdOn;

    public GroupedStagingLineResponse(String refDocNumber, long totalCount, long totalBarcodeStatusCount, long totalVehicleHuNumber,
                                      long totalVehicleScan, String vehicleNumber, Date createdOn, Boolean putAwayFlag) {
        this.refDocNumber = refDocNumber;
        this.totalCount = totalCount;
        this.totalBarcodeStatusCount = totalBarcodeStatusCount;
        this.totalVehicleHuNumber = totalVehicleHuNumber;
        this.totalVehicleScan = totalVehicleScan;
        this.vehicleNumber = vehicleNumber;
        this.createdOn = createdOn;
        this.putAwayFlag = putAwayFlag;
    }
}
