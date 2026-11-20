package com.tekclover.wms.core.model.idmaster;
import lombok.Data;
import java.util.List;

@Data
public class FindCompanyId {
    private List<String> companyCodeId;
    private List<String> languageId;
}
