package com.tekclover.wms.core.model.idmaster;


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
