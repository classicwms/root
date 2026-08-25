package com.tekclover.wms.api.idmaster.model.notification;

import lombok.Data;

import java.util.List;

@Data
public class FindNotification {

    private List<String> languageId;
    private List<String> companyId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> notificationId;

}
