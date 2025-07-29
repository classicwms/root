package com.tekclover.wms.api.idmaster.model.cyclecounttypeid;

import lombok.Data;
import java.util.List;

@Data
public class FindCycleCountTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> cycleCountTypeId;
    private List<String> languageId;

}
