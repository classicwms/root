package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImBatchSerialCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_CODE","ST_MTD"
     */

    private String companyCodeId;
    private String languageId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
    private String storageMethod;
}
