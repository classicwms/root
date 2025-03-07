package com.tekclover.wms.api.idmaster.model.city;

import lombok.Data;

import java.util.List;

@Data
public class FindCity {
    private List<String> cityId;
    private String cityName;
    private String stateId;
    private String countryId;
    private List<String>languageId;
}
