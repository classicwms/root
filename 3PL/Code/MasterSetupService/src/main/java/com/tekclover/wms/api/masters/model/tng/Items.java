package com.tekclover.wms.api.masters.model.tng;

import lombok.Data;

@Data
public class Items {

    private String sku;
    private String description;
    private Double grossWeight;
    private Double netWeight;
    private Double height;
    private Double length;
    private Double width;
    private String lot;
//    private String serial;
//    private String serialBarcode;
    private Long shelfLife;
    private String note;
}
