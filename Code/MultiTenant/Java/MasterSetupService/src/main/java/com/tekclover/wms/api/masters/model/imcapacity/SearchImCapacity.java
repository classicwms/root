package com.tekclover.wms.api.masters.model.imcapacity;

import lombok.Data;
import java.util.List;

@Data
public class SearchImCapacity {
    private List<String> companyCodeId;
    private List<String>plantId;
    private List<String>languageId;
    private List<String>warehouseId;
    private List<String> itemCode;
}
