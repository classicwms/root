package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class FindBillingFormatId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> billFormatId;
    private List<String>languageId;
    private List<Long> statusId;
}
