package com.tekclover.wms.api.inbound.transaction.model.crossdock;

import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.preoutbound.PreOutboundLineV2;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FindCrossDockResponseV6 {

    List<StagingLineEntityV2> stagingLines = new ArrayList<>();
    List<PreOutboundLineV2> preOutboundLine = new ArrayList<>();

}
