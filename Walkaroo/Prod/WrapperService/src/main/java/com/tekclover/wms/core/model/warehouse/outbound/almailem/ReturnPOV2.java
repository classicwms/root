package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class ReturnPOV2 {

    @Valid
    private ReturnPOHeaderV2 returnPOHeader;

    @Valid
    private List<ReturnPOLineV2> returnPOLine;
}
