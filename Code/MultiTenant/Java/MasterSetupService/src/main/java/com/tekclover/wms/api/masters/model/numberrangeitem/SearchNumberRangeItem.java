package com.tekclover.wms.api.masters.model.numberrangeitem;

import lombok.Data;

import java.util.List;

@Data
public class SearchNumberRangeItem {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> itemTypeId;
    private List<Long> sequenceNo;
}
