package com.tekclover.wms.api.masters.model.workcenter;

import lombok.Data;

import java.io.Serializable;


@Data
public class WorkCenterCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, 'WR_ID','WR_TYP'
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long workCenterId;
    private String workCenterType;
}
