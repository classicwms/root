package com.tekclover.wms.api.enterprise.model.employee;

import lombok.Data;

@Data
public class UpdateEmployee {

	private String employeeId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private String companyCode;
	private String warehouseNo;
	private Long processId;
	private String languageId;
	private String stroageSection;
	private String handlingEquipment;
	private String status;
	private String updatedBy;
}
