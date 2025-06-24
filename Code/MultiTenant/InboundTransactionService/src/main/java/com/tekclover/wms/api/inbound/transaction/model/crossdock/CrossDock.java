package com.tekclover.wms.api.inbound.transaction.model.crossdock;

import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CrossDock {

    List<StagingLineEntityV2> stagingLineEntityV2List = new ArrayList<>();
    List<OrderManagementLineV2> orderManagementLineV2List = new ArrayList<>();

}
