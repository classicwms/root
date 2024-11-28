package com.tekclover.wms.api.idmaster.model.numberrange;

import lombok.Data;
import java.util.List;

@Data
public class FindNumberRange {

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> languageId;
    private List<Long> numberRangeCode;
    private List<Long> fiscalYear;
}
