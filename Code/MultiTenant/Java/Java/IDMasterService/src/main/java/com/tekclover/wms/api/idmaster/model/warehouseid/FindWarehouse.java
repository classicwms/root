package com.tekclover.wms.api.idmaster.model.warehouseid;

import lombok.Data;
import java.util.List;

@Data
public class FindWarehouse {
    private String companyCodeId;
    private List<String> warehouseId;
    private String plantId;
    private List<String> languageId;
}
