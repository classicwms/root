package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.List;

@Data
public class RoastedCuminPowder {

    private ProductionOrder productionOrder;

    private List<OperationConsumption> operationConsumption;

    private Sorting sorting;

    private Powder powder;
}
