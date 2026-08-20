package com.tekclover.wms.core.model.warehouse.inbound;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ASNV9 {

    @Valid
    private ASNV9Header asnHeader;

    @Valid
    private List<ASNV9Line> asnLine;

}
