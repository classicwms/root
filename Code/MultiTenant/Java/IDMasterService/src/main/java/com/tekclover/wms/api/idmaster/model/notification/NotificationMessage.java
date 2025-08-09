package com.tekclover.wms.api.idmaster.model.notification;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class NotificationMessage {

    private String receptionToken;
    private String title;
    private String body;
    private String image;
    private Map<String,String> data ;
}
