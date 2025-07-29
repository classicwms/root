package com.tekclover.wms.api.idmaster.model.user;
import lombok.Data;

import java.util.List;

@Data
public class FindUserManagement {
    private List<String> userId;
    private List<String> languageId;
    private List<String> companyCode;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<Long> userRoleId;
    private List<Long> userTypeId;
    private Boolean portalLoggedIn;
    private Boolean hhtLoggedIn;
}
