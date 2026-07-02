package com.tekclover.wms.api.idmaster.model.subitemgroupid;

import lombok.Data;

import java.util.List;

@Data
public class FindSubItemGroupId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> itemTypeId;
    private List<Long> itemGroupId;
    private List<Long> subItemGroupId;
    private String subItemGroup;
    private List<String> languageId;
}
