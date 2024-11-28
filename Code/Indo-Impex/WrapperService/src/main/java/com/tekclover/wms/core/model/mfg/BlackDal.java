package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.List;

@Data
public class BlackDal {

    private ProductionOrder productionOrder;

    private Sorting sorting;

    private Soaking soaking;

    private Peeling peeling;

    private Paste paste;

    private List<OperationConsumption> operationConsumption;
}
