package com.tekclover.wms.core.model.dto;

import com.tekclover.wms.core.model.transaction.PreOutboundLineV2;
import com.tekclover.wms.core.model.transaction.StagingLineEntityV2;
import lombok.Data;

import java.util.List;

@Data
public class CrossDockInputV9 {

    List<StagingLineEntityV2> stagingLines;
    List<PreOutboundLineV2> preOutboundLines;
}
