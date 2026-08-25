package com.tekclover.wms.api.idmaster.model.currency;
import lombok.Data;
import java.util.List;

@Data
public class FindCurrency {
    private List<Long> currencyId;
    private List<String> languageId;
}
