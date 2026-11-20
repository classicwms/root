package com.tekclover.wms.api.idmaster.model.uomid;
import lombok.Data;
import java.util.List;

@Data
public class FindUomId {
    private String companyCodeId;
    private List<String> uomId;
    private List<String> uomType;
    private List<String> languageId;
    private String warehouseId;
    private String plantId;

}
