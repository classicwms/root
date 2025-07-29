package com.tekclover.wms.api.idmaster.model.state;

import lombok.Data;
import java.util.List;

@Data
public class FindState {

    private List<String> stateId;
    private String stateName;
    private String countryId;
    private List<String> languageId;
}
