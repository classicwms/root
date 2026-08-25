package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindWarehouseTypeId {
    private String companyCodeId;
    private String plantId;
    private List<String> warehouseId;
    private List<Long> warehouseTypeId;
    private List<String> languageId;

}
