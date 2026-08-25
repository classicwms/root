package com.tekclover.wms.core.model.kafka;


import com.tekclover.wms.core.model.transaction.AddPickupLine;
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
