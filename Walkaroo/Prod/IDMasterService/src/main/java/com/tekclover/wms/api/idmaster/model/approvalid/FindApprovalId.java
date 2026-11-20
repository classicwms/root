package com.tekclover.wms.api.idmaster.model.approvalid;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
