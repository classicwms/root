package com.tekclover.wms.api.idmaster.model.stocktypeid;

import lombok.Data;
import java.util.List;

@Data
public class FindStockTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> stockTypeId;
    private List<String> languageId;
}
