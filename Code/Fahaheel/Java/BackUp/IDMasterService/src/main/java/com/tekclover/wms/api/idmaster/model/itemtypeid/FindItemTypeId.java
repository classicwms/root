package com.tekclover.wms.api.idmaster.model.itemtypeid;

import lombok.Data;

import java.util.List;

@Data
public class FindItemTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> itemTypeId;
    private String itemType;
    private List<String>languageId;
}
