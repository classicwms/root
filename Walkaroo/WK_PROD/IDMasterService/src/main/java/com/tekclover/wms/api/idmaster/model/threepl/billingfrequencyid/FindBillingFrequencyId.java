package com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid;

import lombok.Data;
import java.util.List;

@Data
public class FindBillingFrequencyId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> languageId;
    private List<Long> billFrequencyId;
    private List<Long> statusId;
}