package com.tekclover.wms.api.transaction.model.outbound.packing;

import lombok.Data;

import java.util.List;

@Data
public class SearchPackingLine {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> preOutboundNo;
    private List<String> refDocNumber;
    private List<String> partnerCode;
    private List<Long> lineNumber;
    private List<Long> packingNo;
    private List<String> itemCode;
    private List<Long> statusId;
}
