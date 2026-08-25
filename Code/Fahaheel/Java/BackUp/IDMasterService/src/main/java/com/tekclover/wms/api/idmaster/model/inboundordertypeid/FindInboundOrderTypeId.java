package com.tekclover.wms.api.idmaster.model.inboundordertypeid;

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
