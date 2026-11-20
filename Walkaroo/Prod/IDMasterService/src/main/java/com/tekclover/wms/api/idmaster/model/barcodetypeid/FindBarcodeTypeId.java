package com.tekclover.wms.api.idmaster.model.barcodetypeid;

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
