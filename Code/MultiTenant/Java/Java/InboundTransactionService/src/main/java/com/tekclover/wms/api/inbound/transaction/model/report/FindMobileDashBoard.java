package com.tekclover.wms.api.inbound.transaction.model.report;

import lombok.Data;

import java.util.List;

// done
@Data
public class FindMobileDashBoard {

    private List<String> companyCode;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> warehouseId;
    private List<String> userID;
}
