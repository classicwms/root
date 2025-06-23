package com.tekclover.wms.core.model.idmaster;
import lombok.Data;

import java.util.List;

@Data
public class FindFloorId {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> floorId;
    private List<String> languageId;
}
