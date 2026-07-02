package com.tekclover.wms.core.model.idmaster;
import lombok.Data;
import java.util.List;

@Data
public class FindBarcodeSubtypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> barcodeTypeId;
    private List<Long> barcodeSubTypeId;
    private List<String> barcodeSubType;
    private List<String> languageId;
}
