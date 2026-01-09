package com.tekclover.wms.api.inbound.transaction.model.pickup.v2;


import lombok.Data;

import java.util.List;

@Data
public class PickerReport {
    private Long pickUpHeaderCount;
    private String assignedPickerId;
    private String levelId;
    private List<PickUpHeaderCount> pickupHeaderV2;
}
