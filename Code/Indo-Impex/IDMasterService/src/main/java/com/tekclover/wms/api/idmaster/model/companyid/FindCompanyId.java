package com.tekclover.wms.api.idmaster.model.companyid;
import lombok.Data;
import java.util.List;

@Data
public class FindCompanyId {
    private List<String> companyCodeId;
    private List<String> languageId;

}
