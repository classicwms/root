package com.tekclover.wms.api.idmaster.model.transfertypeid;
import lombok.Data;
import java.util.List;

@Data
public class FindTransferTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> transferTypeId;
    private List<String> languageId;
}
