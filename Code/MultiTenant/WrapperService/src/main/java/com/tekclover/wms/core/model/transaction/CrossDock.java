package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CrossDock {

//    List<GrLineV2> grLineV2List = new ArrayList<>();
//    List<UnallocatedOrderLine> unallocatedOrderLines = new ArrayList<>();

    List<StagingLineEntityV2> stagingLineEntityV2List = new ArrayList<>();
    List<OrderManagementLineV2> orderManagementLineV2List = new ArrayList<>();

}
