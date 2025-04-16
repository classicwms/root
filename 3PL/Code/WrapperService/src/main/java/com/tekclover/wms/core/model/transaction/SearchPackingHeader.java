package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.List;

@Data
public class SearchPackingHeader {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> preOutboundNo;
    private List<String> refDocNumber;
    private List<String> partnerCode;
    private List<String> qualityInspectionNo;
    private List<String> packingNo;
    private List<Long> statusId;
}
