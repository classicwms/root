package com.tekclover.wms.api.masters.model.tng;

import lombok.Data;

import java.util.List;

@Data
public class ItemSku {

    private String storerKey;
    private String warehouseKey;

    private List<Items> items;
}
