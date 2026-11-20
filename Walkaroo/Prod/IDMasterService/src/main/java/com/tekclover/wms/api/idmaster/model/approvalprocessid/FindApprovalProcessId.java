package com.tekclover.wms.api.idmaster.model.approvalprocessid;
import lombok.Data;

import java.util.List;

@Data
public class FindApprovalProcessId {

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> approvalProcessId;
    private List<String> languageId;
}
