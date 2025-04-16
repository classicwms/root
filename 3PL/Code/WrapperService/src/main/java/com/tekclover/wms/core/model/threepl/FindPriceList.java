package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class FindPriceList {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> moduleId;
    private List<Long> priceListId;
    private List<Long> serviceTypeId;
    private List<Long> statusId;
    private List<String>languageId;
    private List<Long> chargeRangeId;
    private List<Double> chargeRangeTo;
}
