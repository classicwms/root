package com.tekclover.wms.core.model.dto;

import lombok.Data;

@Data

public class InventoryAgeProjection {
    public Long age;
    public String itemCode;
    public String articleNo;
    public String priceSegment;
    public String barCodeId;
    public Long inventoryId;
    public String storageBin;
}
