package com.tekclover.wms.api.idmaster.model.city;
import lombok.Data;

import java.io.Serializable;

@Data
public class CityIdCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `CITY_ID`,`STATE_ID`, `COUNTRY_ID`, `ZIP_CD`,`LANG_ID`
     */
    private String cityId;
    private String stateId;
    private String countryId;
    private String languageId;
}
