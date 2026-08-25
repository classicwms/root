package com.tekclover.wms.api.idmaster.model.threepl.billingformatid;

import lombok.Data;

import java.util.List;

@Data
public class FindBillingFormatId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> languageId;
    private List<Long> billFormatId;
    private List<Long> statusId;
}
