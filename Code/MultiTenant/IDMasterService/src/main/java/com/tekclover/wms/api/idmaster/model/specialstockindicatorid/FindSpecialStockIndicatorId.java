package com.tekclover.wms.api.idmaster.model.specialstockindicatorid;

import lombok.Data;

import java.util.List;

@Data
public class FindSpecialStockIndicatorId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> stockTypeId;
    private List<String> specialStockIndicatorId;
    private List<String> languageId;
}
