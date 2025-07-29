package com.tekclover.wms.api.masters.model.driver;

import lombok.Data;

import java.io.Serializable;

@Data
public class DriverCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `DRIVER_ID`
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long driverId;
}
