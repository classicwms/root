package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class FindBilling {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> partnerCode;
    private List<String> moduleId;
    private List<Long> statusId;
}
