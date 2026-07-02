package com.tekclover.wms.api.idmaster.model.threepl.paymentmodeid;

import lombok.Data;
import java.util.List;

@Data
public class FindPaymentModeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> languageId;
    private List<Long> paymentModeId;
    private List<Long> statusId;
}
