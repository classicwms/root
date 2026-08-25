package com.tekclover.wms.api.outbound.transaction.model.driverremark;

import lombok.Data;

import java.util.List;

@Data
public class SearchDriverRemark {


    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> preOutboundNo;
    private List<String> refDocNumber;
}
