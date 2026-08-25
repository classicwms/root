package com.tekclover.wms.api.idmaster.model.movementtypeid;

import lombok.Data;
import java.util.List;

@Data
public class FindMovementTypeId {

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> movementTypeId;
    private List<String> languageId;

}
