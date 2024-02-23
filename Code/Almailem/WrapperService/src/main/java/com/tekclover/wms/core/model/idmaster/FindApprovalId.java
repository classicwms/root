package com.tekclover.wms.core.model.idmaster;

import lombok.Data;
import java.util.List;

@Data
public class FindApprovalId {
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> approvalId;
    private String approvalLevel;
    private String approverCode;
    private String approvalProcessId;
    private String approverName;
    private List<String> languageId;
}
