package com.tekclover.wms.api.idmaster.model.deliverynotification;


import lombok.Data;

import java.util.List;

@Data
public class FindDeliveryNotification {
    private List<String> cityId;
    private String cityName;
    private String stateId;
    private String countryId;
    private List<String>languageId;
}
