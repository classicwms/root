package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.List;

@Data
public class GarlicPaste {

    private ProductionOrder productionOrder;

    private Sorting sorting;

    private Soaking soaking;

    private Paste paste;

    private List<OperationConsumption> operationConsumption;
}
