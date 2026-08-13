package com.tekclover.wms.api.masters.model.imvariant;

import lombok.Data;

import java.util.List;

@Data
public class SearchImVariant {

    private List<String> warehouseId;
    private List<String> itemCode;
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> variantCode;
    private List<String> variantType;
}
