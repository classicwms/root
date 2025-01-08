package com.tekclover.wms.api.idmaster.model.varientid;

import lombok.Data;
import java.util.List;

@Data
public class FindVariantId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> variantCode;
    private List<String> variantSubCode;
    private List<String> languageId;
}
