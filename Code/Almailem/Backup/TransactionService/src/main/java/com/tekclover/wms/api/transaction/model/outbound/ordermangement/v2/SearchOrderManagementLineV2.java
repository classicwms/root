package com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.SearchOrderManagementLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SearchOrderManagementLineV2 extends SearchOrderManagementLine {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> manufacturerName;

}