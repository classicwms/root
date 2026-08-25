package com.tekclover.wms.api.masters.model.drivervehicleassignment;

import lombok.Data;

import java.io.Serializable;

@Data
public class DriverVehicleAssignmentCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `DRIVER_ID','VEHICLE_ID','ROUTE_ID`'
     */
    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private Long driverId;

    private String vehicleNumber;

    private Long routeId;
}
