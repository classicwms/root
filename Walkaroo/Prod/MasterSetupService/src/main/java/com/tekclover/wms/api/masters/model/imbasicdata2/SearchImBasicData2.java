package com.tekclover.wms.api.masters.model.imbasicdata2;

import lombok.Data;

import java.util.List;

@Data
public class SearchImBasicData2 {

    private List<String> itemCode;
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> itemBarcode;

}
