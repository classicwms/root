package com.tekclover.wms.api.idmaster.model.returntypeid;

import lombok.Data;
import java.util.List;

@Data
public class FindReturnTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> returnTypeId;
    private List<String> languageId;
}
