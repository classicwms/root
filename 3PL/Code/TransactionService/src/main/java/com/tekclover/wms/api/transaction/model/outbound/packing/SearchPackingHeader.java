package com.tekclover.wms.api.transaction.model.outbound.packing;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
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
