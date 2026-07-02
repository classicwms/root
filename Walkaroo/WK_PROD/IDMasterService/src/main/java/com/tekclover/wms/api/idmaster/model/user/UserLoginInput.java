package com.tekclover.wms.api.idmaster.model.user;

import lombok.Data;

@Data
public class UserLoginInput {

    private String userId;
    private String loginPassword;
    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;

}