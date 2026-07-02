package com.tekclover.wms.api.idmaster.model.palletizationlevelid;

import lombok.Data;

import java.util.List;

@Data
public class FindPalletizationLevelId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> palletizationLevelId;
    private String palletizationLevel;
    private String palletizationLevelReference;
    private List<String> languageId;
}
