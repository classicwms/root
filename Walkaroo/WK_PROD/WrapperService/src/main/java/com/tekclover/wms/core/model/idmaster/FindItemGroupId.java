package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindItemGroupId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> itemTypeId;
    private List<String> languageId;
    private List<Long> itemGroupId;
    private String itemGroup;
}
