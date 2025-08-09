package com.tekclover.wms.api.masters.model.threepl.cbminbound;

import lombok.Data;

import java.io.Serializable;
@Data
public class CbmInboundCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`ITM_CODE`
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
}
