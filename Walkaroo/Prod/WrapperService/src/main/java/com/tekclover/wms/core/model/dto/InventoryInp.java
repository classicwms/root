package com.tekclover.wms.core.model.dto;

import lombok.Data;

import java.util.List;

@Data

public class InventoryInp {

    private List<String> companyCode;

    private List<String> languageId;

    private List<String> plantId;

    private List<String> warehouseId;

}
