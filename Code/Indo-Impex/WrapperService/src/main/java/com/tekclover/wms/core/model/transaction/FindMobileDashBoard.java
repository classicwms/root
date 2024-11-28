package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class FindMobileDashBoard {

    private List<String> companyCode;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> warehouseId;
    private List<String> userID;
}
