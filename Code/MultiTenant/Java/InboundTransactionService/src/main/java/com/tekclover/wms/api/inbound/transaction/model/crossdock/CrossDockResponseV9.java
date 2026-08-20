package com.tekclover.wms.api.inbound.transaction.model.crossdock;

import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CrossDockResponseV9 {
    List<GrLineV2> grLine = new ArrayList<>();
    List<OrderManagementLineV2> orderManagementLine = new ArrayList<>();
    List<PutAwayLineV2> putAwayLineV2 = new ArrayList<>();
}
