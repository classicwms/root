package com.tekclover.wms.api.masters.model.cyclecountscheduler;

import lombok.Data;
import java.util.List;

@Data
public class SearchCycleCountScheduler {
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> levelId;
    private List<Long> cycleCountTypeId;
    private List<String> schedulerNumber;
}
