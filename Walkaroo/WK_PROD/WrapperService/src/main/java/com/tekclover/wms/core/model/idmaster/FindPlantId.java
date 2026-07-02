package com.tekclover.wms.core.model.idmaster;

import lombok.Data;
import java.util.List;

@Data
public class FindPlantId {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> languageId;

}
