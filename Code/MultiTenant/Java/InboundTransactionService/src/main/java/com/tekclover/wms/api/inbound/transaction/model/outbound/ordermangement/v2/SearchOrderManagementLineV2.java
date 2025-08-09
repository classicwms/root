package com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.v2;

import com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.SearchOrderManagementLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchOrderManagementLineV2 extends SearchOrderManagementLine {

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