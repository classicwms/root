package com.tekclover.wms.api.transaction.model.threepl.stockmovement;

import lombok.Data;

import java.io.Serializable;

@Data
public class StockMovementCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /**
     * MVT_DOC_NO
     * LANG_ID
     * C_ID
     * PLANT_ID
     * WH_ID
     * ITM_CODE
     */

    private Long movementDocNo;
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
}
