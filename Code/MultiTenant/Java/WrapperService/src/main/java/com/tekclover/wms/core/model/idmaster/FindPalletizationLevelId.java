package com.tekclover.wms.core.model.idmaster;
import lombok.Data;
import java.util.List;

@Data
public class FindPalletizationLevelId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> palletizationLevelId;
    private String palletizationLevel;
    private String palletizationLevelReference;
    private List<String>languageId;
}
