package com.tekclover.wms.api.inbound.orders.model.unallocatedorder;

import lombok.Data;

import java.util.List;

@Data
public class SearchUnallocatedOrderLineV2 extends SearchUnallocatedOrderLine {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> manufacturerName;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private List<String> materialNo;
    private List<String> priceSegment;
    private List<String> articleNo;
    private List<String> gender;
    private List<String> color;
    private List<String> size;
    private List<String> noPairs;
    private List<String> barcodeId;

}
