package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class SearchOrderManagementLineV2 extends SearchOrderManagementLine {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> manufacturerName;

}