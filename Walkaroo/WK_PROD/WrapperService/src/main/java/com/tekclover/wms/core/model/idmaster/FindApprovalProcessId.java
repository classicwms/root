package com.tekclover.wms.core.model.idmaster;
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
