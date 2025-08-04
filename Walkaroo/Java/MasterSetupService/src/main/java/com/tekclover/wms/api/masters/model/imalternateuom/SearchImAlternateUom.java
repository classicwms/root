package com.tekclover.wms.api.masters.model.imalternateuom;

import lombok.Data;

import java.util.List;

@Data
public class SearchImAlternateUom {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> itemCode;
    private List<String> uomId;
    private List<String> alternateUom;
}
