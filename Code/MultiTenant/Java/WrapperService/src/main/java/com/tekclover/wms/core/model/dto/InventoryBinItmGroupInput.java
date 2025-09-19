package com.tekclover.wms.core.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class InventoryBinItmGroupInput {

    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> itemCode;
    private List<String> storageBin;

}
