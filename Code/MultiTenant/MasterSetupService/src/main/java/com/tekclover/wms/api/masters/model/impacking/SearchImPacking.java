package com.tekclover.wms.api.masters.model.impacking;

import lombok.Data;

import java.util.List;

@Data
public class SearchImPacking {
    private List<String> itemCode;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> packingMaterialNo;
}
