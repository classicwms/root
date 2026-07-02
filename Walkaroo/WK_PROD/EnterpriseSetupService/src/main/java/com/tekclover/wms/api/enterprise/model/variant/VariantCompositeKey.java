package com.tekclover.wms.api.enterprise.model.variant;

import lombok.Data;
import java.io.Serializable;

@Data
public class VariantCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, 'VAR_ID', 'LEVEL_ID','VAR_SUB_ID'
     * */
    private Long id;
    private String variantId;
    private String companyId;
    private String plantId;
    private String warehouseId;
    private String variantSubId;
    private Long levelId;
    private String languageId;
}
