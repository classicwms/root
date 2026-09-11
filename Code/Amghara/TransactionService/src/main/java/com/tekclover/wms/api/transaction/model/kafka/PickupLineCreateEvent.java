package com.tekclover.wms.api.transaction.model.kafka;


import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupLineCreateEvent {
    List<PickupLineV2> pickupLineV2List = new ArrayList<>();
}
