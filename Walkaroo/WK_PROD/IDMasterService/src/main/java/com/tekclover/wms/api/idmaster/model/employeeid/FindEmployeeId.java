package com.tekclover.wms.api.idmaster.model.employeeid;
import lombok.Data;
import java.util.List;

@Data
public class FindEmployeeId {

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private List<String> employeeId;
    private List<String> employeeName;
    private List<String> languageId;
}
