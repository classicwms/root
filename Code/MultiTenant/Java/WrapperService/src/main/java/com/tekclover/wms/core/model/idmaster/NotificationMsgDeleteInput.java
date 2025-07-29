package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

@Data
public class NotificationMsgDeleteInput {

    private Long notificationId;

    private String languageId;

    private String companyId;

    private String plantId;

    private String warehouseId;

}
