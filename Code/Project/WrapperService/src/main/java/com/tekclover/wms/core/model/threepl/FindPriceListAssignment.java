package com.tekclover.wms.core.model.threepl;

import lombok.Data;
import java.util.List;
@Data
public class FindPriceListAssignment {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> partnerCode;
    private List<Long> priceListId;
    private List<Long> statusId;
}
