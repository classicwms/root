package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class FindStockMovement {

    private List<Long> movementDocNo;
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> itemCode;
}
