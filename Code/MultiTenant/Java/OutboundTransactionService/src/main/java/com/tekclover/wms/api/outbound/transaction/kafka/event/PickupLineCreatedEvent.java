package com.tekclover.wms.api.outbound.transaction.kafka.event;

import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.AddPickupLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupLineCreatedEvent {

   private List<AddPickupLine> addPickupLines;
   private String loginUserID;

}
