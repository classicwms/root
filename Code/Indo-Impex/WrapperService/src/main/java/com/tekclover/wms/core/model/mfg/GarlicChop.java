package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.List;

@Data
public class GarlicChop {

    private Sorting sorting;

    private Soaking soaking;

    private DiceSliceChop diceSliceChop;

    private ProductionOrder productionOrder;

    private List<OperationConsumption> operationConsumption;
}
