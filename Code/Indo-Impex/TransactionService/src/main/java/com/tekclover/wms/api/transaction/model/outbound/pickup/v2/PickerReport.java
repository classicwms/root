package com.tekclover.wms.api.transaction.model.outbound.pickup.v2;


import lombok.Data;

import java.util.List;

@Data
public class PickerReport {
    private Long pickUpHeaderCount;
    private String assignedPickerId;
    private String levelId;
    private List<PickUpHeaderCount> pickupHeaderV2;
}
