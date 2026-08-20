package com.tekclover.wms.api.inbound.transaction.model.crossdock;

import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.preoutbound.PreOutboundLineV2;
import lombok.Data;

import java.util.List;

@Data
public class CrossDockInputV9 {

    List<StagingLineEntityV2> stagingLines;
    List<PreOutboundLineV2> preOutboundLines;
}
