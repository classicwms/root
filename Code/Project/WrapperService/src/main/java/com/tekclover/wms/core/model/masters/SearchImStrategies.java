package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.List;

@Data
public class SearchImStrategies {

    private List<String> warehouseId;
    private List<String> itemCode;
    private String strategeyTypeId;
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private List<Long> sequenceIndicator;
}
