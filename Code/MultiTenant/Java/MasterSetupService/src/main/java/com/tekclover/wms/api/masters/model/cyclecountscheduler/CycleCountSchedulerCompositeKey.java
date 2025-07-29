package com.tekclover.wms.api.masters.model.cyclecountscheduler;

import lombok.Data;
import java.io.Serializable;

@Data
public class CycleCountSchedulerCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, CC_TYP_ID','SCHE_NO','LEVEL_ID'
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long levelId;
    private Long cycleCountTypeId;
    private String schedulerNumber;
}
