package com.tekclover.wms.core.model.mfg;

import lombok.Data;

import java.util.List;

@Data
public class TomatoPuree {

    private ProductionOrder productionOrder;

    private Paste paste;

    private List<OperationConsumption> operationConsumption;
}
