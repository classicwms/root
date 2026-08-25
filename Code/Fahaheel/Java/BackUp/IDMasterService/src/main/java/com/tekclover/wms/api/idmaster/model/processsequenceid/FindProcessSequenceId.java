package com.tekclover.wms.api.idmaster.model.processsequenceid;

import lombok.Data;
import java.util.List;

@Data
public class FindProcessSequenceId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> processId;
    private List<Long> processSequenceId;
    private List<String> languageId;
}
