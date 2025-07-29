package com.tekclover.wms.api.masters.model.impartner;

import lombok.Data;
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
