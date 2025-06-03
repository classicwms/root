package com.tekclover.wms.core.model.dto;

import lombok.Data;

@Data

public class BinVolume {

    private  String storageBin;
    private  Double occupiedVolume;   // SUM(total_tpl_cbm)
    private  Double occupancyVolume;
    private  String companyDescription;
    private String  plantDescription;
    private  String warehouseDescription;
    // occ_vol from storagebin
}
