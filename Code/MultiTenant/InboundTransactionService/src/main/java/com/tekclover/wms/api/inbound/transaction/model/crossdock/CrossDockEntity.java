package com.tekclover.wms.api.inbound.transaction.model.crossdock;

import lombok.Data;

import java.util.List;

@Data
public class CrossDockEntity {

    List<CrossDockGrLine> crossDockGrLines;
    List<CrossDockUnallocated> crossDockOrderManagementLines;
}
