package com.tekclover.wms.api.transaction.model.inbound.preinbound;

import lombok.Data;

@Data
public class WebHook {

     private String storerKey;

     private String orderID;

     private String orderReference;

     private String eventId;

     private String eventDescription;

     private String eventDate;

}