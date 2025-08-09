package com.tekclover.wms.api.idmaster.model.strategyid;
import lombok.Data;

import java.util.List;

@Data
public class FindStrategyId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> strategyTypeId;
    private List<String> strategyNo;
    private List<String> languageId;
}
