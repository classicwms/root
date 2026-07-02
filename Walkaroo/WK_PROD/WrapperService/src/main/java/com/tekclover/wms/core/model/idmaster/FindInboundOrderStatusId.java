package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindInboundOrderStatusId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> inboundOrderStatusId;
    private List<String> languageId;

}
