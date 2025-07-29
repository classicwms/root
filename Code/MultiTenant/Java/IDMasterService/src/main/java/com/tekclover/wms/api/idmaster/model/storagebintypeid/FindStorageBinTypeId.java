package com.tekclover.wms.api.idmaster.model.storagebintypeid;
import lombok.Data;
import java.util.List;

@Data
public class FindStorageBinTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> storageClassId;
    private List<Long> storageTypeId;
    private List<Long> storageBinTypeId;
    private List<String>languageId;
}
