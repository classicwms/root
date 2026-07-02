package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class PickerReport {

    private Long pickUpHeaderCount;
    private String assignedPickerId;
    private String levelId;
    private List<PickUpHeaderCount> pickupHeaderV2;
}
