package com.tekclover.wms.api.idmaster.model.submovementtypeid;
import lombok.Data;
import java.util.List;

@Data
public class FindSubMovementTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> movementTypeId;
    private List<String> subMovementTypeId;
    private List<String> languageId;
}
