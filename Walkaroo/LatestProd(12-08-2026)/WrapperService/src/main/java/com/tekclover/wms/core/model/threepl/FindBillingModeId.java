package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class FindBillingModeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String>languageId;
    private List<Long> billModeId;
    private List<Long> statusId;
}
