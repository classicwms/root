package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindInboundOrderTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> inboundOrderTypeId;
    private List<String> languageId;
}
