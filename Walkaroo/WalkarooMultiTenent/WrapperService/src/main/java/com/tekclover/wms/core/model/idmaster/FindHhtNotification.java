package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindHhtNotification {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> warehouseId;
    private List<String> deviceId;
    private List<String> userId;
    private Boolean isLoggedIn;
    private Boolean portalUser;
    private Date startDate;
    private Date endDate;
}