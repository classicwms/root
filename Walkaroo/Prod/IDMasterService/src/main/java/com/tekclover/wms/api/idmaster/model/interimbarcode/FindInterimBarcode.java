package com.tekclover.wms.api.idmaster.model.interimbarcode;

import lombok.Data;

import java.util.List;

@Data
public class FindInterimBarcode {
    private List<String> itemCode;
    private List<String> barcode;
    private List<String> referenceField1;
    private List<Long> interimBarcodeId;
}
