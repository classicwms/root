package com.tekclover.wms.core.model.idmaster;

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
