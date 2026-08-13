package com.tekclover.wms.api.idmaster.model.notificationmessage;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindNotificationMessage {

    private List<Long> notificationId;
    private List<String> languageId;
    private List<String> companyId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> userId;
    private List<String> orderType;

    private Date startDate;
    private Date endDate;

}
