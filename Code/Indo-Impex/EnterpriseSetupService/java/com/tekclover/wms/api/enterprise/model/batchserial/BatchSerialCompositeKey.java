package com.tekclover.wms.api.enterprise.model.batchserial;

import lombok.Data;
import java.io.Serializable;

@Data
public class BatchSerialCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, 'ST_MTD', 'LEVEL_ID','MAINT'
     * */

    private String languageId;
    private String companyId;
    private String plantId;
    private String warehouseId;
    private Long levelId;
    private String storageMethod;
    private String maintenance;
    private Long id;
}
