package com.tekclover.wms.api.masters.model.impartner;

import lombok.Data;

@Data
public class ImPartnerInput {

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private String itemCode;
    private String manufacturerName;
    private String partnerItemBarcode;

    private String businessPartnerCode;
    private String businessPartnerType;

}