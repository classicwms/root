package com.tekclover.wms.api.masters.model.vehicle;

import lombok.Data;

import java.io.Serializable;


@Data
public class VehicleCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `VEHICLE_ID`,
     */
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String vehicleNumber;
}
