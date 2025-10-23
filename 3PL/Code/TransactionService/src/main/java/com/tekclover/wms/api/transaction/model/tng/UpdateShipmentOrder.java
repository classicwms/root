package com.tekclover.wms.api.transaction.model.tng;
import lombok.Data;

import java.util.List;

@Data
public class UpdateShipmentOrder {

    private String orderReference;
    private String storerKey;

    private List<Sku> sku;
}
