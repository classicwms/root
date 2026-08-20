package com.tekclover.wms.core.model.dto;

import com.tekclover.wms.core.model.transaction.PreOutboundLineV2;
import com.tekclover.wms.core.model.transaction.StagingLineEntityV2;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FindCrossDockResponseV6 {

    List<StagingLineEntityV2> stagingLines = new ArrayList<>();
    List<PreOutboundLineV2> preOutboundLine = new ArrayList<>();

}
