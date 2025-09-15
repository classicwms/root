package com.tekclover.wms.api.transaction.model.inbound.inventory;

import lombok.Data;

@Data
public class ItemStockDto {

    private String sku;

    private Double stock_quantity;

    private String stock_status;
}
