package com.tekclover.wms.core.model.idmaster;
import lombok.Data;

import java.util.List;

@Data
public class FindWarehouseId {
    private String companyCodeId;
    private List<String> warehouseId;
    private String plantId;
    private List<String> languageId;

}
