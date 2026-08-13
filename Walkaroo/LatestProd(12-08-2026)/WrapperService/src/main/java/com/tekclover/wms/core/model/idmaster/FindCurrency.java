package com.tekclover.wms.core.model.idmaster;
import lombok.Data;
import java.util.List;

@Data
public class FindCurrency {
    private List<Long> currencyId;
    private List<String>languageId;
}
