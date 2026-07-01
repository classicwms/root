package com.tekclover.wms.api.transaction.model.kafka;

import com.tekclover.wms.api.transaction.model.outbound.pickup.AddPickupLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupLineEvent {

    private List<AddPickupLine> pickupLines;
    private String loginUserID;

}