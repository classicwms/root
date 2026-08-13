package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class SalesOrderV2 {

    @Valid
    private SalesOrderHeaderV2 salesOrderHeader;

    @Valid
    private List<SalesOrderLineV2> salesOrderLine;
}
