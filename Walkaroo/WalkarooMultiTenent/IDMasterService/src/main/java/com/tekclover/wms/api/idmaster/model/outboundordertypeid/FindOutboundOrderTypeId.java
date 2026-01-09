package com.tekclover.wms.api.idmaster.model.outboundordertypeid;

import lombok.Data;
import java.util.List;

@Data
public class FindOutboundOrderTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> outboundOrderTypeId;
    private List<String> languageId;
}
