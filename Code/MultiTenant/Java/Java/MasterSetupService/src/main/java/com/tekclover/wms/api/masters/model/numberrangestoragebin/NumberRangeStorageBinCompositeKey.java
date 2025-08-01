package com.tekclover.wms.api.masters.model.numberrangestoragebin;

import lombok.Data;

import java.io.Serializable;

@Data
public class NumberRangeStorageBinCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `FL_ID',' ST_SEC_ID ',' ROW_ID ','AISLE_ID
     */
    private String companyCodeId;

    private String plantId;

    private String languageId;

    private String warehouseId;

    private Long floorId;

    private String storageSectionId;

    private String rowId;

    private String aisleNumber;
}
