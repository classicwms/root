package com.tekclover.wms.api.masters.model.dock;

import lombok.Data;
import java.util.List;

@Data
public class SearchDock {
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> dockType;
    private List<String> dockId;
}
