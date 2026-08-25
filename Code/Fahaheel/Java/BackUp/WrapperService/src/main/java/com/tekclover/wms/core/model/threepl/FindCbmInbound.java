package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.List;

@Data
public class FindCbmInbound {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> itemCode;
    private List<Long> statusId;
}
