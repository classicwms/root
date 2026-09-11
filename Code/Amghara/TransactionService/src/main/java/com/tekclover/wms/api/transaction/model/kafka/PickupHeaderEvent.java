package com.tekclover.wms.api.transaction.model.kafka;


import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PickupHeaderEvent {
    List<PickupHeaderV2> pickupHeaderV2List = new ArrayList<>();
    private String assignPickerId;
    private String statusDescription;
    private Long statusId;
    private String loginUserID;
}
