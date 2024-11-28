package com.tekclover.wms.core.model.threepl;

import lombok.Data;

@Data
public class PriceListInput {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long priceListId;
    private Long serviceTypeId;
    private String moduleId;
    private Long chargeRangeId;

}
