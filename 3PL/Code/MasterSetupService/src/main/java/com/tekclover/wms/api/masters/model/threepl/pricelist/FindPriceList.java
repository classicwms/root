package com.tekclover.wms.api.masters.model.threepl.pricelist;

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
    private List<String>languageId;
    private List<Long> statusId;
    private List<Double> chargeRangeTo;
   private List<Long> chargeRangeId;
}
