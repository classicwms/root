package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.List;

@Data
public class FindHhtUser {
    private List<String> warehouseId;
    private List<String> userId;
    private List<String> companyCodeId;
    private List<String> languageId;
    private List<String> plantId;

}
