package com.tekclover.wms.core.model.idmaster;

import lombok.Data;
import java.util.List;

@Data
public class FindCountry {
    private List<String> countryId;
    private List<String> countryName;
    private List<String> languageId;
}
