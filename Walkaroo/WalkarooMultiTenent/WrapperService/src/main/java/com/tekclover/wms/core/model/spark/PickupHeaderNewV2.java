package com.tekclover.wms.core.model.spark;

import lombok.Data;

@Data
public class PickupHeaderNewV2 {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String shipToCode;
    private String shipToParty;
    private String pickupNumber;

}
