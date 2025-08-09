package com.tekclover.wms.api.idmaster.model.itemgroupid;

import lombok.Data;
import java.util.List;

@Data
public class FindItemGroupId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> itemTypeId;
    private List<Long> itemGroupId;
    private String itemGroup;
    private List<String> languageId;
}
