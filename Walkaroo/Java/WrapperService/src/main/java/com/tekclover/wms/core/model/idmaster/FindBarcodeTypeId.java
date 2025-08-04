package com.tekclover.wms.core.model.idmaster;

import lombok.Data;
import java.util.List;

@Data
public class FindBarcodeTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> barcodeTypeId;
    private List<String> barcodeType;
    private List<String> languageId;
}
