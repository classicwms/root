package com.tekclover.wms.api.idmaster.model.notificationmessage;

import lombok.Data;

@Data
public class NotificationMsgDeleteInput {

    private Long notificationId;

    private String languageId;

    private String companyId;

    private String plantId;

    private String warehouseId;

}
