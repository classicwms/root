package com.tekclover.wms.api.masters.model.route;

import lombok.Data;
import java.util.List;

@Data
public class SearchRoute {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> routeId;
}
