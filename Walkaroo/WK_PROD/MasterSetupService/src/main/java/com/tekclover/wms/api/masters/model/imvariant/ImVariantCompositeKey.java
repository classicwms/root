package com.tekclover.wms.api.masters.model.imvariant;

import lombok.Data;
import java.io.Serializable;

@Data
public class ImVariantCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`, 'VAR_CODE','VAR_TYPE'
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
    private String variantCode;
    private String variantType;
}
