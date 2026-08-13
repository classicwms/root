package com.tekclover.wms.api.masters.model.imalternateparts;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.List;

@Data
public class SearchImAlternateParts {

    private List<String>companyCodeId;
    private List<String>plantId;
    private List<String>languageId;
    private List<String>warehouseId;
    private List<String> itemCode;
    private List<String> altItemCode;
}
