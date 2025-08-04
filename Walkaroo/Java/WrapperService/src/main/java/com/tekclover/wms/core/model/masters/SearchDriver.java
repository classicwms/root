package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.List;

@Data
public class SearchDriver {

    private List<String> warehouseId;
    private List<String>companyCodeId;
    private List<String>plantId;
    private List<String>languageId;
    private List<Long>driverId;
    private List<String> userId;
}
