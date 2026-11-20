package com.tekclover.wms.api.idmaster.model.usertypeid;

import lombok.Data;

import java.util.List;

@Data
public class FindUserTypeId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<Long> userTypeId;
    private List<String> languageId;
}
