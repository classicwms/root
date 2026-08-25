package com.tekclover.wms.api.idmaster.model.plantid;
import lombok.Data;
import java.util.List;

@Data
public class FindPlantId {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> languageId;
}
