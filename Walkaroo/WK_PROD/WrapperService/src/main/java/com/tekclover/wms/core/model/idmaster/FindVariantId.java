package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindVariantId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> languageId;
    private List<String> variantCode;
    private List<String> variantSubCode;
}
