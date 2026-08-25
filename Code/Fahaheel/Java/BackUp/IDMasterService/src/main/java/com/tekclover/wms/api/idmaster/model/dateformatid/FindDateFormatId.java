package com.tekclover.wms.api.idmaster.model.dateformatid;
import lombok.Data;

import java.util.List;
@Data
public class FindDateFormatId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> dateFormatId;
    private String dateFormat;
    private List<String> languageId;
}
