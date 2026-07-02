package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class Periodic {

    @Valid
    private PeriodicHeaderV1 periodicHeaderV1;

    @Valid
    private List<PeriodicLineV1> periodicLineV1;
}
