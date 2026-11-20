package com.tekclover.wms.api.idmaster.model.threepl.billingmodeid;

import lombok.Data;
import java.util.List;

@Data
public class FindBillingModeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> billModeId;
    private List<String> languageId;
    private List<Long> statusId;
}
