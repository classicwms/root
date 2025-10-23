package com.tekclover.wms.api.transaction.model.tng;

import lombok.Data;

@Data
public class DataResponse {

    public String orderKey;
    public String orderReference;
    public String warehouseKey;
}
