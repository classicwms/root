package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindOutboundOrderStatusId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> outboundOrderStatusId;
    private List<String> languageId;
}
