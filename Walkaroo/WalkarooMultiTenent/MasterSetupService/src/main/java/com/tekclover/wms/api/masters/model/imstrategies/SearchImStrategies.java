package com.tekclover.wms.api.masters.model.imstrategies;

import lombok.Data;

import java.util.List;

@Data
public class SearchImStrategies {

    private List<String> warehouseId;
    private List<String> itemCode;
    private List<Long> strategyTypeId;
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private List<Long> sequenceIndicator;

}
