package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.List;

@Data
public class SearchWorkCenter {

    private List<String> warehouseId;
    private List<String> workCenterType;
    private List<Long> workCenterId;
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
}
