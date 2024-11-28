package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.List;

@Data
public class OrderConfirmation {

    private ProductionOrder productionOrder;

    private List<OperationConsumption> operationConsumption;

    private Sorting sorting;

    private Soaking soaking;

    private Peeling peeling;

    private Paste paste;

    private DiceSliceChop diceSliceChop;

    private Powder powder;

    private Cooking cooking;
}
