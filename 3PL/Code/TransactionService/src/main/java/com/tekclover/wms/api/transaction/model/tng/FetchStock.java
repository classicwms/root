package com.tekclover.wms.api.transaction.model.tng;

import lombok.Data;

import java.util.List;

@Data
public class FetchStock {

    private String sku;
    private String description;
    private String warehouse;
    private Double availableQty;

}
