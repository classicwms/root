package com.tekclover.wms.api.idmaster.model.decimalnotationid;

import lombok.Data;
import java.util.List;

@Data
public class FindDecimalNotationId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> decimalNotationId;
    private String decimalNotation;
    private List<String> languageId;

}
