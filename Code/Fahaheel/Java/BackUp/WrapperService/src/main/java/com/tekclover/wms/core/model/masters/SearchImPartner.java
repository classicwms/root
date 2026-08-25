package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchImPartner {

    private List<String> itemCode;
    private List<String> manufacturerName;
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;

    private String businessPartnerCode;
    private String businessPartnerType;
    private String partnerItemBarcode;

}