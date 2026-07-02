package com.tekclover.wms.api.idmaster.model.storageclassid;
import lombok.Data;
import java.util.List;

@Data
public class FindStorageClassId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> storageClassId;
    private List<String> languageId;
}
