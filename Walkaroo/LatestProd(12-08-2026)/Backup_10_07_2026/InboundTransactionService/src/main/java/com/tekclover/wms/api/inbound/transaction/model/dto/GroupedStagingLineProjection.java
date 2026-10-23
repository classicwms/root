package com.tekclover.wms.api.inbound.transaction.model.dto;

import java.util.Date;

public interface GroupedStagingLineProjection {

    String getRefDocNumber();

    Long getTotalCount();

    Long getTotalBarcodeStatusCount();

    Long getTotalVehicleHuNumber();

    Long getTotalVehicleScan();

    String getVehicleNumber();

    Date getCreatedOn();

    Integer getPutAwayFlag(); // map as Integer (0 or 1)
}
