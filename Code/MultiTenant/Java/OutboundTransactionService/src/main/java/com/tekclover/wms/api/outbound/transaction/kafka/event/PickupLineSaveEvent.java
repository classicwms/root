package com.tekclover.wms.api.outbound.transaction.kafka.event;

import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupLineSaveEvent {

    private List<PickupLineV2> pickupLineV2;
    private String loginUserID;
}