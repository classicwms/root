package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class FindBillingFrequencyId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String>languageId;
    private List<Long> billFrequencyId;
    private List<Long> statusId;
}