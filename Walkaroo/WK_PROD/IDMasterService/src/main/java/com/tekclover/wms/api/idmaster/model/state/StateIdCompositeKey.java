package com.tekclover.wms.api.idmaster.model.state;

import lombok.Data;

import java.io.Serializable;

@Data
public class StateIdCompositeKey implements Serializable {
    private static final long serialVersionUID = -7617672247680004647L;

    /*
     * `STATE_ID`,`COUNTRY_ID`, `LANG_ID`
     */
    private String stateId;
    private String countryId;
    private String languageId;
}
