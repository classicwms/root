package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class InterWarehouseTransferOutV2 {

    @Valid
    private InterWarehouseTransferOutHeaderV2 interWarehouseTransferOutHeader;

    @Valid
    private List<InterWarehouseTransferOutLineV2> interWarehouseTransferOutLine;
}
