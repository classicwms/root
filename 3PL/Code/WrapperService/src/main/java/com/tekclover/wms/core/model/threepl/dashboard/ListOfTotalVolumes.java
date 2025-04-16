package com.tekclover.wms.core.model.threepl.dashboard;

import lombok.Data;

@Data
public class ListOfTotalVolumes {

    private Long inventoryId;
    private String threePLPartnerId;
    private Long binClassId;
    private String storageBin;

    private Double totalVolumes;
    private Double occupiedVolumes;
    private Double remainingVolumes;

}
