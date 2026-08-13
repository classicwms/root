package com.tekclover.wms.core.model.idmaster;

import lombok.Data;
import java.util.List;

@Data
public class FindControlTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> controlTypeId;
    private List<String> languageId;

}
