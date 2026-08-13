package com.tekclover.wms.core.model.spark;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
public class AgingDashBoardRequest {

    @NotBlank(message = "companyCodeId is mandatory")
    private List<String> companyCodeId;
    @NotBlank(message = "plantId is mandatory")
    private List<String> plantId;
    @NotBlank(message = "warehouseId is mandatory")
    private List<String> warehouseId;
    private List<String> itemCode;
    private List<String> storageBin;
    @NotBlank(message = "Start Date is mandatory")
    private Date fromDate;
    @NotBlank(message = "End Date is mandatory")
    private Date toDate;

}
