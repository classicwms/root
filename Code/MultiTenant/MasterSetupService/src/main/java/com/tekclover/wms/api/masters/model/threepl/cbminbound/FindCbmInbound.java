package com.tekclover.wms.api.masters.model.threepl.cbminbound;

import lombok.Data;

import java.util.List;

@Data
public class FindCbmInbound {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String>languageId;
    private List<String> itemCode;
    private List<Long> statusId;
}
