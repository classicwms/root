package com.tekclover.wms.core.model.dto;

import com.tekclover.wms.core.model.transaction.GrLineV2;
import com.tekclover.wms.core.model.transaction.OrderManagementLineV2;
import com.tekclover.wms.core.model.transaction.PutAwayLineV2;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CrossDockResponseV9 {
    List<GrLineV2> grLine = new ArrayList<>();
    List<OrderManagementLineV2> orderManagementLine = new ArrayList<>();
    List<PutAwayLineV2> putAwayLineV2 = new ArrayList<>();
}
