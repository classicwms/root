package com.tekclover.wms.api.idmaster.model.storagetypeid;
import lombok.Data;

import java.util.List;

@Data
public class FindStorageTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> storageClassId;
    private List<Long> storageTypeId;
    private List<String> languageId;
}
