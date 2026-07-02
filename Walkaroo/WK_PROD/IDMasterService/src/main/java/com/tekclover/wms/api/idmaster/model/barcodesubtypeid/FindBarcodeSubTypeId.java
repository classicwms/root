package com.tekclover.wms.api.idmaster.model.barcodesubtypeid;
import lombok.Data;
import java.util.List;

@Data
public class FindBarcodeSubTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> barcodeTypeId;
    private List<Long> barcodeSubTypeId;
    private List<String> barcodeSubType;
    private List<String> languageId;
}
