package com.tekclover.wms.api.transaction.model.packing;


import lombok.Data;

import java.util.List;

@Data
public class FindPackingLine {


    private List<String> languageId;

    private List<Long> companyCodeId;

    private List<String> plantId;

    private List<String> warehouseId;

    private List<String> preOutboundNo;

    private List<String> refDocNumber;

    private List<String> partnerCode;

    private List<Long> lineNumber;

    private List<Long> packingNo;

    private List<String> itemCode;

}
